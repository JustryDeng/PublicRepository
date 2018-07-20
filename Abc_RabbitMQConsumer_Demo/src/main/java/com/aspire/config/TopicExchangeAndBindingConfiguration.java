package com.aspire.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * topic路由策略(路由键routingKey,且支持模糊匹配)的交换机注入、Queue与Exchange的绑定注入
 *
 * @author JustryDeng
 * @date 2018年7月16日 上午10:12:12
 */
@Configuration
public class TopicExchangeAndBindingConfiguration {

	/**
	 * 注入Topic路由策略的Exchange交换机实例
	 *
	 * @return Exchange“交换机”实例
	 * @date 2018年7月18日 下午8:47:36
	 */
	@Bean(name = "myTopicExchange")
	TopicExchange getTopicExchange() {
		// 创建并返回名为My-Topic-Exchange的交换机
		return new TopicExchange("My-Topic-Exchange");
	}
	
	
	/**
	 * 将myFirstQueue对应的Queue绑定到此topicExchange,并指定路由键为"routingKey.#"
	 * 即:此Exchange中,路由键以"routingKey."开头的Queue将被匹配到
	 *
	 * @date 2018年7月19日 上午12:20:09
	 */
	@Bean
	Binding bindingQueueOneToTopicExchange(@Qualifier("myFirstQueue") Queue myFirstQueue,
			@Qualifier("myTopicExchange") TopicExchange myTopicExchange) {
		return BindingBuilder.bind(myFirstQueue).to(myTopicExchange).with("routingKey.#");
	}
	
	/**
	 * 将myTwoQueue对应的Queue绑定到此topicExchange,并指定路由键为"#.topic"
	 * 即:此Exchange中,路由键以".topic"结尾的Queue将被匹配到
	 *
	 * @date 2018年7月19日 上午12:20:09
	 */
	@Bean
	Binding bindingQueueTwoToTopicExchange(@Qualifier("myTwoQueue") Queue myTwoQueue,
			@Qualifier("myTopicExchange") TopicExchange myTopicExchange) {
		return BindingBuilder.bind(myTwoQueue).to(myTopicExchange).with("#.topic");
	}
	
	/**
	 * 将myThreeQueue对应的Queue绑定到此topicExchange,并指定路由键为"#"
	 * 即:此topicExchange中,任何Queue都将被匹配到
	 *
	 * @date 2018年7月19日 上午12:20:09
	 */
	@Bean
	Binding bindingQueueThreeToTopicExchange(@Qualifier("myThreeQueue") Queue myThreeQueue,
			@Qualifier("myTopicExchange") TopicExchange myTopicExchange) {
		return BindingBuilder.bind(myThreeQueue).to(myTopicExchange).with("#");
	}
}
