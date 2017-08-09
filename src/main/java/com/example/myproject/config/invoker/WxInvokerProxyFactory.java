package com.example.myproject.config.invoker;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/8/9.
 */
public class WxInvokerProxyFactory implements InitializingBean, MethodInterceptor, BeanClassLoaderAware, FactoryBean<WxInvokerController> {

    private WxInvokerController wxInvokerTemplateMock = MvcUriComponentsBuilder.on(WxInvokerController.class);

    private WxInvokerController proxy;

    private ClassLoader beanClassLoader;

    private UriComponentsBuilder defaultBuilder = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com");

    @Override
    public WxInvokerController getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return WxInvokerController.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        proxy = (WxInvokerController) new ProxyFactory(WxInvokerController.class, this).getProxy(getBeanClassLoader());
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    @Override
    public Object invoke(MethodInvocation inv) throws Throwable {
        // 本来想用高大上一点的mock调用来实现的，但是mock的原理是代理，final的类即String为返回值时不能被代理，故作罢
//        Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethod(defaultBuilder, WxInvokerController.class, inv.getMethod(), inv.getArguments());
        System.out.println(builder.build().toString());
        return "hahaha";
    }
//    @Override
//    public Object invoke(MethodInvocation inv) throws Throwable {
//        Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
//        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(defaultBuilder, mockReturnValue);
//        System.out.println(builder.build().toString());
//        return "hahaha";
//    }

}
