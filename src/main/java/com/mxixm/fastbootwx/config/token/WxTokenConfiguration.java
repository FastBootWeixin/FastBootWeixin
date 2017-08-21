package com.mxixm.fastbootwx.config.token;

import com.mxixm.fastbootwx.config.invoker.WxUrlProperties;
import com.mxixm.fastbootwx.config.invoker.WxVerifyProperties;
import com.mxixm.fastbootwx.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastbootwx.support.WxAccessTokenManager;
import com.mxixm.fastbootwx.support.MemoryWxTokenStore;
import com.mxixm.fastbootwx.support.WxTokenStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

@Configuration
public class WxTokenConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final WxUrlProperties wxUrlProperties;

	private final WxVerifyProperties wxVerifyProperties;

	public WxTokenConfiguration(
			WxVerifyProperties wxVerifyProperties,
			WxUrlProperties wxUrlProperties) {
		this.wxVerifyProperties = wxVerifyProperties;
		this.wxUrlProperties = wxUrlProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public WxTokenStore wxTokenStore() {
		return new MemoryWxTokenStore();
	}

	@Bean
	public WxTokenServer wxTokenServer(WxApiInvoker wxApiInvoker) {
		return new WxTokenServer(wxApiInvoker, wxVerifyProperties, wxUrlProperties);
	}

	@Bean
	public WxAccessTokenManager wxAccessTokenManager(WxTokenServer wxTokenServer, WxTokenStore wxTokenStore) {
		return new WxAccessTokenManager(wxTokenServer, wxTokenStore);
	}

}
