package com.mxixm.fastbootwx.util;

import com.mxixm.fastbootwx.module.Wx;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin  WxUrlUtils
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxUrlUtils
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 23:56
 */
public abstract class WxUrlUtils {

    private static final String WX_OAUTH2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";

    private static final UriComponentsBuilder baseBuilder = UriComponentsBuilder.fromHttpUrl(WX_OAUTH2_URL);

    public static String processMediaUrl(String requestUrl, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (url.startsWith("/") && !StringUtils.isEmpty(requestUrl)) {
            return StringUtils.applyRelativePath(requestUrl, url);
        } else {
            return "http://" + url;
        }
    }

    /**
     * 最后会重定向到redirect_uri/?code=CODE&state=STATE上面
     *
     * @param requestUrl 请求地址
     * @param url 要跳转的地址
     * @param state 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * @param isBase 应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
     *               snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
     * @return
     */
    public static String processRedirectUrl(String requestUrl, String url, String state, boolean isBase) {
        if (url.startsWith(WX_OAUTH2_URL)) {
            return url;
        }
        String redirectUri = processMediaUrl(requestUrl, url);
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
