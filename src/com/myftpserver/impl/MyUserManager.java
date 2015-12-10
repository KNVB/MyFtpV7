package com.myftpserver.impl;

import java.util.Vector;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.myftpserver.User;
import com.myftpserver.impl.DbOp;
import com.myftpserver.UserGroup;
import com.myftpserver.exception.*;
import com.myftpserver.Configuration;
import com.myftpserver.interfaces.UserManager;
import com.myftpserver.handler.FtpSessionHandler;

public class MyUserManager extends UserManager  
{
	String strSql=new String();
	ArrayList<Object> values=null;
	DbOp dbo=null;
	ResultSet rs=null;
	public MyUserManager(Configuration c) 
	{
		super(c);
		try 
		{
			dbo=new DbOp(c);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			dbo=null;
		}
	}
	@Override
	public Vector<User> listAllUser() 
	{
		return null;
	}
	@Override
	public Vector<UserGroup> listAllUserGroup() 
	{
		return null;
	}
	@Override
	public int addUser(User u) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int deleteUser(String uN) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int setPassword(User u) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int addUserGroup(UserGroup userGroup) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int deleteUserGroup(UserGroup userGroup) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int addUserToUserGroup(User user, UserGroup userGroup) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int removeUserFromUserGroup(User user, UserGroup userGroup) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public User login(FtpSessionHandler fs, String password)throws LoginFailureException, AccessDeniedException,InvalidHomeDirectoryException 
	{
		User u=dbo.login(fs, password);
		try
		{
			dbo.loadACL(u);
			fs.setUser(u);
			dbo.getRealHomePath(fs);
			logger.debug("Client path ACL size="+u.getClientPathACL().size());
			logger.debug("Server path ACL size="+u.getServerPathACL().size());
		}
		catch (PathNotFoundException er)
		{
			throw new InvalidHomeDirectoryException(config.getFtpMessage("530_Home_Dir_Not_Found"));
		}
		return u;		
	}
	@Override
	public void close() 
	{
		if (dbo!=null)
		{
			try 
			{
				dbo.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		dbo=null;		
	}

}
