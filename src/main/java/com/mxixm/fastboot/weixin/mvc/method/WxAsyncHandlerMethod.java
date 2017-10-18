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

package com.mxixm.fastboot.weixin.mvc.method;

import com.mxixm.fastboot.weixin.controller.WxBuildinVerify;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * fastboot-weixin  WxAsyncHandlerMethod
 * 做成异步消息队列
 *
 * @author Guangshan
 * @date 2017/10/18 22:40
 * @since 0.2.1
 */
public class WxAsyncHandlerMethod extends HandlerMethod {

    private WxRequest wxRequest;

    // 异步执行器
    private ThreadPoolExecutor asyncExecutor;

    // 封装一个单独的类用于包装asyncExecutor、wxMessageTemplate等
    private WxMessageTemplate wxMessageTemplate;

    private final Method delegateMethod = ClassUtils.getMethod(AsyncMethod.class, "call", (Class<?>[]) null);

    // 代理对象
    private AsyncMethod asyncMethod;

    /**
     * 参考spring的异步怎么做的
     */
    public WxAsyncHandlerMethod(WxRequest wxRequest, ThreadPoolExecutor asyncExecutor, Object bean, Method method) {
        super(bean, method);
        this.wxRequest = wxRequest;
        this.asyncExecutor = asyncExecutor;
        this.asyncMethod = new AsyncMethod(super.getBean(), super.getBridgedMethod());
    }

    @Override
    protected Method getBridgedMethod() {
        return delegateMethod;
    }

    @Override
    public Object getBean() {
        return asyncMethod;
    }

    private static class AsyncMethod {

        private Method method;

        private Object bean;

        AsyncMethod(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        public void call(Object[] args) {
            try {
                Object returnValue = method.invoke(bean, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

}
