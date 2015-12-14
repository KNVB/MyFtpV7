package com.myftpserver;
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
public class UserGroup 
{
	long quota=0;
	String homeDir=null,groupName=null;
	/**
	 * FTP user group object
	 */
	public UserGroup()
	{
		
	}
	/**
	 * Get User group's quota
	 * @return the quota
	 */
	public long getQuota() 
	{
		return quota;
	}
	/**
	 * Set User Group's Quota
	 * @param q quota User Group's Quota
	 */
	public void setQuota(long q)
	{
		quota=q;
	}
	/**
	 * Get User group's home folder
	 * @return the homeDir
	 */
	public String getHomeDir() {
		return homeDir;
	}

	/**
	 * Set User Group's home folder
	 * @param path User's home folder 
	 */
	public void setHomeDir(String path)
	{
		homeDir=path;
	}
	/**
	 * Get User group name
	 * @return the groupName
	 */
	public String getGroupName() 
	{
		return groupName;
	}
	/**
	 * Set User group name
	 * @param name User group name
	 */
	public void setGroupName(String name)
	{
		groupName=name;
	}
}
