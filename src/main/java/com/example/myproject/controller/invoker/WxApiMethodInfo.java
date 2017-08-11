package com.example.myproject.controller.invoker;

import com.example.myproject.controller.invoker.annotation.WxApiBody;
import com.example.myproject.controller.invoker.annotation.WxApiForm;
import com.example.myproject.controller.invoker.annotation.WxApiRequest;
import com.example.myproject.controller.invoker.contributor.WxApiParamContributor;
import com.example.myproject.controller.invoker.contributor.WxApiPathContributor;
import com.example.myproject.util.WxApplicationContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FastBootWeixin  WxApiMethodInfo
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiMethodInfo
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/11 20:53
 */
public class WxApiMethodInfo {

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    private static final CompositeUriComponentsContributor defaultUriComponentsContributor = new CompositeUriComponentsContributor(new WxApiParamContributor(), new WxApiPathContributor());

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final Method method;

    private final WxApiTypeInfo wxApiTypeInfo;

    private final UriComponentsBuilder baseBuilder;

    private final WxApiRequest.Method requestMethod;

    private boolean isMutlipartRequest = false;

    private boolean isWxApiFormPresent = false;

    private boolean isWxApiBodyPresent = false;

    private List<Class> parameterTypeOrAnnotations;

    public WxApiMethodInfo(Method method, WxApiTypeInfo wxApiTypeInfo) {
        this.method = method;
        this.wxApiTypeInfo = wxApiTypeInfo;
        String methodPath = getMethodWxApiRequestPath(method);
        baseBuilder = wxApiTypeInfo.getBaseBuilder().path(methodPath);
        requestMethod = prepareRequestInfo(method);
    }

    private String getMethodWxApiRequestPath(Method method) {
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(method, WxApiRequest.class);
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.path()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.path())) {
            // 默认情况下取方法名为变量名，尝试从环境变量中获取信息
            return WxApplicationContextUtils.resolveStringValue("${" + this.wxApiTypeInfo.getPropertyPrefix() + "." + method.getName() + "}");
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
    private WxApiRequest.Method prepareRequestInfo(Method method) {
        method.getParameterTypes();
        // 保存参数类型，如果有设置的注解类型则为注解类型
        parameterTypeOrAnnotations = Arrays.stream(method.getParameters()).map(p -> {
            if (AnnotatedElementUtils.isAnnotated(p, WxApiBody.class)) {
                return WxApiBody.class;
            } else if (AnnotatedElementUtils.isAnnotated(p, WxApiForm.class)) {
                return WxApiForm.class;
            } else {
                return p.getType();
            }
        }).collect(Collectors.toList());
        // 是不是全是简单属性,简单属性的数量
        long simpleTypeCount = parameterTypeOrAnnotations.stream().filter(p -> BeanUtils.isSimpleValueType(p)).count();
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(method, WxApiRequest.class);
        // 简单参数数量相同
        if (simpleTypeCount == method.getParameterCount()) {
            if (wxApiRequest == null) {
                return WxApiRequest.Method.GET;
            } else {
                return wxApiRequest.method();
            }
        }
        // 非简单参数多于一个，只能是FORM表单形式
        if (method.getParameterCount() - simpleTypeCount > 1) {
            // 默认出现了wxApiForm
            isWxApiFormPresent = true;
            return WxApiRequest.Method.FORM;
        }
        // 如果有一个是文件则以FORM形式提交
        isMutlipartRequest = Arrays.stream(method.getParameters()).filter(p -> isMutlipartForm(p.getType())).findFirst().isPresent();
        if (isMutlipartRequest) {
            isWxApiFormPresent = true;
            return WxApiRequest.Method.FORM;
        }
        // 如果有ApiForm注解，则直接以FORM方式提交
        isWxApiFormPresent = parameterTypeOrAnnotations.stream().filter(p -> p == WxApiForm.class).findFirst().isPresent();
        if (isWxApiFormPresent) {
            return WxApiRequest.Method.FORM;
        }
        WxApiBody wxApiBody = Arrays.stream(method.getParameters()).filter(p -> AnnotatedElementUtils.isAnnotated(p, WxApiBody.class))
                .map(p -> AnnotatedElementUtils.findMergedAnnotation(p, WxApiBody.class))
                .findFirst().orElse(null);
        if (wxApiBody == null) {
            return WxApiRequest.Method.JSON;
        }
        isWxApiBodyPresent = true;
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

    public UriComponentsBuilder fromArgs(Object... args) {
        UriComponentsBuilder base = baseBuilder.cloneBuilder();
        UriComponents uriComponents = applyContributors(base, method, args);
        return UriComponentsBuilder.newInstance().uriComponents(uriComponents);
    }

    private UriComponents applyContributors(UriComponentsBuilder builder, Method method, Object... args) {
        CompositeUriComponentsContributor contributor = defaultUriComponentsContributor;
        int paramCount = method.getParameterTypes().length;
        int argCount = args.length;
        if (paramCount != argCount) {
            throw new IllegalArgumentException("方法参数量为" + paramCount + " 与真实参数量不匹配，真实参数量为" + argCount);
        }
        final Map<String, Object> uriVars = new HashMap<>();
        for (int i = 0; i < paramCount; i++) {
            MethodParameter param = new SynthesizingMethodParameter(method, i);
            param.initParameterNameDiscovery(parameterNameDiscoverer);
            contributor.contributeMethodArgument(param, args[i], builder, uriVars);
        }
        // We may not have all URI var values, expand only what we have
        return builder.build().expand(name -> uriVars.containsKey(name) ? uriVars.get(name) : UriComponents.UriTemplateVariables.SKIP_VALUE);
    }

    public WxApiTypeInfo getWxApiTypeInfo() {
        return wxApiTypeInfo;
    }

    public WxApiRequest.Method getRequestMethod() {
        return requestMethod;
    }

    public boolean isMutlipartRequest() {
        return isMutlipartRequest;
    }

    public boolean isWxApiFormPresent() {
        return isWxApiFormPresent;
    }

    public boolean isWxApiBodyPresent() {
        return isWxApiBodyPresent;
    }

    public Class getReturnType() {
        return this.method.getReturnType();
    }

    public List<Class> getParameterTypeOrAnnotations() {
        return parameterTypeOrAnnotations;
    }
}
