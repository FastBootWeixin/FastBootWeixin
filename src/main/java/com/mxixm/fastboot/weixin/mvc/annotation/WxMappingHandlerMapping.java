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

package com.mxixm.fastboot.weixin.mvc.annotation;

import com.mxixm.fastboot.weixin.annotation.*;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.menu.WxMenuManager;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageTemplate;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.web.session.WxSessionManager;
import com.mxixm.fastboot.weixin.mvc.condition.WxRequestCondition;
import com.mxixm.fastboot.weixin.mvc.converter.WxXmlMessageConverter;
import com.mxixm.fastboot.weixin.mvc.method.WxAsyncHandlerFactory;
import com.mxixm.fastboot.weixin.mvc.method.WxMappingHandlerMethodNamingStrategy;
import com.mxixm.fastboot.weixin.mvc.method.WxMappingInfo;
import com.mxixm.fastboot.weixin.mvc.method.WxMappingInfos;
import com.mxixm.fastboot.weixin.service.WxBuildinVerifyService;
import com.mxixm.fastboot.weixin.util.WildcardUtils;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxMappingHandlerMapping
 *
 * @author Guangshan
 * @date 2017/09/21 23:45
 * @since 0.1.2
 */
public class WxMappingHandlerMapping extends AbstractHandlerMethodMapping<WxMappingInfo> implements InitializingBean, EmbeddedValueResolverAware {

    /**
     * http://blog.csdn.net/Mr_SeaTurtle_/article/details/52992207
     */
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    private static final ParamsRequestCondition WX_VERIFY_PARAMS_CONDITION = new ParamsRequestCondition("echostr", "nonce", "signature", "timestamp");

    private static final ParamsRequestCondition WX_POST_PARAMS_CONDITION = new ParamsRequestCondition("nonce", "signature", "timestamp");

    private static final ConsumesRequestCondition WX_POST_CONSUMES_CONDITION = new ConsumesRequestCondition(MediaType.TEXT_XML_VALUE);

    private static final Method WX_VERIFY_METHOD = ClassUtils.getMethod(WxBuildinVerifyService.class, "verify", (Class<?>[]) null);

    private final HandlerMethod wxVerifyMethodHandler;

    private final HandlerMethod defaultHandlerMethod;

    private static final Method SUPPLIER_METHOD = ClassUtils.getMethod(Supplier.class, "get");

    /**
     * mappingRegistry同父类完全不同，故自己创建一个
     * 也因为此，要把父类所有使用mappingRegistry的地方覆盖父类方法
     */
    // private final MappingRegistry mappingRegistry = new MappingRegistry();

    private final String path;

    /**
     * 可以加一个开关功能:已经加了
     */
    private final WxMenuManager wxMenuManager;

    private final WxSessionManager wxSessionManager;

    private final WxAsyncHandlerFactory wxAsyncHandlerFactory;

    private final WxXmlMessageConverter wxXmlMessageConverter;

    private StringValueResolver embeddedValueResolver;

    // private final WxRequestContext wxRequestContext = new WxRequestContext();

    public WxMappingHandlerMapping(String path, WxBuildinVerifyService wxBuildinVerifyService, WxMenuManager wxMenuManager,
                                   WxSessionManager wxSessionManager, WxAsyncMessageTemplate wxAsyncMessageTemplate,
                                   WxXmlMessageConverter wxXmlMessageConverter) {
        super();
        this.path = (path.startsWith("/") ? "" : "/") + path;
        this.wxVerifyMethodHandler = new HandlerMethod(wxBuildinVerifyService, WX_VERIFY_METHOD);
        this.wxMenuManager = wxMenuManager;
        this.wxSessionManager = wxSessionManager;
        this.wxAsyncHandlerFactory = new WxAsyncHandlerFactory(wxAsyncMessageTemplate);
        this.defaultHandlerMethod = new HandlerMethod((Supplier)(() -> HttpEntity.EMPTY), SUPPLIER_METHOD);
        this.setHandlerMethodMappingNamingStrategy(new WxMappingHandlerMethodNamingStrategy());
        this.wxXmlMessageConverter = wxXmlMessageConverter;
    }

