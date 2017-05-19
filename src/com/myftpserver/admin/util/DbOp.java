package com.myftpserver.admin.util;
import java.util.*;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.apache.logging.log4j.Logger;

import com.myftpserver.server.FtpServer;
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
		jdbcURL = "jdbc:sqlite:admin.db";
		this.logger=logger;
		Class.forName(jdbcDriver);
		dbConn = DriverManager.getConnection(jdbcURL);
	}
	public List<FtpServer<?>> getServerList()
	{
		FtpServer<?> ftpServer; 
		ResultSet rs = null;
		PreparedStatement stmt=null;
		String sql="select config_json from server where active=?";
		List<FtpServer<?>> serverList=new ArrayList<FtpServer<?>>();
		try 
		{
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, 1);
			rs=stmt.executeQuery();
			while (rs.next())
			{
				ftpServer=new FtpServer(rs.getString("config_json"));
				serverList.add(ftpServer);
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			releaseResource(rs, stmt);
		}		
		return serverList;
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
