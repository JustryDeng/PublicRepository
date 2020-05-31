package com;

import com.aspire.mapper.mysqlmapper.MysqlMapper;
import com.aspire.mapper.oraclemapper.OracleMapper;
import com.aspire.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 多数据源测试
 *
 * @author JustryDeng
 * @date 2018年9月2日 上午11:14:47
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@SuppressWarnings("all")
public class AbcMultiDatabaseDemoApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(AbcMultiDatabaseDemoApplicationTests.class);

    @Autowired
    private MysqlMapper mysqlMapper;

    @Autowired
    private OracleMapper oracleMapper;

    @Autowired
    private TestService service;

    /** 简单测试 */
    @Test
    public void multiDatasourceTest() {
        String mysqlString = mysqlMapper.simpleSelect(1);
        System.out.println("查询MySQ -> " + mysqlString);
        String oracleString = oracleMapper.simpleSelect(1);
        System.out.println("查询ORACLE -> " + oracleString);
    }

    /** 事务测试1 => 不发生异常, 正常插入 */
    @Test
    public void multiDatasourceTxTest1() {
        int randomIntValue = ThreadLocalRandom.current().nextInt(10000) + 100;
        service.oracleInsert(randomIntValue, "info-" + randomIntValue, false);
        service.mysqlInsert(randomIntValue, "info-" + randomIntValue, false);
        service.oracleAndMysqlInsert(randomIntValue + 1, "info-" + (randomIntValue + 1), false);
    }

    /** 事务测试2 => 发生异常，回滚 */
    @Test
    public void multiDatasourceTxTest2() {
        int randomIntValue = ThreadLocalRandom.current().nextInt(10000) + 100;
        // 【只涉及oracle的事务中】service发生了异常， oracle回滚了
        try {
            service.oracleInsert(randomIntValue, "info-" + randomIntValue, true);
        } catch (Exception e) {
            System.err.println("oracle \t" + e.getMessage());
        }
        // 【只涉及mysql的事务中】service发生了异常， mysql回滚了
        try {
            service.mysqlInsert(randomIntValue, "info-" + randomIntValue, true);
        } catch (Exception e) {
            System.err.println("mysql \t" + e.getMessage());
        }
        // 【既涉及oracle又涉及mysql的事务中】service发生了异常， oracle和mysql都没有没回滚
        try {
            service.oracleAndMysqlInsert(randomIntValue + 1, "info-" + (randomIntValue + 1), true);
        } catch (Exception e) {
            System.err.println("oracle+mysql \t" + e.getMessage());
        }
    }

}