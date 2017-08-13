package com.example.myproject.mvc.annotation;

import com.example.myproject.annotation.WxButton;
import com.example.myproject.controller.WxVerifyController;
import com.example.myproject.module.Wx;
import com.example.myproject.module.WxRequest;
import com.example.myproject.module.event.WxEvent;
import com.example.myproject.module.menu.WxButtonItem;
import com.example.myproject.module.menu.WxMenuManager;
import com.example.myproject.mvc.method.WxMappingHandlerMethodNamingStrategy;
import com.example.myproject.mvc.method.WxMappingInfo;
import com.example.myproject.mvc.WxRequestUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WxMappingHandlerMapping extends AbstractHandlerMethodMapping<WxMappingInfo> implements InitializingBean {

    /**
     * http://blog.csdn.net/Mr_SeaTurtle_/article/details/52992207
     */
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    private static final String[] WX_VERIFY_PARAMS = new String[]{
            "echostr", "nonce", "signature", "timestamp"
    };

    private static final String[] WX_POST_PARAMS = new String[]{
            "openid", "nonce", "signature", "timestamp"
    };

    private static final ParamsRequestCondition WX_VERIFY_PARAMS_CONDITION = new ParamsRequestCondition(WX_VERIFY_PARAMS);

    private static final ParamsRequestCondition WX_POST_PARAMS_CONDITION = new ParamsRequestCondition(WX_POST_PARAMS);

    private static final ConsumesRequestCondition WX_POST_CONSUMES_CONDITION = new ConsumesRequestCondition(MediaType.TEXT_XML_VALUE);

    private static final Method WX_VERIFY_METHOD = ClassUtils.getMethod(WxVerifyController.class, "verify", null);

    private final HandlerMethod wxVerifyMethodHandler;

    private final Jaxb2RootElementHttpMessageConverter xmlConverter;

    // mappingRegistry同父类完全不同，故自己创建一个
    // 也因为此，要把父类所有使用mappingRegistry的地方覆盖父类方法
    private final MappingRegistry mappingRegistry = new MappingRegistry();

    // 可以加一个开关功能
    @Autowired
    private WxMenuManager wxMenuManager;

    public WxMappingHandlerMapping(WxVerifyController wxVerifyController) {
        super();
        this.wxVerifyMethodHandler = new HandlerMethod(wxVerifyController, WX_VERIFY_METHOD);
        this.xmlConverter = new Jaxb2RootElementHttpMessageConverter();
        this.setHandlerMethodMappingNamingStrategy(new WxMappingHandlerMethodNamingStrategy());
    }

    @Override
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        // 只接受根目录的请求
        if (!"/".equals(lookupPath)) {
            return null;
        }
        if (isWxVerifyRequest(request)) {
            return wxVerifyMethodHandler;
        }
        if (isWxPostRequest(request)) {
            return lookupHandlerMethod(lookupPath, request);
        }
        return null;
    }

    // 判断是否是微信verify请求
    private boolean isWxVerifyRequest(HttpServletRequest request) {
        return "GET".equals(request.getMethod())
                && WX_VERIFY_PARAMS_CONDITION.getMatchingCondition(request) != null;
    }

    // 判断是否是微信POST请求
    private boolean isWxPostRequest(HttpServletRequest request) {
        return "POST".equals(request.getMethod())
                && WX_POST_PARAMS_CONDITION.getMatchingCondition(request) != null
                && WX_POST_CONSUMES_CONDITION.getMatchingCondition(request) != null;
    }

    public Map<WxMappingInfo, HandlerMethod> getHandlerMethods() {
        this.mappingRegistry.acquireReadLock();
        try {
            return Collections.unmodifiableMap(this.mappingRegistry.getMappings());
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    /**
     * Return the handler methods for the given mapping value.
     *
     * @param mappingName the mapping value
     * @return a list of matching HandlerMethod's or {@code null}; the returned
     * list will never be modified and is safe to iterate.
     * @see #setHandlerMethodMappingNamingStrategy
     */
    @Override
    public List<HandlerMethod> getHandlerMethodsForMappingName(String mappingName) {
        return this.mappingRegistry.getHandlerMethodsByMappingName(mappingName);
    }

    /**
     * Return the internal mapping registry. Provided for testing purposes.
     */
    MappingRegistry getMappingRegistry() {
        return this.mappingRegistry;
    }

    /**
     * Register the given mapping.
     * <p>This method may be invoked at runtime after initialization has completed.
     *
     * @param mapping the mapping for the handler method
     * @param handler the handler
     * @param method  the method
     */
    @Override
    public void registerMapping(WxMappingInfo mapping, Object handler, Method method) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    /**
     * Un-register the given mapping.
     * <p>This method may be invoked at runtime after initialization has completed.
     *
     * @param mapping the mapping to unregister
     */
    @Override
    public void unregisterMapping(WxMappingInfo mapping) {
        this.mappingRegistry.unregister(mapping);
    }

    /**
     * 父类中只有getHandlerInternal方法有使用
     * <p>
     * Look up the best-matching handler method for the current request.
     * If multiple matches are found, the best match is selected.
     *
     * @param lookupPath mapping lookup path within the current servlet mapping
     * @param request    the current request
     * @return the best-matching handler method, or {@code null} if no match
     * @see #handleMatch(Object, String, HttpServletRequest)
     * @see #handleNoMatch(Set, String, HttpServletRequest)
     */
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        this.mappingRegistry.acquireReadLock();
        try {
            // ServletWebRequest
            HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
            WxRequest wxRequest = (WxRequest) xmlConverter.read(WxRequest.class, inputMessage);
            WxRequestUtils.setWxRequestToRequestAttribute(request, wxRequest);
            HandlerMethod handlerMethod = null;
            switch (wxRequest.getCategory()) {
                case BUTTON:
                    handlerMethod = lookupButtonHandlerMethod(wxRequest);break;
                case EVENT:
                    handlerMethod = lookupEventHandlerMethod(wxRequest);break;
                case MESSAGE:
                    handlerMethod = lookupButtonHandlerMethod(wxRequest);break;
            }
            handleMatch(handlerMethod, request);
            return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    protected void handleMatch(HandlerMethod handlerMethod, HttpServletRequest request) {
        // 返回XML
        if (handlerMethod != null) {
            request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(MediaType.TEXT_XML));
        }
    }

    private HandlerMethod lookupButtonHandlerMethod(WxRequest wxRequest) {
        return mappingRegistry.getMappingButtonByEventKey(wxRequest.getEventKey());
    }

    private HandlerMethod lookupEventHandlerMethod(WxRequest wxRequest) {
        return mappingRegistry.getMappingEventByEventType(wxRequest.getEventType());
    }

    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, WxController.class);
    }

    /**
     * Register a handler method and its unique mapping. Invoked at startup for
     * each detected handler method.
     *
     * @param handler the bean value of the handler or the handler instance
     * @param method  the method to register
     * @param mapping the mapping conditions associated with the handler method
     * @throws IllegalStateException if another method was already registered
     *                               under the same mapping
     */
    protected void registerHandlerMethod(Object handler, Method method, WxMappingInfo mapping) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    @Override
    protected Set<String> getMappingPathPatterns(WxMappingInfo info) {
        return Collections.singleton("/");
    }

    @Override
    protected WxMappingInfo getMatchingMapping(WxMappingInfo mapping, HttpServletRequest request) {
        return mapping.getMatchingCondition(request);
    }

    @Override
    protected Comparator<WxMappingInfo> getMappingComparator(HttpServletRequest request) {
        return (info1, info2) -> info1.compareTo(info2, request);
    }

    /**
     * Uses method and type-level @{@link RequestMapping} annotations to builder
     * the RequestMappingInfo.
     *
     * @return the created RequestMappingInfo, or {@code null} if the method
     * does not have a {@code @RequestMapping} annotation.
     */
    @Override
    protected WxMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        WxMappingInfo info = createWxMappingInfo(method);
        if (info != null) {
            WxMappingInfo typeInfo = createWxMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
        }
        return info;
    }

    private WxMappingInfo createWxMappingInfo(AnnotatedElement element) {
        WxButton wxButton = AnnotatedElementUtils.findMergedAnnotation(element, WxButton.class);
        // 由于这个机制，所以无法为同一个方法绑定多个WxButton、WxEventMapping、WxMessageMapping
        if (wxButton != null) {
            return createWxButtonMappingInfo(wxButton);
        }
        WxMessageMapping wxMessageMapping = AnnotatedElementUtils.findMergedAnnotation(element, WxMessageMapping.class);
        if (wxMessageMapping != null) {
            return createWxMessageMappingInfo(wxMessageMapping);
        }
        WxEventMapping wxEventMapping = AnnotatedElementUtils.findMergedAnnotation(element, WxEventMapping.class);
        if (wxEventMapping != null) {
            return createWxEventMappingInfo(wxEventMapping);
        }
        return null;
    }

    private WxMappingInfo createWxButtonMappingInfo(WxButton wxButton) {
        // 在这里加上菜单管理是否启用的判断
        WxButtonItem wxButtonItem = wxMenuManager.add(wxButton);
        return WxMappingInfo
                .category(Wx.Category.BUTTON)
                // eventKey是url，如果类型是VIEW的话
                .eventKey(wxButtonItem.getKey())
                .mappingName(wxButton.name())
                .buttonTypes(wxButton.type())
                .build();
    }

    private WxMappingInfo createWxMessageMappingInfo(WxMessageMapping wxMessageMapping) {
        return WxMappingInfo
                .category(Wx.Category.MESSAGE)
                .messageTypes(wxMessageMapping.type())
                .mappingName(wxMessageMapping.name())
                .build();
    }

    private WxMappingInfo createWxEventMappingInfo(WxEventMapping wxEventMapping) {
        return WxMappingInfo
                .category(Wx.Category.EVENT)
                .eventTypes(wxEventMapping.type())
                .mappingName(wxEventMapping.name())
                .build();
    }

    /**
     * A registry that maintains all mappings to handler methods, exposing methods
     * to perform lookups and providing concurrent access.
     * <p>
     * <p>Package-private for testing purposes.
     */
    class MappingRegistry {

        private final Map<WxMappingInfo, MappingRegistration<WxMappingInfo>> registry = new HashMap<>();

        private final Map<WxMappingInfo, HandlerMethod> mappingLookup = new LinkedHashMap<>();

        private final Map<String, HandlerMethod> eventKeyLookup = new LinkedHashMap<>();

        private final Map<WxEvent.Type, HandlerMethod> eventTypeLookup = new LinkedHashMap<>();

        private final MultiValueMap<Wx.Category, WxMappingInfo> categoryLookup = new LinkedMultiValueMap<>();

        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap<>();

        private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap<HandlerMethod, CorsConfiguration>();

        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        /**
         * Return all mappings and handler methods. Not thread-safe.
         *
         * @see #acquireReadLock()
         */
        public Map<WxMappingInfo, HandlerMethod> getMappings() {
            return this.mappingLookup;
        }

        public HandlerMethod getMappingButtonByEventKey(String eventKey) {
            return this.eventKeyLookup.get(eventKey);
        }

        public HandlerMethod getMappingEventByEventType(WxEvent.Type eventType) {
            return this.eventTypeLookup.get(eventType);
        }

        /**
         * Return handler methods by mapping value. Thread-safe for concurrent use.
         */
        public List<HandlerMethod> getHandlerMethodsByMappingName(String mappingName) {
            return this.nameLookup.get(mappingName);
        }

        /**
         * Acquire the read lock when using getMappings and getMappingsByUrl.
         */
        public void acquireReadLock() {
            this.readWriteLock.readLock().lock();
        }

        /**
         * Release the read lock after using getMappings and getMappingsByUrl.
         */
        public void releaseReadLock() {
            this.readWriteLock.readLock().unlock();
        }

        public void register(WxMappingInfo mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();
            try {
                HandlerMethod handlerMethod = createHandlerMethod(handler, method);
                assertUniqueMethodMapping(handlerMethod, mapping);

                if (logger.isInfoEnabled()) {
                    logger.info("Mapped \"" + mapping + "\" onto " + handlerMethod);
                }
                this.mappingLookup.put(mapping, handlerMethod);

                this.categoryLookup.add(mapping.getCategory(), mapping);

                if (!StringUtils.isEmpty(mapping.getEventKey())) {
                    eventKeyLookup.put(mapping.getEventKey(), handlerMethod);
                }
                if (!mapping.getWxEventTypeCondition().isEmpty()) {
                    mapping.getWxEventTypeCondition().getEnums().forEach(
                            e -> eventTypeLookup.put(e, handlerMethod)
                    );
                }

                String name = null;
                if (getNamingStrategy() != null) {
                    name = getNamingStrategy().getName(handlerMethod, mapping);
                    addMappingName(name, handlerMethod);
                }
                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, name));
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void assertUniqueMethodMapping(HandlerMethod newHandlerMethod, WxMappingInfo mapping) {
            HandlerMethod handlerMethod = this.mappingLookup.get(mapping);
            if (handlerMethod != null && !handlerMethod.equals(newHandlerMethod)) {
                throw new IllegalStateException(
                        "Ambiguous mapping. Cannot map '" + newHandlerMethod.getBean() + "' method \n" +
                                newHandlerMethod + "\nto " + mapping + ": There is already '" +
                                handlerMethod.getBean() + "' bean method\n" + handlerMethod + " mapped.");
            }
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.<HandlerMethod>emptyList();
            }

            for (HandlerMethod current : oldList) {
                if (handlerMethod.equals(current)) {
                    return;
                }
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Mapping value '" + name + "'");
            }

            List<HandlerMethod> newList = new ArrayList<HandlerMethod>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);

            if (newList.size() > 1) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Mapping value clash for handlerMethods " + newList +
                            ". Consider assigning explicit names.");
                }
            }
        }

        public void unregister(WxMappingInfo mapping) {
            this.readWriteLock.writeLock().lock();
            try {
                MappingRegistration<WxMappingInfo> definition = this.registry.remove(mapping);
                if (definition == null) {
                    return;
                }

                this.mappingLookup.remove(definition.getMapping());

                if (mapping.getEventKey() != null) {
                    eventKeyLookup.remove(mapping.getEventKey());
                }

                removeMappingName(definition);

                this.corsLookup.remove(definition.getHandlerMethod());
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void removeMappingName(MappingRegistration<WxMappingInfo> definition) {
            String name = definition.getMappingName();
            if (name == null) {
                return;
            }
            HandlerMethod handlerMethod = definition.getHandlerMethod();
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                return;
            }
            if (oldList.size() <= 1) {
                this.nameLookup.remove(name);
                return;
            }
            List<HandlerMethod> newList = new ArrayList<HandlerMethod>(oldList.size() - 1);
            for (HandlerMethod current : oldList) {
                if (!current.equals(handlerMethod)) {
                    newList.add(current);
                }
            }
            this.nameLookup.put(name, newList);
        }
    }

    private static class MappingRegistration<T> {

        private final T mapping;

        private final HandlerMethod handlerMethod;

        private final String mappingName;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod, String mappingName) {
            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull(handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.mappingName = mappingName;
        }

        public T getMapping() {
            return this.mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public String getMappingName() {
            return this.mappingName;
        }
    }

}
