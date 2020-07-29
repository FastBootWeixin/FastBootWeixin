package com.mxixm.fastboot.weixin.web;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * todo 类似于Spring MVC的方式去配置WxHandlerMethod，提供过滤不同类型事件的拦截器配置
 */
public interface WxHandlerInterceptor extends HandlerInterceptor {

    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
        return wxRequest == null || preHandle(wxRequest, response, handler);
    }

    @Override
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
        if (wxRequest != null) {
            postHandle(wxRequest, response, handler, modelAndView);
        }
    }

    @Override
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
        if (wxRequest != null) {
            afterCompletion(wxRequest, response, handler, ex);
        }
    }

    default boolean preHandle(WxRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    default void postHandle(WxRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    default void afterCompletion(WxRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
