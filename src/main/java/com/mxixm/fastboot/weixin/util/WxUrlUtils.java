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
import org.springframework.util.StringUtils;

import java.net.URI;

/**
 * FastBootWeixin WxUrlUtils
 *
 * @author Guangshan
 * @date 2017/8/20 23:56
 * @since 0.1.2
 */
public abstract class WxUrlUtils {

    public static String HTTP_PROTOCOL = "http:";

    public static String HTTPS_PROTOCOL = "https:";

    public static String RELAX_PROTOCOL = "//";

    public static String BASE_PATH = "/";

    public static String HOST_DOT = ".";

    /**
     * 实在没有的时候，把流量引给我自己，一般不会有这种情况
     */
    public static String DEFAULT_HOST = "mxixm.com";

    public static String mediaUrl(String requestUrl, String targetUrl) {
        String lowerUrl = targetUrl.toLowerCase();
        // 已经包含协议，直接返回
        if (lowerUrl.startsWith(HTTP_PROTOCOL) || lowerUrl.startsWith(HTTPS_PROTOCOL)) {
            return targetUrl;
        }

        // 先解析出来protocol和host
        // 是否要考虑context-path? request.getContextPath()有可能不是/，可能会带来一些问题。可参考UrlPathHelper
        // 菜单链接可能同样有这个问题
        String protocol = HTTP_PROTOCOL;
        String host = DEFAULT_HOST;
        if (!StringUtils.isEmpty(requestUrl)) {
            URI uri = URI.create(requestUrl);
            protocol = uri.getScheme() + ":";
            host = uri.getHost();
        }

        // 如果是//开头，则拼接协议后返回
        if (lowerUrl.startsWith(RELAX_PROTOCOL)) {
            return protocol + targetUrl;
        }

        // 如果是/开头，则是绝对路径
        if (lowerUrl.startsWith(BASE_PATH)) {
            return protocol + RELAX_PROTOCOL + host + targetUrl;
        }

        // 如果是域名
        if (lowerUrl.indexOf(HOST_DOT) > -1) {
            // 如果包含.且(不包含/，或者包含/但.在/之前)
            if (lowerUrl.indexOf(BASE_PATH) == -1 || lowerUrl.indexOf(HOST_DOT) < lowerUrl.indexOf(BASE_PATH)) {
                // 则前面是域名，加上协议和//
                return protocol + RELAX_PROTOCOL + targetUrl;
            }
        }

        // 不是域名，则是一个相对路径
        return protocol + RELAX_PROTOCOL + host + BASE_PATH + targetUrl;
    }

    public static String mediaUrl(String targetUrl) {
        return mediaUrl(Wx.Environment.instance().getCallbackUrl(), targetUrl);
    }

    /**
     * 判断是否是回调地址
     *
     * @param targetUrl 判断的url
     * @param callbackHost 回调域名
     * @return the result
     */
    private static boolean isCallbackUrlInternal(String targetUrl, String callbackHost) {
        String urlHost = URI.create(targetUrl).getHost();
        return urlHost.equals(callbackHost);
    }

    /**
     * 判断是否是回调地址
     *
     * @param targetUrl 判断的url
     * @return the result
     */
    public static boolean isCallbackUrl(String targetUrl) {
        return isCallbackUrlInternal(targetUrl, Wx.Environment.instance().getCallbackHost());
    }

    /**
     * 判断是否是回调地址
     *
     * @param requestUrl 请求地址
     * @param targetUrl 判断的url
     * @return the result
     */
    public static boolean isCallbackUrl(String requestUrl, String targetUrl) {
        String callbackHost = Wx.Environment.instance().getCallbackHost();
        if (StringUtils.isEmpty(callbackHost)) {
            callbackHost = URI.create(requestUrl).getHost();
        }
        return isCallbackUrlInternal(targetUrl, callbackHost);
    }

}
