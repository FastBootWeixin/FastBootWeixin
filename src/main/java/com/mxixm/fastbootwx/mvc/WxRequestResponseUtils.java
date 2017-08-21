package com.mxixm.fastbootwx.mvc;

import com.mxixm.fastbootwx.module.WxRequest;
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

    private static final String RAW_WX_MESSAGE_REQUEST_ATTRIBUTE = "RAW_WX_MESSAGE_REQUEST_ATTRIBUTE";

    public static final String HEADER_X_WX_REQUEST_URL = "X-Wx-Request-Url";

    public static void setWxRequestToRequestAttribute(HttpServletRequest request, WxRequest wxRequest) {
        request.setAttribute(RAW_WX_MESSAGE_REQUEST_ATTRIBUTE, wxRequest);
    }

    public static WxRequest getWxRequestFromRequestAttribute(HttpServletRequest request) {
        return (WxRequest)request.getAttribute(RAW_WX_MESSAGE_REQUEST_ATTRIBUTE);
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
     * @param args
     */
    public static void main(String ... args) {
        System.out.println(randomString(-229985452)+' '+randomString(-147909649));
    }

    public static String randomString(int seed) {
        Random rand = new Random(seed);
        StringBuilder sb = new StringBuilder();
        while(true) {
            int n = rand.nextInt(27);
            if (n == 0) break;
            sb.append((char) ('`' + n));
        }
        return sb.toString();
    }

}
