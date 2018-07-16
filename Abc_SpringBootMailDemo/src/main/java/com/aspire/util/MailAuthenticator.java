package com.aspire.util;

import javax.mail.Authenticator;

/**
 * 通过账号密码(或账号授权码)进行身份验证
 */
public class MailAuthenticator extends Authenticator {
	
	/** 邮箱号xxx@xxx.xxx */
	private String userName;
	
	/**
	 * 邮箱密码或授权码(不同邮箱可能不一样,如163邮箱的话,就是授权码)
	 */
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public MailAuthenticator(String username, String password) {
		this.userName = username;
		this.password = password;
	}
}
