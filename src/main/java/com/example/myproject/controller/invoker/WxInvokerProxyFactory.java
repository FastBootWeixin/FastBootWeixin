package com.example.myproject.controller.invoker;

import com.example.myproject.config.invoker.WxUrlProperties;
import com.example.myproject.controller.invoker.executor.WxApiExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/9.
 */
public class WxInvokerProxyFactory<T> implements InitializingBean, MethodInterceptor, BeanClassLoaderAware, FactoryBean<T> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());
    // private WxInvokerController wxInvokerTemplateMock = MvcUriComponentsBuilder.on(WxInvokerController.class);

    private static final ObjectMapper jsonConvert = new ObjectMapper();

    private static final String WX_ACCESS_TOKEN_PARAM_NAME = "access_token";

    private final WxApiTypeInfo wxApiTypeInfo;

    private final Map<Method, WxApiMethodInfo> methodCache = new HashMap<>();

    private ClassLoader beanClassLoader;

    // 被代理的接口
    private Class<T> clazz;

    private T proxy;

    private final WxApiExecutor wxApiExecutor;

    // 理论上构造方法上不能做这么多事的，以后再优化
    public WxInvokerProxyFactory(Class<T> clazz, WxUrlProperties wxUrlProperties, WxApiExecutor wxApiExecutor) {
        this.clazz = clazz;
        this.wxApiExecutor = wxApiExecutor;
        this.wxApiTypeInfo = new WxApiTypeInfo(clazz, wxUrlProperties.getHost());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        proxy = (T) new ProxyFactory(clazz, this).getProxy(getBeanClassLoader());
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    /**
     * 后续加上缓存，一定要加
     *
     * @param inv
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation inv) throws Throwable {
        if (ReflectionUtils.isObjectMethod(inv.getMethod())) {
            return ReflectionUtils.invokeMethod(inv.getMethod(), inv.getThis(), inv.getArguments());
        }
        WxApiMethodInfo wxApiMethodInfo = methodCache.get(inv.getMethod());
        if (wxApiMethodInfo == null) {
            wxApiMethodInfo = new WxApiMethodInfo(inv.getMethod(), wxApiTypeInfo);
            methodCache.put(inv.getMethod(), wxApiMethodInfo);
        }
        return wxApiExecutor.execute(wxApiMethodInfo, inv.getArguments());
    }

    @Override
    public T getObject() throws Exception {
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


    /**
     @Override public Object invoke(MethodInvocation inv) throws Throwable {
     if (ReflectionUtils.isObjectMethod(inv.getMethod())) {
     return ReflectionUtils.invokeMethod(inv.getMethod(), inv.getThis(), inv.getArguments());
     }
     // 本来想用高大上一点的mock调用来实现的，但是mock的原理是代理，final的类即String为返回值时不能被代理，故作罢
     // Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
     UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethod(baseBuilder, clazz, inv.getMethod(), inv.getArguments());
     System.out.println(builder.build().toString());
     return "hahaha";
     }

     @Override public Object invoke(MethodInvocation inv) throws Throwable {
     Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
     UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(baseBuilder, mockReturnValue);
     System.out.println(builder.build().toString());
     return "hahaha";
     }
     */
}
