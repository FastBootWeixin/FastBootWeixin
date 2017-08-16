/*
 * Copyright 2002-2017 the original author or authors.
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

package com.example.myproject.controller.invoker.handler;

import com.example.myproject.exception.WxApiResponseException;
import com.example.myproject.exception.WxApiResultException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;

/**
 * 增加api异常判断
 */
public class WxResponseErrorHandler extends DefaultResponseErrorHandler {

    // 数了一下，是15
    public static final int WX_API_ERROR_CODE_END = 15;

    /**
     * Delegates to {@link #hasError(HttpStatus)} with the response status code.
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return hasError(getHttpStatusCode(response)) || hasApiResultError(response);
    }

    /**
     * This default implementation throws a {@link HttpClientErrorException} if the response status code
     * is {@link HttpStatus.Series#CLIENT_ERROR}, a {@link HttpServerErrorException}
     * if it is {@link HttpStatus.Series#SERVER_ERROR},
     * and a {@link RestClientException} in other cases.
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = getHttpStatusCode(response);
        switch (statusCode.series()) {
            case CLIENT_ERROR:
                throw new WxApiResponseException(new String(getResponseBody(response)), response, statusCode);
            case SERVER_ERROR:
                throw new WxApiResponseException(new String(getResponseBody(response)), response, statusCode);
            default:
                throw new WxApiResultException(new String(getResponseBody(response)));
        }
    }

    /**
     * Indicates whether the response has an empty message body.
     * <p>Implementation tries to read the first bytes of the response stream:
     * <ul>
     * <li>if no bytes are available, the message body is empty</li>
     * <li>otherwise it is not empty and the stream is reset to its start for further reading</li>
     * </ul>
     *
     * @return {@code true} if the response has a zero-length message body, {@code false} otherwise
     * @throws IOException in case of I/O errors
     */
    private boolean hasApiResultError(ClientHttpResponse response) throws IOException {
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
            if (WxApiResultException.hasException(new String(bytes))) {
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
