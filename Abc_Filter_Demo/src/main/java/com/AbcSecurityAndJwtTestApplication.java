package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan// 允许扫描Servlet组件(过滤器、监听器等)
@SpringBootApplication
public class AbcSecurityAndJwtTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbcSecurityAndJwtTestApplication.class, args);
	}
}
