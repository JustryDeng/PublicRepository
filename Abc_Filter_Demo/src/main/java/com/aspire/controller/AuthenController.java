package com.aspire.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签名鉴权测试
 *
 * @author JustryDeng
 * @DATE 2018年9月11日 下午9:08:53
 */
@RestController
public class AuthenController {

	@PostMapping("/authen/test1")
	public String test1(@RequestHeader("Authorization") String autho, @RequestBody String motto) {
		return "进来了1！" + "\n" + autho + "\n" + motto;
	}
	
	@PostMapping("/authen/test2")
	public String test2() {
		return "进来了2！";
	}
	
	@PostMapping("/authen/test3")
	public String test3() {
		return "进来了3！";
	}
}
