package com.myftpserver.oldImpl;


import com.myftpserver.*;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import java.sql.*;
import java.util.Vector;
import java.util.ArrayList;

public final class MyUserManager extends UserManager 
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
			// TODO Auto-generated catch block
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
	public User login(String userName, String password) throws AccessDeniedException,InvalidHomeDirectoryException,LoginFailureException
	{
		// TODO Auto-generated method stub
		
		User u=dbo.login(userName, password);
		try
		{
			String realPath=dbo.getRealHomePath(userName,"/",FileManager.READ_PERMISSION);
			dbo.loadACL(u);
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
