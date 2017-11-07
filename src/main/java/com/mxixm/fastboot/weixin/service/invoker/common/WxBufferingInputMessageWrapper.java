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

package com.mxixm.fastboot.weixin.service.invoker.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * FastBootWeixin WxBufferingInputMessageWrapper
 * 数据全部放入buffer
 *
 * @author Guangshan
 * @date 2017/08/13 10:53
 * @since 0.1.2
 */
public final class WxBufferingInputMessageWrapper implements HttpInputMessage, Closeable {

    private final HttpInputMessage httpInputMessage;

    private byte[] body;

    private ByteArrayInputStream byteArrayInputStream;

    public WxBufferingInputMessageWrapper(HttpInputMessage httpInputMessage) {
        this.httpInputMessage = httpInputMessage;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.httpInputMessage.getHeaders();
    }

    @Override
    public InputStream getBody() throws IOException {
        this.init();
        if (byteArrayInputStream == null) {
            byteArrayInputStream = new ByteArrayInputStream(this.body);
        }
        return byteArrayInputStream;
    }

    /**
     * 构造后初始化，不想加在构造方法中，一定要记着初始化
     *
     * @throws IOException
     */
    public WxBufferingInputMessageWrapper init() throws IOException {
        if (this.body != null) {
            return this;
        }
        if (httpInputMessage instanceof WxBufferingInputMessageWrapper) {
            this.body = ((WxBufferingInputMessageWrapper) httpInputMessage).getRawBody();
        } else {
            this.body = StreamUtils.copyToByteArray(httpInputMessage.getBody());
        }
        return this;
    }

    public byte[] getRawBody() {
        return this.body;
    }

    @Override
    public void close() throws IOException {
        if (this.httpInputMessage != null && this.httpInputMessage.getBody() != null) {
            try {
                this.httpInputMessage.getBody().close();
            } catch (IOException e) {
                // ignore it
            }
        }
    }
}
