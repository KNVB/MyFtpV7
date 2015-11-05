package com.myftpserver.impl;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.sql.ResultSet;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.User;
import com.myftpserver.ActiveClient;
import com.myftpserver.Configuration;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;


public class MyFileManager extends FileManager 
{
	DbOp dbo=null;
	ResultSet rs=null;
	String strSql=new String();

	public MyFileManager(Configuration c) 
	{
		super(c);
		// TODO Auto-generated constructor stub
		try 
		{
			dbo=new DbOp(c);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			dbo=null;
		}
	}
	@Override
	public long getPathSize(FtpSessionHandler fs, String clientPath)throws AccessDeniedException, PathNotFoundException 
	{
		// TODO Auto-generated method stub
		long pathSize=0;
		String serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		pathSize=new File(serverPath).length();
		return pathSize;
	}

	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException 
	{
		// TODO Auto-generated method stub
		String clientPath=null,serverPath;
		User user=fs.getUser();
		clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(),inPath);
		
		serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		if (isReadableServerPath(user.getServerPathACL(),Paths.get(serverPath)))
			fs.setCurrentPath(clientPath);
		else
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		
	}

	@Override
	public void getFile(FtpSessionHandler fs, ChannelHandlerContext ctx,String clientPath) throws AccessDeniedException, InterruptedException, PathNotFoundException 
	{
		// TODO Auto-generated method stub
		User user=fs.getUser();
		String serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		logger.debug("serverPath="+serverPath);
		if (isReadableServerPath(user.getServerPathACL(),Paths.get(serverPath)))
		{	
			if (fs.isPassiveModeTransfer)
			{
				logger.debug("File download in passive mode");
				//Utility.sendFileToClient(fs.getPassiveChannelContext(),fs,Paths.get(serverPath));
				//PassiveModeTx passiveModeTx=new PassiveModeTx(fs.getPassiveChannelContext());
				//passiveModeTx.transFile(Paths.get(serverPath),ctx,fs.getTransferMode());
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("502_Command_Not_Implemeneted"));
			}
			else
			{
				logger.debug("File download in active mode");
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
				ActiveClient activeClient=new ActiveClient(fs,ctx);
				activeClient.sendFile(serverPath);
			}
		}
		else
		{	
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		}
		
	}

	@Override
	public void showFileNameList(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException, InterruptedException 
	{
		// TODO Auto-generated method stub
		String serverPath=new String();
		StringBuilder fileNameList=new StringBuilder();
		String clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(), inPath);
		config=fs.getConfig();
		serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		logger.debug("Server Path="+serverPath);
		logger.debug("Server Path ACL="+fs.getUser().getServerPathACL());
		fileNameList=getFileNameList(fs,serverPath);
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
		if (fs.isPassiveModeTransfer)
		{
			logger.debug("Passive mode");
			
			//PassiveModeTx passiveModeTx=new PassiveModeTx(fs.getPassiveChannelContext());
			//passiveModeTx.transFileNameList(fs,ctx, fileNameList);
			
			//Utility.sendMessageToClient(fs.getPassiveChannelContext(),fs,fileNameList.toString());
			
			//fs.getPassiveChannel().close();
			//Utility.sendMessageToClient(ctx,fs,config.getFtpMessage("502_Command_Not_Implemeneted"));
		}
		else
		{
			logger.debug("Active mode");
			ActiveClient activeClient=new ActiveClient(fs,ctx);
			activeClient.sendFileNameList(fileNameList);
		}		
	}

	@Override
	public void showFullDirList(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException, InterruptedException 
	{
		// TODO Auto-generated method stub
		String serverPath=new String();
		StringBuilder fileNameList=new StringBuilder();
		String clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(), inPath);
		config=fs.getConfig();
		serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		logger.debug("Server Path="+serverPath);
		logger.debug("Server Path ACL="+fs.getUser().getServerPathACL());
		fileNameList=getFullDirList(fs,serverPath);
		if (fs.isPassiveModeTransfer)
		{
			logger.debug("Passive mode");
			//Utility.sendMessageToClient(ctx,fs,config.getFtpMessage("502_Command_Not_Implemeneted"));
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
			//fs.getPassiveChannelContext().close().addListener(new PassiveTxCompleteListener(fs.getPassiveServer(),fs,ctx));
			//PassiveModeTx passiveModeTx=new PassiveModeTx(fs.getPassiveChannelContext());
			//passiveModeTx.transFileNameList(fs,ctx, fileNameList);
		}
		else
		{
			logger.debug("Active mode");
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
			ActiveClient activeClient=new ActiveClient(fs,ctx);
			activeClient.sendFileNameList(fileNameList);
		}
	}
