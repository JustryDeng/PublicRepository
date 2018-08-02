package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement//开启事物支持
public class SpringBootForJavaAnnotationApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBootForJavaAnnotationApplication.class, args);
	}
}