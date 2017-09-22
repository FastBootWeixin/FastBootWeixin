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
