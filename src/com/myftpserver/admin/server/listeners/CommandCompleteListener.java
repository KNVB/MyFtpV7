package com.myftpserver.admin.server.listeners;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class CommandCompleteListener implements ChannelFutureListener {
	private Logger logger;
	private String remoteIp, message;

	/**
	 * It is triggered when a ftp command is executed successfully
	 * 
	 * @param logger
	 *            Message logger
	 * @param remoteIp
	 *            client IP Address
	 * @param ftpMessage
	 *            return message for the action perform
	 */
	public CommandCompleteListener(Logger logger, String remoteIp,
			String message) {
		this.logger = logger;
		this.remoteIp = remoteIp;
		this.message = message;
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		logger.info("Message:" + message + " sent to:" + remoteIp);
	}

}
