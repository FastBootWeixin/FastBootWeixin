package com.mxixm.fastbootwx.web;

import com.mxixm.fastbootwx.config.token.WxTokenServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;

/**
 * 微信oauth2的interceptor
 *
 * @Copyright (c) 2016, Guangshan Group All Rights Reserved.
 */
public class WxOAuth2Interceptor implements HandlerInterceptor {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    /**
     * 登录回调函数，可以通过构造函数指定，也可以直接注入进来
     */
    @Autowired(required = false)
    private WxOAuth2Callback wxOAuth2Callback;

    @Autowired
    private WxTokenServer wxTokenServer;

    public WxOAuth2Interceptor() {
        super();
    }

    public WxOAuth2Interceptor(WxOAuth2Callback wxOAuth2Callback) {
        super();
        this.wxOAuth2Callback = wxOAuth2Callback;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = UriComponentsBuilder.fromHttpUrl();
        String wxRedirectParam = requestUrl.substring(requestUrl.lastIndexOf("?"));
        String
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }

}
