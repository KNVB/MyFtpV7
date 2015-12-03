package com.myftpserver.impl;

import java.sql.*;

import com.myftpserver.*;
import com.myftpserver.exception.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import org.apache.log4j.Logger;


public class DbOp 
{
	private String jdbcDriver = new String();
	private String jdbcURL = new String();
	private Connection dbConn = null;
	private Logger logger = null;
	private Configuration config = null;

	public DbOp(Configuration c) throws Exception 
	{
		jdbcDriver = "org.sqlite.JDBC";
		jdbcURL = "jdbc:sqlite:user.db";
		logger=c.getLogger();
		config=c;
		Class.forName(jdbcDriver);
		dbConn = DriverManager.getConnection(jdbcURL);
	}
	public User login(FtpSessionHandler fs, String password) throws LoginFailureException
	{
		User u=null;
		int result=0;
		String sql;
		String userName=fs.getUserName();
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try
		{
			sql="select * from user where user_name=? and password=? and active=1";
			stmt=dbConn.prepareStatement(sql);
			stmt.setString(1, userName);
			stmt.setString(2, password);
			rs=stmt.executeQuery();
			if (rs.next())
			{
			  u=new User();
			  //u.setHomeDir(rs.getString("home_dir"));
			  u.setName(rs.getString("user_name"));
			  u.setPassword(rs.getString("password"));
			  u.setQuota(rs.getInt("quota"));
			  u.setActive(true);
			}
			else
				result=UserManager.INVAILD_USERNAME_OR_PASSWORD;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			releaseResource(rs, stmt);
		}
		if (result!=0)
		{
			u=null;
			throw new LoginFailureException(config.getFtpMessage("530_Invalid_Login")); 
		}
		return u;
	}
	public void loadACL(User user)
	{
		String sql;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try
		{
			sql ="select vir_dir,permission,phy_dir ";
			sql+="from virdir_2_phydir where user_name=? and active=1";
			stmt=dbConn.prepareStatement(sql);
			stmt.setString(1, user.getName());
			rs=stmt.executeQuery();
			while (rs.next())
			{
				logger.debug("vir_dir="+rs.getString("vir_dir")+",phy_dir="+rs.getString("phy_dir")+",permission="+rs.getString("permission"));
				if ((rs.getString("vir_dir")!=null) && (!rs.getString("vir_dir").equals("")))
				{
					if (rs.getString("phy_dir")==null)
						user.addClientPathACL(rs.getString("vir_dir").trim(), rs.getString("permission").trim() +"\tnull");
					else	
						user.addClientPathACL(rs.getString("vir_dir").trim(), rs.getString("permission").trim() +"\t"+rs.getString("phy_dir").trim());
				}
				if ((rs.getString("phy_dir")!=null) && (!rs.getString("phy_dir").equals("")))
				{
					if (rs.getString("vir_dir")==null)
						user.addServerPathACL(rs.getString("phy_dir").trim(), rs.getString("permission").trim()+"\tnull");
					else	
						user.addServerPathACL(rs.getString("phy_dir").trim(), rs.getString("permission").trim()+"\t"+rs.getString("vir_dir").trim());
					
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			releaseResource(rs, stmt);
		}
	}
	public String getRealPath(FtpSessionHandler fs,String virPath,String permission)throws AccessDeniedException,PathNotFoundException 
	{
		String realPath=null,pathPerm=null;
		
		String clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(), virPath);
		User user=fs.getUser();
		logger.debug(permission==null);
		logger.debug("user ="+user.getName()+",currentPath="+fs.getCurrentPath()+",virPath="+virPath+",permission="+permission+",clientPath="+clientPath);
		realPath=Utility.getRealPath(fs,clientPath, permission);
		logger.debug("user ="+user.getName()+",currentPath="+fs.getCurrentPath()+",virPath="+virPath+",permission="+permission+",clientPath="+clientPath+",realPath="+realPath+",pathPerm="+pathPerm);
		return realPath;
	}
	public void getRealHomePath(FtpSessionHandler fs)throws AccessDeniedException,PathNotFoundException 
	{
		// TODO Auto-generated method stub
		fs.setCurrentPath("/");
		getRealPath(fs,"/",FileManager.READ_PERMISSION);
	}
	public void close() throws Exception 
	{
		dbConn.close();
		dbConn = null;
	}
	
	private void releaseResource(ResultSet r, PreparedStatement s) 
	{
		if (r != null) 
		{
			try 
			{
				r.close();
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (s != null) 
		{
			try 
			{
				s.close();
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		r = null;
		s = null;
	}
}