package com.aspire.util;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
 
import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * 声明:此代码摘录自https://blog.csdn.net/wltsysterm/article/details/80977455
 * 声明:关于Socket的相关知识,本人会在后面的闲暇时间进行学习整理,请持续关注博客更新
 *
 * @author JustryDeng
 * @DATE 2018年9月8日 下午4:34:02
 */
public class HttpsClientRequestFactory extends SimpleClientHttpRequestFactory {
	
    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) {
        try {
            if (!(connection instanceof HttpsURLConnection)) {
                throw new RuntimeException("An instance of HttpsURLConnection is expected");
            }
 
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
 
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
 
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            httpsConnection.setSSLSocketFactory(new MyCustomSSLSocketFactory(sslContext.getSocketFactory()));
 
            httpsConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
 
            super.prepareConnection(httpsConnection, httpMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /**
     * We need to invoke sslSocket.setEnabledProtocols(new String[] {"SSLv3"});
     * see http://www.oracle.com/technetwork/java/javase/documentation/cve-2014-3566-2342133.html (Java 8 section)
     */
    // SSLSocketFactory用于创建 SSLSockets
    private static class MyCustomSSLSocketFactory extends SSLSocketFactory {
 
        private final SSLSocketFactory delegate;
 
        public MyCustomSSLSocketFactory(SSLSocketFactory delegate) {
            this.delegate = delegate;
        }
 
        // 返回默认启用的密码套件。除非一个列表启用，对SSL连接的握手会使用这些密码套件。
        // 这些默认的服务的最低质量要求保密保护和服务器身份验证
        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }
 
        // 返回的密码套件可用于SSL连接启用的名字
        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }
 

        @Override
        public Socket createSocket(final Socket socket, final String host, final int port, 
        		final boolean autoClose) throws IOException {
            final Socket underlyingSocket = delegate.createSocket(socket, host, port, autoClose);
            return overrideProtocol(underlyingSocket);
        }
 

        @Override
        public Socket createSocket(final String host, final int port) throws IOException {
            final Socket underlyingSocket = delegate.createSocket(host, port);
            return overrideProtocol(underlyingSocket);
        }
 
        @Override
        public Socket createSocket(final String host, final int port, final InetAddress localAddress, 
        		final int localPort) throws
                IOException {
            final Socket underlyingSocket = delegate.createSocket(host, port, localAddress, localPort);
            return overrideProtocol(underlyingSocket);
        }
 
        @Override
        public Socket createSocket(final InetAddress host, final int port) throws IOException {
            final Socket underlyingSocket = delegate.createSocket(host, port);
            return overrideProtocol(underlyingSocket);
        }
 
        @Override
        public Socket createSocket(final InetAddress host, final int port, final InetAddress localAddress, 
        		final int localPort) throws
                IOException {
            final Socket underlyingSocket = delegate.createSocket(host, port, localAddress, localPort);
            return overrideProtocol(underlyingSocket);
        }
 
        private Socket overrideProtocol(final Socket socket) {
            if (!(socket instanceof SSLSocket)) {
                throw new RuntimeException("An instance of SSLSocket is expected");
            }
            ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1"});
            return socket;
        }
    }
}

