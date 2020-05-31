package com;

import com.aspire.author.JustryDeng;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 *
 * @author {@link JustryDeng}
 * @date 2020/5/31 17:00:19
 */
@EnableTransactionManagement
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AbcMultiDatabaseDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbcMultiDatabaseDemoApplication.class, args);
	}
}
