package com.myftpserver.impl;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import com.myftpserver.*;
import com.myftpserver.exception.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import org.apache.log4j.Logger;


public class DbOp {
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
	public User login(String userName, String password) throws LoginFailureException
	{
		User u=null;
		int result=0;
		String sql;
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
			  u.setHomeDir(rs.getString("home_dir"));
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
					user.addClientPathACL(rs.getString("vir_dir").trim(), rs.getString("permission").trim());
				}
				if ((rs.getString("phy_dir")!=null) &&   (!rs.getString("phy_dir").equals("")))
				{
					user.addServerPathACL(rs.getString("phy_dir").trim(), rs.getString("permission").trim());
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
	private String queryRealPath(String userName,String currentPath,String virPath,String permission)throws AccessDeniedException,PathNotFoundException
	{
		int i,resultCode=-1;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String realPath=null,pathPerm;
		String clientPath=virPath,restPath=new String(),sql;
		sql="select phy_dir,permission from virdir_2_phydir where user_name=? and vir_dir=? and active=1";
		if (clientPath.indexOf("/")==-1)
		{
			clientPath=currentPath+clientPath;
		}
		else	
		{
			if (clientPath.endsWith("/") && (!clientPath.equals("/")))
			{
			clientPath=clientPath.substring(0,clientPath.length()-1);
			}
		}
		
		try
		{	
			while(true)
			{
				stmt=dbConn.prepareStatement(sql);
				stmt.setString(1, userName);
				stmt.setString(2, clientPath);
				logger.debug("clientPath="+clientPath);
				rs=stmt.executeQuery();
				if (rs.next())
				{
					pathPerm=rs.getString("permission");
					if (pathPerm.indexOf(FileManager.NO_ACCESS)>-1)
					{
						resultCode=FileManager.ACCESS_DENIED;
						break;
					}
					else
					{
						if (pathPerm.indexOf(permission)==-1)
						{
							i=clientPath.lastIndexOf("/");
							restPath=clientPath.substring(i)+restPath;
							if (i==0)
								i=1;
							clientPath=clientPath.substring(0,i);
						}
						else
						{
							realPath=rs.getString("phy_dir");
							if (!restPath.equals(""))
							{
								realPath+=restPath;
							}
							
							if (!Files.exists(Paths.get(realPath)))
								resultCode=FileManager.PATH_NOT_FOUND;
							break;
						}
					}
				}
				else
				{
					if (clientPath.equals("/"))
					{
						resultCode=FileManager.ACCESS_DENIED;
						break;
					}
					else
					{
						i=clientPath.lastIndexOf("/");
						if (i==-1)
							clientPath=currentPath;
						else	
						{
							restPath=clientPath.substring(i)+restPath;
							if (i==0)
								i=1;
							clientPath=clientPath.substring(0,i);
						}
					}
				}
			}
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			resultCode=-2;
		}
		catch (StringIndexOutOfBoundsException se)
		{
			se.printStackTrace();
			resultCode=-3;
		}
		finally
		{
			releaseResource(rs, stmt);
			logger.debug("resultCode="+resultCode);
			switch (resultCode)
			{
				case FileManager.ACCESS_DENIED:throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
				case FileManager.PATH_NOT_FOUND:throw new PathNotFoundException(config.getFtpMessage("450_Directory_Not_Found"));
				default							:try
												 {	
													logger.debug("realPath="+realPath);
													realPath=Paths.get(realPath).toRealPath().toString();
												 }
												 catch (Exception e)
												 {
													 e.printStackTrace();
												 }
			}
		}
		logger.debug("RealPath="+realPath);
		return realPath;
	}
	public String getRealHomePath(String userName,String virPath,String permission) throws AccessDeniedException,PathNotFoundException
	{
		return queryRealPath(userName,"/",virPath,permission);
	}
	public String getRealPath(FtpSessionHandler fs,String virPath,String permission) throws AccessDeniedException,PathNotFoundException
	{
		return queryRealPath(fs.getUserName(),fs.getCurrentPath(),virPath,permission);
	}
	public Vector<User> listAllUser() 
	{
		// TODO Auto-generated method stub
		User u;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		Vector<User> userList = new Vector<User>();

		try {
			stmt = dbConn.prepareStatement("select * from user");
			rs = stmt.executeQuery();
			while (rs.next()) {
				u = new User();
				u.setHomeDir(rs.getString("home_dir"));
				u.setName(rs.getString("user_name"));
				u.setPassword(rs.getString("password"));
				u.setQuota(rs.getInt("quota"));
				if (rs.getInt("active") > 0)
					u.setActive(true);
				else
					u.setActive(false);
				userList.add(u);
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
		return userList;
	}
	public boolean isReadable(String userName,Path path)
	{
		String sql="select permission from virdir_2_phydir ";
		ResultSet rs = null;
		boolean result=false;
		PreparedStatement stmt = null;
		sql+="where user_name=? and phy_dir ";
		try
		{
			if (Files.isDirectory(path))
			{
				sql+=" like '"+path.toString()+"%'";
			}
			else
			{
				sql+=" ='"+path.toString()+"'";
			}
			stmt=dbConn.prepareStatement(sql);
			stmt.setString(1, userName);
			rs=stmt.executeQuery();
			
			if (rs.next())
			{
				if (rs.getString("permission").indexOf(FileManager.NO_ACCESS)==-1)
				{
					result=true;
				}
			}
			else
			{
				result=true;
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
		return result;
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