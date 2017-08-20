package com.example.myproject.module.message.processer;

import org.springframework.util.StringUtils;

/**
 * FastBootWeixin  WxMediaUrlUtils
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMediaUrlUtils
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 23:56
 */
public abstract class WxMediaUrlUtils {

    public static String processUrl(String requestUrl, String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        if (url.startsWith("/")) {
            return StringUtils.applyRelativePath(requestUrl, url);
        } else {
            return "http://" + url;
        }
    }

}
