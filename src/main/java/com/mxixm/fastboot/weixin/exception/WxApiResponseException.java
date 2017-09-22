/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;

import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin WxApiResponseException
 *
 * @author Guangshan
 * @date 2017/7/23 23:38
 * @since 0.1.2
 */
public class WxApiResponseException extends WxApiException {

    private HttpStatus statusCode;

    private ResponseEntity responseEntity;

    private ClientHttpResponse clientHttpResponse;

    public WxApiResponseException(ResponseEntity responseEntity) {
        // this必须第一句（除了注释意外的第一句）
        this(responseEntity.getBody() instanceof byte[] ? new String((byte[]) responseEntity.getBody(), StandardCharsets.UTF_8) : String.valueOf(responseEntity.getBody()), responseEntity);
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
