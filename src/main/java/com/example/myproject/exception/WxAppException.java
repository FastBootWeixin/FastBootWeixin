package com.example.myproject.exception;

import java.util.Arrays;

/**
 * FastBootWeixin  WxAppException
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxAppException
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 23:38
 */
public class WxAppException extends RuntimeException {

    private final Throwable original;

    public WxAppException(String message) {
        super(message);
        original = null;
    }

    public WxAppException(Throwable original) {
        super(original);
        this.original = original;
    }

    public WxAppException(String message, Throwable original) {
        super(message, original);
        this.original = original;
    }

}
