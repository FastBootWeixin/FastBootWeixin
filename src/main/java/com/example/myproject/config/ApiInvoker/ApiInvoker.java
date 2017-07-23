package com.example.myproject.config.ApiInvoker;

import com.example.myproject.module.token.AccessToken;
import com.example.myproject.support.AccessTokenManager;
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
public class ApiInvoker {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private RestTemplate apiInvokerRestTemplate;

    private ApiVerifyProperties apiVerifyProperties;

    private ApiUrlProperties apiUrlProperties;

    private AccessTokenManager accessTokenManager;

    public ApiInvoker(RestTemplate apiInvokerRestTemplate, AccessTokenManager accessTokenManager, ApiUrlProperties apiUrlProperties) {
        this.apiInvokerRestTemplate = apiInvokerRestTemplate;
        this.accessTokenManager = accessTokenManager;
        this.apiUrlProperties = apiUrlProperties;
    }

    private String parseUrl(String rawUrl) {
        return "/" + rawUrl;
    }

    public String getCallbackIp() {
        return this.apiInvokerRestTemplate.getForObject(parseUrl(apiUrlProperties.getGetCallbackIp()), String.class, accessTokenManager.getToken());
    }

}
