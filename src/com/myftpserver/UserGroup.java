package com.myftpserver;

public class UserGroup 
{
	long quota=0;
	String homeDir=null,groupName=null;
	
	public void setQuota(long q)
	{
		quota=q;
	}
	public void setHomeDir(String path)
	{
		homeDir=path;
	}
	public void setGroupName(String n)
	{
		groupName=n;
	}
	/**
	 * @return the quota
	 */
	public long getQuota() {
		return quota;
	}
	/**
	 * @return the homeDir
	 */
	public String getHomeDir() {
		return homeDir;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
}
