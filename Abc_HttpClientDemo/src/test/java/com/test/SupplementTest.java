package com.test;

import com.AbcHttpClientDemoApplication;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

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

}