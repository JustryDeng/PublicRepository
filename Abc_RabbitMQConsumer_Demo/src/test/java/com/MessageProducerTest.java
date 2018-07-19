package com;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 模拟消息生产者 向Exchange发送消息 注:在正常使用时，消息生产者 一般都和 消息消费者 不处在同一个项目或服务下;
 * 这里只是测试，所以本人在同一个项目下，使用单元测试，模拟的消息生产者(用法是一样的)
 *
 * @author JustryDeng
 * @date 2018年7月18日 下午4:39:25
 */
@SpringBootTest(classes = AbcRabbitMqDemoApplication.class)
@RunWith(SpringRunner.class)
public class MessageProducerTest {

	/** 装配AMQP模板 */
	@Autowired
	private AmqpTemplate amqpTemplate;
	
	/** 装配RabbitMessaging模板 */
	@Autowired
	private RabbitMessagingTemplate rabbitMessagingTemplate;


	/**
	 * headers路由策略---WhereAllMap匹配测试
	 *
	 * @date 2018年7月19日 下午5:15:01
	 */
	@Test
	public void headersExchangeWhereAllMapTest() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("name", "邓沙利文");
		headers.put("motto", "justry");
		rabbitMessagingTemplate.convertAndSend("My-Headers-Exchange", "", "通过[头交换机]传递数据咯", headers);
	}
	
	
	/**
	 * headers路由策略测试WhereAnyMap匹配测试
	 *
	 * @date 2018年7月19日 下午5:15:01
	 */
	@Test
	public void headersExchangeWhereAnyMapTest() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("name", "邓沙利文");
		headers.put("motto", "justry123");
		rabbitMessagingTemplate.convertAndSend("My-Headers-Exchange", "", "", headers);
	}

	/**
	 * topic路由策略(可以通配的路由键)测试
	 *
	 * @date 2018年7月18日 下午4:42:54
	 */
	@Test
	public void topicExchangeTest1() {
		// 此消息能匹配上 路由键为“routingKey.#”和“#”的队列
		amqpTemplate.convertAndSend("My-Topic-Exchange", "routingKey.myTest", "1");
	}

	@Test
	public void topicExchangeTest2() {
		// 此消息能匹配上 路由键为“#.topic”和“#”的队列
		amqpTemplate.convertAndSend("My-Topic-Exchange", "myTest.topic", "2");
	}

	@Test
	public void topicExchangeTest3() {
		// 此消息能匹配上 路由键为“#”的队列
		amqpTemplate.convertAndSend("My-Topic-Exchange", "myTest", "3");
	}

	/**
	 * direct路由策略(具体的路由键)测试
	 *
	 * @date 2018年7月18日 下午4:42:54
	 */
	@Test
	public void directExchangeTest() {
//		rabbitMessagingTemplate.convertAndSend("My-Direct-Exchange", "routingKey.First", "1234578");
		amqpTemplate.convertAndSend("My-Direct-Exchange", "routingKey.First", "1234578");
	}

	/**
	 * fanout路由策略(即:广播模式) 测试
	 *
	 * @date 2018年7月18日 下午4:42:54
	 */
	@Test
	public void fanoutExchangeTest() {
		amqpTemplate.convertAndSend("My-Fanout-Exchange", "", "123");
	}
}
