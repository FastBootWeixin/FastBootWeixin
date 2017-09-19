/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.module.message.support;

import com.mxixm.fastboot.weixin.annotation.WxAsyncMessage;
import com.mxixm.fastboot.weixin.annotation.WxMapping;
import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.mvc.WxWebUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WxAsyncMessageReturnValueHandler implements HandlerMethodReturnValueHandler, InitializingBean, DisposableBean {

    // 异步执行器
    private ThreadPoolExecutor asyncExecutor;

    private WxMessageTemplate wxMessageTemplate;

    private WxProperties wxProperties;

    public WxAsyncMessageReturnValueHandler(WxProperties wxProperties, WxMessageTemplate wxMessageTemplate) {
        this.wxProperties = wxProperties;
        this.wxMessageTemplate = wxMessageTemplate;
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
        boolean isWxAsyncMessage = AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), WxAsyncMessage.class) ||
                returnType.hasMethodAnnotation(WxAsyncMessage.class) || isIterableType || isArrayType;
        Class realType = getRealType(returnType);
        boolean isWxMessage = WxMessage.class.isAssignableFrom(realType);
        boolean isWxStringMessage = CharSequence.class.isAssignableFrom(realType) &&
                returnType.hasMethodAnnotation(WxMapping.class);
        return isWxAsyncMessage && (isWxMessage || isWxStringMessage);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                  ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
        outputMessage.getBody();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequestAttribute(request);
        this.asyncExecutor.submit(() -> {
            if (returnValue instanceof WxMessage) {
                wxMessageTemplate.sendMessage(wxRequest, (WxMessage) returnValue);
            } else if (returnValue instanceof CharSequence) {
                wxMessageTemplate.sendMessage(wxRequest, returnValue.toString());
            } else if (returnValue instanceof Iterable) {
                if (CharSequence.class.isAssignableFrom(getRealType(returnType))) {
                    ((Iterable) returnValue).forEach(v -> wxMessageTemplate.sendMessage(wxRequest, v.toString()));
                } else if (WxMessage.class.isAssignableFrom(getRealType(returnType))) {
                    ((Iterable) returnValue).forEach(v -> wxMessageTemplate.sendMessage(wxRequest, (WxMessage) v));
                }
            } else if (returnType.getParameterType().isArray()) {
                if (CharSequence.class.isAssignableFrom(getRealType(returnType))) {
                    Arrays.stream((Object[]) returnValue).forEach(v -> wxMessageTemplate.sendMessage(wxRequest, v.toString()));
                } else if (WxMessage.class.isAssignableFrom(getRealType(returnType))) {
                    Arrays.stream((WxMessage[]) returnValue).forEach(v -> wxMessageTemplate.sendMessage(wxRequest, (WxMessage) v));
                }
            }
        });
    }

    private Class getRealType(MethodParameter returnType) {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        this.asyncExecutor = new ThreadPoolExecutor(
                // 正常情况下的线程数，默认6
                wxProperties.getMessage().getPoolCoreSize(),
                // 线程池最大线程数，默认12
                wxProperties.getMessage().getPoolMaxSize(),
                // 线程存活时间：多少秒，默认80秒
                wxProperties.getMessage().getPoolKeepAliveInSeconds(), TimeUnit.SECONDS,
                // 使用arrayList阻塞队列，默认10000
                new ArrayBlockingQueue<>(wxProperties.getMessage().getMaxQueueSize()),
                // 线程名
                new WxAsyncMessageThreadFactory(Executors.defaultThreadFactory()),
                // 忽视最早入队的日志
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Override
    public void destroy() {
        if (this.asyncExecutor != null) {
            this.asyncExecutor.shutdown();
        }
    }

    private static class WxAsyncMessageThreadFactory implements ThreadFactory {
        private final ThreadFactory delegate;
        private final Thread.UncaughtExceptionHandler exceptionHandler;

        public WxAsyncMessageThreadFactory(ThreadFactory threadFactory) {
            super();
            this.delegate = threadFactory;
            this.exceptionHandler = new LogUncaughtExceptionHandler();
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = this.delegate.newThread(r);
            t.setUncaughtExceptionHandler(exceptionHandler);
            t.setName("WxAsyncMessage-Sender-" + t.getName());
            return t;
        }
    }

    private static class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private static final Logger logger = Logger.getLogger("WxAsyncMessage");

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.log(Level.SEVERE, "线程：" + t.getName() + ",执行异常", e);
        }
    }

}
