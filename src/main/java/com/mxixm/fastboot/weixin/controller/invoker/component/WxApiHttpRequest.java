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

package com.mxixm.fastboot.weixin.controller.invoker.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * FastBootWeixin WxApiHttpRequest
 * 包装ClientHttpRequest，用于生成包装过的ClientHttpResponse
 *
 * @author Guangshan
 * @date 2017/08/23 22:31
 * @since 0.1.2
 */
public final class WxApiHttpRequest implements ClientHttpRequest {

    private ClientHttpRequest delegate;

    public WxApiHttpRequest(ClientHttpRequest delegate) {
        this.delegate = delegate;
    }

    @Override
    public HttpMethod getMethod() {
        return this.delegate.getMethod();
    }

    @Override
    public URI getURI() {
        return this.delegate.getURI();
    }

    @Override
    public ClientHttpResponse execute() throws IOException {
        return new WxApiHttpResponse(this.delegate.execute(), this);
    }

    @Override
    public OutputStream getBody() throws IOException {
        return this.delegate.getBody();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }
}
