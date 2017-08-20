package com.example.myproject.config.media;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.controller.invoker.executor.WxApiInvoker;
import com.example.myproject.module.media.WxMediaManager;
import com.example.myproject.module.media.WxMediaStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

@Configuration
public class WxMediaConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final WxApiInvokeSpi wxApiInvokeSpi;

	private final WxApiInvoker wxApiInvoker;

	public WxMediaConfiguration(WxApiInvokeSpi wxApiInvokeSpi, WxApiInvoker wxApiInvoker) {
		this.wxApiInvokeSpi = wxApiInvokeSpi;
		this.wxApiInvoker = wxApiInvoker;
	}

	@Bean
	@ConditionalOnMissingBean
	public WxMediaStore wxMediaStore() {
		return new WxMediaStore();
	}

	@Bean
	public WxMediaManager wxMediaManager() {
		return new WxMediaManager(wxApiInvokeSpi, wxApiInvoker, wxMediaStore());
	}

}
