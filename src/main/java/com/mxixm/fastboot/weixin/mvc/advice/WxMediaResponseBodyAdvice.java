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

package com.mxixm.fastboot.weixin.mvc.advice;

import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.media.WxMediaResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxMediaResponseBodyAdvice
 * ResponseBodyAdvice Spring 4.1以上才支持。
 * 这个作用是为响应自动添加fromUser
 * 不加这个注解会有问题@ControllerAdvice，不识别
 *
 * @author Guangshan
 * @date 2017/08/15 23:43
 * @since 0.1.2
 */
@ControllerAdvice
public class WxMediaResponseBodyAdvice implements ResponseBodyAdvice<WxMediaResource>, Ordered {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100000;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return ResourceHttpMessageConverter.class.isAssignableFrom(converterType) &&
                WxMediaResource.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public WxMediaResource beforeBodyWrite(WxMediaResource body, MethodParameter returnType,
                                           MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                           ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null || !body.isUrlMedia()) {
            return body;
        }
        try {
            response.getHeaders().setLocation(body.getURI());
            response.setStatusCode(HttpStatus.FOUND);
            return null;
        } catch (IOException e) {
            throw new WxAppException("系统异常");
        }
    }

}