    @Override
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        // 只接受根目录的请求
        if (!path.equals(lookupPath)) {
            if (logger.isDebugEnabled()) {
                logger.debug("path not match with wxHandlerMapping, path is" + lookupPath);
            }
            return null;
        }
        if (isWxVerifyRequest(request)) {
            return wxVerifyMethodHandler;
        }
        if (isWxPostRequest(request)) {
            // WxRequest.Body wxRequestBody = wxRequestContext.get(request);
            WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
            if (wxRequest == null) {
                wxRequest = new WxRequest(request, wxSessionManager);
                WxWebUtils.setWxRequestToRequest(request, wxRequest);
                wxRequest.setBody(wxXmlMessageConverter.read(request));
                wxRequest.setButton(wxMenuManager.getMapping(wxRequest.getBody()));
            }
            final HandlerMethod handlerMethod = super.getHandlerInternal(request);
            return handlerMethod != null ? handlerMethod : defaultHandlerMethod;
        }
        return null;
    }

    /**
     * 父类中只有getHandlerInternal方法有使用
     */
    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        return super.lookupHandlerMethod(WxWebUtils.getWxRequestFromRequest(request).getBody().getCategory().name(), request);
    }

    /**
     * 判断是否是微信verify请求
     */
    private boolean isWxVerifyRequest(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod())
                && WX_VERIFY_PARAMS_CONDITION.getMatchingCondition(request) != null;
    }

    /**
     * 判断是否是微信POST请求
     */
    private boolean isWxPostRequest(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                && WX_POST_PARAMS_CONDITION.getMatchingCondition(request) != null
                && WX_POST_CONSUMES_CONDITION.getMatchingCondition(request) != null;
    }

    @Override
    protected void handleMatch(WxMappingInfo mapping, String lookupPath, HttpServletRequest request) {
        super.handleMatch(mapping, lookupPath, request);
        // 返回XML
        if (mapping != null) {
            request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(MediaType.TEXT_XML));
            if (logger.isDebugEnabled()) {
                logger.debug("find match wx handler, mapping is " + mapping);
            }
        }
    }

    @Override
    protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        HandlerExecutionChain chain = super.getHandlerExecutionChain(handler, request);
        // chain.addInterceptor(wxRequestContext);
        return chain;
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, WxController.class);
    }

    @Override
    protected Set<String> getMappingPathPatterns(WxMappingInfo info) {
        return info.getCategories().getEnumStrings();
    }

    @Override
    protected WxMappingInfo getMatchingMapping(WxMappingInfo mapping, HttpServletRequest request) {
        return mapping.getMatchingCondition(request);
    }

    @Override
    protected Comparator<WxMappingInfo> getMappingComparator(HttpServletRequest request) {
        return (info1, info2) -> info1.compareTo(info2, request);
    }

    @Override
    protected WxMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        return createWxMappingInfo(method, handlerType);
    }

    private WxMappingInfo createWxMappingInfo(Method method, Class<?> handlerType) {
        WxMappingInfo wxButtonInfo = createWxButtonInfo(method, handlerType);
        WxMappingInfo wxButtonMappingInfo = createWxButtonMappingInfo(method, handlerType);
        WxMappingInfo wxMessageMappingInfo = createWxMessageMappingInfo(method, handlerType);
        WxMappingInfo wxEventMappingInfo = createWxEventMappingInfo(method, handlerType);
        int count = countNotNull(wxButtonInfo, wxButtonMappingInfo, wxMessageMappingInfo, wxEventMappingInfo);
        if (count == 0) {
            WxMappingInfo wxMappingInfo = createWxMappingInfo(method);
            if (wxMappingInfo != null) {
                WxMappingInfo typeWxMappingInfo = createWxMappingInfo(handlerType);
                if (typeWxMappingInfo != null) {
                    wxMappingInfo = wxMappingInfo.combine(typeWxMappingInfo);
                }
            }
            return wxMappingInfo;
        } else if (count == 1) {
            return getNotNull(wxButtonInfo, wxButtonMappingInfo, wxMessageMappingInfo, wxEventMappingInfo);
        } else {
           return new WxMappingInfos(wxButtonInfo, wxButtonMappingInfo, wxMessageMappingInfo, wxEventMappingInfo);
        }
    }

    /**
     * 创建@WxButton的信息，这个和其他不同之处在于不需要去找类上的注解，只支持方法级注解
     * @param method 方法
     * @param handlerType 类
     * @return 返回
     */
    private WxMappingInfo createWxButtonInfo(Method method, Class<?> handlerType) {
        return createWxButtonInfo(method);
    }

    private WxMappingInfo createWxButtonInfo(AnnotatedElement element) {
        WxButton wxButton = AnnotatedElementUtils.findMergedAnnotation(element, WxButton.class);
        // 在这里加上菜单管理是否启用的判断
        if (wxButton == null) {
            return null;
        }
        WxMenu.Button button = wxMenuManager.addButton(wxButton);
        // 使用WxButton定义有一些特殊性，order和group可能具有特异性，故不考虑
        return WxMappingInfo
                .create(Wx.Category.BUTTON)
                .mappingName(button.getName())
                .buttonTypes(button.getType())
                .buttonKeys(button.getKey())
                .buttonNames(button.getName())
                .buttonUrls(button.getUrl())
                .buttonLevels(button.isMain() ? WxButton.Level.MAIN : null)
                .buttonMediaIds(button.getMediaId())
                .buttonAppIds(button.getAppId())
                .buttonPagePaths(button.getPagePath())
                .build();
    }

    private WxMappingInfo createWxButtonMappingInfo(Method method, Class<?> handlerType) {
        WxMappingInfo wxMappingInfo = createWxButtonMappingInfo(method);
        if (wxMappingInfo != null) {
            WxMappingInfo typeWxMappingInfo = createWxButtonMappingInfo(handlerType);
            if (typeWxMappingInfo != null) {
                wxMappingInfo = wxMappingInfo.combine(typeWxMappingInfo);
            }
        }
        return wxMappingInfo;
    }

    private WxMappingInfo createWxButtonMappingInfo(AnnotatedElement element) {
        WxButtonMapping wxButtonMapping = AnnotatedElementUtils.findMergedAnnotation(element, WxButtonMapping.class);
        if (wxButtonMapping == null) {
            return null;
        }
        return WxMappingInfo
                .create(Wx.Category.BUTTON)
                .mappingName(wxButtonMapping.name())
                .buttonTypes(wxButtonMapping.type())
                .buttonKeys(resolveEmbeddedValuesInPatterns(wxButtonMapping.keys()))
                .buttonNames(resolveEmbeddedValuesInPatterns(wxButtonMapping.names()))
                .buttonUrls(resolveEmbeddedValuesInPatterns(wxButtonMapping.urls()))
                .buttonMediaIds((wxButtonMapping.mediaIds()))
                .buttonAppIds(resolveEmbeddedValuesInPatterns(wxButtonMapping.appIds()))
                .buttonPagePaths(resolveEmbeddedValuesInPatterns(wxButtonMapping.pagePaths()))
                .buttonGroups(wxButtonMapping.group())
                .buttonOrders(wxButtonMapping.order())
                .buttonLevels(wxButtonMapping.level())
                .build();
    }

    private WxMappingInfo createWxMessageMappingInfo(Method method, Class<?> handlerType) {
        WxMappingInfo wxMappingInfo = createWxMessageMappingInfo(method);
        if (wxMappingInfo != null) {
            WxMappingInfo typeWxMappingInfo = createWxMessageMappingInfo(handlerType);
            if (typeWxMappingInfo != null) {
                wxMappingInfo = wxMappingInfo.combine(typeWxMappingInfo);
            }
        }
        return wxMappingInfo;
    }

    private WxMappingInfo createWxMessageMappingInfo(AnnotatedElement element) {
        WxMessageMapping wxMessageMapping = AnnotatedElementUtils.findMergedAnnotation(element, WxMessageMapping.class);
        if (wxMessageMapping == null) {
            return null;
        }
        return WxMappingInfo
                .create(Wx.Category.MESSAGE)
                .messageTypes(wxMessageMapping.type())
                .messageContents(resolveEmbeddedValuesInPatterns(wxMessageMapping.contents()))
                .mappingName(wxMessageMapping.name())
                .build();
    }

    private WxMappingInfo createWxEventMappingInfo(Method method, Class<?> handlerType) {
        WxMappingInfo wxMappingInfo = createWxEventMappingInfo(method);
        if (wxMappingInfo != null) {
            WxMappingInfo typeWxMappingInfo = createWxEventMappingInfo(handlerType);
            if (typeWxMappingInfo != null) {
                wxMappingInfo = wxMappingInfo.combine(typeWxMappingInfo);
            }
        }
        return wxMappingInfo;
    }

    private WxMappingInfo createWxEventMappingInfo(AnnotatedElement element) {
        WxEventMapping wxEventMapping = AnnotatedElementUtils.findMergedAnnotation(element, WxEventMapping.class);
        if (wxEventMapping == null) {
            return null;
        }
        return WxMappingInfo
                .create(Wx.Category.EVENT)
                .eventTypes(wxEventMapping.type())
                .eventScenes(resolveEmbeddedValuesInPatterns(wxEventMapping.scenes()))
                .eventKeys(resolveEmbeddedValuesInPatterns(wxEventMapping.keys()))
                .mappingName(wxEventMapping.name())
                .build();
    }

    private WxMappingInfo createWxMappingInfo(AnnotatedElement element) {
        WxMapping wxMapping = element.getAnnotation(WxMapping.class);
        if (wxMapping == null) {
            return null;
        }
        return WxMappingInfo
                .create(wxMapping.category())
                .mappingName(wxMapping.name())
                .build();
    }

    private int countNotNull(WxMappingInfo... wxMappingInfos) {
        int count = 0;
        for (WxMappingInfo wxMappingInfo : wxMappingInfos) {
            if (Objects.nonNull(wxMappingInfo)) {
                count++;
            }
        }
        return count;
    }

    private WxMappingInfo getNotNull(WxMappingInfo... wxMappingInfos) {
        for (WxMappingInfo wxMappingInfo : wxMappingInfos) {
            if (Objects.nonNull(wxMappingInfo)) {
                return wxMappingInfo;
            }
        }
        return null;
    }

    @Override
    protected HandlerMethod createHandlerMethod(Object handler, Method method) {
        if (handler instanceof String) {
            String beanName = (String) handler;
            handler = this.getApplicationContext().getAutowireCapableBeanFactory().getBean(beanName);
        }
        if (AnnotatedElementUtils.hasAnnotation(method, WxAsyncMessage.class) || AnnotatedElementUtils.hasAnnotation(handler.getClass(), WxAsyncMessage.class)) {
            return new HandlerMethod(wxAsyncHandlerFactory.createHandler(handler), method);
        } else {
            return new HandlerMethod(handler, method);
        }
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, WxMappingInfo mapping) {
        if (mapping instanceof WxMappingInfos) {
            WxMappingInfo[] wxMappingInfos = ((WxMappingInfos) mapping).getWxMappingInfos();
            for (WxMappingInfo wxMappingInfo : wxMappingInfos) {
                super.registerHandlerMethod(handler, method, wxMappingInfo);
            }
        } else {
            super.registerHandlerMethod(handler, method, mapping);
        }
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.embeddedValueResolver = stringValueResolver;
    }

    protected String[] resolveEmbeddedValuesInPatterns(String[] patterns) {
        if (this.embeddedValueResolver == null) {
            return patterns;
        }
        else {
            String[] resolvedPatterns = new String[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                resolvedPatterns[i] = this.embeddedValueResolver.resolveStringValue(patterns[i]);
            }
            return resolvedPatterns;
        }
    }

    /**
     * 用于拦截微信请求
     * 用于修复加入Spring Boot Starter Actuator后框架失效的问题
     * 这里的时序是这样的，先进入WebMvcMetricsFilter，调用本类的getHandler方法
     * 此时调用这里的get方法，把WxRequestBody设置到ThreadLocal中。
     * 再次执行真实调用，进入这里的preHandle，最后调用afterCompletion清除threadLocal
     * 那么有这么一种风险，设置完threadLocal后，在未执行preHandle之前，发生了异常，此时将会导致threadLocal无法释放
     * 可能带来阴性bug，此处要想完美解决问题，必须使用filter或者执行类型判断ServletRequestWrapper，拿到原始request。
     * 暂时使用原始request的方式，这里不用了
     */
    private class WxRequestContext implements HandlerInterceptor {

        ThreadLocal<WxRequest.Body> wxRequestBodyHolder = new ThreadLocal<>();

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            WxRequest wxRequest = new WxRequest(request, wxSessionManager);
            WxWebUtils.setWxRequestToRequest(request, wxRequest);
            wxRequest.setBody(get(request));
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            this.remove();
        }

        public void set(HttpServletRequest request) throws IOException {
            wxRequestBodyHolder.set(wxXmlMessageConverter.read(request));
        }

        public WxRequest.Body get(HttpServletRequest request) throws IOException {
            if (wxRequestBodyHolder.get() == null) {
                this.set(request);
            }
            return wxRequestBodyHolder.get();
        }

        public void remove() {
            wxRequestBodyHolder.remove();
        }

    }

}
