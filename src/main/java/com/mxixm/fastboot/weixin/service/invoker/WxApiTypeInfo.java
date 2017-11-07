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

package com.mxixm.fastboot.weixin.service.invoker;

import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiRequest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * FastBootWeixin WxApiTypeInfo
 *
 * @author Guangshan
 * @date 2017/8/11 21:01
 * @since 0.1.2
 */
public class WxApiTypeInfo {

    private static final String WX_API_PROPERTY_PREFIX = "wx.url";

    private final Class clazz;

    private final String propertyPrefix;

    private final UriComponentsBuilder baseBuilder;

    /**
     * @param clazz       代理类
     * @param defaultHost 默认host
     */
    public WxApiTypeInfo(Class clazz, String defaultHost) {
        this.clazz = clazz;
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(clazz, WxApiRequest.class);
        String host = getTypeWxApiHost(wxApiRequest, defaultHost);
        String typePath = getTypeWxApiRequestPath(wxApiRequest);
        // 固定https请求
        propertyPrefix = getTypeWxApiPropertyPrefix(wxApiRequest);
        baseBuilder = UriComponentsBuilder.newInstance().scheme("https").host(host).path(typePath);
    }

    private String getTypeWxApiHost(WxApiRequest wxApiRequest, String defaultHost) {
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.host()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.host())) {
            return defaultHost;
        }
        return wxApiRequest.host();
    }

    private String getTypeWxApiPropertyPrefix(WxApiRequest wxApiRequest) {
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.prefix()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.prefix())) {
            return WX_API_PROPERTY_PREFIX;
        }
        return wxApiRequest.prefix();
    }

    private String getTypeWxApiRequestPath(WxApiRequest wxApiRequest) {
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.path()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.path())) {
            return "/";
        }
        return wxApiRequest.path();
    }

    public String getPropertyPrefix() {
        return propertyPrefix;
    }

    public UriComponentsBuilder getBaseBuilder() {
        return baseBuilder.cloneBuilder();
    }
}
