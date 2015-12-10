package com.myftpserver;
 
import java.io.File;
import java.util.TreeMap;
public class User 
{
	int quota=0; //Quota in Kilo byte
	boolean active=false;
	TreeMap<String, String> serverPathACL = null,clientPathACL=null;
	String name=new String(),password=new String(),homeDir=new String();
	
	
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
	public TreeMap<String, String> getClientPathACL()
	{
		return clientPathACL;
	}
	public TreeMap<String, String> getServerPathACL()
	{
		return serverPathACL;
	}
	public void addClientPathACL(String virDir,String permission)
	{
		clientPathACL.put(virDir, permission);
	}
	public void addServerPathACL(String phyDir,String permission)
	{
		serverPathACL.put(phyDir, permission);
	}
	public String getName() 
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) 
	{
		this.password = password;
	}
	public String getHomeDir() 
	{
		return homeDir;
	}
	public void setHomeDir(String homeDir) 
	{
		this.homeDir = homeDir;
	}
	public int getQuota() 
	{
		return quota;
	}
	public void setQuota(int quota) 
	{
		this.quota = quota;
	}
	public boolean isActive() 
	{
		return active;
	}
	public void setActive(boolean active) 
	{
		this.active = active;
	}
	
	

}
