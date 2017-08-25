package com.mxixm.fastboot.weixin.exception;

/**
 * FastBootWeixin  WxException
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxException
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 23:38
 */
public class WxException extends RuntimeException {

    public WxException(String message) {
        super(message);
    }

    public WxException(Throwable cause) {
        super(cause);
    }

    public WxException(String message, Throwable cause) {
        super(message, cause);
    }

}
