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

package com.mxixm.fastboot.weixin.service.invoker;

import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiBody;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiForm;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiRequest;
import com.mxixm.fastboot.weixin.service.invoker.contributor.WxApiParamContributor;
import com.mxixm.fastboot.weixin.service.invoker.contributor.WxApiPathContributor;
import com.mxixm.fastboot.weixin.util.WxContextUtils;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * FastBootWeixin WxApiMethodInfo
 *
 * @author Guangshan
 * @date 2017/8/11 20:53
 * @since 0.1.2
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

    private List<MethodParameter> methodParameters;

    public WxApiMethodInfo(Method method, WxApiTypeInfo wxApiTypeInfo) {
        this.method = method;
        this.wxApiTypeInfo = wxApiTypeInfo;
        String methodPath = getMethodWxApiRequestPath(method);
        // 带有参数时，分别处理
        if (methodPath.contains("?")) {
            String path = methodPath.substring(0, methodPath.indexOf("?"));
            String query = methodPath.substring(methodPath.indexOf("?") + 1);
            this.baseBuilder = wxApiTypeInfo.getBaseBuilder().path(path).query(query);
        } else {
            this.baseBuilder = wxApiTypeInfo.getBaseBuilder().path(methodPath);
        }
        this.requestMethod = prepareRequestInfo(method);
    }

    private String getMethodWxApiRequestPath(Method method) {
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(method, WxApiRequest.class);
        if (wxApiRequest == null || StringUtils.isEmpty(wxApiRequest.path()) || ValueConstants.DEFAULT_NONE.equals(wxApiRequest.path())) {
            // 默认情况下取方法名为变量名，尝试从环境变量中获取信息
            return WxContextUtils.resolveStringValue("${" + this.wxApiTypeInfo.getPropertyPrefix() + "." + method.getName() + "}");
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
     * @return the result
     */
    private WxApiRequest.Method prepareRequestInfo(Method method) {
        // 保存参数类型，如果有设置的注解类型则为注解类型
        methodParameters = IntStream.range(0, method.getParameterCount()).mapToObj(i -> {
            MethodParameter methodParameter = new SynthesizingMethodParameter(method, i);
            methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
            // 预热缓存
            methodParameter.getParameterName();
            return methodParameter;
        }).collect(Collectors.toList());
        // 是不是全是简单属性,简单属性的数量
        long simpleParameterCount = methodParameters.stream()
                .filter(p -> BeanUtils.isSimpleValueType(p.getParameterType()))
                .filter(p -> !p.hasParameterAnnotation(WxApiBody.class))
                .filter(p -> !p.hasParameterAnnotation(WxApiForm.class))
                .count();
        WxApiRequest wxApiRequest = AnnotatedElementUtils.findMergedAnnotation(method, WxApiRequest.class);
        // 简单参数数量相同
        if (simpleParameterCount == method.getParameterCount()) {
            if (wxApiRequest == null) {
                return WxApiRequest.Method.GET;
            } else {
                return wxApiRequest.method();
            }
        }
        // 非简单参数多于一个，只能是FORM表单形式
        if (method.getParameterCount() - simpleParameterCount > 1) {
            // 默认出现了wxApiForm
            isWxApiFormPresent = true;
            return WxApiRequest.Method.FORM;
        }
        // 如果有一个是文件则以FORM形式提交
        isMutlipartRequest = Arrays.stream(method.getParameters())
                .filter(p -> WxWebUtils.isMutlipart(p.getType())).findFirst().isPresent();
        if (isMutlipartRequest) {
            isWxApiFormPresent = true;
            return WxApiRequest.Method.FORM;
        }
        // 如果有ApiForm注解，则直接以FORM方式提交
        isWxApiFormPresent = methodParameters.stream()
                .filter(p -> p.hasParameterAnnotation(WxApiForm.class)).findFirst().isPresent();
        if (isWxApiFormPresent) {
            return WxApiRequest.Method.FORM;
        }
        WxApiBody wxApiBody = methodParameters.stream()
                .filter(p -> p.hasParameterAnnotation(WxApiBody.class))
                .map(p -> p.getParameterAnnotation(WxApiBody.class))
                .findFirst().orElse(null);
        if (wxApiBody == null) {
            return WxApiRequest.Method.JSON;
        }
        isWxApiBodyPresent = true;
        return WxApiRequest.Method.valueOf(wxApiBody.type().name());
    }

    /**
     * 这里有个问题，如果原始连接是带有固定参数的，这里会解析出问题
     *
     * @param args
     * @return the result
     */
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
        final Map<String, Object> uriVars = new HashMap<>(8);
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

    public List<MethodParameter> getMethodParameters() {
        return methodParameters;
    }
}
