package com.example.myproject.config.server;

import com.example.myproject.config.invoker.WxVerifyProperties;
import com.example.myproject.controller.WxVerifyController;
import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.controller.invoker.common.WxMediaResourceMessageConverter;
import com.example.myproject.mvc.advice.WxResponseBodyAdvice;
import com.example.myproject.mvc.annotation.WxMappingHandlerMapping;
import com.example.myproject.mvc.param.WxArgumentResolver;
import com.example.myproject.support.DefaultWxUserProvider;
import com.example.myproject.support.WxUserProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Configuration
public class WxBuildinMvcConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final WxVerifyProperties wxVerifyProperties;

	private final BeanFactory beanFactory;

	public WxBuildinMvcConfiguration(WxVerifyProperties wxVerifyProperties, BeanFactory beanFactory) {
		this.wxVerifyProperties = wxVerifyProperties;
		this.beanFactory = beanFactory;
	}

	@Bean
	public WxVerifyController wxVerifyController() {
		return new WxVerifyController(wxVerifyProperties);
	}

	@Bean
	public WxMappingHandlerMapping wxRequestMappingHandlerMapping() {
		WxMappingHandlerMapping wxMappingHandlerMapping = new WxMappingHandlerMapping(wxVerifyController());
		wxMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
		return wxMappingHandlerMapping;
	}

//	@Bean
//	@ConditionalOnMissingBean
//	public WxUserProvider userProvider(WxApiInvokeSpi wxApiInvokeSpi) {
//		return new DefaultWxUserProvider(wxApiInvokeSpi);
//	}
//	@Bean
//	public WxUserProvider userProvider() {
//		return new DefaultWxUserProvider();
//	}

	@Bean
	public WxMvcConfigurer wxMvcConfigurer(WxUserProvider wxUserProvider) {
		return new WxMvcConfigurer(wxUserProvider, beanFactory);
	}

	@Bean
	public WxResponseBodyAdvice wxResponseBodyAdvice() {
		return new WxResponseBodyAdvice();
	}

	public static class WxMvcConfigurer extends WebMvcConfigurerAdapter {

		private HandlerMethodArgumentResolver handlerMethodArgumentResolver;

		/**
		 * 之前这里产生循环依赖，因为ConversionService是这个里面生成的，而conversionService又被WxApiExecutor依赖
		 * WxApiExecutor -> WxApiInvokeSpi -> WxUserProvider -> WxMvcConfigurer -> ConversionService -> WxAPIExecutor
		 * 于是产生了循环依赖
		 * 临时处理先把ConversionService的依赖去掉，后期考虑优化依赖关系
		 * @param wxUserProvider
		 * @param beanFactory
         */
		public WxMvcConfigurer(WxUserProvider wxUserProvider, BeanFactory beanFactory) {
			if (beanFactory instanceof ConfigurableBeanFactory) {
				this.handlerMethodArgumentResolver = new WxArgumentResolver((ConfigurableBeanFactory) beanFactory);
			} else {
				this.handlerMethodArgumentResolver = new WxArgumentResolver(wxUserProvider);
			}
		}

		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
			argumentResolvers.add(this.handlerMethodArgumentResolver);
		}

		@Override
		public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			converters.add(new WxMediaResourceMessageConverter());
		}
	}

}
