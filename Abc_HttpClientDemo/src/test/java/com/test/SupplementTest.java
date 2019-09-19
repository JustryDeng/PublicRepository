package com.test;

import com.AbcHttpClientDemoApplication;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 知识补充
 *
 * @date 2019年9月18日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbcHttpClientDemoApplication.class })
public class SupplementTest {


	/**
	 * 防止响应乱码
	 */
	@Test
	public void test1() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet("http://localhost:12345/doGetControllerOne");
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			HttpEntity responseEntity = response.getEntity();
			System.out.println("响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("响应内容长度为:" + responseEntity.getContentLength());
				// 主动设置编码，来防止响应乱码
				String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
				System.out.println("响应内容为:" + responseStr);
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/// ------------------------------------------------------------------------ 分割线
	
	/**
	 * 发送HTTPS请求
	 */
	@Test
	public void test2() {
		CloseableHttpClient httpClient = getHttpClient(true);
		HttpGet httpGet = new HttpGet("https://localhost:8484/doGetControllerOne");
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			HttpEntity responseEntity = response.getEntity();
			System.out.println("HTTPS响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("HTTPS响应内容长度为:" + responseEntity.getContentLength());
				// 主动设置编码，来防止响应乱码
				String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
				System.out.println("HTTPS响应内容为:" + responseStr);
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据是否是https请求，获取HttpClient客户端
	 *
	 * TODO 本人这里没有进行完美封装。对于 校不校验校验证书的选择，本人这里是写死
	 *      在代码里面的，你们再使用时，可以灵活二次封装。
	 *
	 * 提示: 此工具类的封装、相关客户端、服务端证书的生成，可参考我的这篇博客:
	 *      <linked>https://blog.csdn.net/justry_deng/article/details/91569132</linked>
	 *
	 *
	 * @param isHttps 是否是HTTPS请求
	 *
	 * @return  HttpClient实例
	 * @date 2019/9/18 17:57
	 */
	private CloseableHttpClient getHttpClient(boolean isHttps) {
		CloseableHttpClient httpClient;
		if (isHttps) {
			SSLConnectionSocketFactory sslSocketFactory;
			try {
				/// 如果不作证书校验的话
				sslSocketFactory = getSocketFactory(false, null, null);

				/// 如果需要证书检验的话
				// 证书
				//InputStream ca = this.getClass().getClassLoader().getResourceAsStream("client/ds.crt");
				// 证书的别名，即:key。 注:cAalias只需要保证唯一即可，不过推荐使用生成keystore时使用的别名。
				// String cAalias = System.currentTimeMillis() + "" + new SecureRandom().nextInt(1000);
				//sslSocketFactory = getSocketFactory(true, ca, cAalias);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			httpClient = HttpClientBuilder.create().setSSLSocketFactory(sslSocketFactory).build();
			return httpClient;
		}
		httpClient = HttpClientBuilder.create().build();
		return httpClient;
	}

	/**
	 * HTTPS辅助方法, 为HTTPS请求 创建SSLSocketFactory实例、TrustManager实例
	 *
	 * @param needVerifyCa
	 *         是否需要检验CA证书(即:是否需要检验服务器的身份)
	 * @param caInputStream
	 *         CA证书。(若不需要检验证书，那么此处传null即可)
	 * @param cAalias
	 *         别名。(若不需要检验证书，那么此处传null即可)
	 *         注意:别名应该是唯一的， 别名不要和其他的别名一样，否者会覆盖之前的相同别名的证书信息。别名即key-value中的key。
	 *
	 * @return SSLConnectionSocketFactory实例
	 * @throws NoSuchAlgorithmException
	 *         异常信息
	 * @throws CertificateException
	 *         异常信息
	 * @throws KeyStoreException
	 *         异常信息
	 * @throws IOException
	 *         异常信息
	 * @throws KeyManagementException
	 *         异常信息
	 * @date 2019/6/11 19:52
	 */
	private static SSLConnectionSocketFactory getSocketFactory(boolean needVerifyCa, InputStream caInputStream, String cAalias)
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
			IOException, KeyManagementException {
		X509TrustManager x509TrustManager;

		// https请求，需要校验证书
		if (needVerifyCa) {
			KeyStore keyStore = getKeyStore(caInputStream, cAalias);
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
				throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
			}
			x509TrustManager = (X509TrustManager) trustManagers[0];
			// 这里传TLS或SSL其实都可以的
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
			return new SSLConnectionSocketFactory(sslContext);
		}
		// https请求，不作证书校验
		x509TrustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
				// 不验证
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
		return new SSLConnectionSocketFactory(sslContext);
	}

	/**
	 * 获取(密钥及证书)仓库
	 * 注:该仓库用于存放 密钥以及证书
	 *
	 * @param caInputStream
	 *         CA证书(此证书应由要访问的服务端提供)
	 * @param cAalias
	 *         别名
	 *         注意:别名应该是唯一的， 别名不要和其他的别名一样，否者会覆盖之前的相同别名的证书信息。别名即key-value中的key。
	 * @return 密钥、证书 仓库
	 * @throws KeyStoreException 异常信息
	 * @throws CertificateException 异常信息
	 * @throws IOException 异常信息
	 * @throws NoSuchAlgorithmException 异常信息
	 * @date 2019/6/11 18:48
	 */
	private static KeyStore getKeyStore(InputStream caInputStream, String cAalias)
			throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		// 证书工厂
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		// 秘钥仓库
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(null);
		keyStore.setCertificateEntry(cAalias, certificateFactory.generateCertificate(caInputStream));
		return keyStore;
	}

	/// ------------------------------------------------------------------------ 分割线

	/**
	 * application/x-www-form-urlencoded 请求(即:表单请求，表单数据编码为键值对)
	 *
	 * 注:multipart/form-data也属于表单请求。不过其将表单数据编码为了一条消息，每个控件对应消息的一部分。
	 *
	 * 注:没有文件的话，用默认的application/x-www-form-urlencoded就行;
	 *    有文件的话，要用multipart/form-data进行编码编码。
	 *
	 */
	@Test
	public void test3() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://localhost:12345/form/data");
		CloseableHttpResponse response = null;
		try {
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

			List<BasicNameValuePair> params = new ArrayList<>(2);
			params.add(new BasicNameValuePair("name", "邓沙利文"));
			params.add(new BasicNameValuePair("age", "25"));
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
			httpPost.setEntity(formEntity);

			response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			System.out.println("HTTPS响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("HTTPS响应内容长度为:" + responseEntity.getContentLength());
				// 主动设置编码，来防止响应乱码
				String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
				System.out.println("HTTPS响应内容为:" + responseStr);
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/// ------------------------------------------------------------------------ 分割线

	/**
	 *
	 * 发送文件
	 *
	 * multipart/form-data传递文件(及相关信息)
	 *
	 * 注:如果想要灵活方便的传输文件的话，
	 *    除了引入org.apache.httpcomponents基本的httpclient依赖外
	 *    在额外引入org.apache.httpcomponents的httpmime依赖。
	 *    追注:即便不引入httpmime依赖，也是能传输文件的，不过功能不够强大。
	 *
	 */
	@Test
	public void test4() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://localhost:12345/file");
		CloseableHttpResponse response = null;
		try {
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			// 第一个文件
			String filesKey = "files";
			File file1 = new File("C:\\Users\\JustryDeng\\Desktop\\back.jpg");
			multipartEntityBuilder.addBinaryBody(filesKey, file1);
			// 第二个文件(多个文件的话，使用可一个key就行，后端用数组或集合进行接收即可)
			File file2 = new File("C:\\Users\\JustryDeng\\Desktop\\头像.jpg");
			// 防止服务端收到的文件名乱码。 我们这里可以先将文件名URLEncode，然后服务端拿到文件名时在URLDecode。就能避免乱码问题。
			// 文件名其实是放在请求头的Content-Disposition里面进行传输的，如其值为form-data; name="files"; filename="头像.jpg"
			multipartEntityBuilder.addBinaryBody(filesKey, file2, ContentType.DEFAULT_BINARY, URLEncoder.encode(file2.getName(), "utf-8"));
			// 其它参数(注:自定义contentType，设置UTF-8是为了防止服务端拿到的参数出现乱码)
			ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
			multipartEntityBuilder.addTextBody("name", "邓沙利文", contentType);
			multipartEntityBuilder.addTextBody("age", "25", contentType);

			HttpEntity httpEntity = multipartEntityBuilder.build();
			httpPost.setEntity(httpEntity);

			response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			System.out.println("HTTPS响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("HTTPS响应内容长度为:" + responseEntity.getContentLength());
				// 主动设置编码，来防止响应乱码
				String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
				System.out.println("HTTPS响应内容为:" + responseStr);
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/// ------------------------------------------------------------------------ 分割线

	/**
	 *
	 * 发送流
	 *
	 */
	@Test
	public void test5() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://localhost:12345/is?name=邓沙利文");
		CloseableHttpResponse response = null;
		try {
			InputStream is = new ByteArrayInputStream("流啊流~".getBytes());
			InputStreamEntity ise = new InputStreamEntity(is);
			httpPost.setEntity(ise);

			response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			System.out.println("HTTPS响应状态为:" + response.getStatusLine());
			if (responseEntity != null) {
				System.out.println("HTTPS响应内容长度为:" + responseEntity.getContentLength());
				// 主动设置编码，来防止响应乱码
				String responseStr = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
				System.out.println("HTTPS响应内容为:" + responseStr);
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (httpClient != null) {
					httpClient.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}