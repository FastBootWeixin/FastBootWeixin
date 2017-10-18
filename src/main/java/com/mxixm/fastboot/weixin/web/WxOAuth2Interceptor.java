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

package com.mxixm.fastboot.weixin.web;

import com.mxixm.fastboot.weixin.util.WxWebUtils;
import com.mxixm.fastboot.weixin.util.WxRedirectUtils;
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
 * FastBootWeixin WxOAuth2Interceptor
 * 微信oauth2的interceptor
 * 如果重定向的url中有参数，微信也会原封不动的把这些参数加上，再把自己的参数往后面加，所以我觉得state应该没什么卵用了，自己拼参数就足够了
 *
 * @author Guangshan
 * @date 2017/09/21 23:46
 * @since 0.1.2.
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
        WxWebUser sessionUser = WxWebUtils.getWxWebUserFromSession(request);
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
                WxWebUtils.setWxWebUserToSession(request, wxWebUser);
                return true;
            }
        }
        String redirectUrl = request.getRequestURL() + (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString());
        // 如果重定向到授权，则肯定可以获得信息，但是如果重定向到基本，则无法获得信息，所以默认重定向到授权
        response.sendRedirect(WxRedirectUtils.redirect(redirectUrl, false));
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
