package com.aspire.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * 查看RestTemplate发送的HTTP、HTTPS请求
 *
 * @author JustryDeng
 * @DATE 2018年9月7日 下午6:26:25
 */
@RestController
public class RestTemplateTest {

	/**
	 * RestTemplate --- HTTP发送Get请求示例
	 * 
	 * @throws UnsupportedEncodingException 
	 *            
	 * @DATE 2018年9月7日 下午6:26:44
	 */
	@GetMapping("/restTemplate/doHttpGet")
	public String doGetControllerOne(HttpServletRequest request) throws UnsupportedEncodingException {
		System.out.println(request.getCharacterEncoding());
		// GET方式传输中文,需要转码
		String JustryDeng = new String(request.getHeader("JustryDeng").getBytes("ISO-8859-1"),"utf-8");
		System.out.println(JustryDeng);
		System.out.println("flag的值为:" + request.getParameter("flag"));
		return "我是一只小小小小鸟~";
	}
	
	/**
	 * RestTemplate --- HTTP发送Post请求示例
	 * @throws UnsupportedEncodingException 
	 *             注:为了看起来方便,这里声明了异常,并没有处理;实际操作时需要处理异常
	 * @DATE 2018年9月8日 下午2:18:12
	 */
	@PostMapping("/restTemplate/doHttpPost")
	public String doPostControllerTwo(HttpServletRequest request,@RequestBody String jsonString) 
			throws UnsupportedEncodingException {
		System.out.println(request.getCharacterEncoding());
		// 并不是请求体中的(中文)数据,需要转码
		String JustryDeng = new String(request.getHeader("JustryDeng").getBytes("ISO-8859-1"),"utf-8");
		System.out.println(JustryDeng);
		System.out.println("flag的值为:" + request.getParameter("flag"));
		// 获取请求体中的数据
		System.out.println("请求体中的数据为:" + jsonString);
		return "我是post的响应数据";
	}
}
