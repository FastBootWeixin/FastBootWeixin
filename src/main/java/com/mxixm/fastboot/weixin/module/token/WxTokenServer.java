/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.module.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastboot.weixin.exception.WxAccessTokenException;
import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.web.WxWebUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxTokenServer
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @date 2017/7/23 17:14
 * @since 0.1.2
 */
public class WxTokenServer {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private WxApiInvoker wxApiInvoker;

    private WxProperties wxProperties;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    public WxTokenServer(WxApiInvoker wxApiInvoker, WxProperties wxProperties) {
        this.wxApiInvoker = wxApiInvoker;
        this.wxProperties = wxProperties;
    }

    public WxAccessToken refreshToken() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxProperties.getUrl().getHost()).path(wxProperties.getUrl().getRefreshToken())
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", wxProperties.getAppid())
                .queryParam("secret", wxProperties.getAppsecret());
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

    public WxWebUser getWxWebUserByCode(String code) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxProperties.getUrl().getHost()).path(wxProperties.getUrl().getGetUserAccessTokenByCode())
                .queryParam("grant_type", "authorization_code")
                .queryParam("appid", wxProperties.getAppid())
                .queryParam("secret", wxProperties.getAppsecret())
                .queryParam("code", code);
        return getWxWebUserByBuilder(builder);
    }

    public WxWebUser getWxWebUserByRefreshToken(String refreshToken) {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("https").host(wxProperties.getUrl().getHost()).path(wxProperties.getUrl().getGetUserAccessTokenByCode())
                .queryParam("grant_type", "authorization_code")
                .queryParam("appid", wxProperties.getAppid())
                .queryParam("secret", wxProperties.getAppsecret())
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
                .scheme("https").host(wxProperties.getUrl().getHost()).path(wxProperties.getUrl().getGetUserAccessTokenByCode())
                .queryParam("access_token", wxWebUser.getAccessToken())
                .queryParam("openid", wxWebUser.getOpenId())
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
                .scheme("https").host(wxProperties.getUrl().getHost()).path(wxProperties.getUrl().getGetUserAccessTokenByCode())
                .queryParam("access_token", wxWebUser.getAccessToken())
                .queryParam("openid", wxWebUser.getOpenId());
        String result = wxApiInvoker.getForObject(builder.toUriString(), String.class);
        if (WxAccessTokenException.hasException(result)) {
            return false;
        } else {
            return true;
        }
    }

}
