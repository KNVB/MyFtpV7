package com.myftpserver.abstracts;

import com.myftpserver.User;
import com.myftpserver.UserGroup;
import com.myftpserver.exception.*;
import com.myftpserver.handler.FtpSessionHandler;

import java.util.Vector;

import org.apache.logging.log4j.Logger;
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
public abstract class UserManager 
{
	/**
	 * Constant for specified invalid user name or password error code
	 */
	public static final int INVAILD_USERNAME_OR_PASSWORD=1;
	/**
	 * Message logger
	 */
	public Logger logger;
	/**
	 * The UserManager class is an abstract class that provide method for FTP user management
	 * @param logger Message logger
	 */
	public UserManager(Logger logger)
	{
		this.logger=logger;
	}
	/**
	 * Get a list of user object
	 * @return a list of user object
	 */
	public abstract Vector<User> listAllUser();
	/**
	 * Get a list of user group object
	 * @return a list of user group object
	 */
	public abstract Vector<UserGroup> listAllUserGroup();
	/**
	 * Add an user object
	 * @param u user object
	 * @return add user result
	 */
	public abstract int addUser(User u);
	/**
	 * Delete an user object
	 * @param uN user name
	 * @return delete user result
	 */
	public abstract int deleteUser(String uN);
	/**
	 * Update an user information
	 * @param u user object
	 * @return Update an user information result
	 */
	public abstract int upDateUserInfo(User u);
	/**
	 * Add an user group object
	 * @param userGroup user group object
	 * @return add user group result
	 */
	public abstract int addUserGroup(UserGroup userGroup);
	/**
	 * Delete an user group object
	 * @param userGroup userGroup object
	 * @return delete user group result
	 */
	public abstract int deleteUserGroup(UserGroup userGroup);
	/**
	 * Add an user to user group
	 * @param user user object
	 * @param userGroup user group object
	 * @return Add an user to user group result
	 */
	public abstract int addUserToUserGroup(User user,UserGroup userGroup);
	/**
	 * Remove an user from user group
	 * @param user user object
	 * @param userGroup user group object
	 * @return Remove an user from user group result
	 */
	public abstract int removeUserFromUserGroup(User user,UserGroup userGroup);
	/**
	 * Perform User login
	 * @param fs FtpSessionHandler object
	 * @param password user password
	 * @return user object
	 * @throws LoginFailureException
	 * @throws AccessDeniedException
	 * @throws InvalidHomeDirectoryException
	 */
	public abstract User login(FtpSessionHandler fs,String password)throws LoginFailureException, AccessDeniedException, InvalidHomeDirectoryException;
	public abstract void close();
}