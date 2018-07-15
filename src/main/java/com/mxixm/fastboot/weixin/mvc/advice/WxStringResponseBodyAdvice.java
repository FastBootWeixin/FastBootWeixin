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

import com.mxixm.fastboot.weixin.annotation.WxMapping;
import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.WxUserMessage;
import com.mxixm.fastboot.weixin.module.message.parameter.WxRequestMessageParameter;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxStringResponseBodyAdvice
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
public class WxStringResponseBodyAdvice implements ResponseBodyAdvice<String>, Ordered {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private Marshaller xmlConverter;

    private WxMessageProcessor wxMessageProcessor;

    public WxStringResponseBodyAdvice(WxMessageProcessor wxMessageProcessor) {
        this.wxMessageProcessor = wxMessageProcessor;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(WxUserMessage.Text.class);
            xmlConverter = jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            throw new WxAppException(e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 30000;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return StringHttpMessageConverter.class.isAssignableFrom(converterType) &&
                CharSequence.class.isAssignableFrom(returnType.getParameterType()) &&
                returnType.hasMethodAnnotation(WxMapping.class);
    }

    @Override
    public String beforeBodyWrite(String body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!(request instanceof ServletServerHttpRequest) || StringUtils.isEmpty(body)) {
            return null;
        }
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(servletRequest);
        WxUserMessage text = WxMessage.Text.builder().content(body).build();
        return parseXml(wxMessageProcessor.process(new WxRequestMessageParameter(wxRequest), text));
    }

    private String parseXml(WxMessage text) {
        try {
            StringWriter writer = new StringWriter();
            xmlConverter.marshal(text, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new WxAppException(e);
        }
    }

}
