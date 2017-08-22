package com.mxixm.fastbootwx.config.token;

import com.mxixm.fastbootwx.config.invoker.WxUrlProperties;
import com.mxixm.fastbootwx.config.invoker.WxVerifyProperties;
import com.mxixm.fastbootwx.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastbootwx.exception.WxAccessTokenException;
import com.mxixm.fastbootwx.exception.WxAppException;
import com.mxixm.fastbootwx.module.token.WxAccessToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxixm.fastbootwx.module.user.WxUser;
import com.mxixm.fastbootwx.web.WxWebUser;
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
                .queryParam("grant_type", "client_credential")
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

    public WxWebUser getUserAccessTokenByCode(String code) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxUrlProperties.getHost()).path(wxUrlProperties.getGetUserAccessTokenByCode())
                .queryParam("grant_type", "authorization_code")
                .queryParam("appid", wxVerifyProperties.getAppid())
                .queryParam("secret", wxVerifyProperties.getAppsecret())
                .queryParam("code", code);
        return getWxWebUserByBuilder(builder);
    }

    public WxWebUser getUserAccessTokenByRefreshToken(String refreshToken) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxUrlProperties.getHost()).path(wxUrlProperties.getGetUserAccessTokenByCode())
                .queryParam("grant_type", "authorization_code")
                .queryParam("appid", wxVerifyProperties.getAppid())
                .queryParam("secret", wxVerifyProperties.getAppsecret())
                .queryParam("refresh_token", refreshToken);
        return getWxWebUserByBuilder(builder);
    }

    private WxWebUser getWxWebUserByBuilder(UriComponentsBuilder builder) {
        String result = wxApiInvoker.getForObject(builder.toUriString(), String.class);
        if (WxAccessTokenException.hasException(result)) {
            throw new WxAccessTokenException(result);
        } else {
            try {
                return jsonConverter.readValue(result, WxWebUser.class);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new WxAppException("获取Token时转换Json失败");
            }
        }
    }

    public WxUser getWxUserByWxWebUser(WxWebUser wxWebUser) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxUrlProperties.getHost()).path(wxUrlProperties.getGetUserAccessTokenByCode())
                .queryParam("access_token", wxWebUser.getAccessToken())
                .queryParam("openId", wxWebUser.getOpenId())
                .queryParam("lang", "zh_CN");
        String result = wxApiInvoker.getForObject(builder.toUriString(), String.class);
        if (WxAccessTokenException.hasException(result)) {
            throw new WxAccessTokenException(result);
        } else {
            try {
                return jsonConverter.readValue(result, WxUser.class);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new WxAppException("获取Token时转换Json失败");
            }
        }
    }

    public boolean isVerifyUserAccessToken(WxWebUser wxWebUser) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxUrlProperties.getHost()).path(wxUrlProperties.getGetUserAccessTokenByCode())
                .queryParam("access_token", wxWebUser.getAccessToken())
                .queryParam("openId", wxWebUser.getOpenId());
        String result = wxApiInvoker.getForObject(builder.toUriString(), String.class);
        if (WxAccessTokenException.hasException(result)) {
            return false;
        } else {
            return true;
        }
    }

}
