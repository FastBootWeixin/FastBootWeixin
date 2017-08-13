package com.example.myproject.exception;

/**
 * FastBootWeixin  WxApiException
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiException
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 14:48
 */
public class WxApiException extends WxException {

    public WxApiException(String message) {
        super(message);
    }

    public WxApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
