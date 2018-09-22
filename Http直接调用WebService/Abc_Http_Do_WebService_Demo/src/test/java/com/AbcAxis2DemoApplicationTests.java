package com;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbcAxis2DemoApplicationTests {

	@Test
	public void test() throws XMLStreamException, ClientProtocolException, IOException {
		// webservice的wsdl地址
		final String wsdlURL = "http://127.0.0.1:9527/webservice/test?wsdl";
		// 设置编码。(因为是直接传的xml,所以我们设置为text/xml;charset=utf8)
		final String contentType = "text/xml;charset=utf8";
		
	    /// 拼接要传递的xml数据(注意:此xml数据的模板我们根据wsdlURL从SoapUI中获得,只需要修改对应的变量值即可)
		String name = "邓沙利文";
		Integer age = 24;
		String motto = "一杆清台!";
		StringBuffer xMLcontent = new StringBuffer("");
		xMLcontent.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap"
				+ "/envelope/\" xmlns:ser=\"http://server.aspire.com/\">\n");
		xMLcontent.append("   <soapenv:Header/>\n");
		xMLcontent.append("   <soapenv:Body>\n");
		xMLcontent.append("      <ser:userMethod>\n");
		xMLcontent.append("         <!--Optional:-->\n");
		xMLcontent.append("         <name>" + name + "</name>\n");
		xMLcontent.append("         <!--Optional:-->\n");
		xMLcontent.append("         <age>" + age + "</age>\n");
		xMLcontent.append("         <!--Optional:-->\n");
		xMLcontent.append("         <motto>" + motto + "</motto>\n");
		xMLcontent.append("      </ser:userMethod>\n");
		xMLcontent.append("   </soapenv:Body>\n");
		xMLcontent.append("</soapenv:Envelope>");

		// 调用工具类方法发送http请求
		String responseXML = HttpSendUtil.doHttpPostByHttpClient(wsdlURL,contentType, xMLcontent.toString());
		// 当然我们也可以调用这个工具类方法发送http请求
		// String responseXML = HttpSendUtil.doHttpPostByRestTemplate(wsdlURL, contentType, xMLcontent.toString());
		
		// 利用axis2的OMElement,将xml数据转换为OMElement
		OMElement omElement = OMXMLBuilderFactory
				.createOMBuilder(new ByteArrayInputStream(responseXML.getBytes()), "utf-8").getDocumentElement();
		
		// 根据responseXML的数据样式,定位到对应element,然后获得其childElements,遍历
		@SuppressWarnings("unchecked")
		Iterator<OMElement> it = omElement.getFirstElement().getFirstElement().getFirstElement().getChildElements();
		while (it.hasNext()) {
			OMElement element = it.next();
			System.out.println("属性名:" + element.getLocalName() + "\t属性值:" + element.getText());
		}

	}
}

/**
 * HTTP工具类
 *
 * @author JustryDeng
 * @DATE 2018年9月22日 下午10:29:08
 */
class HttpSendUtil {

	/**
	 * 使用apache的HttpClient发送http
	 *
	 * @param wsdlURL
	 *            请求URL
	 * @param contentType
	 *            如:application/json;charset=utf8
	 * @param content
	 *            数据内容
	 * @DATE 2018年9月22日 下午10:29:17
	 */
	static String doHttpPostByHttpClient(final String wsdlURL, final String contentType, final String content)
			throws ClientProtocolException, IOException {
		// 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		// 创建Post请求
		HttpPost httpPost = new HttpPost(wsdlURL);
		StringEntity entity = new StringEntity(content.toString(), "UTF-8");
		// 将数据放入entity中
		httpPost.setEntity(entity);
		httpPost.setHeader("Content-Type", contentType);
		// 响应模型
		CloseableHttpResponse response = null;
		String result = null;
		try {
			// 由客户端执行(发送)Post请求
			response = httpClient.execute(httpPost);
			// 从响应模型中获取响应实体
			// 注意:和doHttpPostByRestTemplate方法用的不是同一个HttpEntity
			org.apache.http.HttpEntity responseEntity = response.getEntity();
			System.out.println("响应ContentType为:" + responseEntity.getContentType());
			System.out.println("响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				result = EntityUtils.toString(responseEntity);
				System.out.println("响应内容为:" + result);
			}
		} finally {
			// 释放资源
			if (httpClient != null) {
				httpClient.close();
			}
			if (response != null) {
				response.close();
			}
		}
		return result;
	}

	/**
	 * 使用springframework的RestTemplate发送http
	 *
	 * @param wsdlURL
	 *            请求URL
	 * @param contentType
	 *            如:application/json;charset=utf8
	 * @param content
	 *            数据内容
	 * @DATE 2018年9月22日 下午10:30:48
	 */
	static String doHttpPostByRestTemplate(final String wsdlURL, final String contentType, final String content) {
		// http使用无参构造;https需要使用有参构造
		RestTemplate restTemplate = new RestTemplate();
		// 解决中文乱码
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		converterList.remove(1);
		HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		converterList.add(1, converter);
		restTemplate.setMessageConverters(converterList);
		// 设置Content-Type
		HttpHeaders headers = new HttpHeaders();
		headers.remove("Content-Type");
		headers.add("Content-Type", contentType);
		// 数据信息封装
		// 注意:和doHttpPostByHttpClient方法用的不是同一个HttpEntity
		org.springframework.http.HttpEntity<String> formEntity = new org.springframework.http.HttpEntity<String>(
				content, headers);
		String result = restTemplate.postForObject(wsdlURL, formEntity, String.class);
		return result;
	}
}
