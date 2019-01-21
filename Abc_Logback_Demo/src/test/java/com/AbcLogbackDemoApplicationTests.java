package com;

import com.alibaba.fastjson.JSON;
import com.aspire.mapper.JavaAnnotationMapper;
import com.aspire.model.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbcLogbackDemoApplication.class })
public class AbcLogbackDemoApplicationTests {

	static final Logger logger = LoggerFactory.getLogger(AbcLogbackDemoApplicationTests.class);

	@Autowired
	JavaAnnotationMapper javaAnnotationMapper;

	/**
	 * logback测试
	 *
	 * @date 2018年7月26日 下午4:12:56
	 */
	@Test
	public void logbackTest() {
		long startTime = System.currentTimeMillis();
		logger.info("进入logbackTest方法了！");
		try {
			Employee employee = new Employee("邓某", 24, "男");
			logger.info("employee对象的相应参数为:" + JSON.toJSONString(employee));
			javaAnnotationMapper.singleInsertAutoGetKey(employee);
			Integer id = employee.getId();
			logger.info("向表中插入employee对象的数据后,自动获取到的主键为:" + id);
			 System.out.println(1 / 0);
		} catch (Exception e) {
			logger.trace("出错咯！错误信息:" + e.getMessage(), e);
		}
		long endTime = System.currentTimeMillis();
		double consumeTime = (endTime - startTime) * 1.0 / 1000;
		logger.info("SpringBoot使用logback示例。");
		logger.info("logbackTest方法执行完毕,用时{}秒", consumeTime);
	}

}
