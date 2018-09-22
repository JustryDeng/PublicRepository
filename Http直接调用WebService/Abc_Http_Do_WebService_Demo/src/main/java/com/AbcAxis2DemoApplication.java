package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AbcAxis2DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbcAxis2DemoApplication.class, args);
	}
}
