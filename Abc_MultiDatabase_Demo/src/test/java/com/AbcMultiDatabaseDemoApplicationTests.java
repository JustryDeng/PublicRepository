package com;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 多数据源测试
 *
 * @author JustryDeng
 * @Date 2018年9月2日 上午11:14:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcMultiDatabaseDemoApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(AbcMultiDatabaseDemoApplicationTests.class);

	@Autowired
	private com.aspire.mapper.mysqlmapper.MysqlMapper mysqlMapper;

	@Autowired
	private com.aspire.mapper.oraclemapper.OracleMapper oracleMapper;

	@Test
	public void multiDatasourceTest() {
		try {
			String mysqlString = mysqlMapper.singleSelect(1);
			System.out.println("查询MySQ >>> " + mysqlString);
			String oracleString = oracleMapper.singleSelect(1);
			System.out.println("查询ORACLE >>> " + oracleString);
		} catch (Exception e) {
			logger.error("---------", e);
		}
	}
	
}