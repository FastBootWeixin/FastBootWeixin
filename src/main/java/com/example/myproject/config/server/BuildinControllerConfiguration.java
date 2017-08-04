package com.example.myproject.config.server;

import com.example.myproject.config.ApiInvoker.ApiVerifyProperties;
import com.example.myproject.controller.WxVerifyController;
import com.example.myproject.mvc.annotation.WxMappingHandlerMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.lang.invoke.MethodHandles;

@Configuration
public class BuildinControllerConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final ApiVerifyProperties apiVerifyProperties;

	public BuildinControllerConfiguration(ApiVerifyProperties apiVerifyProperties) {
		this.apiVerifyProperties = apiVerifyProperties;
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

}
