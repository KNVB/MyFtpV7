package com.myftpserver;
 
import java.io.File;
import java.util.TreeMap;
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
public class User 
{
	int quota=0; //Quota in Kilo byte
	boolean active=false;
	TreeMap<String, String> serverPathACL = null,clientPathACL=null;
	String name=new String(),password=new String(),homeDir=new String();
	/**
	 * FTP user object	
	 */
	public User()
	{
		if (File.separator.equals("\\"))
		{	
			serverPathACL=new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
			clientPathACL=new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		}
		else
		{	
			serverPathACL=new TreeMap<String, String>();
			clientPathACL=new TreeMap<String, String>();
		}
	}
	/**
	 * Get client/virtual path Access Control List
	 * @return client/virtual path Access Control List
	 */
	public TreeMap<String, String> getClientPathACL()
	{
		return clientPathACL;
	}
	/**
	 * Get server path Access Control List
	 * @return server path Access Control List
	 */
	public TreeMap<String, String> getServerPathACL()
	{
		return serverPathACL;
	}
	/**
	 * Add a client/virtual path, its permission to client/virtual path Access Control List
	 * @param virDir the virtual path
	 * @param permission access permission
	 */
	public void addClientPathACL(String virDir,String permission)
	{
		clientPathACL.put(virDir, permission);
	}
	/**
	 * Add a server path, its permission to server path Access Control List
	 * @param phyDir the server path
	 * @param permission access permission
	 */	
	public void addServerPathACL(String phyDir,String permission)
	{
		serverPathACL.put(phyDir, permission);
	}
	/**
	 * Get Login name
	 * @return login name
	 */
	public String getName() 
	{
		return name;
	}
	/**
	 * Set Login name
	 * @param name login name
	 */
	public void setName(String name) 
	{
		this.name = name;
	}
	/**
	 * Get login password
	 * @return login password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Set Login password
	 * @param password login password
	 */	
	public void setPassword(String password) 
	{
		this.password = password;
	}
	/**
	 * Get User's home folder
	 * @return User's home folder 
	 */
	public String getHomeDir() 
	{
		return homeDir;
	}
	/**
	 * Set User's home folder
	 * @param homeDir User's home folder 
	 */
	public void setHomeDir(String homeDir) 
	{
		this.homeDir = homeDir;
	}
	/**
	 * Get User's Quota
	 * @return User's Quota
	 */
	public int getQuota() 
	{
		return quota;
	}
	/**
	 * Set User's Quota
	 * @param quota User's Quota
	 */
	public void setQuota(int quota) 
	{
		this.quota = quota;
	}
	/**
	 * Is the user account active 
	 * @return true if the user account is active
	 */
	public boolean isActive() 
	{
		return active;
	}
	/**
	 * Set the user account active 
	 * @param active active
	 */	
	public void setActive(boolean active) 
	{
		this.active = active;
	}
}
