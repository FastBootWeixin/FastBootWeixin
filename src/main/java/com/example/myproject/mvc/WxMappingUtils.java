package com.example.myproject.mvc;

import com.example.myproject.module.message.receive.RawWxMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * 绑定一些参数
 */
public class WxMappingUtils {

    private static final String RAW_WX_MESSAGE_REQUEST_ATTRIBUTE = "RAW_WX_MESSAGE_REQUEST_ATTRIBUTE";

    public static void setRawWxMessageToRequest(HttpServletRequest request, RawWxMessage rawWxMessage) {
        request.setAttribute(RAW_WX_MESSAGE_REQUEST_ATTRIBUTE, rawWxMessage);
    }

    public static RawWxMessage getRawWxMessageFromRequest(HttpServletRequest request) {
        return (RawWxMessage)request.getAttribute(RAW_WX_MESSAGE_REQUEST_ATTRIBUTE);
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
