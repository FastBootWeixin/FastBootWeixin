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

import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.parameter.WxRequestMessageParameter;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxMessageResponseBodyAdvice
 * ResponseBodyAdvice Spring 4.1以上才支持。
 * 这个作用是为响应自动添加fromUser
 * 不加这个注解会有问题@ControllerAdvice，不识别
 *
 * @author Guangshan
 * @date 2017/08/15 23:43
 * @since 0.1.2
 */
@Deprecated
@ControllerAdvice
public class WxMessageResponseBodyAdvice implements ResponseBodyAdvice<WxMessage>, Ordered {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private WxMessageProcessor wxMessageProcessor;

    public WxMessageResponseBodyAdvice(WxMessageProcessor wxMessageProcessor) {
        this.wxMessageProcessor = wxMessageProcessor;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 200000;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return AbstractXmlHttpMessageConverter.class.isAssignableFrom(converterType) &&
                WxMessage.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public WxMessage beforeBodyWrite(WxMessage body, MethodParameter returnType,
                                     MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                     ServerHttpRequest request, ServerHttpResponse response) {
        if (!(request instanceof ServletServerHttpRequest) || body == null) {
            return body;
        }
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(servletRequest);
        return wxMessageProcessor.process(new WxRequestMessageParameter(wxRequest), body);
    }

}
