package com.example.myproject.config.Token;

import com.example.myproject.config.ApiInvoker.ApiUrlProperties;
import com.example.myproject.config.ApiInvoker.ApiVerifyProperties;
import com.example.myproject.module.token.AccessToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin  ApiInvoker
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @summary FastBootWeixin  ApiInvoker
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:14
 */
public class TokenServer {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private RestTemplate apiInvokerRestTemplate;

    private ApiVerifyProperties apiVerifyProperties;

    private ApiUrlProperties apiUrlProperties;

    public TokenServer(RestTemplate apiInvokerRestTemplate, ApiVerifyProperties apiVerifyProperties, ApiUrlProperties apiUrlProperties) {
        this.apiInvokerRestTemplate = apiInvokerRestTemplate;
        this.apiVerifyProperties = apiVerifyProperties;
        this.apiUrlProperties = apiUrlProperties;
    }

    private String parseUrl(String rawUrl) {
        return "/" + rawUrl;
    }

    public AccessToken refreshToken() {
        return apiInvokerRestTemplate.getForObject(parseUrl(apiUrlProperties.getRefreshToken()), AccessToken.class,
                apiVerifyProperties.getAppid(), apiVerifyProperties.getAppsecret());
    }

}
