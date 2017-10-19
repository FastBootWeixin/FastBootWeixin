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

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FastBootWeixin WxAsyncMessageReturnValueHandler
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public class WxAsyncMessageTemplate implements InitializingBean, DisposableBean {

    // 异步执行器
    private ThreadPoolExecutor asyncExecutor;

    private WxMessageTemplate wxMessageTemplate;

    private WxProperties wxProperties;

    public WxAsyncMessageTemplate(WxProperties wxProperties, WxMessageTemplate wxMessageTemplate) {
        this.wxProperties = wxProperties;
        this.wxMessageTemplate = wxMessageTemplate;
    }

    public void send(WxRequest wxRequest, Object returnValue) {
        this.asyncExecutor.submit(() -> sendMessage(wxRequest, returnValue));
    }

    public void send(WxRequest wxRequest, Supplier supplier) {
        this.asyncExecutor.submit(() -> sendMessage(wxRequest, supplier.get()));
    }

    private void sendMessage(WxRequest wxRequest, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof WxMessage) {
            wxMessageTemplate.sendMessage(wxRequest, (WxMessage) value);
        } else if (value instanceof CharSequence) {
            wxMessageTemplate.sendMessage(wxRequest, value.toString());
        } else if (value instanceof Iterable) {
            ((Iterable) value).forEach(v -> sendMessage(wxRequest, v));
        } else if (value.getClass().isArray()) {
            Arrays.stream((Object[]) value).forEach(v -> sendMessage(wxRequest, v));
        }
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
