package com.mxixm.fastbootwx.module.message.support;

import com.mxixm.fastbootwx.annotation.WxAsyncMessage;
import com.mxixm.fastbootwx.annotation.WxButton;
import com.mxixm.fastbootwx.config.message.WxAsyncMessageProperties;
import com.mxixm.fastbootwx.module.WxRequest;
import com.mxixm.fastbootwx.module.message.WxMessage;
import com.mxixm.fastbootwx.module.message.WxMessageTemplate;
import com.mxixm.fastbootwx.mvc.WxRequestResponseUtils;
import com.mxixm.fastbootwx.mvc.annotation.WxMessageMapping;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WxAsyncMessageReturnValueHandler implements HandlerMethodReturnValueHandler, InitializingBean, DisposableBean {

    // 异步执行器
    private ThreadPoolExecutor asyncExecutor;

    private WxMessageTemplate wxMessageTemplate;

    private WxAsyncMessageProperties wxAsyncMessageProperties;

    public WxAsyncMessageReturnValueHandler(WxAsyncMessageProperties wxAsyncMessageProperties, WxMessageTemplate wxMessageTemplate) {
        this.wxAsyncMessageProperties = wxAsyncMessageProperties;
        this.wxMessageTemplate = wxMessageTemplate;
    }

    /**
     * 有WxAsyncMessage注解且
     * 返回值是WxMessage的子类
     * 或者是CharSequence的子类，且有注解WxButton或者WXMessageMapping
     *
     * @param returnType
     * @return
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        boolean isWxAsyncMessage = AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), WxAsyncMessage.class) ||
                returnType.hasMethodAnnotation(WxAsyncMessage.class);
        boolean isWxMessage = WxMessage.class.isAssignableFrom(returnType.getParameterType());
        boolean isWxStringMessage = CharSequence.class.isAssignableFrom(returnType.getParameterType()) &&
                (returnType.hasMethodAnnotation(WxButton.class) || returnType.hasMethodAnnotation(WxMessageMapping.class));
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
        WxRequest wxRequest = WxRequestResponseUtils.getWxRequestFromRequestAttribute(request);
        this.asyncExecutor.submit(() -> {
            if (returnValue instanceof WxMessage) {
                wxMessageTemplate.sendMessage(wxRequest, (WxMessage) returnValue);
            } else {
                wxMessageTemplate.sendMessage(wxRequest, returnValue.toString());
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.asyncExecutor = new ThreadPoolExecutor(
                // 正常情况下的线程数，默认6
                wxAsyncMessageProperties.getPoolCoreSize(),
                // 线程池最大线程数，默认12
                wxAsyncMessageProperties.getPoolMaxSize(),
                // 线程存活时间：多少秒，默认80秒
                wxAsyncMessageProperties.getPoolKeepAliveInSeconds(), TimeUnit.SECONDS,
                // 使用arrayList阻塞队列，默认10000
                new ArrayBlockingQueue<>(wxAsyncMessageProperties.getMaxQueueSize()),
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
