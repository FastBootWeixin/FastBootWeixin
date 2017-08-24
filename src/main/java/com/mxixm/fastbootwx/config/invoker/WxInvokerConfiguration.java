package com.mxixm.fastbootwx.config.invoker;

import com.mxixm.fastbootwx.common.WxBeanNames;
import com.mxixm.fastbootwx.config.token.WxTokenServer;
import com.mxixm.fastbootwx.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastbootwx.controller.invoker.WxInvokerProxyFactory;
import com.mxixm.fastbootwx.controller.invoker.common.WxHttpInputMessageConverter;
import com.mxixm.fastbootwx.controller.invoker.component.WxApiHttpRequestFactory;
import com.mxixm.fastbootwx.controller.invoker.executor.WxApiExecutor;
import com.mxixm.fastbootwx.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastbootwx.controller.invoker.handler.WxResponseErrorHandler;
import com.mxixm.fastbootwx.support.WxAccessTokenManager;
import com.mxixm.fastbootwx.support.DefaultWxUserProvider;
import com.mxixm.fastbootwx.support.WxUserProvider;
import com.mxixm.fastbootwx.util.WxApplicationContextUtils;
import com.mxixm.fastbootwx.web.WxUserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties({WxVerifyProperties.class, WxInvokerProperties.class, WxUrlProperties.class})
@ConditionalOnClass(RestTemplate.class)
@PropertySource("classpath:/wx.properties")
public class WxInvokerConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final WxInvokerProperties wxInvokerProperties;

	private final ObjectProvider<HttpMessageConverters> messageConverters;

	public WxInvokerConfiguration(
			WxInvokerProperties wxInvokerProperties,
			ObjectProvider<HttpMessageConverters> messageConverters) {
		this.wxInvokerProperties = wxInvokerProperties;
		this.messageConverters = messageConverters;
	}

	@Bean
	public WxApplicationContextUtils wxApplicationContextUtils() {
		return new WxApplicationContextUtils();
	}

	/**
	 * 是否有必要模仿Spring不提供RestTemplate，只提供RestTemplateBuilder
	 * @return
     */
	@Bean(name = WxBeanNames.WX_API_INVOKER_NAME)
	public WxApiInvoker wxApiInvoker() {
		RestTemplateBuilder builder = new RestTemplateBuilder();
		builder = builder.requestFactory(new WxApiHttpRequestFactory(wxInvokerProperties))
				.errorHandler(new WxResponseErrorHandler());
		HttpMessageConverters converters = this.messageConverters.getIfUnique();
		List<HttpMessageConverter<?>> converterList = new ArrayList<>();
		// 加入默认转换
		converterList.add(new WxHttpInputMessageConverter());
		if (converters != null) {
			converterList.addAll(converters.getConverters());
			builder = builder.messageConverters(Collections.unmodifiableList(converterList));
		}
		return new WxApiInvoker(builder.build());
	}

	/*
		┌─────┐
	|  wxInvokerProxyFactory defined in class path resource [com/example/myproject/config/invoker/WxInvokerConfiguration.class]
	↑     ↓
	|  wxApiExecutor defined in class path resource [com/example/myproject/config/invoker/WxInvokerConfiguration.class]
	↑     ↓
	|  org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration$EnableWebMvcConfiguration
	↑     ↓
	|  wxButtonArgumentResolver defined in class path resource [com/example/myproject/config/server/WxBuildinMvcConfiguration.class]
	↑     ↓
	|  defaultWxUserProvider (field private com.example.myproject.controller.invoker.WxApiInvokeSpi com.example.myproject.support.DefaultWxUserProvider.wxApiInvokeSpi)
	└─────┘
	 */
	/**
	 * 这里之前引用了conversionService，这个conversionService是在WxMvcConfigurer时初始化的
	 * 于是产生了循环依赖
	 * @param wxAccessTokenManager
	 * @return
	 */
	@Bean
	public WxApiExecutor wxApiExecutor(WxAccessTokenManager wxAccessTokenManager) {
		return new WxApiExecutor(wxApiInvoker(), wxAccessTokenManager);
	}

	@Bean
	public WxInvokerProxyFactory<WxApiInvokeSpi> wxInvokerProxyFactory(WxUrlProperties wxUrlProperties, WxApiExecutor wxApiExecutor) {
		return new WxInvokerProxyFactory(WxApiInvokeSpi.class, wxUrlProperties, wxApiExecutor);
	}


	@Bean
	@ConditionalOnMissingBean
	public WxUserProvider userProvider(WxUserManager wxUserManager) {
		return new DefaultWxUserProvider(wxUserManager);
	}

	@Bean
	public WxUserManager wxUserManager(@Lazy WxTokenServer wxTokenServer, @Lazy WxApiInvokeSpi wxApiInvokeSpi) {
		return new WxUserManager(wxTokenServer, wxApiInvokeSpi);
	}

	/**
	 * 只考虑微信的消息转换，后期可以优化
	 * 其实这里完全可以使用系统的Bean，但是这里我想特殊处理，只对微信消息做转换，所以定制化了几个converter
	 * @return
	 */
	private HttpMessageConverters getDefaultWxMessageConverters() {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		stringConverter.setWriteAcceptCharset(false);
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		Jaxb2RootElementHttpMessageConverter xmlConverter = new Jaxb2RootElementHttpMessageConverter();
		AllEncompassingFormHttpMessageConverter formConverter = new AllEncompassingFormHttpMessageConverter();
		ResourceHttpMessageConverter resourceConverter = new ResourceHttpMessageConverter();
		HttpMessageConverters wxMessageConverters = new HttpMessageConverters(stringConverter, jsonConverter, xmlConverter, formConverter, resourceConverter);
		return wxMessageConverters;
	}

}
