package com.example.myproject.controller.invoker;

import com.example.myproject.common.WxBeanNames;
import com.example.myproject.config.invoker.WxUrlProperties;
import com.example.myproject.controller.invoker.annotation.WxApiRequest;
import com.example.myproject.controller.invoker.contributor.WxApiParamContributor;
import com.example.myproject.controller.invoker.contributor.WxApiPathContributor;
import com.example.myproject.support.AccessTokenManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/9.
 */
public class WxInvokerProxyFactory<T> implements InitializingBean, MethodInterceptor, BeanClassLoaderAware, BeanFactoryAware, FactoryBean<T> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());
    // private WxInvokerController wxInvokerTemplateMock = MvcUriComponentsBuilder.on(WxInvokerController.class);

    private static final CompositeUriComponentsContributor defaultUriComponentsContributor = new CompositeUriComponentsContributor(new WxApiParamContributor(), new WxApiPathContributor());

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final String WX_API_PROPERTY_PREFIX = "wx.api.url";

    private static final String WX_ACCESS_TOKEN_PARAM_NAME = "access_token";

    private final UriComponentsBuilder baseBuilder;

    private final String propertyPrefix;

    private ClassLoader beanClassLoader;

    private ConfigurableBeanFactory configurableBeanFactory;

    private BeanExpressionContext expressionContext;

    // 被代理的接口
    private Class<T> clazz;

    private T proxy;

    @Autowired
    private WxUrlProperties wxUrlProperties;

    @Autowired
    @Qualifier(WxBeanNames.API_INVOKER_REST_TEMPLATE_NAME)
    private RestTemplate wxApiInvokerExecutor;

    @Autowired
    private AccessTokenManager accessTokenManager;

    // 理论上构造方法上不能做这么多事的，以后再优化
    public WxInvokerProxyFactory(Class<T> clazz, WxUrlProperties wxUrlProperties, AccessTokenManager accessTokenManager, RestTemplate wxApiInvokerExecutor) {
        this.clazz = clazz;
        this.wxUrlProperties = wxUrlProperties;
        this.accessTokenManager = accessTokenManager;
        this.wxApiInvokerExecutor = wxApiInvokerExecutor;
        // 取到根路径
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(clazz, WxApiRequest.class);
        String host = getTypeWxApiHost(wxApiRequest);
        String typePath = getTypeWxApiRequest(wxApiRequest);
        // 固定https请求
        propertyPrefix = getTypeWxApiPropertyPrefix(wxApiRequest);
        baseBuilder = UriComponentsBuilder.newInstance().scheme("https").host(host).path(typePath);
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
     * @param inv
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation inv) throws Throwable {
        if (ReflectionUtils.isObjectMethod(inv.getMethod())) {
            return ReflectionUtils.invokeMethod(inv.getMethod(), inv.getThis(), inv.getArguments());
        }
        UriComponentsBuilder builder = fromMethod(inv.getMethod(), inv.getArguments());
        // 替换accessToken
        builder.replaceQueryParam(WX_ACCESS_TOKEN_PARAM_NAME, accessTokenManager.getToken());
        String result = wxApiInvokerExecutor.getForObject(builder.toUriString(), String.class);
        return result;
    }


    private UriComponentsBuilder fromMethod(Method method, Object... args) {
        UriComponentsBuilder base = baseBuilder.cloneBuilder();
//        String typePath = baseBuilder.
//        String path = pathMatcher.combine(typePath, methodPath);
        String methodPath = getMethodWxApiRequest(method);
        base.path(methodPath);
        UriComponents uriComponents = applyContributors(base, method, args);
        return UriComponentsBuilder.newInstance().uriComponents(uriComponents);
    }

    private String getTypeWxApiHost(WxApiRequest wxApiRequest) {
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.host()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.host())) {
            return wxUrlProperties.getHost();
        }
        return wxApiRequest.host();
    }

    private String getTypeWxApiPropertyPrefix(WxApiRequest wxApiRequest) {
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.prefix()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.prefix())) {
            return WX_API_PROPERTY_PREFIX;
        }
        return wxApiRequest.prefix();
    }

    private String getTypeWxApiRequest(WxApiRequest wxApiRequest) {
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.path()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.path())) {
            return "/";
        }
        return wxApiRequest.path();
    }

    private String getMethodWxApiRequest(Method method) {
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(method, WxApiRequest.class);
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.path()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.path())) {
            // 默认情况下取方法名为变量名，尝试从环境变量中获取信息
            return resolveStringValue("${" + propertyPrefix + "." + method.getName() + "}");
        }
        return wxApiRequest.path();
    }

    private UriComponents applyContributors(UriComponentsBuilder builder, Method method, Object... args) {
        CompositeUriComponentsContributor contributor = defaultUriComponentsContributor;
        int paramCount = method.getParameterTypes().length;
        int argCount = args.length;
        if (paramCount != argCount) {
            throw new IllegalArgumentException("方法参数量为" + paramCount + " 与真实参数量不匹配，真实参数量为" + argCount);
        }
        final Map<String, Object> uriVars = new HashMap<String, Object>();
        for (int i = 0; i < paramCount; i++) {
            MethodParameter param = new SynthesizingMethodParameter(method, i);
            param.initParameterNameDiscovery(parameterNameDiscoverer);
            contributor.contributeMethodArgument(param, args[i], builder, uriVars);
        }
        // We may not have all URI var values, expand only what we have
        return builder.build().expand(name -> uriVars.containsKey(name) ? uriVars.get(name) : UriComponents.UriTemplateVariables.SKIP_VALUE);
    }


    /**
     * 解析参数值并执行spel表达式，得到最终结果
     * Spring的@Value也是这样做的
     */
    private String resolveStringValue(String value) {
        if (this.configurableBeanFactory == null) {
            return value;
        }
        String placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(value);
        BeanExpressionResolver exprResolver = this.configurableBeanFactory.getBeanExpressionResolver();
        if (exprResolver == null) {
            return value;
        }
        return exprResolver.evaluate(placeholdersResolved, this.expressionContext).toString();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
            this.expressionContext = new BeanExpressionContext(configurableBeanFactory, null);
        }
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
    @Override
    public Object invoke(MethodInvocation inv) throws Throwable {
        if (ReflectionUtils.isObjectMethod(inv.getMethod())) {
            return ReflectionUtils.invokeMethod(inv.getMethod(), inv.getThis(), inv.getArguments());
        }
        // 本来想用高大上一点的mock调用来实现的，但是mock的原理是代理，final的类即String为返回值时不能被代理，故作罢
        // Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethod(baseBuilder, clazz, inv.getMethod(), inv.getArguments());
        System.out.println(builder.build().toString());
        return "hahaha";
    }

    @Override
    public Object invoke(MethodInvocation inv) throws Throwable {
        Object mockReturnValue = ReflectionUtils.invokeMethod(inv.getMethod(), wxInvokerTemplateMock, inv.getArguments());
        UriComponentsBuilder builder = MvcUriComponentsBuilder.fromMethodCall(baseBuilder, mockReturnValue);
        System.out.println(builder.build().toString());
        return "hahaha";
    }
    */
}
