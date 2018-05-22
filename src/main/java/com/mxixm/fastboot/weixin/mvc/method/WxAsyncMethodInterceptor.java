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

import com.mxixm.fastboot.weixin.exception.WxApiException;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageTemplate;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

/**
 * fastboot-weixin  WxAsyncMethodInterceptor
 *
 * @author Guangshan
 * @date 2017/10/18 22:40
 * @since 0.2.1
 */
public class WxAsyncMethodInterceptor implements MethodInterceptor {

    public final WxAsyncMessageTemplate wxAsyncMessageTemplate;

    public WxAsyncMethodInterceptor(WxAsyncMessageTemplate wxAsyncMessageTemplate) {
        this.wxAsyncMessageTemplate = wxAsyncMessageTemplate;
    }

    /**
     * 关于这个方法，还发现了一个很神奇的现象。。。
     * 当我调试时，偶尔会发现微信会收到消息，消息内容是被代理的对象的toString()，这就奇怪了，从哪里来的呢？
     * 而且不调试时没这个问题。。。仔细分析发现，应该就是和调试时调试器会调用对象的toString方法导致的
     * 因为调用了代理对象的toString，进入这个拦截器，只要进入这个拦截器，最终就会调用send。因为toString()最终返回string，故会发出那个消息。。。
     * 又一次被调试时toString坑了。添加方法过滤解决此问题。
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (ReflectionUtils.isObjectMethod(invocation.getMethod())) {
            return ReflectionUtils.invokeMethod(invocation.getMethod(), invocation.getThis(), invocation.getArguments());
        }
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest();
        wxAsyncMessageTemplate.send(wxRequest, () -> {
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                throw new WxApiException(e.getMessage(), e);
            }
        });
        return null;
    }

}