//----------------------------------------------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	private StringBuilder getFullDirList(FtpSessionHandler fs, String serverPath) throws AccessDeniedException, PathNotFoundException 
	{
		// TODO Auto-generated method stub
		StringBuilder resultString=new StringBuilder();
		TreeMap<String,String> result=new TreeMap<String,String>();
		Hashtable<String, String> clientPathACL=fs.getUser().getClientPathACL();
		//Hashtable<String, String> serverPathACL=fs.getUser().getServerPathACL();
		Hashtable<Path, String> serverPathACL=fs.getUser().getServerPathACL();
		logger.debug("Server Path ACL size="+serverPathACL.size());
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
		{
			for (Path path : directoryStream) 
            {
	            if (isReadableServerPath(serverPathACL,path))
	            	result.put((path.getFileName().toString()),Utility.formatPathName(path));
            }
			logger.debug("Client Path ACL size="+clientPathACL.size());
			addVirtualDirectoryList(fs,clientPathACL,result);
			Set<?> set = result.entrySet();
		    // Get an iterator
		    Iterator<?> i = set.iterator();
		    // Display elements
		    while(i.hasNext()) 
		    {
		        Map.Entry<String,String> me = (Map.Entry<String, String>)i.next();
		        resultString.append(me.getValue()+me.getKey()+"\r\n");
		    }
		}
        catch (IOException ex) 
    	{}
    	return resultString;
	}
	private StringBuilder getFileNameList(FtpSessionHandler fs, String serverPath) 
	{
		// TODO Auto-generated method stub
		StringBuilder resultString=new StringBuilder();
		ArrayList<String> result=new ArrayList<String>();
		Hashtable<String, String> clientPathACL=fs.getUser().getClientPathACL();
		Hashtable<Path, String> serverPathACL=fs.getUser().getServerPathACL();
		
		logger.debug("Server Path ACL size="+serverPathACL.size());
		
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
		{
			for (Path path : directoryStream) 
            {
				if (isReadableServerPath(serverPathACL,path))
					result.add(path.getFileName().toString());
            }
			logger.debug("Client Path ACL size="+clientPathACL.size());
			addVirtualDirectoryName(fs.getCurrentPath(),clientPathACL,result);
			logger.debug("result1="+result.toString());
			Collections.sort(result);
			logger.debug("result2="+result.toString());
			for (String temp :result)
			{
				resultString.append(temp+"\r\n");
			}
		}
        catch (IOException ex) 
    	{}
    	return resultString;
	}
	private void addVirtualDirectoryList(FtpSessionHandler fs,Hashtable<String, String> clientPathACL,TreeMap<String,String> nameList) throws AccessDeniedException, PathNotFoundException, IOException 
	{
		int index;
		String virDir,parentDir,currentPath=fs.getCurrentPath();
		Enumeration<String> clientPaths=clientPathACL.keys();
		while (clientPaths.hasMoreElements())
		{
			virDir=clientPaths.nextElement();
			if (virDir.equals("")||virDir.equals(currentPath))
				continue;
			else
			{
				index=virDir.lastIndexOf("/");
				parentDir=virDir.substring(0,index+1);
				if (parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
				{
					String serverPath=dbo.getRealPath(fs, virDir, FileManager.READ_PERMISSION);
					virDir=virDir.replaceAll(currentPath, "");
					index=virDir.indexOf("/");
					if (index==0)
						virDir=virDir.substring(index+1);
					logger.debug("Current Path="+currentPath+",Virtual Client Path="+virDir+",Parent folder="+parentDir+",serverPath="+serverPath+",index="+index);
					nameList.put(virDir,Utility.formatPathName(Paths.get(serverPath)));
				}
			}
			
		}
		
	}
	private void addVirtualDirectoryName(String currentPath,Hashtable<String, String> clientPathACL, ArrayList<String> nameList) 
	{
		int index;
		String virDir,parentDir;
		Enumeration<String> clientPaths=clientPathACL.keys();
		while (clientPaths.hasMoreElements())
		{
			virDir=clientPaths.nextElement();
			if (virDir.equals("")||virDir.equals(currentPath))
				continue;
			else
			{
				index=virDir.lastIndexOf("/");
				parentDir=virDir.substring(0,index+1);
				if (parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
				{
					virDir=virDir.replaceAll(currentPath, "");
					index=virDir.indexOf("/");
					if (index==0)
						virDir=virDir.substring(index+1);
					logger.debug("Current Path="+currentPath+",Virtual Client Path="+virDir+",Parent folder="+parentDir+",index="+index);
					nameList.add(virDir);
				}
			}
			
		}
		
	}
	
	private boolean isReadableServerPath(Hashtable<Path, String> serverPathACL,Path p)
	{
		Path aclPath;
		boolean result=true;
		String permission=null;
		if (Files.isDirectory(p))
		{
			Enumeration<Path> serverPaths=serverPathACL.keys();
			while (serverPaths.hasMoreElements())
			{
				aclPath=serverPaths.nextElement();
				if (p.startsWith(aclPath))
				{
					permission=serverPathACL.get(aclPath);
					if (permission.indexOf(FileManager.NO_ACCESS)>-1)
						result=false;
				}
			}
		}
		else
		{
			if (serverPathACL.containsKey(p))
			{
				permission=serverPathACL.get(p);
				if (permission.indexOf(FileManager.NO_ACCESS)>-1)
					result=false;
			}
		}
		return result;
	}

	@Override
	public void close() 
	{
		// TODO Auto-generated method stub
		if (dbo!=null)
		{
			try 
			{
				dbo.close();
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dbo=null;
	}
}