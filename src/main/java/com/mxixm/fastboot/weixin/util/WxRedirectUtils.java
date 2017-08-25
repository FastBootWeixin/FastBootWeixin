package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.module.Wx;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin  WxRedirectUtils
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxRedirectUtils
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/23 23:43
 */
public class WxRedirectUtils {

    public static final String BASE_REDIRECT = "br:";

    public static final String AUTH_REDIRECT = "ar:";

    private static final String WX_OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    private static final UriComponentsBuilder baseBuilder = UriComponentsBuilder.fromHttpUrl(WX_OAUTH2_URL);

    /**
     * 跳转到基本的微信认证页面
     * @param url
     * @return
     */
    public static String baseRedirect(String url) {
        return BASE_REDIRECT + url;
    }

    /**
     * 跳转到带认证的微信认证页面
     * @param url
     * @return
     */
    public static String authRedirect(String url) {
        return AUTH_REDIRECT + url;
    }

    public static String redirect(String url) {
        return redirect(null, url, null, true);
    }

    public static String redirect(String baseUrl, String url) {
        return redirect(baseUrl, url, null, true);
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
        if (url == null) {
            url = "";
        }
        if (url.startsWith(BASE_REDIRECT)) {
            isBase = true;
            url = url.substring(3);
        } else if (url.startsWith(AUTH_REDIRECT)) {
            isBase = false;
            url = url.substring(3);
        }
        if (url.startsWith(WX_OAUTH2_URL)) {
            return url;
        }
        String redirectUri = WxUrlUtils.mediaUrl(baseUrl, url);
        try {
            redirectUri = UriUtils.encode(redirectUri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // ignore it
        }
        String finalRedirectUri = baseBuilder.cloneBuilder().queryParam("appid", Wx.Environment.instance().getWxAppId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", isBase ? "snsapi_base" : "snsapi_userinfo")
                .queryParam("state", state).build().toUriString() + "#wechat_redirect";
        return finalRedirectUri;
    }

}
