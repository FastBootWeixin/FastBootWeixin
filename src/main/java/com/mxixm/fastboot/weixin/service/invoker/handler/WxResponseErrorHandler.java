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

package com.mxixm.fastboot.weixin.service.invoker.handler;

import com.mxixm.fastboot.weixin.exception.WxApiResponseException;
import com.mxixm.fastboot.weixin.exception.WxApiResultException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin WxResponseErrorHandler
 * 增加api异常判断
 *
 * @author Guangshan
 * @date 2017/09/21 23:35
 * @since 0.1.2
 */
public class WxResponseErrorHandler implements ResponseErrorHandler {

    // 数了一下，是15
    public static final int WX_API_ERROR_CODE_END = 15;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return hasError(getHttpStatusCode(response)) || hasApiResultError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = getHttpStatusCode(response);
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                throw new WxApiResponseException(new String(getResponseBody(response), StandardCharsets.UTF_8), response, statusCode);
            case SERVER_ERROR:
                throw new WxApiResponseException(new String(getResponseBody(response), StandardCharsets.UTF_8), response, statusCode);
            default:
                throw new WxApiResultException(new String(getResponseBody(response), StandardCharsets.UTF_8));
        }
    }

    protected boolean hasError(HttpStatus statusCode) {
        return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
                statusCode.series() == HttpStatus.Series.SERVER_ERROR);
    }

    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            if (responseBody != null) {
                return FileCopyUtils.copyToByteArray(responseBody);
            }
        } catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }

    protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        try {
            return response.getStatusCode();
        } catch (IllegalArgumentException ex) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(),
                    response.getHeaders(), getResponseBody(response), getCharset(response));
        }
    }

    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return (contentType != null ? contentType.getCharset() : null);
    }

    protected boolean hasApiResultError(ClientHttpResponse response) throws IOException {
        // 只有响应是json时才可能有api错误
        // 微信太坑了！Content-Type标准里，编码使用charset标记的，这丫竟然用encoding标记！
        // 参考这个：https://www.zhihu.com/question/20615748
        if (!(MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())
                || MediaType.APPLICATION_JSON.includes(response.getHeaders().getContentType())
                || MediaType.TEXT_PLAIN.includes(response.getHeaders().getContentType()))) {
            return false;
        }
        InputStream body = response.getBody();
        if (body == null) {
            return false;
        } else if (body.markSupported()) {
            body.mark(WX_API_ERROR_CODE_END);
            byte[] bytes = new byte[WX_API_ERROR_CODE_END];
            body.read(bytes);
            body.reset();
            if (WxApiResultException.hasException(new String(bytes, StandardCharsets.UTF_8))) {
                return true;
            } else {
                return false;
            }
        } else if (body instanceof PushbackInputStream) {
            PushbackInputStream pushbackInputStream = (PushbackInputStream) body;
            byte[] bytes = new byte[WX_API_ERROR_CODE_END];
            pushbackInputStream.read(bytes);
            pushbackInputStream.unread(bytes);
            if (WxApiResultException.hasException(new String(bytes))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
