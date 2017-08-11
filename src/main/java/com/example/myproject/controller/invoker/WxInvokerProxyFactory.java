package com.example.myproject.controller.invoker;

import com.example.myproject.config.invoker.WxUrlProperties;
import com.example.myproject.controller.invoker.annotation.WxApiBody;
import com.example.myproject.controller.invoker.annotation.WxApiForm;
import com.example.myproject.controller.invoker.annotation.WxApiRequest;
import com.example.myproject.controller.invoker.contributor.WxApiParamContributor;
import com.example.myproject.controller.invoker.contributor.WxApiPathContributor;
import com.example.myproject.support.AccessTokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2017/8/9.
 */
public class WxInvokerProxyFactory<T> implements InitializingBean, MethodInterceptor, BeanClassLoaderAware, BeanFactoryAware, FactoryBean<T> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());
    // private WxInvokerController wxInvokerTemplateMock = MvcUriComponentsBuilder.on(WxInvokerController.class);

    private static final CompositeUriComponentsContributor defaultUriComponentsContributor = new CompositeUriComponentsContributor(new WxApiParamContributor(), new WxApiPathContributor());

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private static final ObjectMapper jsonConvert = new ObjectMapper();

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

    private final WxUrlProperties wxUrlProperties;

    private final RestTemplate wxApiInvokerExecutor;

    private final AccessTokenManager accessTokenManager;

    // 理论上构造方法上不能做这么多事的，以后再优化
    public WxInvokerProxyFactory(Class<T> clazz, WxUrlProperties wxUrlProperties, AccessTokenManager accessTokenManager, RestTemplate wxApiInvokerExecutor) {
        this.clazz = clazz;
        this.wxUrlProperties = wxUrlProperties;
        this.accessTokenManager = accessTokenManager;
        this.wxApiInvokerExecutor = wxApiInvokerExecutor;
        // 取到根路径
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(clazz, WxApiRequest.class);
        String host = getTypeWxApiHost(wxApiRequest);
        String typePath = getTypeWxApiRequestPath(wxApiRequest);
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
        UriComponentsBuilder builder = fromMethod(inv.getMethod(), inv.getArguments());
        // 替换accessToken
        builder.replaceQueryParam(WX_ACCESS_TOKEN_PARAM_NAME, accessTokenManager.getToken());
        WxApiRequest.Method method = getMethodWxApiRequestMethod(inv.getMethod());

        String result;
        switch (method) {
            case GET: {
                wxApiInvokerExecutor.getForObject(builder.toUriString(), String.class);
                break;
            }
            case JSON: {
                String body = jsonConvert.writeValueAsString()
            }
        }

        return result;
    }

    private String getJsonBody(Method method, Object[] arguments) {
        for (Parameter p : method.getParameters()) {

        }

    }

    /**
     * 获取一个application/json头
     *
     * @return
     */
    private HttpHeaders buildJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * 获取一个application/json头
     *
     * @return
     */
    private HttpHeaders buildXmlHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        return headers;
    }

    private UriComponentsBuilder fromMethod(Method method, Object... args) {
        UriComponentsBuilder base = baseBuilder.cloneBuilder();
//        String typePath = baseBuilder.
//        String path = pathMatcher.combine(typePath, methodPath);
        String methodPath = getMethodWxApiRequestPath(method);
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

    private String getTypeWxApiRequestPath(WxApiRequest wxApiRequest) {
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.path()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.path())) {
            return "/";
        }
        return wxApiRequest.path();
    }

    private String getMethodWxApiRequestPath(Method method) {
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(method, WxApiRequest.class);
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.path()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.path())) {
            // 默认情况下取方法名为变量名，尝试从环境变量中获取信息
            return resolveStringValue("${" + propertyPrefix + "." + method.getName() + "}");
        }
        return wxApiRequest.path();
    }

    /**
     * 尝试获取请求方法，逻辑看里面
     * 有以下几种情况：1、简单类型参数与总参数相同，获取注解上的请求方式
     * 1、简单类型参数比总参数少1，即有一个请求body，则可能有两种方式，一种是表单，一种是整个请求体，如何去区分？
     * 2、少多个，则以表单提交
     *
     * @param method
     * @return
     */
    private WxApiRequest.Method getMethodWxApiRequestMethod(Method method) {
        Stream<Parameter> parameterStream = Arrays.stream(method.getParameters());
        // 是不是全是简单属性,简单属性的数量
        List<Parameter> parameters = parameterStream.filter(p -> BeanUtils.isSimpleValueType(p.getType()))
                // 简单属性上是否有body注解
                .filter(p -> !(AnnotatedElementUtils.isAnnotated(p, WxApiBody.class) || AnnotatedElementUtils.isAnnotated(p, WxApiForm.class)))
                .collect(Collectors.toList());
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(method, WxApiRequest.class);
        // 简单参数数量相同
        if (parameters.size() == method.getParameters().length) {
            if (wxApiRequest == null) {
                return WxApiRequest.Method.GET;
            } else {
                return wxApiRequest.method();
            }
        }
        // 非简单参数多于一个，只能是FORM表单形式
        if (method.getParameters().length - parameters.size() > 1) {
            return WxApiRequest.Method.FORM;
        }
        // 如果有一个是文件则以FORM形式提交
        boolean isMutlipartForm = parameterStream.filter(p -> isMutlipartForm(p.getType())).findFirst().isPresent();
        if (isMutlipartForm) {
            return WxApiRequest.Method.FORM;
        }
        WxApiForm wxApiForm = parameterStream.filter(p -> AnnotatedElementUtils.isAnnotated(p, WxApiForm.class))
                .map(p -> AnnotatedElementUtils.findMergedAnnotation(method, WxApiForm.class))
                .findFirst().orElse(null);
        if (wxApiForm != null) {
            return WxApiRequest.Method.FORM;
        }
        WxApiBody wxApiBody = parameterStream.filter(p -> AnnotatedElementUtils.isAnnotated(p, WxApiBody.class))
                .map(p -> AnnotatedElementUtils.findMergedAnnotation(method, WxApiBody.class))
                .findFirst().orElse(null);
        if (wxApiBody == null) {
            return WxApiRequest.Method.JSON;
        }
        return WxApiRequest.Method.valueOf(wxApiBody.type().name());
    }

    /**
     * 暂时只支持这几种类型
     *
     * @param paramType
     * @return
     */
    private boolean isMutlipartForm(Class paramType) {
        return (InputStream.class.isAssignableFrom(paramType) ||
                Reader.class.isAssignableFrom(paramType) ||
                File.class.isAssignableFrom(paramType) ||
                Resource.class.isAssignableFrom(paramType));
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
