package com.example.myproject.config.invoker;

import com.example.myproject.support.AccessTokenManager;
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
public class WxInvokerTemplate {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private RestTemplate wxInvokerTemplate;

    private WxUrlProperties wxUrlProperties;

    private AccessTokenManager accessTokenManager;

    public WxInvokerTemplate(RestTemplate wxInvokerTemplate, AccessTokenManager accessTokenManager, WxUrlProperties wxUrlProperties) {
        this.wxInvokerTemplate = wxInvokerTemplate;
        this.accessTokenManager = accessTokenManager;
        this.wxUrlProperties = wxUrlProperties;
    }

    private String parseUrl(String rawUrl) {
        return "/" + rawUrl;
    }

    public String getCallbackIp() {
        return this.wxInvokerTemplate.getForObject(parseUrl(wxUrlProperties.getGetCallbackIp()), String.class, accessTokenManager.getToken());
    }

    public String getMenu() {
        return this.wxInvokerTemplate.getForObject(parseUrl(wxUrlProperties.getGetMenu()), String.class, accessTokenManager.getToken());
    }

    public String createMenu(String menuJson) {
        return this.wxInvokerTemplate.postForObject(parseUrl(wxUrlProperties.getCreateMenu()), menuJson, String.class, accessTokenManager.getToken());
    }

}
