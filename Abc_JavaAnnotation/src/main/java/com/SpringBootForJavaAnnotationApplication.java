package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * 注:@EnableTransactionManagement为开启事物支持
 * @author JustryDeng
 * @date 2019/1/10 11:42
 */
@SpringBootApplication
@EnableTransactionManagement
public class SpringBootForJavaAnnotationApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBootForJavaAnnotationApplication.class, args);
	}
}