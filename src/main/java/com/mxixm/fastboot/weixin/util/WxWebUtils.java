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

import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.message.parameter.HttpRequestMessageParameter;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;
import com.mxixm.fastboot.weixin.module.message.parameter.WxRequestMessageParameter;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.web.WxWebUser;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;

/**
 * FastBootWeixin WxWebUtils
 * 绑定一些参数
 * todo 是否有必要抽离一个WxQequestContext
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public class WxWebUtils {

    private static final String WX_REQUEST_ATTRIBUTE = "WX_REQUEST_ATTRIBUTE";

    public static final String X_WX_REQUEST_URL = "X-Wx-Request-Url";

    public static final String WX_SESSION_USER = "WX_SESSION_USER";

    /**
     * 为了应对多重包装RequestAttributeChangeIgnoringWrapper导致的属性设置无效，这里简单的这样处理了一下
     * @param request
     * @param wxRequest
     */
    public static void setWxRequestToRequest(HttpServletRequest request, WxRequest wxRequest) {
        ServletRequest servletRequest = request;
        while (servletRequest instanceof HttpServletRequestWrapper) {
            servletRequest = ((HttpServletRequestWrapper) servletRequest).getRequest();
        }
        servletRequest.setAttribute(WX_REQUEST_ATTRIBUTE, wxRequest);
    }

    public static WxRequest getWxRequestFromRequest(HttpServletRequest request) {
        return (WxRequest) request.getAttribute(WX_REQUEST_ATTRIBUTE);
    }

    public static WxRequest.Body getWxRequestBodyFromRequest(HttpServletRequest request) {
        WxRequest wxRequest = getWxRequestFromRequest(request);
        if (wxRequest == null) {
            return null;
        }
        return wxRequest.getBody();
    }

    public static void setWxWebUserToSession(HttpServletRequest request, WxWebUser wxWebUser) {
        request.getSession().setAttribute(WX_SESSION_USER, wxWebUser);
    }

    public static WxWebUser getWxWebUserFromSession(HttpServletRequest request) {
        return (WxWebUser) request.getSession().getAttribute(WX_SESSION_USER);
    }

    /**
     * 暂时只支持这几种类型
     *
     * @param paramType
     * @return the result
     */
    public static boolean isMutlipart(Class paramType) {
        return (byte[].class == paramType ||
                InputStream.class.isAssignableFrom(paramType) ||
                Reader.class.isAssignableFrom(paramType) ||
                File.class.isAssignableFrom(paramType) ||
                InputStreamSource.class.isAssignableFrom(paramType));
    }

    /**
     * 同上面方法，不过request从RequestContextHolder中取
     * @param wxRequest
     */
    public static void setWxRequestToRequest(WxRequest wxRequest) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            requestAttributes.setAttribute(WX_REQUEST_ATTRIBUTE, wxRequest, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public static WxRequest getWxRequestFromRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return (WxRequest) requestAttributes.getAttribute(WX_REQUEST_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        }
        return null;
    }

    public static WxRequest.Body getWxRequestBodyFromRequest() {
        WxRequest wxRequest = getWxRequestFromRequest();
        if (wxRequest == null) {
            return null;
        }
        return wxRequest.getBody();
    }

    public static void setWxWebUserToSession(WxWebUser wxWebUser) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            requestAttributes.setAttribute(WX_SESSION_USER, wxWebUser, RequestAttributes.SCOPE_SESSION);
        }
    }

    public static WxWebUser getWxWebUserFromSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return (WxWebUser) requestAttributes.getAttribute(WX_SESSION_USER, RequestAttributes.SCOPE_SESSION);
        }
        return null;
    }

    public static WxMessageParameter getWxMessageParameter() {
        WxRequest wxRequest = getWxRequestFromRequest();
        if (wxRequest != null) {
            return new WxRequestMessageParameter(wxRequest);
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return new HttpRequestMessageParameter(((ServletRequestAttributes) requestAttributes).getRequest());
        }
        return new HttpRequestMessageParameter();
    }

    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        throw new WxAppException("无法获取上下文中的HttpServletRequest，请不要在异步调用中使用此方法");
    }

}
