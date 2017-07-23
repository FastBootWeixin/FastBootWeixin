package com.example.myproject.exception;

import java.util.Arrays;

/**
 * FastBootWeixin  WxApiException
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiException
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 23:38
 */
public class WxApiException extends RuntimeException {

    int errcode;

    String errmsg;

    WxApiResultCode resultCode;

    public WxApiException(int errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.resultCode = WxApiResultCode.of(errcode);
    }

    enum WxApiResultCode {

        System_Busy(-1, "系统繁忙，此时请开发者稍候再试"),
        Success(0, "请求成功"),
        App_Secret_Error(40001, "AppSecret错误或者AppSecret不属于这个公众号，请开发者确认AppSecret的正确性"),
        Grant_Type_Error(40002, "请确保grant_type字段值为client_credential"),
        Invalid_IP_Error(40164, "调用接口的IP地址不在白名单中，请在接口IP白名单中进行设置"),
        Unknown_Error(99999, "未知错误，请查看errmsg");

        int errcode;

        String errdesc;

        WxApiResultCode(int errcode, String errdesc) {
            this.errcode = errcode;
            this.errdesc = errdesc;
        }

        public static WxApiResultCode of(int errcode) {
            return Arrays.stream(WxApiResultCode.values()).filter(v -> v.errcode == errcode).findFirst().orElse(Unknown_Error);
        }

    }

}
