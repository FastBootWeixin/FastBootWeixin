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

    public static String HTTP_PROTOCOL = "http://";

    public static String HTTPS_PROTOCOL = "https://";

    public static String BASE_PATH = "/";

    public static String mediaUrl(String requestUrl, String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.startsWith(HTTP_PROTOCOL) || lowerUrl.startsWith(HTTPS_PROTOCOL)) {
            return url;
        }
        if (lowerUrl.startsWith(BASE_PATH) && !StringUtils.isEmpty(requestUrl)) {
            URI uri = URI.create(requestUrl);
            String hostUrl = uri.getScheme() + "://" + uri.getHost();
            return hostUrl + url;
        } else {
            return HTTP_PROTOCOL + url;
        }
    }

    /**
     * 判断是否是回调地址
     *
     * @param url 判断的url
     * @param defaultTrue 默认情况下是true
     * @return dummy
     */
    public static boolean isCallbackUrl(String url, boolean defaultTrue) {
        String urlHost = URI.create(url).getHost();
        String callbackDomain = Wx.Environment.instance().getCallbackDomain();
        if (defaultTrue && StringUtils.isEmpty(callbackDomain)) {
            return true;
        }
        return urlHost.equals(callbackDomain);
    }

    /**
     * 判断是否是回调地址
     *
     * @return dummy
     */
    public static boolean isCallbackUrl(String url) {
        return isCallbackUrl(url, false);
    }

}
