package com.aspire.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于测试的Controller
 *
 * @author JustryDeng
 * @Date 2018年8月25日 上午11:09:53
 */
@RestController
public class TestController {

	@Value("${spring.datasource.username}")
	private String datasourceUsername;
	
	/**
	 * 当用户认证后;满足ShiroConfigure中配置的放行条件时,可访问"/test"
	 *
	 * @Date 2018年8月25日 上午11:10:49
	 */
	@GetMapping("/test")
	public String testMethod() {
		String string = "配置文件中的参数是:" + datasourceUsername;
		return string;
	}
}
