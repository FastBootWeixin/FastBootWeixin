package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.module.Wx;
import com.sun.jndi.toolkit.url.Uri;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public static final String NO_REDIRECT = "nr:";

    private static final String WX_OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    private static final UriComponentsBuilder baseBuilder = UriComponentsBuilder.fromHttpUrl(WX_OAUTH2_URL);

    /**
     * 跳转到基本的微信认证页面
     *
     * @param url
     * @return
     */
    public static String baseRedirect(String url) {
        return BASE_REDIRECT + url;
    }

    /**
     * 跳转到带认证的微信认证页面
     *
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

        String redirectUri = WxUrlUtils.mediaUrl(baseUrl, url);
        if (!isRedirect || !isCallback(baseUrl, url)) {
            return redirectUri;
        }
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

    /**
     * 判断是否是回调地址
     *
     * @return
     */
    private static boolean isCallback(String baseUrl, String url) {
        try {
            String callbackUrlHost = null;
            String urlHost = new URI(url).getHost();
            String callbackUrl = Wx.Environment.instance().getCallbackUrl();
            if (!StringUtils.isEmpty(callbackUrl)) {
                callbackUrlHost = new URI(callbackUrl).getHost();
            } else if (!StringUtils.isEmpty(baseUrl)) {
                callbackUrlHost = new URI(baseUrl).getHost();
            } else {
                // 不满足条件，强行返回true
                return true;
            }
            return callbackUrlHost.equals(urlHost);
        } catch (URISyntaxException e) {
            // ignore it
        }
        return true;
    }

}
