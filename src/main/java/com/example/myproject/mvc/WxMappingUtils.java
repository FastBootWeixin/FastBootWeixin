package com.example.myproject.mvc;

import com.example.myproject.module.message.reveive.RawWxMessage;

import javax.servlet.http.HttpServletRequest;

/**
 * 绑定一些参数
 */
public class WxMappingUtils {

    private static final String RAW_WX_MESSAGE_REQUEST_ATTRIBUTE = "RAW_WX_MESSAGE_REQUEST_ATTRIBUTE";

    public static void setRawWxMessageToRequest(HttpServletRequest request, RawWxMessage rawWxMessage) {
        request.setAttribute(RAW_WX_MESSAGE_REQUEST_ATTRIBUTE, rawWxMessage);
    }

    public static RawWxMessage getRawwXMessageFromRequest(HttpServletRequest request) {
        return (RawWxMessage)request.getAttribute(RAW_WX_MESSAGE_REQUEST_ATTRIBUTE);
    }

}
