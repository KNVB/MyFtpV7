/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.MpscLinkedQueueNode;
import io.netty.util.internal.OneTimeTask;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.netty.channel.unix.FileDescriptor.pipe;
import static io.netty.util.internal.ObjectUtil.checkNotNull;

public abstract class AbstractEpollStreamChannel extends AbstractEpollChannel {

    private static final String EXPECTED_TYPES =
            " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " +
                    StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEpollStreamChannel.class);
    static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = new ClosedChannelException();

    static {
        CLOSED_CHANNEL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    }

    /**
     * The future of the current connection attempt.  If not null, subsequent
     * connection attempts will fail.
     */
    private ChannelPromise connectPromise;
    private ScheduledFuture<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;
    private final Queue<SpliceInTask> spliceQueue = PlatformDependent.newMpscQueue();

    // Lazy init these if we need to splice(...)
    private FileDescriptor pipeIn;
    private FileDescriptor pipeOut;

    /**
     * @deprecated Use {@link #AbstractEpollStreamChannel(Channel, Socket)}.
     */
    @Deprecated
    protected AbstractEpollStreamChannel(Channel parent, int fd) {
        this(parent, new Socket(fd));
    }

    /**
     * @deprecated Use {@link #AbstractEpollStreamChannel(Socket, boolean)}.
     */
    @Deprecated
    protected AbstractEpollStreamChannel(int fd) {
        this(new Socket(fd));
    }

    /**
     * @deprecated Use {@link #AbstractEpollStreamChannel(Socket, boolean)}.
     */
    @Deprecated
    protected AbstractEpollStreamChannel(FileDescriptor fd) {
        this(new Socket(fd.intValue()));
    }

    /**
     * @deprecated Use {@link #AbstractEpollStreamChannel(Socket, boolean)}.
     */
    @Deprecated
    protected AbstractEpollStreamChannel(Socket fd) {
        this(fd, fd.getSoError() == 0);
    }

    protected AbstractEpollStreamChannel(Channel parent, Socket fd) {
        super(parent, fd, Native.EPOLLIN, true);
        // Add EPOLLRDHUP so we are notified once the remote peer close the connection.
        flags |= Native.EPOLLRDHUP;
    }

    protected AbstractEpollStreamChannel(Socket fd, boolean active) {
        super(null, fd, Native.EPOLLIN, active);
        // Add EPOLLRDHUP so we are notified once the remote peer close the connection.
        flags |= Native.EPOLLRDHUP;
    }

    @Override
    protected AbstractEpollUnsafe newUnsafe() {
        return new EpollStreamUnsafe();
    }

    /**
     * Splice from this {@link AbstractEpollStreamChannel} to another {@link AbstractEpollStreamChannel}.
     * The {@code len} is the number of bytes to splice. If using {@link Integer#MAX_VALUE} it will
     * splice until the {@link ChannelFuture} was canceled or it was failed.
     *
     * Please note:
     * <ul>
     *   <li>both channels need to be registered to the same {@link EventLoop}, otherwise an
     *   {@link IllegalArgumentException} is thrown. </li>
     *   <li>{@link EpollChannelConfig#getEpollMode()} must be {@link EpollMode#LEVEL_TRIGGERED} for this and the
     *   target {@link AbstractEpollStreamChannel}</li>
     * </ul>
     *
     */
    public final ChannelFuture spliceTo(final AbstractEpollStreamChannel ch, final int len) {
        return spliceTo(ch, len, newPromise());
    }

    /**
     * Splice from this {@link AbstractEpollStreamChannel} to another {@link AbstractEpollStreamChannel}.
     * The {@code len} is the number of bytes to splice. If using {@link Integer#MAX_VALUE} it will
     * splice until the {@link ChannelFuture} was canceled or it was failed.
     *
     * Please note:
     * <ul>
     *   <li>both channels need to be registered to the same {@link EventLoop}, otherwise an
     *   {@link IllegalArgumentException} is thrown. </li>
     *   <li>{@link EpollChannelConfig#getEpollMode()} must be {@link EpollMode#LEVEL_TRIGGERED} for this and the
     *   target {@link AbstractEpollStreamChannel}</li>
     * </ul>
     *
     */
    public final ChannelFuture spliceTo(final AbstractEpollStreamChannel ch, final int len,
                                        final ChannelPromise promise) {
        if (ch.eventLoop() != eventLoop()) {
            throw new IllegalArgumentException("EventLoops are not the same.");
        }
        if (len < 0) {
            throw new IllegalArgumentException("len: " + len + " (expected: >= 0)");
        }
        if (ch.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED
                || config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
        }
        checkNotNull(promise, "promise");
        if (!isOpen()) {
            promise.tryFailure(CLOSED_CHANNEL_EXCEPTION);
        } else {
            SpliceInTask task = new SpliceInChannelTask(ch, len, checkNotNull(promise, "promise"));
            spliceQueue.add(task);
            failSpliceIfClosed(promise);
        }
        return promise;
    }

    /**
     * Splice from this {@link AbstractEpollStreamChannel} to another {@link FileDescriptor}.
     * The {@code offset} is the offset for the {@link FileDescriptor} and {@code len} is the
     * number of bytes to splice. If using {@link Integer#MAX_VALUE} it will splice until the
     * {@link ChannelFuture} was canceled or it was failed.
     *
     * Please note:
     * <ul>
     *   <li>{@link EpollChannelConfig#getEpollMode()} must be {@link EpollMode#LEVEL_TRIGGERED} for this
     *   {@link AbstractEpollStreamChannel}</li>
     *   <li>the {@link FileDescriptor} will not be closed after the {@link ChannelFuture} is notified</li>
     * </ul>
     */
    public final ChannelFuture spliceTo(final FileDescriptor ch, final int offset, final int len) {
        return spliceTo(ch, offset, len, newPromise());
    }

    /**
     * Splice from this {@link AbstractEpollStreamChannel} to another {@link FileDescriptor}.
     * The {@code offset} is the offset for the {@link FileDescriptor} and {@code len} is the
     * number of bytes to splice. If using {@link Integer#MAX_VALUE} it will splice until the
     * {@link ChannelFuture} was canceled or it was failed.
     *
     * Please note:
     * <ul>
     *   <li>{@link EpollChannelConfig#getEpollMode()} must be {@link EpollMode#LEVEL_TRIGGERED} for this
     *   {@link AbstractEpollStreamChannel}</li>
     *   <li>the {@link FileDescriptor} will not be closed after the {@link ChannelPromise} is notified</li>
     * </ul>
     */
    public final ChannelFuture spliceTo(final FileDescriptor ch, final int offset, final int len,
                                        final ChannelPromise promise) {
        if (len < 0) {
            throw new IllegalArgumentException("len: " + len + " (expected: >= 0)");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0 but was " + offset);
        }
        if (config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
        }
        checkNotNull(promise, "promise");
        if (!isOpen()) {
            promise.tryFailure(CLOSED_CHANNEL_EXCEPTION);
        } else {
            SpliceInTask task = new SpliceFdTask(ch, offset, len, checkNotNull(promise, "promise"));
            spliceQueue.add(task);
            failSpliceIfClosed(promise);
        }
        return promise;
    }

    private void failSpliceIfClosed(ChannelPromise promise) {
        if (!isOpen()) {
            // Seems like the Channel was closed in the meantime try to fail the promise to prevent any
            // cases where a future may not be notified otherwise.
            if (promise.tryFailure(CLOSED_CHANNEL_EXCEPTION)) {
                eventLoop().execute(new OneTimeTask() {
                    @Override
                    public void run() {
                        // Call this via the EventLoop as it is a MPSC queue.
                        clearSpliceQueue();
                    }
                });
            }
        }
    }

    /**
     * Write bytes form the given {@link ByteBuf} to the underlying {@link java.nio.channels.Channel}.
     * @param buf           the {@link ByteBuf} from which the bytes should be written
     */
    private boolean writeBytes(ChannelOutboundBuffer in, ByteBuf buf, int writeSpinCount) throws Exception {
        int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            in.remove();
            return true;
        }

        if (buf.hasMemoryAddress() || buf.nioBufferCount() == 1) {
            int writtenBytes = doWriteBytes(buf, writeSpinCount);
            in.removeBytes(writtenBytes);
            return writtenBytes == readableBytes;
        } else {
            ByteBuffer[] nioBuffers = buf.nioBuffers();
            return writeBytesMultiple(in, nioBuffers, nioBuffers.length, readableBytes, writeSpinCount);
        }
    }

    private boolean writeBytesMultiple(
            ChannelOutboundBuffer in, IovArray array, int writeSpinCount) throws IOException {

        long expectedWrittenBytes = array.size();
        final long initialExpectedWrittenBytes = expectedWrittenBytes;

        int cnt = array.count();

        assert expectedWrittenBytes != 0;
        assert cnt != 0;

        boolean done = false;
        int offset = 0;
        int end = offset + cnt;
        for (int i = writeSpinCount - 1; i >= 0; i--) {
            long localWrittenBytes = fd().writevAddresses(array.memoryAddress(offset), cnt);
            if (localWrittenBytes == 0) {
                break;
            }
            expectedWrittenBytes -= localWrittenBytes;

            if (expectedWrittenBytes == 0) {
                // Written everything, just break out here (fast-path)
                done = true;
                break;
            }

            do {
                long bytes = array.processWritten(offset, localWrittenBytes);
                if (bytes == -1) {
                    // incomplete write
                    break;
                } else {
                    offset++;
                    cnt--;
                    localWrittenBytes -= bytes;
                }
            } while (offset < end && localWrittenBytes > 0);
        }
        in.removeBytes(initialExpectedWrittenBytes - expectedWrittenBytes);
        return done;
    }

    private boolean writeBytesMultiple(
            ChannelOutboundBuffer in, ByteBuffer[] nioBuffers,
            int nioBufferCnt, long expectedWrittenBytes, int writeSpinCount) throws IOException {

        assert expectedWrittenBytes != 0;
        final long initialExpectedWrittenBytes = expectedWrittenBytes;

        boolean done = false;
        int offset = 0;
        int end = offset + nioBufferCnt;
        for (int i = writeSpinCount - 1; i >= 0; i--) {
            long localWrittenBytes = fd().writev(nioBuffers, offset, nioBufferCnt);
            if (localWrittenBytes == 0) {
                break;
            }
            expectedWrittenBytes -= localWrittenBytes;

            if (expectedWrittenBytes == 0) {
                // Written everything, just break out here (fast-path)
                done = true;
                break;
            }
            do {
                ByteBuffer buffer = nioBuffers[offset];
                int pos = buffer.position();
                int bytes = buffer.limit() - pos;
                if (bytes > localWrittenBytes) {
                    buffer.position(pos + (int) localWrittenBytes);
                    // incomplete write
                    break;
                } else {
                    offset++;
                    nioBufferCnt--;
                    localWrittenBytes -= bytes;
                }
            } while (offset < end && localWrittenBytes > 0);
        }

        in.removeBytes(initialExpectedWrittenBytes - expectedWrittenBytes);
        return done;
    }

    /**
     * Write a {@link DefaultFileRegion}
     *
     * @param region        the {@link DefaultFileRegion} from which the bytes should be written
     * @return amount       the amount of written bytes
     */
    private boolean writeFileRegion(
            ChannelOutboundBuffer in, DefaultFileRegion region, int writeSpinCount) throws Exception {
        final long regionCount = region.count();
        if (region.transfered() >= regionCount) {
            in.remove();
            return true;
        }

        final long baseOffset = region.position();
        boolean done = false;
        long flushedAmount = 0;

        for (int i = writeSpinCount - 1; i >= 0; i--) {
            final long offset = region.transfered();
            final long localFlushedAmount =
                    Native.sendfile(fd().intValue(), region, baseOffset, offset, regionCount - offset);
            if (localFlushedAmount == 0) {
                break;
            }

            flushedAmount += localFlushedAmount;
            if (region.transfered() >= regionCount) {
                done = true;
                break;
            }
        }

        if (flushedAmount > 0) {
            in.progress(flushedAmount);
        }

        if (done) {
            in.remove();
        }
        return done;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        int writeSpinCount = config().getWriteSpinCount();
        for (;;) {
            final int msgCount = in.size();

            if (msgCount == 0) {
                // Wrote all messages.
                clearFlag(Native.EPOLLOUT);
                // Return here so we not set the EPOLLOUT flag.
                return;
            }

            // Do gathering write if the outbounf buffer entries start with more than one ByteBuf.
            if (msgCount > 1 && in.current() instanceof ByteBuf) {
                if (!doWriteMultiple(in, writeSpinCount)) {
                    // Break the loop and so set EPOLLOUT flag.
                    break;
                }

                // We do not break the loop here even if the outbound buffer was flushed completely,
                // because a user might have triggered another write and flush when we notify his or her
                // listeners.
            } else { // msgCount == 1
                if (!doWriteSingle(in, writeSpinCount)) {
                    // Break the loop and so set EPOLLOUT flag.
                    break;
                }
            }
        }
        // Underlying descriptor can not accept all data currently, so set the EPOLLOUT flag to be woken up
        // when it can accept more data.
        setFlag(Native.EPOLLOUT);
    }

    protected boolean doWriteSingle(ChannelOutboundBuffer in, int writeSpinCount) throws Exception {
        // The outbound buffer contains only one message or it contains a file region.
        Object msg = in.current();
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            if (!writeBytes(in, buf, writeSpinCount)) {
                // was not able to write everything so break here we will get notified later again once
                // the network stack can handle more writes.
                return false;
            }
        } else if (msg instanceof DefaultFileRegion) {
            DefaultFileRegion region = (DefaultFileRegion) msg;
            if (!writeFileRegion(in, region, writeSpinCount)) {
                // was not able to write everything so break here we will get notified later again once
                // the network stack can handle more writes.
                return false;
            }
        } else if (msg instanceof SpliceOutTask) {
            if (!((SpliceOutTask) msg).spliceOut()) {
                return false;
            }
            in.remove();
        } else {
            // Should never reach here.
            throw new Error();
        }

        return true;
    }

    private boolean doWriteMultiple(ChannelOutboundBuffer in, int writeSpinCount) throws Exception {
        if (PlatformDependent.hasUnsafe()) {
            // this means we can cast to IovArray and write the IovArray directly.
            IovArray array = IovArrayThreadLocal.get(in);
            int cnt = array.count();
            if (cnt >= 1) {
                // TODO: Handle the case where cnt == 1 specially.
                if (!writeBytesMultiple(in, array, writeSpinCount)) {
                    // was not able to write everything so break here we will get notified later again once
                    // the network stack can handle more writes.
                    return false;
                }
            } else { // cnt == 0, which means the outbound buffer contained empty buffers only.
                in.removeBytes(0);
            }
        } else {
            ByteBuffer[] buffers = in.nioBuffers();
            int cnt = in.nioBufferCount();
            if (cnt >= 1) {
                // TODO: Handle the case where cnt == 1 specially.
                if (!writeBytesMultiple(in, buffers, cnt, in.nioBufferSize(), writeSpinCount)) {
                    // was not able to write everything so break here we will get notified later again once
                    // the network stack can handle more writes.
                    return false;
                }
            } else { // cnt == 0, which means the outbound buffer contained empty buffers only.
                in.removeBytes(0);
            }
        }

        return true;
    }

    @Override
    protected Object filterOutboundMessage(Object msg) {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            if (!buf.hasMemoryAddress() && (PlatformDependent.hasUnsafe() || !buf.isDirect())) {
                if (buf instanceof CompositeByteBuf) {
                    // Special handling of CompositeByteBuf to reduce memory copies if some of the Components
                    // in the CompositeByteBuf are backed by a memoryAddress.
                    CompositeByteBuf comp = (CompositeByteBuf) buf;
                    if (!comp.isDirect() || comp.nioBufferCount() > Native.IOV_MAX) {
                        // more then 1024 buffers for gathering writes so just do a memory copy.
                        buf = newDirectBuffer(buf);
                        assert buf.hasMemoryAddress();
                    }
                } else {
                    // We can only handle buffers with memory address so we need to copy if a non direct is
                    // passed to write.
                    buf = newDirectBuffer(buf);
                    assert buf.hasMemoryAddress();
                }
            }
            return buf;
        }

        if (msg instanceof DefaultFileRegion || msg instanceof SpliceOutTask) {
            return msg;
        }

        throw new UnsupportedOperationException(
                "unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
    }

    protected void shutdownOutput0(final ChannelPromise promise) {
        try {
            fd().shutdown(false, true);
            promise.setSuccess();
        } catch (Throwable cause) {
            promise.setFailure(cause);
        }
    }

    @Override
    protected void doClose() throws Exception {
        try {
            ChannelPromise promise = connectPromise;
            if (promise != null) {
                // Use tryFailure() instead of setFailure() to avoid the race against cancel().
                promise.tryFailure(CLOSED_CHANNEL_EXCEPTION);
                connectPromise = null;
            }

            ScheduledFuture<?> future = connectTimeoutFuture;
            if (future != null) {
                future.cancel(false);
                connectTimeoutFuture = null;
            }
            // Calling super.doClose() first so splceTo(...) will fail on next call.
            super.doClose();
        } finally {
            safeClosePipe(pipeIn);
            safeClosePipe(pipeOut);
            clearSpliceQueue();
        }
    }

    private void clearSpliceQueue() {
        for (;;) {
            SpliceInTask task = spliceQueue.poll();
            if (task == null) {
                break;
            }
            task.promise.tryFailure(CLOSED_CHANNEL_EXCEPTION);
        }
    }

    /**
     * Connect to the remote peer
     */
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            fd().bind(localAddress);
        }

        boolean success = false;
        try {
            boolean connected = fd().connect(remoteAddress);
            if (!connected) {
                setFlag(Native.EPOLLOUT);
            }
            success = true;
            return connected;
        } finally {
            if (!success) {
                doClose();
            }
        }
    }

    private void safeClosePipe(FileDescriptor fd) {
        if (fd != null) {
            try {
                fd.close();
            } catch (IOException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Error while closing a pipe", e);
                }
            }
        }
    }

    class EpollStreamUnsafe extends AbstractEpollUnsafe {

        private RecvByteBufAllocator.Handle allocHandle;

        private boolean handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close) {
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                } else {
                    byteBuf.release();
                }
            }
            pipeline.fireChannelReadComplete();
            pipeline.fireExceptionCaught(cause);
            if (close || cause instanceof IOException) {
                shutdownInput();
                return true;
            }
            return false;
        }

        @Override
        public void connect(
                final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
            if (!promise.setUncancellable() || !ensureOpen(promise)) {
                return;
            }

            try {
                if (connectPromise != null) {
                    throw new IllegalStateException("connection attempt already made");
                }

                boolean wasActive = isActive();
                if (doConnect(remoteAddress, localAddress)) {
                    fulfillConnectPromise(promise, wasActive);
                } else {
                    connectPromise = promise;
                    requestedRemoteAddress = remoteAddress;

                    // Schedule connect timeout.
                    int connectTimeoutMillis = config().getConnectTimeoutMillis();
                    if (connectTimeoutMillis > 0) {
                        connectTimeoutFuture = eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {
                                ChannelPromise connectPromise = AbstractEpollStreamChannel.this.connectPromise;
                                ConnectTimeoutException cause =
                                        new ConnectTimeoutException("connection timed out: " + remoteAddress);
                                if (connectPromise != null && connectPromise.tryFailure(cause)) {
                                    close(voidPromise());
                                }
                            }
                        }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
                    }

                    promise.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (future.isCancelled()) {
                                if (connectTimeoutFuture != null) {
                                    connectTimeoutFuture.cancel(false);
                                }
                                connectPromise = null;
                                close(voidPromise());
                            }
                        }
                    });
                }
            } catch (Throwable t) {
                closeIfClosed();
                promise.tryFailure(annotateConnectException(t, remoteAddress));
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
            if (promise == null) {
                // Closed via cancellation and the promise has been notified already.
                return;
            }
            active = true;

            // trySuccess() will return false if a user cancelled the connection attempt.
            boolean promiseSet = promise.trySuccess();

            // Regardless if the connection attempt was cancelled, channelActive() event should be triggered,
            // because what happened is what happened.
            if (!wasActive && isActive()) {
                pipeline().fireChannelActive();
            }

            // If a user cancelled the connection attempt, close the channel, which is followed by channelInactive().
            if (!promiseSet) {
                close(voidPromise());
            }
        }

        private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
            if (promise == null) {
                // Closed via cancellation and the promise has been notified already.
                return;
            }

            // Use tryFailure() instead of setFailure() to avoid the race against cancel().
            promise.tryFailure(cause);
            closeIfClosed();
        }

        private void finishConnect() {
            // Note this method is invoked by the event loop only if the connection attempt was
            // neither cancelled nor timed out.

            assert eventLoop().inEventLoop();

            boolean connectStillInProgress = false;
            try {
                boolean wasActive = isActive();
                if (!doFinishConnect()) {
                    connectStillInProgress = true;
                    return;
                }
                fulfillConnectPromise(connectPromise, wasActive);
            } catch (Throwable t) {
                fulfillConnectPromise(connectPromise, annotateConnectException(t, requestedRemoteAddress));
            } finally {
                if (!connectStillInProgress) {
                    // Check for null as the connectTimeoutFuture is only created if a connectTimeoutMillis > 0 is used
                    // See https://github.com/netty/netty/issues/1770
                    if (connectTimeoutFuture != null) {
                        connectTimeoutFuture.cancel(false);
                    }
                    connectPromise = null;
                }
            }
        }

        @Override
        void epollOutReady() {
            if (connectPromise != null) {
                // pending connect which is now complete so handle it.
                finishConnect();
            } else {
                super.epollOutReady();
            }
        }

        /**
         * Finish the connect
         */
        private boolean doFinishConnect() throws Exception {
            if (fd().finishConnect()) {
                clearFlag(Native.EPOLLOUT);
                return true;
            } else {
                setFlag(Native.EPOLLOUT);
                return false;
            }
        }

        @Override
        void epollInReady() {
            if (fd().isInputShutdown()) {
                return;
            }
            final ChannelConfig config = config();
            boolean edgeTriggered = isFlagSet(Native.EPOLLET);

            if (!readPending && !edgeTriggered && !config.isAutoRead()) {
                // ChannelConfig.setAutoRead(false) was called in the meantime
                clearEpollIn0();
                return;
            }

            final ChannelPipeline pipeline = pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
            if (allocHandle == null) {
                this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
            }

            ByteBuf byteBuf = null;
            boolean close = false;
            try {
                // if edgeTriggered is used we need to read all messages as we are not notified again otherwise.
                final int maxMessagesPerRead = edgeTriggered
                        ? Integer.MAX_VALUE : config.getMaxMessagesPerRead();
                int messages = 0;
                int totalReadAmount = 0;
                do {
                    SpliceInTask spliceTask = spliceQueue.peek();
                    if (spliceTask != null) {
                        if (spliceTask.spliceIn(allocHandle)) {
                            // We need to check if it is still active as if not we removed all SpliceTasks in
                            // doClose(...)
                            if (isActive()) {
                                spliceQueue.remove();
                            }
                            continue;
                        } else {
                            break;
                        }
                    }

                    // we use a direct buffer here as the native implementations only be able
                    // to handle direct buffers.
                    byteBuf = allocHandle.allocate(allocator);
                    int writable = byteBuf.writableBytes();
                    int localReadAmount = doReadBytes(byteBuf);
                    if (localReadAmount <= 0) {
                        // not was read release the buffer
                        byteBuf.release();
                        close = localReadAmount < 0;
                        break;
                    }
                    readPending = false;
                    pipeline.fireChannelRead(byteBuf);
                    byteBuf = null;

                    if (totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
                        allocHandle.record(totalReadAmount);

                        // Avoid overflow.
                        totalReadAmount = localReadAmount;
                    } else {
                        totalReadAmount += localReadAmount;
                    }

                    if (localReadAmount < writable) {
                        // Read less than what the buffer can hold,
                        // which might mean we drained the recv buffer completely.
                        break;
                    }
                    if (!edgeTriggered && !config.isAutoRead()) {
                        // This is not using EPOLLET so we can stop reading
                        // ASAP as we will get notified again later with
                        // pending data
                        break;
                    }
                } while (++ messages < maxMessagesPerRead || isRdHup());

                pipeline.fireChannelReadComplete();
                allocHandle.record(totalReadAmount);

                if (close) {
                    shutdownInput();
                    close = false;
                }
            } catch (Throwable t) {
                boolean closed = handleReadException(pipeline, byteBuf, t, close);
                if (!closed) {
                    // trigger a read again as there may be something left to read and because of epoll ET we
                    // will not get notified again until we read everything from the socket
                    eventLoop().execute(new Runnable() {
                        @Override
                        public void run() {
                            epollInReady();
                        }
                    });
                }
            } finally {
                // Check if there is a readPending which was not processed yet.
                // This could be for two reasons:
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelRead(...) method
                // * The user called Channel.read() or ChannelHandlerContext.read() in channelReadComplete(...) method
                //
                // See https://github.com/netty/netty/issues/2254
                if (!readPending && !config.isAutoRead()) {
                    clearEpollIn0();
                }
            }
        }
    }

    protected abstract class SpliceInTask extends MpscLinkedQueueNode<SpliceInTask> {
        final ChannelPromise promise;
        int len;

        protected SpliceInTask(int len, ChannelPromise promise) {
            this.promise = promise;
            this.len = len;
        }

        @Override
        public SpliceInTask value() {
            return this;
        }

        abstract boolean spliceIn(RecvByteBufAllocator.Handle handle) throws IOException;

        protected final int spliceIn(FileDescriptor pipeOut, RecvByteBufAllocator.Handle handle) throws IOException {
            // calculate the maximum amount of data we are allowed to splice
            int length = Math.min(handle.guess(), len);
            int splicedIn = 0;
            for (;;) {
                // Splicing until there is nothing left to splice.
                int localSplicedIn = Native.splice(fd().intValue(), -1, pipeOut.intValue(), -1, length);
                if (localSplicedIn == 0) {
                    break;
                }
                splicedIn += localSplicedIn;
                length -= localSplicedIn;
            }

            // record the number of bytes we spliced before
            handle.record(splicedIn);
            return splicedIn;
        }
    }

    // Let it directly implement channelFutureListener as well to reduce object creation.
    private final class SpliceInChannelTask extends SpliceInTask implements ChannelFutureListener {
        private final AbstractEpollStreamChannel ch;

        SpliceInChannelTask(AbstractEpollStreamChannel ch, int len, ChannelPromise promise) {
            super(len, promise);
            this.ch = ch;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                promise.setFailure(future.cause());
            }
        }

        @Override
        public boolean spliceIn(RecvByteBufAllocator.Handle handle) throws IOException {
            assert ch.eventLoop().inEventLoop();
            if (len == 0) {
                promise.setSuccess();
                return true;
            }
            try {
                // We create the pipe on the target channel as this will allow us to just handle pending writes
                // later in a correct fashion without get into any ordering issues when spliceTo(...) is called
                // on multiple Channels pointing to one target Channel.
                FileDescriptor pipeOut = ch.pipeOut;
                if (pipeOut == null) {
                    // Create a new pipe as non was created before.
                    FileDescriptor[] pipe = pipe();
                    ch.pipeIn = pipe[0];
                    pipeOut = ch.pipeOut = pipe[1];
                }

                int splicedIn = spliceIn(pipeOut, handle);
                if (splicedIn > 0) {
                    // Integer.MAX_VALUE is a special value which will result in splice forever.
                    if (len != Integer.MAX_VALUE) {
                        len -= splicedIn;
                    }

                    // Depending on if we are done with splicing inbound data we set the right promise for the
                    // outbound splicing.
                    final ChannelPromise splicePromise;
                    if (len == 0) {
                        splicePromise = promise;
                    } else {
                        splicePromise = ch.newPromise().addListener(this);
                    }

                    boolean autoRead = config().isAutoRead();

                    // Just call unsafe().write(...) and flush() as we not want to traverse the whole pipeline for this
                    // case.
                    ch.unsafe().write(new SpliceOutTask(ch, splicedIn, autoRead), splicePromise);
                    ch.unsafe().flush();
                    if (autoRead && !splicePromise.isDone()) {
                        // Write was not done which means the target channel was not writable. In this case we need to
                        // disable reading until we are done with splicing to the target channel because:
                        //
                        // - The user may want to to trigger another splice operation once the splicing was complete.
                        config().setAutoRead(false);
                    }
                }

                return len == 0;
            } catch (Throwable cause) {
                promise.setFailure(cause);
                return true;
            }
        }
    }

    private final class SpliceOutTask {
        private final AbstractEpollStreamChannel ch;
        private final boolean autoRead;
        private int len;

        SpliceOutTask(AbstractEpollStreamChannel ch, int len, boolean autoRead) {
            this.ch = ch;
            this.len = len;
            this.autoRead = autoRead;
        }

        public boolean spliceOut() throws Exception {
            assert ch.eventLoop().inEventLoop();
            try {
                int splicedOut = Native.splice(ch.pipeIn.intValue(), -1, ch.fd().intValue(), -1, len);
                len -= splicedOut;
                if (len == 0) {
                    if (autoRead) {
                        // AutoRead was used and we spliced everything so start reading again
                        config().setAutoRead(true);
                    }
                    return true;
                }
                return false;
            } catch (IOException e) {
                if (autoRead) {
                    // AutoRead was used and we spliced everything so start reading again
                    config().setAutoRead(true);
                }
                throw e;
            }
        }
    }

    private final class SpliceFdTask extends SpliceInTask {
        private final FileDescriptor fd;
        private final ChannelPromise promise;
        private int offset;

        SpliceFdTask(FileDescriptor fd, int offset, int len, ChannelPromise promise) {
            super(len, promise);
            this.fd = fd;
            this.promise = promise;
            this.offset = offset;
        }

        @Override
        public SpliceFdTask value() {
            return this;
        }

        @Override
        public boolean spliceIn(RecvByteBufAllocator.Handle handle) throws IOException {
            assert eventLoop().inEventLoop();
            if (len == 0) {
                promise.setSuccess();
                return true;
            }

            try {
                FileDescriptor[] pipe = pipe();
                FileDescriptor pipeIn = pipe[0];
                FileDescriptor pipeOut = pipe[1];
                try {
                    int splicedIn = spliceIn(pipeOut, handle);
                    if (splicedIn > 0) {
                        // Integer.MAX_VALUE is a special value which will result in splice forever.
                        if (len != Integer.MAX_VALUE) {
                            len -= splicedIn;
                        }
                        do {
                            int splicedOut = Native.splice(pipeIn.intValue(), -1, fd.intValue(), offset, splicedIn);
                            splicedIn -= splicedOut;
                        } while (splicedIn > 0);
                        if (len == 0) {
                            promise.setSuccess();
                            return true;
                        }
                    }
                    return false;
                } finally {
                    safeClosePipe(pipeIn);
                    safeClosePipe(pipeOut);
                }
            } catch (Throwable cause) {
                promise.setFailure(cause);
                return true;
            }
        }
    }
}
