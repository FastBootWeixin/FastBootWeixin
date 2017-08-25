package com.mxixm.fastboot.weixin.controller.invoker.component;

import com.mxixm.fastboot.weixin.config.invoker.WxInvokerProperties;
import com.mxixm.fastboot.weixin.exception.WxAppException;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * FastBootWeixin  WxApiHttpRequestFactory
 * 装饰器，装饰器，还是装饰器
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiHttpRequestFactory
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/13 16:18
 */
public class WxApiHttpRequestFactory implements ClientHttpRequestFactory {

    private WxInvokerProperties wxInvokerProperties;

    private ClientHttpRequestFactory delegate;

    public WxApiHttpRequestFactory(WxInvokerProperties wxInvokerProperties) {
        this.wxInvokerProperties = wxInvokerProperties;
        this.delegate = getClientHttpRequestFactory();
    }

    /**
     * 获取连接工厂
     * @return
     */
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpClient httpClient = getHttpClient();
        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(wxInvokerProperties.getConnectTimeout());
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(wxInvokerProperties.getReadTimeout());
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(wxInvokerProperties.getConnectionRequestTimeout());
        return clientHttpRequestFactory;
    }

    /**
     * 获取HttpClient，判断是否启用HTTPS
     * @return
     */
    private HttpClient getHttpClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        // 长连接保持30秒
        PoolingHttpClientConnectionManager pollingConnectionManager;
        if (wxInvokerProperties.isEnableHttps()) {
            SSLContext sslContext;
            try {
                sslContext = new SSLContextBuilder().loadTrustMaterial(null, (x509Certificates, s) -> true).build();
            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                throw new WxAppException("初始化HTTPS失败", e);
            }
            builder.setSSLContext(sslContext);
            // don't check Hostnames, either.
            //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            // here's the special part:
            //      -- need to builder an SSL Socket Factory, to use our weakened "trust strategy";
            //      -- and builder a Registry, to register it.
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();
            // now, we builder connection-manager using our Registry.
            //      -- allows multi-threaded use
            pollingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, null, null, null, wxInvokerProperties.getTimeToLive(), TimeUnit.SECONDS);
        } else {
            pollingConnectionManager = new PoolingHttpClientConnectionManager(wxInvokerProperties.getTimeToLive(), TimeUnit.SECONDS);
        }
        // 总连接数
        pollingConnectionManager.setMaxTotal(wxInvokerProperties.getMaxTotal());
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(wxInvokerProperties.getMaxPerRoute());
        builder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是2次，没有开启
        builder.setRetryHandler(new DefaultHttpRequestRetryHandler(wxInvokerProperties.getRetryCount(), wxInvokerProperties.isRequestSentRetryEnabled()));
        // 保持长连接配置，需要在头添加Keep-Alive
        builder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6"));
        headers.add(new BasicHeader("Connection", "keep-alive"));
        builder.setDefaultHeaders(headers);
        return builder.build();
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return new WxApiHttpRequest(this.delegate.createRequest(uri, httpMethod));
    }
}
