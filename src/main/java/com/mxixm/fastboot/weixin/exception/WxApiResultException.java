package com.mxixm.fastboot.weixin.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

/**
 * FastBootWeixin  WxApiResultException
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1433747234
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiResultException
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 23:38
 */
public class WxApiResultException extends WxApiException {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    public static final String WX_API_RESULT_SUCCESS = "\"errcode\":0,";

    public static final String WX_API_RESULT_ERRCODE = "\"errcode\":";

    public static final String WX_API_RESULT_ERRMSG = "\"errmsg\":";

    private int code;

    private String errorMessage;

    WxApiResultCode resultCode;

    public WxApiResultException(int code, String errorMessage) {
        super(code + ":" + errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
        this.resultCode = WxApiResultCode.of(code);
    }

    public WxApiResultException(String errorResult) {
        super(errorResult);
        // 设置code和errorMessage
        prepare(errorResult);
        this.resultCode = WxApiResultCode.of(code);
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public WxApiResultCode getResultCode() {
        return resultCode;
    }

    public void prepare(String errorResult) {
        this.code = WxApiResultCode.Unknown_Error.errcode;
        this.errorMessage = WxApiResultCode.Unknown_Error.errdesc;
        try {
            int codeStringStart = errorResult.indexOf(WX_API_RESULT_ERRCODE);
            int codeStart = errorResult.indexOf(":", codeStringStart) + 1;
            int codeEnd = errorResult.indexOf(",", codeStart);
            this.code = Integer.parseInt(errorResult.substring(codeStart, codeEnd));
            int messageStringStart = errorResult.indexOf(WX_API_RESULT_ERRMSG);
            int messageStart = errorResult.indexOf(":", messageStringStart) + 2;
            int messageEnd = errorResult.indexOf("\"", messageStart);
            this.errorMessage = errorResult.substring(messageStart, messageEnd);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static boolean hasException(String result) {
        if (StringUtils.hasLength(result)) {
            // 包含错误码但是错误码不是0
            if (result.contains(WX_API_RESULT_ERRCODE) && !result.contains(WX_API_RESULT_SUCCESS)) {
                return true;
            }
        }
        return false;
    }

    public enum WxApiResultCode {

        System_Busy(-1, "系统繁忙，此时请开发者稍候再试"),
        Success(0, "请求成功"),
        App_Secret_Error(40001, "AppSecret错误或者AppSecret不属于这个公众号，请开发者确认AppSecret的正确性"),
        Grant_Type_Error(40002, "请确保grant_type字段值为client_credential"),
        Menu_No_Exist(46003, "不存在的菜单数据"),
        Invalid_IP_Error(40164, "调用接口的IP地址不在白名单中，请在接口IP白名单中进行设置"),
        Unknown_Error(99999, "未知错误，请查看message");
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
