package com.mxixm.fastboot.weixin.util;

import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * FastBootWeixin  WxUrlUtils
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxUrlUtils
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 23:56
 */
public abstract class WxUrlUtils {

    public static String mediaUrl(String requestUrl, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (url.startsWith("/") && !StringUtils.isEmpty(requestUrl)) {
            URI uri = URI.create(requestUrl);
            String hostUrl = uri.getScheme() + "://" + uri.getHost();
            return hostUrl + url;
        } else {
            return "http://" + url;
        }
    }

}
