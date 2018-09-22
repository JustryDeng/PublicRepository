package com.aspire.server;

import javax.xml.ws.Endpoint;

/**
 * 发布WebService
 *
 * @author JustryDeng
 * @DATE 2018年9月22日 下午9:06:19
 */
public class MyServer {
	
	public static void main(String[] args) {
		// 自己设置一个访问地址
		String address = "http://127.0.0.1:9527/webservice/test";
		Endpoint.publish(address, new MyWebService());
	}
}

