package com;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.aspire.util.HttpsClientRequestFactory;
import com.google.gson.Gson;

/**
 * 单元测试
 *
 * @author JustryDeng
 * @DATE 2018年9月7日 下午6:37:05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcHttpsTestApplicationTests {

	/**
	 * RestTemplate 发送  HTTP GET请求 --- 测试
	 * @throws UnsupportedEncodingException 
	 *
	 * @date 2018年7月13日 下午4:18:50
	 */
	@Test
	public void doHttpGetTest() throws UnsupportedEncodingException {
		// -------------------------------> 获取Rest客户端实例
		RestTemplate restTemplate = new RestTemplate();
		
		// -------------------------------> 解决(响应数据可能)中文乱码 的问题
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		converterList.remove(1); // 移除原来的转换器
		// 设置字符编码为utf-8
		HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		converterList.add(1, converter); // 添加新的转换器(注:convert顺序错误会导致失败)
		restTemplate.setMessageConverters(converterList);
		
		// -------------------------------> (选择性设置)请求头信息
		// HttpHeaders实现了MultiValueMap接口
		HttpHeaders httpHeaders = new HttpHeaders();
		// 给请求header中添加一些数据
		httpHeaders.add("JustryDeng", "这是一个大帅哥!");
		
		// -------------------------------> 注:GET请求 创建HttpEntity时,请求体传入null即可
		// 请求体的类型任选即可;只要保证 请求体 的类型与HttpEntity类的泛型保持一致即可
		String httpBody = null;
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpBody, httpHeaders);
		
		// -------------------------------> URI
		StringBuffer paramsURL = new StringBuffer("http://127.0.0.1:9527/restTemplate/doHttpGet");
		// 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
		paramsURL.append("?flag=" + URLEncoder.encode("&", "utf-8"));
		URI uri = URI.create(paramsURL.toString());
		
		//  -------------------------------> 执行请求并返回结果
		// 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
		ResponseEntity<String> response = 
				restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
		
		// -------------------------------> 响应信息
		//响应码,如:401、302、404、500、200等
		System.err.println(response.getStatusCodeValue());
		Gson gson = new Gson();
		// 响应头
		System.err.println(gson.toJson(response.getHeaders()));
		// 响应体
		if(response.hasBody()) {
			System.err.println(response.getBody());
		}
		
	}
	
	
	/**
	 * RestTemplate 发送  HTTP POST请求 --- 测试
	 * @throws UnsupportedEncodingException 
	 *
	 * @date 2018年9月8日 下午2:12:50
	 */
	@Test
	public void doHttpPostTest() throws UnsupportedEncodingException {
		// -------------------------------> 获取Rest客户端实例
		RestTemplate restTemplate = new RestTemplate();
		
		// -------------------------------> 解决(响应数据可能)中文乱码 的问题
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		converterList.remove(1); // 移除原来的转换器
		// 设置字符编码为utf-8
		HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		converterList.add(1, converter); // 添加新的转换器(注:convert顺序错误会导致失败)
		restTemplate.setMessageConverters(converterList);
		
		// -------------------------------> (选择性设置)请求头信息
		// HttpHeaders实现了MultiValueMap接口
		HttpHeaders httpHeaders = new HttpHeaders();
		// 设置contentType
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		// 给请求header中添加一些数据
		httpHeaders.add("JustryDeng", "这是一个大帅哥!");
		
		// ------------------------------->将请求头、请求体数据，放入HttpEntity中
		// 请求体的类型任选即可;只要保证 请求体 的类型与HttpEntity类的泛型保持一致即可
		// 这里手写了一个json串作为请求体 数据 (实际开发时,可使用fastjson、gson等工具将数据转化为json串)
		String httpBody = "{\"motto\":\"唉呀妈呀！脑瓜疼！\"}";
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpBody, httpHeaders);
	
		// -------------------------------> URI
		StringBuffer paramsURL = new StringBuffer("http://127.0.0.1:9527/restTemplate/doHttpPost");
		// 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
		paramsURL.append("?flag=" + URLEncoder.encode("&", "utf-8"));
		URI uri = URI.create(paramsURL.toString());
		
		//  -------------------------------> 执行请求并返回结果
		// 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
		ResponseEntity<String> response = 
				restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
		
		// -------------------------------> 响应信息
		//响应码,如:401、302、404、500、200等
		System.err.println(response.getStatusCodeValue());
		Gson gson = new Gson();
		// 响应头
		System.err.println(gson.toJson(response.getHeaders()));
		// 响应体
		if(response.hasBody()) {
			System.err.println(response.getBody());
		}
		
	}
	
	
	/**
	 * RestTemplate 发送  HTTPS GET请求 --- 测试
	 *
	 * @DATE 2018年9月8日 下午4:46:34
	 */
	public static void main(String[] args) {
		// -------------------------------> 获取Rest客户端实例
		RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());
		
		// -------------------------------> 解决(响应数据可能)中文乱码 的问题
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		converterList.remove(1); // 移除原来的转换器
		// 设置字符编码为utf-8
		HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		converterList.add(1, converter); // 添加新的转换器(注:convert顺序错误会导致失败)
		restTemplate.setMessageConverters(converterList);
		
		// -------------------------------> (选择性设置)请求头信息
		// HttpHeaders实现了MultiValueMap接口
		HttpHeaders httpHeaders = new HttpHeaders();
		// 给请求header中添加一些数据
		httpHeaders.add("JustryDeng", "这是一个大帅哥!");
		
		// -------------------------------> 注:GET请求 创建HttpEntity时,请求体传入null即可
		// 请求体的类型任选即可;只要保证 请求体 的类型与HttpEntity类的泛型保持一致即可
		String httpBody = null;
		HttpEntity<String> httpEntity = new HttpEntity<String>(httpBody, httpHeaders);
		
		// -------------------------------> URI
		StringBuffer paramsURL = new StringBuffer("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm");
		// 字符数据最好encoding一下;这样一来，某些特殊字符才能传过去(如:flag的参数值就是“&”,不encoding的话,传不过去)
		paramsURL.append("?tel=" + "13548417409");
		URI uri = URI.create(paramsURL.toString());
		
		//  -------------------------------> 执行请求并返回结果
		// 此处的泛型  对应 响应体数据   类型;即:这里指定响应体的数据装配为String
		ResponseEntity<String> response = 
				restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
		
		// -------------------------------> 响应信息
		//响应码,如:401、302、404、500、200等
		System.err.println(response.getStatusCodeValue());
		Gson gson = new Gson();
		// 响应头
		System.err.println(gson.toJson(response.getHeaders()));
		// 响应体
		if(response.hasBody()) {
			System.err.println(response.getBody());
		}
	}

}
