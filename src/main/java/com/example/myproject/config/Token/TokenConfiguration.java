package com.example.myproject.config.Token;

import com.example.myproject.common.BeanNames;
import com.example.myproject.config.ApiInvoker.ApiInvokerProperties;
import com.example.myproject.config.ApiInvoker.ApiUrlProperties;
import com.example.myproject.config.ApiInvoker.ApiVerifyProperties;
import com.example.myproject.support.AccessTokenManager;
import com.example.myproject.support.MemoryTokenStore;
import com.example.myproject.support.TokenStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

@Configuration
@EnableConfigurationProperties({ApiVerifyProperties.class, ApiInvokerProperties.class, ApiUrlProperties.class})
@ConditionalOnClass(RestTemplate.class)
public class TokenConfiguration {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final RestTemplate apiInvokerRestTemplate;

	private final ApiUrlProperties apiUrlProperties;

	private final ApiVerifyProperties apiVerifyProperties;

	public TokenConfiguration(
			@Autowired @Qualifier(BeanNames.API_INVOKER_REST_TEMPLATE_NAME) RestTemplate apiInvokerRestTemplate,
			ApiVerifyProperties apiVerifyProperties,
			ApiUrlProperties apiUrlProperties) {
		this.apiInvokerRestTemplate = apiInvokerRestTemplate;
		this.apiVerifyProperties = apiVerifyProperties;
		this.apiUrlProperties = apiUrlProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public TokenStore tokenStore() {
		return new MemoryTokenStore();
	}

	@Bean
	public TokenServer tokenServer() {
		return new TokenServer(apiInvokerRestTemplate, apiVerifyProperties, apiUrlProperties);
	}

	@Bean
	public AccessTokenManager accessTokenManager(TokenStore tokenStore) {
		return new AccessTokenManager(tokenServer(), tokenStore);
	}

}
