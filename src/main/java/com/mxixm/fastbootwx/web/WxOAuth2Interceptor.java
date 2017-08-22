package com.mxixm.fastbootwx.web;

import com.mxixm.fastbootwx.mvc.WxRequestResponseUtils;
import com.mxixm.fastbootwx.util.WxUrlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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

    /**
     * 登录回调函数，可以通过构造函数指定，也可以直接注入进来
     */
    @Autowired(required = false)
    private WxOAuth2Callback wxOAuth2Callback;

    @Autowired
    private WxUserManager wxUserManager;

    public WxOAuth2Interceptor() {
        super();
    }

    public WxOAuth2Interceptor(WxOAuth2Callback wxOAuth2Callback) {
        super();
        this.wxOAuth2Callback = wxOAuth2Callback;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WxWebUser sessionUser = WxRequestResponseUtils.getWxWebUserFromSession(request);
        if (sessionUser != null) {
            return true;
        }
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        if (!StringUtils.isEmpty(code)) {
            WxWebUser wxWebUser = wxUserManager.getWxWebUser(code);
            if (wxWebUser != null && wxWebUser.getOpenId() != null) {
                if (wxOAuth2Callback != null) {
                    wxOAuth2Callback.after(new WxOAuth2Callback.WxOAuth2Context(wxWebUser, state, response, request));
                }
                WxRequestResponseUtils.setWxWebUserToSession(request, wxWebUser);
                return true;
            }
        }
        String redirectUrl = request.getRequestURI() + (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString());
        response.sendRedirect(WxUrlUtils.processRedirectUrl(redirectUrl));
        return false;
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
