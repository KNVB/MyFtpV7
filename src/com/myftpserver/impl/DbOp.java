package com.myftpserver.impl;

import com.myftpserver.*;
import com.util.MessageBundle;
import com.myftpserver.exception.*;
import com.myftpserver.abstracts.UserManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.abstracts.ServerConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

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
public class DbOp 
{
	private Logger logger = null;
	private Connection dbConn = null;
	private String jdbcURL = new String();
	private String jdbcDriver = new String();
	/**
	 * Database object,initialize db connection
	 * @param logger Message logger
	 * @throws Exception
	 */
	public DbOp(Logger logger) throws Exception 
	{
		jdbcDriver = "org.sqlite.JDBC";
		jdbcURL = "jdbc:sqlite:user.db";
		this.logger=logger;
		Class.forName(jdbcDriver);
		dbConn = DriverManager.getConnection(jdbcURL);
	}
	/**
	 * Perform user login
	 * @param fs FtpSessionHandler
	 * @param password login password
	 * @return User object
	 * @throws LoginFailureException
	 */
	public User login(FtpSessionHandler fs, String password) throws LoginFailureException
	{
		String sql;
		User u=null;
		int result=0;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String userName=fs.getUserName();
		ServerConfig serverConfig=fs.getServerConfig();
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
			  u.setName(rs.getString("user_name"));
			  u.setPassword(rs.getString("password"));
			  u.setQuota(rs.getInt("quota"));
			  u.setActive(true);
			  u.setDownloadSpeedLitmit(rs.getLong("downloadSpeedLimit"));
			  u.setUploadSpeedLitmit(rs.getLong("uploadSpeedLimit"));
			  if ((rs.getString("userLocale")!=null) && (!rs.getString("userLocale").equals(serverConfig.getServerLocale())))
				  fs.setMessageBundle(new MessageBundle(new Locale(rs.getString("userLocale"))));
			  else
				  fs.setMessageBundle(serverConfig.getMessageBundle());
			  if (rs.getString("diskSpaceUsed")!=null)
			  {
				  u.setDiskSpaceUsed(rs.getDouble("diskSpaceUsed"));
			  }
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
			throw new LoginFailureException(serverConfig.getFtpMessage("530_Invalid_Login")); 
		}
		return u;
	}
	/**
	 * Load Access control list to user object
	 * @param user
	 */
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
	public int upDateUserInfo(User user) 
	{
		int result=-1;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String sql="update user set password=?,";
		sql+="quota=?,diskSpaceUsed=?,";
		sql+="userLocale=?,downloadSpeedLimit=?,";
		sql+="uploadSpeedLimit=? where user_name=?";
		
		try 
		{
			stmt=dbConn.prepareStatement(sql);
			stmt.setString(1, user.getPassword());
			stmt.setDouble(2, user.getQuota());
			stmt.setDouble(3, user.getDiskSpaceUsed());
			stmt.setString(4,user.getUserLocale());
			stmt.setDouble(5, user.getDownloadSpeedLitmit());
			stmt.setDouble(6, user.getUploadSpeedLitmit());
			stmt.setString(7, user.getName());
			result=stmt.executeUpdate();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			releaseResource(rs, stmt);
		}
		return result;
	}
	/**
	 * Close db connection
	 * @throws Exception
	 */
	public void close() throws Exception 
	{
		dbConn.close();
		dbConn = null;
	}
	/**
	 * Release resource for 
	 * @param r ResultSet object
	 * @param s PreparedStatement object
	 */
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
				e.printStackTrace();
			}
		}
		r = null;
		s = null;
	}	
}
