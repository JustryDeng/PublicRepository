package com.aspire.client;

import com.aspire.service.impl.MyService;
import com.aspire.service.impl.MyServiceImplService;

public class WebServiceClientTest {
	public static void main(String[] args) {
		// 创建一个用于产生MyService接口实例的工厂
		MyServiceImplService myServiceImplService = new MyServiceImplService();
		// 得到MyService接口实例
		MyService myServiceImpl = myServiceImplService.getMyServiceImplPort();
		// 调用MyService接口的方法即可
		int addRes = myServiceImpl.add(100, 123);
		System.out.println(addRes);
		System.out.println("------------华丽分割线------------");
		int minusRes = myServiceImpl.minus(100, 123);
		System.out.println(minusRes);
	} 
}
