package com.aspire.server;

import javax.xml.ws.Endpoint;

import com.aspire.service.impl.MyServiceImpl;

public class MyServer {
	public static void main(String[] args) {
		// 自己设置一个访问地址
		String address = "http://localhost:9812/WebServiceTest";
		Endpoint.publish(address, new MyServiceImpl());
	}

}
