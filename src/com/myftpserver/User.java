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
	private double quota=0; //Quota in Kilo byte
	private boolean active=false;
	private double diskSpaceUsed=-1;//Disk Space used in Kilo byte
	private long ulBWLimit=0,dlBWLimit=0;//Upload and Download speed in Kilo byte per second
	private String userLocale=new String("en_us");
	private TreeMap<String, String> serverPathACL = null,clientPathACL=null;
	private String name=new String(),password=new String(),homeDir=new String();
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
	public double getQuota() 
	{
		return quota;
	}
	/**
	 * Set User's Quota
	 * @param quota User's Quota
	 */
	public void setQuota(double quota) 
	{
		this.quota = quota;
	}
	/**
	 * Is the user account active 
	 * @return if true the user account is active
	 */
	public boolean isActive() 
	{
		return active;
	}
	/**
	 * Set the user account active 
	 * @param active whether user account is active
	 */	
	public void setActive(boolean active) 
	{
		this.active = active;
	}
	/**
	 *Get upload speed limit for the user
	 *@return the upload speed limit in Kilo Byte/s for the user
	 */
	public long getUploadSpeedLitmit()
	{
		return this.ulBWLimit;
	}
	/**
	 *Set upload speed limit for the user
	 *@param limit the upload speed limit in Kilo Byte/s for the user 
	 */
	public void setUploadSpeedLitmit(long limit)
	{
		this.ulBWLimit=limit;
	}
	/**
	 *Get download speed limit for the user
	 *@return the download speed limit in Kilo Byte/s for the user
	 */
	public long getDownloadSpeedLitmit()
	{
		return this.dlBWLimit;
	}
	/**
	 *Set download speed limit for the user
	 *@param limit the download speed limit in Kilo Byte/s for the user 
	 */
	public void setDownloadSpeedLitmit(long limit)
	{
		this.dlBWLimit=limit;
	}
	/**
	 * Get User locale 
	 * @return User locale 
	 */
	public String getUserLocale() 
	{
		return userLocale;
	}
	/**
	 * Set User locale
	 * @param userLocale locale
	 */
	public void setUserLocale(String userLocale) 
	{
		this.userLocale = userLocale;
	}
	/**
	 * Get Disk Space used by this user
	 * @return Disk Space used by this user
	 */
	public double getDiskSpaceUsed() 
	{
		return diskSpaceUsed;
	}
	/**
	 * Set Disk Space used by this user
	 * @param diskSpaceUsed Disk Space used by this user
	 */
	public void setDiskSpaceUsed(double diskSpaceUsed) 
	{
		this.diskSpaceUsed = diskSpaceUsed;
	}	
}
