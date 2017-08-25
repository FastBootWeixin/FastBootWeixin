package com.mxixm.fastboot.weixin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;

import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin  WxApiResponseException
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiResponseException
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 23:38
 */
public class WxApiResponseException extends WxApiException {

    private HttpStatus statusCode;

    private ResponseEntity responseEntity;

    private ClientHttpResponse clientHttpResponse;

    public WxApiResponseException(ResponseEntity responseEntity) {
        // this必须第一句（除了注释意外的第一句）
        this(responseEntity.getBody() instanceof byte[] ? new String((byte[]) responseEntity.getBody(), StandardCharsets.UTF_8) : String.valueOf(responseEntity.getBody()) , responseEntity);
    }

    public WxApiResponseException(String message, ResponseEntity responseEntity) {
        super(message);
        this.responseEntity = responseEntity;
        this.statusCode = responseEntity.getStatusCode();
    }

    public WxApiResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public WxApiResponseException(String message, ResponseEntity responseEntity, Throwable cause) {
        super(message, cause);
        this.responseEntity = responseEntity;
        this.statusCode = responseEntity.getStatusCode();
    }

    public WxApiResponseException(String message, ClientHttpResponse clientHttpResponse, HttpStatus statusCode) {
        super(message);
        this.clientHttpResponse = clientHttpResponse;
        this.statusCode = statusCode;
    }

}
