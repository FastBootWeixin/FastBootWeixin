package com.example.myproject.config.server;

import com.example.myproject.config.ApiInvoker.ApiVerifyProperties;
import com.example.myproject.controller.WxVerifyController;
import com.example.myproject.mvc.annotation.WxMappingHandlerMapping;
import com.example.myproject.mvc.param.WxArgumentResolver;
import com.example.myproject.support.DefaultUserProvider;
import com.example.myproject.support.UserProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Configuration
public class BuildinControllerConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final ApiVerifyProperties apiVerifyProperties;

	private final BeanFactory beanFactory;

	public BuildinControllerConfiguration(ApiVerifyProperties apiVerifyProperties, BeanFactory beanFactory) {
		this.apiVerifyProperties = apiVerifyProperties;
		this.beanFactory = beanFactory;
	}

	@Bean
	public WxVerifyController wxVerifyController() {
		return new WxVerifyController(apiVerifyProperties);
	}

	@Bean
	public WxMappingHandlerMapping wxRequestMappingHandlerMapping() {
		WxMappingHandlerMapping wxMappingHandlerMapping = new WxMappingHandlerMapping(wxVerifyController());
		wxMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
		return wxMappingHandlerMapping;
	}

	@Bean
	@ConditionalOnMissingBean
	public UserProvider userProvider() {
		return new DefaultUserProvider();
	}

	@Bean
	public WxMvcConfigurer wxButtonArgumentResolver(UserProvider userProvider) {
		return new WxMvcConfigurer(userProvider, beanFactory);
	}

	public static class WxMvcConfigurer extends WebMvcConfigurerAdapter {

		private HandlerMethodArgumentResolver handlerMethodArgumentResolver;

		public WxMvcConfigurer(UserProvider userProvider, BeanFactory beanFactory) {
			if (beanFactory instanceof ConfigurableBeanFactory) {
				this.handlerMethodArgumentResolver = new WxArgumentResolver((ConfigurableBeanFactory) beanFactory);
			} else {
				this.handlerMethodArgumentResolver = new WxArgumentResolver(userProvider);
			}
		}

		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
			argumentResolvers.add(this.handlerMethodArgumentResolver);
		}

	}

}
