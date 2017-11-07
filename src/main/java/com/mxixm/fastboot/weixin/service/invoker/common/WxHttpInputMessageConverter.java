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

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;

/**
 * FastBootWeixin WxHttpInputMessageConverter
 *
 * @author Guangshan
 * @date 2017/08/13 10:53
 * @since 0.1.2
 */
public class WxHttpInputMessageConverter extends AbstractHttpMessageConverter<HttpInputMessage> {

    public WxHttpInputMessageConverter() {
        super(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return HttpInputMessage.class.isAssignableFrom(clazz);
    }

    @Override
    protected HttpInputMessage readInternal(Class<? extends HttpInputMessage> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // 为何不直接返回inputMessage？因为在请求结束后，原始的inputMessage会被系统关闭，所以做了一层包装
        return new WxBufferingInputMessageWrapper(inputMessage).init();
    }

    @Override
    protected void writeInternal(HttpInputMessage httpInputMessage, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        StreamUtils.copy(httpInputMessage.getBody(), outputMessage.getBody());
    }

}
