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
import com.mxixm.fastboot.weixin.module.message.WxGroupMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxTemplateMessage;
import com.mxixm.fastboot.weixin.module.message.WxUserMessage;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;
import com.mxixm.fastboot.weixin.util.WxMessageUtils;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;

/**
 * FastBootWeixin WxSyncMessageReturnValueHandler
 * 其实@WxAsyncMessage并不是通过这个类发送的，这里应该叫做CustomerMessage，这里的消息不需要异步发送，直接调用发送即可
 * todo 按照上面的逻辑重构
 * done 已重构
 * 该类不处理声明为被动返回的消息，即返回XML消息，其他类型都通过本类处理
 * 而对于消息的发送，都采用异步的方式我觉得没有问题，如果同步发送，报错之后响应给微信，微信会返回给用户服务器故障，这是不友好的，所以这里固定异步发送。
 * 但是有点区别，如果是普通的@RequestMapping呢，可能就不需要异步发送了
 * todo 分离出来两种，WxMappingReturnValueHandler和WxMessageReturnValueHandler
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @update 2018-5-24 17:24:21
 * @since 0.6.1
 */
@Deprecated
public abstract class AbstractWxMessageReturnValueHandler implements HandlerMethodReturnValueHandler {

    /**
     * 有WxAsyncMessage注解且
     * 返回值是WxMessage的子类
     * 或者是CharSequence的子类，且有注解WxButton或者WxMessageMapping
     * 这里其实应该判断反向的，即不需要Async发送的。只有几个用户类型的消息不需要异步发送
     *
     * @param returnType
     * @return result
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 是否需要xml格式的相应，如果需要xml响应，则不通过这个处理器处理
        // 判断逻辑是方法声明为WxMaping且支持XML响应且没有显式声明为WxAsyncMessage
        boolean returnXml = WxMessageUtils.supportsXmlResponse(returnType.getParameterType()) &&
                returnType.hasMethodAnnotation(WxMapping.class) && !returnType.hasMethodAnnotation(WxAsyncMessage.class);
        // 逻辑分开，快速返回，防止代码不易理解
        if (returnXml) {
            return false;
        }
        // 如果不需要响应为xml，则进入内部判断
        return supportsReturnTypeInternal(returnType);
    }

    /**
     * 额外进行判断
     * @param returnType
     * @return result
     */
    protected abstract boolean supportsReturnTypeInternal(MethodParameter returnType);

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // @WxAsyncMessage声明的Handler恒为null，这里提供支持
        if (returnValue != null) {
            handlReturnValueInternal(returnValue);
        }
        mavContainer.setRequestHandled(true);
        HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
        outputMessage.getBody();


    }

    /**
     * 处理returnValue
     * @param returnValue
     */
    protected abstract void handlReturnValueInternal(Object returnValue);

    /**
     * 只查找一层
     * @param returnType 返回类型
     * @return result
     */
    protected Class getGenericType(MethodParameter returnType) {
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

    /**
     * 有WxAsyncMessage注解且
     * 返回值是WxMessage的子类
     * 或者是CharSequence的子类，且有注解WxButton或者WXMessageMapping
     * 这里其实应该判断反向的，即不需要Async发送的。只有几个用户类型的消息不需要异步发送
     *
     * @param returnType
     * @return result
     */
    @Deprecated
    public boolean supportsReturnTypeOld(MethodParameter returnType) {
        // 如果是iterable或者array，都只能作为asyncMessage消息处理
        boolean isIterableType = Iterable.class.isAssignableFrom(returnType.getParameterType());
        boolean isArrayType = returnType.getParameterType().isArray();
        // 如果是群发消息，只能作为asyncMessage消息处理
        boolean isGroupMessage = WxGroupMessage.class.isAssignableFrom(returnType.getParameterType());
        // 如果是模板消息，只能作为asyncMessage消息处理
        boolean isTemplateMessage = WxTemplateMessage.class.isAssignableFrom(returnType.getParameterType());
        // 如果是模板消息，只能作为asyncMessage消息处理
        boolean isMiniProgramMessage = WxUserMessage.MiniProgram.class.isAssignableFrom(returnType.getParameterType());
        // 理论上WxAsyncMessage已经被上层处理过了，这里保险起见再处理一次
        boolean needAsyncSend = isIterableType || isArrayType || isGroupMessage || isTemplateMessage || isMiniProgramMessage;
        Class realType = getGenericType(returnType);
        boolean isWxMessage = WxMessage.class.isAssignableFrom(realType);
        boolean isWxStringMessage = CharSequence.class.isAssignableFrom(realType) &&
                returnType.hasMethodAnnotation(WxMapping.class);
        return needAsyncSend && (isWxMessage || isWxStringMessage);
    }

}
