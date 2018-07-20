package com.aspire.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * headers路由策略的交换机注入、Queue与Exchange的绑定注入
 *
 * @author JustryDeng
 * @date 2018年7月19日 下午4:42:09
 */
@Configuration
public class HeadersExchangeAndBindingConfiguration {

	/**
	 * 注入Headers路由策略的Exchange交换机实例
	 *
	 * @return Exchange“交换机”实例
	 * @date 2018年7月18日 下午8:47:48
	 */
	@Bean(name = "myHeadersExchange")
	HeadersExchange getDirectExchange() {
		// 创建并返回名为My-Headers-Exchange的交换机
		return new HeadersExchange("My-Headers-Exchange");
	}

	/**
	 * 将Queue绑定到此headersExchange(并指定:当headers中所有的“map”被此Queue匹配时,才可使用此队列)
	 * 注:此示例是匹配的.whereAll(Map<String,Object> map);
	 *    也可以只匹配.whereAll(String... headersKeys);
	 *
	 * @date 2018年7月19日 下午4:54:47
	 */
	@Bean
	Binding bindingQueueOneToHeadersAllExchange(@Qualifier("myFirstQueue") Queue myFirstQueue,
			@Qualifier("myHeadersExchange") HeadersExchange myHeadersExchange) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("name", "邓沙利文");
		headers.put("motto", "justry");
		return BindingBuilder.bind(myFirstQueue).to(myHeadersExchange).whereAll(headers).match();
	}
	
	/**
	 * 将Queue绑定到此headersExchange(并指定:当headers中任意一个map被此Queue匹配时,就会使用此队列)
	 * 注:此示例是匹配的.whereAny(Map<String,Object> map);
	 *    也可以只匹配.whereAny(String... headersKeys);
	 *
	 * @date 2018年7月19日 下午4:55:47
	 */
	@Bean
	Binding bindingQueueOneToHeadersAnyExchange(@Qualifier("myThreeQueue") Queue myThreeQueue,
			@Qualifier("myHeadersExchange") HeadersExchange myHeadersExchange) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("name", "邓沙利文");
		headers.put("motto", "justry");
		return BindingBuilder.bind(myThreeQueue).to(myHeadersExchange).whereAny(headers).match();
	}

}
