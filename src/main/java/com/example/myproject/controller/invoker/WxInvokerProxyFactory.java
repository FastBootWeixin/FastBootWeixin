package com.example.myproject.controller.invoker;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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

    private static final CompositeUriComponentsContributor defaultUriComponentsContributor;

    static {
        defaultUriComponentsContributor = new CompositeUriComponentsContributor(
                new PathVariableMethodArgumentResolver(), new RequestParamMethodArgumentResolver(true));
    }

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private UriComponentsBuilder defaultBuilder = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com");

    private ClassLoader beanClassLoader;

    // 被代理的接口
    private Class<T> clazz;

    private T proxy;

    public WxInvokerProxyFactory(Class<T> clazz) {
        this.clazz = clazz;
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

    @Override
    public Object invoke(MethodInvocation inv) throws Throwable {
        if (ReflectionUtils.isObjectMethod(inv.getMethod())) {
            return ReflectionUtils.invokeMethod(inv.getMethod(), inv.getThis(), inv.getArguments());
        }
        // 本来想用高大上一点的mock调用来实现的，但是mock的原理是代理，final的类即String为返回值时不能被代理，故作罢
        // Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethod(defaultBuilder, clazz, inv.getMethod(), inv.getArguments());
        System.out.println(builder.build().toString());
        return "hahaha";
    }


    private static UriComponentsBuilder fromMethodInternal(UriComponentsBuilder baseUrl,
                                                           Class<?> controllerType, Method method, Object... args) {

        baseUrl = baseUrl.cloneBuilder();
        String typePath = getTypeRequestMapping(controllerType);
        String methodPath = getMethodRequestMapping(method);
        String path = pathMatcher.combine(typePath, methodPath);
        baseUrl.path(path);
        UriComponents uriComponents = applyContributors(baseUrl, method, args);
        return UriComponentsBuilder.newInstance().uriComponents(uriComponents);
    }

    private static String getTypeRequestMapping(Class<?> controllerType) {
        Assert.notNull(controllerType, "'controllerType' must not be null");
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(controllerType, RequestMapping.class);
        if (requestMapping == null) {
            return "/";
        }
        String[] paths = requestMapping.path();
        if (ObjectUtils.isEmpty(paths) || StringUtils.isEmpty(paths[0])) {
            return "/";
        }
        if (paths.length > 1 && logger.isWarnEnabled()) {
            logger.warn("Multiple paths on controller " + controllerType.getName() + ", using first one");
        }
        return paths[0];
    }

    private static String getMethodRequestMapping(Method method) {
        Assert.notNull(method, "'method' must not be null");
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping == null) {
            throw new IllegalArgumentException("No @RequestMapping on: " + method.toGenericString());
        }
        String[] paths = requestMapping.path();
        if (ObjectUtils.isEmpty(paths) || StringUtils.isEmpty(paths[0])) {
            return "/";
        }
        if (paths.length > 1 && logger.isWarnEnabled()) {
            logger.warn("Multiple paths on method " + method.toGenericString() + ", using first one");
        }
        return paths[0];
    }

    private static UriComponents applyContributors(UriComponentsBuilder builder, Method method, Object... args) {
        CompositeUriComponentsContributor contributor = defaultUriComponentsContributor;
        if (contributor == null) {
            logger.debug("Using default CompositeUriComponentsContributor");
            contributor = defaultUriComponentsContributor;
        }

        int paramCount = method.getParameterTypes().length;
        int argCount = args.length;
        if (paramCount != argCount) {
            throw new IllegalArgumentException("Number of method parameters " + paramCount +
                    " does not match number of argument values " + argCount);
        }

        final Map<String, Object> uriVars = new HashMap<String, Object>();
        for (int i = 0; i < paramCount; i++) {
            MethodParameter param = new SynthesizingMethodParameter(method, i);
            param.initParameterNameDiscovery(parameterNameDiscoverer);
            contributor.contributeMethodArgument(param, args[i], builder, uriVars);
        }

        // We may not have all URI var values, expand only what we have
        return builder.build().expand(new UriComponents.UriTemplateVariables() {
            @Override
            public Object getValue(String name) {
                return uriVars.containsKey(name) ? uriVars.get(name) : UriComponents.UriTemplateVariables.SKIP_VALUE;
            }
        });
    }




//    @Override
//    public Object invoke(MethodInvocation inv) throws Throwable {
//        Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
//        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(defaultBuilder, mockReturnValue);
//        System.out.println(builder.build().toString());
//        return "hahaha";
//    }

}
