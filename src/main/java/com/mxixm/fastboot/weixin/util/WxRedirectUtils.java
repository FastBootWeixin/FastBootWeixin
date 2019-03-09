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

package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.module.Wx;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin WxRedirectUtils
 * update： 2019年3月9日 BASE_REDIRECT方式不再推荐使用，因为BASE_REDIRECT会导致未关注用户打开不了页面，认证失败
 * 而snsapi_userinfo则适用于所有用户，且对于已关注用户会静默授权，完全替代BASE_REDIRECT功能，故舍弃BASE的逻辑
 *
 * @author Guangshan
 * @date 2017/8/23 23:43
 * @since 0.1.2
 */
public class WxRedirectUtils {

    public static final String BASE_REDIRECT = "br:";

    public static final String AUTH_REDIRECT = "ar:";

    public static final String NO_REDIRECT = "nr:";

    private static final String WX_OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    private static final UriComponentsBuilder baseBuilder = UriComponentsBuilder.fromHttpUrl(WX_OAUTH2_URL);

    /**
     * 跳转到基本的微信认证页面
     *
     * @param url
     * @return the result
     */
    public static String baseRedirect(String url) {
        return BASE_REDIRECT + url;
    }

    /**
     * 跳转到带认证的微信认证页面
     *
     * @param url
     * @return the result
     */
    public static String authRedirect(String url) {
        return AUTH_REDIRECT + url;
    }

    public static String redirect(String url) {
        return redirect(null, url, null, false);
    }

    public static String redirect(String baseUrl, String url) {
        return redirect(baseUrl, url, null, false);
    }

    public static String redirect(String baseUrl, String url, boolean isBase) {
        return redirect(baseUrl, url, null, isBase);
    }

    public static String redirect(String url, boolean isBase) {
        return redirect(null, url, null, isBase);
    }

    public static String redirect(WxRedirect wxRedirect) {
        return redirect(wxRedirect.getBaseUrl(), wxRedirect.getUrl(), wxRedirect.getState(), wxRedirect.isBase());
    }

    public static String redirect(String baseUrl, String url, String state, boolean isBase) {
        boolean isRedirect = true;
        if (url == null) {
            url = "";
        }
        if (url.startsWith(WX_OAUTH2_URL)) {
            return url;
        } else if (url.startsWith(BASE_REDIRECT)) {
            isBase = true;
            url = url.substring(3);
        } else if (url.startsWith(AUTH_REDIRECT)) {
            isBase = false;
            url = url.substring(3);
        } else if (url.startsWith(NO_REDIRECT)) {
            isRedirect = false;
            url = url.substring(3);
        }

        String redirectUrl = WxUrlUtils.absoluteUrl(baseUrl, url);
        if (!isRedirect || !WxUrlUtils.isCallbackUrl(baseUrl, redirectUrl)) {
            return redirectUrl;
        }
        try {
            redirectUrl = UriUtils.encode(redirectUrl, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            // java.io.UnsupportedEncodingException在SB2.0中不再抛出，故兼容下，处理为Exception
            // ignore it
        }
        return baseBuilder.cloneBuilder().queryParam("appid", Wx.Environment.instance().getWxAppId())
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("response_type", "code")
                .queryParam("scope", isBase ? "snsapi_base" : "snsapi_userinfo")
                .queryParam("state", state).build().toUriString() + "#wechat_redirect";
    }

}
