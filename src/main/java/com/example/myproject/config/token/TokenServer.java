package com.example.myproject.config.token;

import com.example.myproject.config.invoker.WxUrlProperties;
import com.example.myproject.config.invoker.WxVerifyProperties;
import com.example.myproject.module.token.AccessToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin  WxInvokerTemplate
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxInvokerTemplate
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:14
 */
public class TokenServer {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private RestTemplate apiInvokerRestTemplate;

    private WxVerifyProperties wxVerifyProperties;

    private WxUrlProperties wxUrlProperties;

    public TokenServer(RestTemplate apiInvokerRestTemplate, WxVerifyProperties wxVerifyProperties, WxUrlProperties wxUrlProperties) {
        this.apiInvokerRestTemplate = apiInvokerRestTemplate;
        this.wxVerifyProperties = wxVerifyProperties;
        this.wxUrlProperties = wxUrlProperties;
    }

    private String parseUrl(String rawUrl) {
        return "/" + rawUrl;
    }

    public AccessToken refreshToken() {
        return apiInvokerRestTemplate.getForObject(parseUrl(wxUrlProperties.getRefreshToken()), AccessToken.class,
                wxVerifyProperties.getAppid(), wxVerifyProperties.getAppsecret());
    }

}
