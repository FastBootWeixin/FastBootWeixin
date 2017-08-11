package com.example.myproject.controller.invoker;

import com.example.myproject.controller.invoker.annotation.WxApiRequest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * FastBootWeixin  WxApiTypeInfo
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiTypeInfo
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/11 21:01
 */
public class WxApiTypeInfo {

    private static final String WX_API_PROPERTY_PREFIX = "wx.api.url";

    private final Class clazz;

    private final String propertyPrefix;

    private final UriComponentsBuilder baseBuilder;

    /**
     *
     * @param clazz 代理类
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
