/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myproject.config.ApiInvoker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.lang.invoke.MethodHandles;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnClass(RestTemplate.class)
public class ApiInvokerConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final ObjectProvider<HttpMessageConverters> messageConverters;

	private final ApiInvokerProperties apiInvokerProperties;

	private final ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers;

	public ApiInvokerConfiguration(
			ApiInvokerProperties apiInvokerProperties,
			ObjectProvider<HttpMessageConverters> messageConverters,
			ObjectProvider<List<RestTemplateCustomizer>> restTemplateCustomizers) {
		this.apiInvokerProperties = apiInvokerProperties;
		this.messageConverters = messageConverters;
		this.restTemplateCustomizers = restTemplateCustomizers;
	}

	/**
	 * 是否有必要模仿Spring不提供RestTemplate，只提供RestTemplateBuilder
	 * @return
     */
	@Bean
	@ConditionalOnMissingBean
	public RestTemplate apiInvokeRestTemplate() {
		RestTemplateBuilder builder = new RestTemplateBuilder();
		// 关闭自动检查RequestFactory
		builder.detectRequestFactory(false).requestFactory(getClientHttpRequestFactory())
		.errorHandler(new DefaultResponseErrorHandler());
		HttpMessageConverters converters = this.messageConverters.getIfUnique();
		if (converters != null) {
			builder = builder.messageConverters(converters.getConverters());
		}
		List<RestTemplateCustomizer> customizers = this.restTemplateCustomizers
				.getIfAvailable();
		if (!CollectionUtils.isEmpty(customizers)) {
			customizers = new ArrayList<>(customizers);
			AnnotationAwareOrderComparator.sort(customizers);
			builder = builder.customizers(customizers);
		}
		return builder.build();
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		// 长连接保持30秒
		PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(apiInvokerProperties.getTimeToLive(), TimeUnit.SECONDS);
		// 总连接数
		pollingConnectionManager.setMaxTotal(apiInvokerProperties.getMaxTotal());
		// 同路由的并发数
		pollingConnectionManager.setDefaultMaxPerRoute(apiInvokerProperties.getMaxPerRoute());
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setConnectionManager(pollingConnectionManager);
		// 重试次数，默认是2次，没有开启
		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(apiInvokerProperties.getRetryCount(), apiInvokerProperties.isRequestSentRetryEnabled()));
		// 保持长连接配置，需要在头添加Keep-Alive
		httpClientBuilder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
		headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
		headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6"));
		headers.add(new BasicHeader("Connection", "keep-alive"));
		httpClientBuilder.setDefaultHeaders(headers);
		HttpClient httpClient = httpClientBuilder.build();
		// httpClient连接配置，底层是配置RequestConfig
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		// 连接超时
		clientHttpRequestFactory.setConnectTimeout(apiInvokerProperties.getConnectTimeout());
		// 数据读取超时时间，即SocketTimeout
		clientHttpRequestFactory.setReadTimeout(apiInvokerProperties.getReadTimeout());
		// 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
		clientHttpRequestFactory.setConnectionRequestTimeout(apiInvokerProperties.getConnectionRequestTimeout());
		return clientHttpRequestFactory;
	}

	private HttpClient getHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		// 长连接保持30秒
		PoolingHttpClientConnectionManager pollingConnectionManager;
		if (apiInvokerProperties.isEnableHttps()) {
			SSLContext sslContext;
			try {
				sslContext = new SSLContextBuilder().loadTrustMaterial(null, (x509Certificates, s) -> true).build();
			} catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
				throw new IllegalArgumentException("初始化HTTPS失败");
			}
			builder.setSSLContext(sslContext);
			// don't check Hostnames, either.
			//      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			// here's the special part:
			//      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
			//      -- and create a Registry, to register it.
			SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslSocketFactory)
					.build();
			// now, we create connection-manager using our Registry.
			//      -- allows multi-threaded use
			pollingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, null, null, null, apiInvokerProperties.getTimeToLive(), TimeUnit.SECONDS);
		} else {
			pollingConnectionManager = new PoolingHttpClientConnectionManager(apiInvokerProperties.getTimeToLive(), TimeUnit.SECONDS);
		}
		// 总连接数
		pollingConnectionManager.setMaxTotal(apiInvokerProperties.getMaxTotal());
		// 同路由的并发数
		pollingConnectionManager.setDefaultMaxPerRoute(apiInvokerProperties.getMaxPerRoute());
		builder.setConnectionManager(pollingConnectionManager);
		// 重试次数，默认是2次，没有开启
		builder.setRetryHandler(new DefaultHttpRequestRetryHandler(apiInvokerProperties.getRetryCount(), apiInvokerProperties.isRequestSentRetryEnabled()));
		// 保持长连接配置，需要在头添加Keep-Alive
		builder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
		headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
		headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6"));
		headers.add(new BasicHeader("Connection", "keep-alive"));
		builder.setDefaultHeaders(headers);

		CloseableHttpClient client = builder.setConnectionManager(pollingConnectionManager).build();

		return client;
	}

}
