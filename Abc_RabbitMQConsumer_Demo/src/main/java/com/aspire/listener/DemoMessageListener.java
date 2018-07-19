package com.aspire.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 注入 消息监听器
 *
 * @author JustryDeng
 * @date 2018年7月17日 下午8:53:41
 */
@Component
public class DemoMessageListener {

	@RabbitListener(queues = "My-First-Queue") // 指定Queue队列
	public void firstConsumer(String string) {
		System.out.println("我是:My-First-Queue" + "\tString:" + string);
	}

	@RabbitListener(queues = "My-Two-Queue") // 指定Queue队列
	public void twoConsumer(Integer num) {
		System.out.println("我是:My-Two-Queue" + "\tInteger:" + num);
	}

	@RabbitListener(queues = "My-Three-Queue") // 指定Queue队列
	public void threeConsumer() {
		System.out.println("我是:My-Three-Queue");

	}
}
