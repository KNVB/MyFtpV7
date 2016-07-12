package com.myftpserver.command;
import com.util.Utility;
import java.io.IOException;
import org.apache.logging.log4j.Logger;
import java.nio.file.InvalidPathException;
import com.myftpserver.abstracts.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.exception.NotAFileException;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.abstracts.FtpServerConfig;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;


/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 * @author SITO3
 *
 */
public class RETR implements FtpCommandInterface
{
	/**
	 *This command causes the server-DTP to transfer a copy of the file,<br>
     *specified in the pathname, to the server- or user-DTP at the other end of the data connection.<br>
     *For detail information about RETR command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
	 */
	public RETR()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(FtpSessionHandler fs, String inPath) 
	{
		Logger logger=fs.getLogger();
		FtpServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		logger.debug("inPath="+inPath+"|");
		try 
		{
			fs.setDownloadFile(fm.getDownloadFileObject(fs, inPath));
			Utility.sendFileToClient(fs);
		} 
		catch (InterruptedException|NotAFileException |AccessDeniedException |IOException err) 
		{
			Utility.handleTransferException(fs,err.getMessage());
		}
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.handleTransferException(fs,fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
	}
	/*@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		logger.debug("param="+param+"|");
		try 
		{
			String serverPath=fm.getDownloadFileServerPath(fs,param);
			Utility.sendFileToClient(fs,serverPath);
		}
		catch (InterruptedException|NotAFileException |AccessDeniedException |IOException err) 
		{
			Utility.handleTransferException(fs,err.getMessage());
		}
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.handleTransferException(fs,fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
	}*/

}
