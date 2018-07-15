/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.mvc.processor;

import com.mxixm.fastboot.weixin.annotation.WxAsyncMessage;
import com.mxixm.fastboot.weixin.annotation.WxMapping;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageTemplate;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * fastboot-weixin  WxMappingReturnValueHandler
 * 只支持WxMessage类型
 *
 * @author Guangshan
 * @date 2018/7/14 10:29
 * @since 0.6.2
 */
public class WxMessageReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final WxMessageTemplate wxMessageTemplate;

    private final WxAsyncMessageTemplate wxAsyncMessageTemplate;

    public WxMessageReturnValueHandler(WxMessageTemplate wxMessageTemplate, WxAsyncMessageTemplate wxAsyncMessageTemplate) {
        this.wxMessageTemplate = wxMessageTemplate;
        this.wxAsyncMessageTemplate = wxAsyncMessageTemplate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        if (returnType.hasMethodAnnotation(WxMapping.class)) {
            return false;
        }
        ResolvableType resolvableType = ResolvableType.forMethodParameter(returnType);
        ResolvableType arrayType = ResolvableType.forArrayComponent(ResolvableType.forClass(WxMessage.class));
        ResolvableType iterableType = ResolvableType.forClassWithGenerics(Iterable.class, WxMessage.class);
        return arrayType.isAssignableFrom(resolvableType) || iterableType.isAssignableFrom(resolvableType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnType.hasMethodAnnotation(WxAsyncMessage.class)) {
            wxAsyncMessageTemplate.send(WxWebUtils.getWxMessageParameter(), returnValue);
        } else {
            wxMessageTemplate.sendMessage(WxWebUtils.getWxMessageParameter(), returnValue);
        }
        HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
        mavContainer.setRequestHandled(true);
        outputMessage.getBody();
    }

}
