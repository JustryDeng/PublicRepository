package com.aspire.model;

import java.io.Serializable;

/**
 * 用户模型
 * 注意:此模型必须能够序列化，即:必须implements Serializable
 *
 * @author JustryDeng
 * @Date 2018年8月23日 下午4:35:44
 */
public class User implements Serializable{

	/** 序列化UID为-4914585368925337032L */
	private static final long serialVersionUID = -4914585368925337032L;

	/** 用户名 */
	private String username;

	/** 用户密码 */
	private String password;

	/** 用户角色 */
	private String userRoles;

	/**
	 * 无参构造
	 */
	public User() {
	}

	/**
	 * 全参构造
	 */
	public User(String username, String password, String userRoles) {
		super();
		this.username = username;
		this.password = password;
		this.userRoles = userRoles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String userRoles) {
		this.userRoles = userRoles;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("usrename : " + username);
		sb.append(",password : " + password);
		sb.append(",userRoles : " + userRoles);
		return sb.toString();
	}

}
