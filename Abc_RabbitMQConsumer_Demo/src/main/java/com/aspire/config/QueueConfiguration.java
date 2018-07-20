package com.aspire.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Queue队列注入
 *
 * @author JustryDeng
 * @date 2018年7月18日 下午8:40:47
 */
@Configuration
public class QueueConfiguration {

	/**
	 * 注入第一个Queue队列 实例
	 *
	 * @return Queue队列实例
	 * @date 2018年7月17日 下午8:52:04
	 */
	@Bean(name = "myFirstQueue")
	public Queue getFirstQueue() {
		// 设置队列名为My-First-Queue
		return new Queue("My-First-Queue");
	}

	/**
	 * 注入第二个Queue队列 实例
	 *
	 * @return Queue队列实例
	 * @date 2018年7月17日 下午8:53:04
	 */
	@Bean(name = "myTwoQueue")
	public Queue getTwoQueue() {
		// 设置队列名为My-Two-Queue
		return new Queue("My-Two-Queue");
	}

	/**
	 * 注入第三个Queue队列 实例
	 *
	 * @return Queue队列实例
	 * @date 2018年7月17日 下午8:54:04
	 */
	@Bean(name = "myThreeQueue")
	public Queue getThreeQueue() {
		// 设置队列名为My-Three-Queue
		return new Queue("My-Three-Queue");
	}
	
	/**
	 * 关于MQ传递消息时,实际上用的HttpClient的什么请求方式  的测试
	 *
	 * @return Queue队列实例
	 * @date 2018年7月17日 下午8:54:04
	 */
	@Bean(name = "myRequestTestQueue")
	public Queue getRequestTestQueue() {
		// 设置队列名为My-Request-Test-Queue
		return new Queue("My-Request-Test-Queue");
	}
}
