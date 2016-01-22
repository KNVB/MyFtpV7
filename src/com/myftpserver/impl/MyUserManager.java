package com.myftpserver.impl;

import java.util.Vector;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.UserGroup;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.UserManager;
import com.myftpserver.handler.FtpSessionHandler;
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
public class MyUserManager extends UserManager  
{
	DbOp dbo=null;
	ResultSet rs=null;
	String strSql=new String();
	ArrayList<Object> values=null;
	/**
	 * An File Manager implementation
	 * @param logger Message logger
	 */
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
	public int upDateUserInfo(User u)  
	{
		return dbo.upDateUserInfo(u);
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
