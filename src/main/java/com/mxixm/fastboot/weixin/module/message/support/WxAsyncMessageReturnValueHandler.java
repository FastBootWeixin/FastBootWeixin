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

package com.mxixm.fastboot.weixin.module.message.support;

import com.mxixm.fastboot.weixin.annotation.WxAsyncMessage;
import com.mxixm.fastboot.weixin.annotation.WxMapping;
import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.module.message.WxGroupMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxTemplateMessage;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;

/**
 * FastBootWeixin WxAsyncMessageReturnValueHandler
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public class WxAsyncMessageReturnValueHandler implements HandlerMethodReturnValueHandler {

    private WxAsyncMessageTemplate wxAsyncMessageTemplate;

    private WxProperties wxProperties;

    public WxAsyncMessageReturnValueHandler(WxProperties wxProperties, WxAsyncMessageTemplate wxAsyncMessageTemplate) {
        this.wxProperties = wxProperties;
        this.wxAsyncMessageTemplate = wxAsyncMessageTemplate;
    }

    /**
     * 有WxAsyncMessage注解且
     * 返回值是WxMessage的子类
     * 或者是CharSequence的子类，且有注解WxButton或者WXMessageMapping
     *
     * @param returnType
     * @return dummy
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 如果是iterable或者array，都作为asyncMessage消息处理
        boolean isIterableType = Iterable.class.isAssignableFrom(returnType.getParameterType());
        boolean isArrayType = returnType.getParameterType().isArray();
        boolean isGroupMessage = WxGroupMessage.class.isAssignableFrom(returnType.getParameterType());
        boolean isTemplateMessage = WxTemplateMessage.class.isAssignableFrom(returnType.getParameterType());
        // 理论上WxAsyncMessage已经被上层处理过了，这里保险起见再处理一次
        boolean needAsyncSend = isIterableType || isArrayType || isGroupMessage || isTemplateMessage;
        Class realType = getGenericType(returnType);
        boolean isWxMessage = WxMessage.class.isAssignableFrom(realType);
        boolean isWxStringMessage = CharSequence.class.isAssignableFrom(realType) &&
                returnType.hasMethodAnnotation(WxMapping.class);
        return needAsyncSend && (isWxMessage || isWxStringMessage);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
        outputMessage.getBody();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
        wxAsyncMessageTemplate.send(wxRequest, returnValue);
    }

    private Class getGenericType(MethodParameter returnType) {
        boolean isIterable = Iterable.class.isAssignableFrom(returnType.getParameterType());
        if (isIterable) {
            if (returnType.getGenericParameterType() instanceof ParameterizedType) {
                return (Class) ((ParameterizedType) returnType.getGenericParameterType()).getActualTypeArguments()[0];
            } else {
                return returnType.getParameterType();
            }
        }
        if (returnType.getParameterType().isArray()) {
            return returnType.getParameterType().getComponentType();
        }
        return returnType.getParameterType();
    }

}
