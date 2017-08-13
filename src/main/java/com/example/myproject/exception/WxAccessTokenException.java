package com.example.myproject.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

/**
 * FastBootWeixin  WxAccessTokenException
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxAccessTokenException
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 23:38
 */
public class WxAccessTokenException extends WxApiResultException {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    public WxAccessTokenException(int code, String errorMessage) {
        super(code, errorMessage);
    }

    public WxAccessTokenException(String errorResult) {
        super(errorResult);
    }

}
