package com.aspire.service;

import com.aspire.author.JustryDeng;

/**
 * 多数据源下的事务测试
 *
 * @author {@link JustryDeng}
 * @date 2020/5/31 17:22:28
 */
@SuppressWarnings("all")
public interface TestService {

    void oracleInsert(Integer id, String info, boolean occurException);

    void mysqlInsert(Integer id, String info, boolean occurException);

    void oracleAndMysqlInsert(Integer id, String info, boolean occurException);
}
