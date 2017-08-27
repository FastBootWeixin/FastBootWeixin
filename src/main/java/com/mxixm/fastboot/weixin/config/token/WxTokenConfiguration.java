package com.mxixm.fastboot.weixin.config.token;

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastboot.weixin.module.token.WxTokenServer;
import com.mxixm.fastboot.weixin.support.MemoryWxTokenStore;
import com.mxixm.fastboot.weixin.support.WxAccessTokenManager;
import com.mxixm.fastboot.weixin.support.WxTokenStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

@Configuration
public class WxTokenConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final WxProperties wxProperties;

	public WxTokenConfiguration(
			WxProperties wxProperties) {
		this.wxProperties = wxProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public WxTokenStore wxTokenStore() {
		return new MemoryWxTokenStore();
	}

	@Bean
	public WxTokenServer wxTokenServer(WxApiInvoker wxApiInvoker) {
		return new WxTokenServer(wxApiInvoker, wxProperties);
	}

	@Bean
	public WxAccessTokenManager wxAccessTokenManager(WxTokenServer wxTokenServer, WxTokenStore wxTokenStore) {
		return new WxAccessTokenManager(wxTokenServer, wxTokenStore);
	}

}
