package com.aspire.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录的Controller
 *
 * @author JustryDeng
 * @Date 2018年8月25日 下午12:24:37
 */
@RestController
public class ShiroHandler {

	private static final Logger logger = LoggerFactory.getLogger(ShiroHandler.class);

	@RequestMapping("login")
	public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
		String flag = "success";
		// 当前用户
		Subject currentUser = SecurityUtils.getSubject();

		// 验证是否身份认证֤
		if (!currentUser.isAuthenticated()) {
			// 将用户名、密码封装为token
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			// rememberMe
			token.setRememberMe(true);
			try {
				// 执行登录
				currentUser.login(token);
			} catch (UnknownAccountException uae) {
				logger.error("There is no user with username of " + token.getPrincipal());
				flag = "fail";
			} catch (IncorrectCredentialsException ice) {
				logger.error("Password for account " + token.getPrincipal() + " was incorrect!");
				flag = "fail";
			} catch (LockedAccountException lae) {
				logger.error("The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
				flag = "fail";
			} catch (AuthenticationException ae) {
				logger.error("authentication fail!");
				flag = "fail";
			}
		}
		return flag;
	}

}
