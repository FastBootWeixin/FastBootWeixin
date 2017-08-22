package com.mxixm.fastbootwx.mvc;

import com.mxixm.fastbootwx.module.WxRequest;
import com.mxixm.fastbootwx.module.user.WxUser;
import com.mxixm.fastbootwx.web.WxWebUser;
import org.springframework.core.io.InputStreamSource;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Random;

/**
 * 绑定一些参数
 */
public class WxRequestResponseUtils {

    private static final String WX_REQUEST_ATTRIBUTE = "WX_REQUEST_ATTRIBUTE";

    public static final String X_WX_REQUEST_URL = "X-Wx-Request-Url";


    public static final String WX_SESSION_USER = "WX_SESSION_USER";

    public static void setWxRequestToRequestAttribute(HttpServletRequest request, WxRequest wxRequest) {
        request.setAttribute(WX_REQUEST_ATTRIBUTE, wxRequest);
    }

    public static WxRequest getWxRequestFromRequestAttribute(HttpServletRequest request) {
        return (WxRequest) request.getAttribute(WX_REQUEST_ATTRIBUTE);
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
     * @return
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
