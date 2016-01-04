package com.myftpserver.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.logging.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.UserGroup;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.UserManager;
import com.myftpserver.handler.FtpSessionHandler;

public class MyUserManager extends UserManager  
{
	DbOp dbo=null;
	ResultSet rs=null;
	String strSql=new String();
	ArrayList<Object> values=null;

	public MyUserManager(Logger logger) 
	{
		super(logger);
		try 
		{
			dbo=new DbOp(logger);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			dbo=null;
		}
	}


	@Override
	public Vector<User> listAllUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<UserGroup> listAllUserGroup() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		User u=dbo.login(fs, password);
		dbo.loadACL(u);
		fs.setUser(u);
		logger.debug("Client path ACL size="+u.getClientPathACL().size());
		logger.debug("Server path ACL size="+u.getServerPathACL().size());
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
