package com.example.myproject.config.token;

import com.example.myproject.config.invoker.WxUrlProperties;
import com.example.myproject.config.invoker.WxVerifyProperties;
import com.example.myproject.controller.invoker.executor.WxApiInvoker;
import com.example.myproject.exception.WxAccessTokenException;
import com.example.myproject.exception.WxAppException;
import com.example.myproject.module.token.WxAccessToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin  WxTokenServer
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxTokenServer
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:14
 */
public class WxTokenServer {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private WxApiInvoker wxApiInvoker;

    private WxVerifyProperties wxVerifyProperties;

    private WxUrlProperties wxUrlProperties;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    public WxTokenServer(WxApiInvoker wxApiInvoker, WxVerifyProperties wxVerifyProperties, WxUrlProperties wxUrlProperties) {
        this.wxApiInvoker = wxApiInvoker;
        this.wxVerifyProperties = wxVerifyProperties;
        this.wxUrlProperties = wxUrlProperties;
    }

    public WxAccessToken refreshToken() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxUrlProperties.getHost()).path(wxUrlProperties.getRefreshToken())
                .query("grant_type=client_credential")
                .queryParam("appid", wxVerifyProperties.getAppid())
                .queryParam("secret", wxVerifyProperties.getAppsecret());
        String result = wxApiInvoker.getForObject(builder.toUriString(), String.class);
        if (WxAccessTokenException.hasException(result)) {
            throw new WxAccessTokenException(result);
        } else {
            try {
                return jsonConverter.readValue(result, WxAccessToken.class);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new WxAppException("获取Token时转换Json失败");
            }
        }
    }

}
