package com.aspire.service.impl;

import com.aspire.config.database.MysqlDatabaseConfig;
import com.aspire.config.database.OracleDatabaseConfig;
import com.aspire.mapper.mysqlmapper.MysqlMapper;
import com.aspire.mapper.oraclemapper.OracleMapper;
import com.aspire.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 多数据源下的事务测试
 *
 * @author {@link JustryDeng}
 * @date 2020/5/31 17:22:28
 */
@Service
@SuppressWarnings("all")
public class TestServiceImpl implements TestService {

    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

    @Autowired
    OracleMapper oracleMapper;

    @Autowired
    MysqlMapper mysqlMapper;

    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = OracleDatabaseConfig.TX_MANAGER_NAME)
    public void oracleInsert(Integer id, String info, boolean occurException) {
        oracleMapper.simpleInsert(id, info);
        if (occurException) {
            int a = 1 / 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = MysqlDatabaseConfig.TX_MANAGER_NAME)
    public void mysqlInsert(Integer id, String info, boolean occurException) {
        mysqlMapper.simpleInsert(id, info);
        if (occurException) {
            int a = 1 / 0;
        }
    }

    @Autowired
    @Qualifier(OracleDatabaseConfig.TX_MANAGER_NAME)
    private DataSourceTransactionManager oracleTxManager;

    @Autowired
    @Qualifier(MysqlDatabaseConfig.TX_MANAGER_NAME)
    private DataSourceTransactionManager mysqlTxManager;

    /**
     * (涉及到多个数据源的service, 可以)手动管理事务
     */
    @Override
    public void oracleAndMysqlInsert(Integer id, String info, boolean occurException) {
        // 操作前的oracle status
        DefaultTransactionDefinition oracleDef = new DefaultTransactionDefinition();
        oracleDef.setName("oracle-tx");
        oracleDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus oracleStatus = oracleTxManager.getTransaction(oracleDef);

        // 操作前的mysql status
        DefaultTransactionDefinition mysqlDef = new DefaultTransactionDefinition();
        mysqlDef.setName("mysql-tx");
        mysqlDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus mysqlStatus = mysqlTxManager.getTransaction(mysqlDef);

        try {
            // 业务逻辑
            oracleMapper.simpleInsert(id, info);
            mysqlMapper.simpleInsert(id, info);
            if (occurException) {
                int a = 1 / 0;
            }

            // 提交(后定义的status需要先commit)
            mysqlTxManager.commit(mysqlStatus);
            oracleTxManager.commit(oracleStatus);
        } catch (Exception e) {
            // 回滚(后定义的status需要先rollback)
            mysqlTxManager.rollback(mysqlStatus);
            oracleTxManager.rollback(oracleStatus);
            throw e;
        }

    }
}
