package com.aspire.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * fanout路由策略(广播机制)的交换机注入、Queue与Exchange的绑定注入
 *
 * @author JustryDeng
 * @date 2018年7月18日 下午8:41:07
 */
@Configuration
public class FanoutExchangeAndBindingConfiguration {

	/**
	 * 注入Fanout路由策略的Exchange交换机实例
	 *
	 * @return Exchange“交换机”实例
	 * @date 2018年7月17日 下午8:55:04
	 */
	@Bean(name = "myFanoutExchange")
	FanoutExchange getFanoutExchange() {
		// 创建并返回名为My-Fanout-Exchange的交换机
		return new FanoutExchange("My-Fanout-Exchange");
	}


	/**
	 * 将myFirstQueue对应的队列，绑定到myFanoutExchange对应的交换机
	 *
	 * @date 2018年7月17日 下午9:02:04
	 */
	@Bean
	Binding bindingQueueOneToFanoutExchange(@Qualifier("myFirstQueue") Queue myFirstQueue,
			@Qualifier("myFanoutExchange") FanoutExchange myFanoutExchange) {
		return BindingBuilder.bind(myFirstQueue).to(myFanoutExchange);
	}

	/**
	 * 将myTwoQueue对应的队列，绑定到myFanoutExchange对应的交换机
	 *
	 * @date 2018年7月17日 下午9:03:04
	 */
	@Bean
	Binding bindingQueueTwoToFanoutExchange(@Qualifier("myTwoQueue") Queue myTwoQueue,
			@Qualifier("myFanoutExchange") FanoutExchange myFanoutExchange) {
		return BindingBuilder.bind(myTwoQueue).to(myFanoutExchange);
	}

	/**
	 * 将myThreeQueue对应的队列，绑定到myFanoutExchange对应的交换机
	 *
	 * @date 2018年7月17日 下午9:04:04
	 */
	@Bean
	Binding bindingQueueThreeToFanoutExchange(@Qualifier("myThreeQueue") Queue myThreeQueue,
			@Qualifier("myFanoutExchange") FanoutExchange myFanoutExchange) {
		return BindingBuilder.bind(myThreeQueue).to(myFanoutExchange);
	}
}
