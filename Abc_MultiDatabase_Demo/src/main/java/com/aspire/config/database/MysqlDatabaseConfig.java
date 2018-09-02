package com.aspire.config.database;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * 数据源配置 
 * 提示:如果@Bean后面不指定id,那么默认以方法名字为id
 *
 * @author JustryDeng
 * @Date 2018年8月30日 上午7:13:33
 */
@Configuration
@MapperScan(basePackages = "com.aspire.mapper.mysqlmapper", sqlSessionTemplateRef = "mysqlSqlSessionTemplate")
public class MysqlDatabaseConfig {

	@Bean
	// 根据application.properteis系统配置文件中,对应属性的前缀,指明使用其对应的数据
	@ConfigurationProperties(prefix = "spring.datasource.database-one")
	public DataSource mysqlDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@DependsOn("mysqlDataSource")
	public SqlSessionFactory mysqlSqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(mysqlDataSource());
		return factoryBean.getObject();
	}

	/**
	 * DefaultSqlSession和SqlSessionTemplate都实现了SqlSession,但我们
	 * 注入线程安全的SqlSessionTemplate,而不使用默认的线程不安全的DefaultSqlSession
	 *
	 * @Date 2018年9月2日 上午10:46:39
	 */
	@Bean
	@DependsOn("mysqlSqlSessionFactory")
	public SqlSessionTemplate mysqlSqlSessionTemplate() throws Exception {
		SqlSessionTemplate template = new SqlSessionTemplate(mysqlSqlSessionFactory());
		return template;
	}

}
