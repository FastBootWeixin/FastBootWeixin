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

package com.mxixm.fastboot.weixin.service.invoker.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * 同样是兼容SB2.0, Spring5才加入的这个方法
     * 不写Override，写了在4.x版本会报错，注意递归调用
     * @return
     */
    public String getMethodValue() {
        return this.delegate.getMethod().name();
    }

    @Override
    public URI getURI() {
        return this.delegate.getURI();
    }

    @Override
    public ClientHttpResponse execute() throws IOException {
        return new WxApiHttpResponse(this.delegate.execute(), this);
    }

    /**
     * 针对下面这个比较恶心的魔法逻辑的说明：
     * 由于Spring5在对ContentType是MULTIPART_FORM_DATA的头处理时自动添加了charset参数。
     * see org.springframework.http.converter.FormHttpMessageConverter.writeMultipart
     * 如果设置了multipartCharset，则不会添加charset参数。
     * 但是在获取filename时又去判断如果设置了multipartCharset，则调用MimeDelegate.encode(filename, this.multipartCharset.name())
     * 对filename进行encode，但是MimeDelegate调用了javax.mail.internet.MimeUtility这个类，这个类有可能没有被依赖进来，不是强依赖的
     * 故会导致一样的报错。实在没办法切入源码了，故加入下面这个逻辑。
     * 在Spring4中在multipartCharset为空时，也不会自动添加charset，故没有此问题。
     * 而根本原因是微信的服务器兼容性太差了，header中的charset是有http标准规定的，竟然不兼容！
     *
     * @return OutputStream
     * @throws IOException
     */
    @Override
    public OutputStream getBody() throws IOException {
        HttpHeaders httpHeaders = this.delegate.getHeaders();
        MediaType contentType = httpHeaders.getContentType();
        if (contentType != null && contentType.includes(MediaType.MULTIPART_FORM_DATA)) {
            Map<String, String> parameters = contentType.getParameters();
            if (parameters.containsKey("charset")) {
                Map<String, String> newParameters = new LinkedHashMap<>(contentType.getParameters());
                newParameters.remove("charset");
                MediaType newContentType = new MediaType(MediaType.MULTIPART_FORM_DATA, newParameters);
                httpHeaders.setContentType(newContentType);
            }
        }
        return this.delegate.getBody();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }
}
