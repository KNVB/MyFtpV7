package com.myftpserver.admin.server;

import com.myftpserver.admin.object.AdminUser;

public class AdminUserManager 
{
	public static final int LOGIN_OK=0;
	public static final int LOGIN_FAILED=1;
	public static final int UNKNOWN_ERROR=2;
	
	public AdminUserManager()
	{
		
	}
	public int login(AdminUser adminUser)
	{
		if (adminUser.getName().equals("陳大") && adminUser.getPassword().equals("文"))
			return LOGIN_OK;
		else
			return LOGIN_FAILED;
	}
}
