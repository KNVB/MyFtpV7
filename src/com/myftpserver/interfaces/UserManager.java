package com.myftpserver.interfaces;


import com.myftpserver.Configuration;
import com.myftpserver.User;
import com.myftpserver.UserGroup;
import com.myftpserver.exception.*;
import com.myftpserver.handler.FtpSessionHandler;

import java.util.Vector;

import org.apache.log4j.Logger;

public abstract class UserManager 
{
	public static final int INVAILD_USERNAME_OR_PASSWORD=1;
	public Logger logger;
	public Configuration config;
	public UserManager(Configuration c)
	{
		this.config=c;
		this.logger=config.getLogger();
	}
	public abstract Vector<User> listAllUser();
	public abstract Vector<UserGroup> listAllUserGroup();
	public abstract int addUser(User u);
	public abstract int deleteUser(String uN);
	public abstract int setPassword(User u);
	public abstract int addUserGroup(UserGroup userGroup);
	public abstract int deleteUserGroup(UserGroup userGroup);
	public abstract int addUserToUserGroup(User user,UserGroup userGroup);
	public abstract int removeUserFromUserGroup(User user,UserGroup userGroup);
	public abstract User login(FtpSessionHandler fs,String password)throws LoginFailureException, AccessDeniedException, InvalidHomeDirectoryException;
	//public abstract void setConfig(Configuration config);
	public abstract void close();
	//public abstract void setConfig(Configuration c);

}
