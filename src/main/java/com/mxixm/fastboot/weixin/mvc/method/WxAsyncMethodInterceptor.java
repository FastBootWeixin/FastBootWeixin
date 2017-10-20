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
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
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
