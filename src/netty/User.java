package netty;

import java.io.Serializable;

public class User implements Serializable  
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5964714288038429186L;
	String name,password;
	public User()
	{
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
