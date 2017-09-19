/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.mvc;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.web.WxWebUser;
import org.springframework.core.io.InputStreamSource;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Random;

/**
 * 绑定一些参数
 */
public class WxWebUtils {

    private static final String WX_REQUEST_ATTRIBUTE = "WX_REQUEST_ATTRIBUTE";

    public static final String X_WX_REQUEST_URL = "X-Wx-Request-Url";


    public static final String WX_SESSION_USER = "WX_SESSION_USER";

    public static void setWxRequestToRequestAttribute(HttpServletRequest request, WxRequest wxRequest) {
        request.setAttribute(WX_REQUEST_ATTRIBUTE, wxRequest);
    }

    public static WxRequest getWxRequestFromRequestAttribute(HttpServletRequest request) {
        return (WxRequest) request.getAttribute(WX_REQUEST_ATTRIBUTE);
    }

    public static WxRequest.Body getWxRequestBodyFromRequestAttribute(HttpServletRequest request) {
        WxRequest wxRequest = getWxRequestFromRequestAttribute(request);
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
     * @return dummy
     */
    public static boolean isMutlipart(Class paramType) {
        return (byte[].class == paramType ||
                InputStream.class.isAssignableFrom(paramType) ||
                Reader.class.isAssignableFrom(paramType) ||
                File.class.isAssignableFrom(paramType) ||
                InputStreamSource.class.isAssignableFrom(paramType));
    }


    /**
     * hello world
     *
     * @param args
     */
    public static void main1(String... args) {
        System.out.println(randomString(-229985452) + ' ' + randomString(-147909649));
    }

    public static String randomString(int seed) {
        Random rand = new Random(seed);
        StringBuilder sb = new StringBuilder();
        while (true) {
            int n = rand.nextInt(27);
            if (n == 0) break;
            sb.append((char) ('`' + n));
        }
        return sb.toString();
    }

}
