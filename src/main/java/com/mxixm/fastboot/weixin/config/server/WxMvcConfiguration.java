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

package com.mxixm.fastboot.weixin.config.server;

import com.mxixm.fastboot.weixin.annotation.EnableWxMvc;
import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.module.menu.DefaultWxButtonEventKeyStrategy;
import com.mxixm.fastboot.weixin.module.menu.WxButtonEventKeyStrategy;
import com.mxixm.fastboot.weixin.module.menu.WxMenuManager;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageTemplate;
import com.mxixm.fastboot.weixin.module.user.WxUserProvider;
import com.mxixm.fastboot.weixin.module.web.session.WxSessionManager;
import com.mxixm.fastboot.weixin.mvc.advice.WxMediaResponseBodyAdvice;
import com.mxixm.fastboot.weixin.mvc.annotation.WxMappingHandlerMapping;
import com.mxixm.fastboot.weixin.mvc.converter.WxXmlMessageConverter;
import com.mxixm.fastboot.weixin.mvc.param.WxArgumentResolver;
import com.mxixm.fastboot.weixin.mvc.processor.WxMappingReturnValueHandler;
import com.mxixm.fastboot.weixin.mvc.processor.WxMessageReturnValueHandler;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.service.WxBuildinVerifyService;
import com.mxixm.fastboot.weixin.service.WxXmlCryptoService;
import com.mxixm.fastboot.weixin.service.invoker.common.WxMediaResourceMessageConverter;
import com.mxixm.fastboot.weixin.web.WxOAuth2Interceptor;
import com.mxixm.fastboot.weixin.web.WxUserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * FastBootWeixin WxBuildinMvcConfiguration
 *
 * @author Guangshan
 * @date 2017/09/21 23:33
 * @since 0.1.2
 */
@Configuration
public class WxMvcConfiguration implements ImportAware {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final WxProperties wxProperties;

    private final BeanFactory beanFactory;

    private final WxMessageProcessor wxMessageProcessor;

    private final WxApiService wxApiService;

    private boolean menuAutoCreate = true;

    public WxMvcConfiguration(WxProperties wxProperties, BeanFactory beanFactory, @Lazy WxMessageProcessor wxMessageProcessor, @Lazy WxApiService wxApiService) {
        this.wxProperties = wxProperties;
        this.beanFactory = beanFactory;
        this.wxMessageProcessor = wxMessageProcessor;
        this.wxApiService = wxApiService;
    }

    @Bean
    public WxBuildinVerifyService wxBuildinVerify() {
        return new WxBuildinVerifyService(wxProperties.getToken());
    }

    @Bean
    public WxMappingHandlerMapping wxRequestMappingHandlerMapping(@Lazy WxSessionManager wxSessionManager, @Lazy WxAsyncMessageTemplate wxAsyncMessageTemplate, @Lazy WxMenuManager wxMenuManager, WxXmlMessageConverter wxXmlMessageConverter) {
        WxMappingHandlerMapping wxMappingHandlerMapping = new WxMappingHandlerMapping(wxProperties.getPath(), wxBuildinVerify(), wxMenuManager, wxSessionManager, wxAsyncMessageTemplate, wxXmlMessageConverter);
        wxMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return wxMappingHandlerMapping;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxButtonEventKeyStrategy wxButtonEventKeyStrategy() {
        return new DefaultWxButtonEventKeyStrategy();
    }

    @Bean
    public WxMenuManager wxMenuManager(WxButtonEventKeyStrategy wxButtonEventKeyStrategy) {
        return new WxMenuManager(wxApiService, wxButtonEventKeyStrategy, menuAutoCreate);
    }

    @Bean
    public WxMediaResourceMessageConverter wxMediaResourceMessageConverter() {
        return new WxMediaResourceMessageConverter();
    }

    @Bean
    public WxMvcConfigurer wxMvcConfigurer() {
        return new WxMvcConfigurer(wxOAuth2Interceptor(), wxProperties);
    }

    @Bean
    public WxMvcAdapterCustomer wxMvcAdapterCustomer() {
        return new WxMvcAdapterCustomer();
    }

    /**
     * 0.6.2使用WxXmlMessageConverter替换
    @Bean
    public WxMessageResponseBodyAdvice wxMessageResponseBodyAdvice() {
        return new WxMessageResponseBodyAdvice(wxMessageProcessor);
    }

    @Bean
    public WxStringResponseBodyAdvice wxStringResponseBodyAdvice() {
        return new WxStringResponseBodyAdvice(wxMessageProcessor);
    }
     **/

    @Bean
    public WxMediaResponseBodyAdvice wxMediaResponseBodyAdvice() {
        return new WxMediaResponseBodyAdvice();
    }

    @Bean
    public WxOAuth2Interceptor wxOAuth2Interceptor() {
        return new WxOAuth2Interceptor();
    }

    @Bean
    public WxXmlCryptoService wxXmlCryptoService() {
        return new WxXmlCryptoService(wxProperties);
    }

    @Bean
    public WxXmlMessageConverter wxXmlMessageConverter() {
        return new WxXmlMessageConverter(wxMessageProcessor, wxXmlCryptoService());
    }

    @Bean
    public WxMappingReturnValueHandler wxMappingReturnValueHandler(WxXmlMessageConverter wxXmlMessageConverter, WxAsyncMessageTemplate wxAsyncMessageTemplate) {
        return new WxMappingReturnValueHandler(wxXmlMessageConverter, wxAsyncMessageTemplate);
    }

    @Bean
    public WxMessageReturnValueHandler wxMessageReturnValueHandler(WxMessageTemplate wxMessageTemplate, WxAsyncMessageTemplate wxAsyncMessageTemplate) {
        return new WxMessageReturnValueHandler(wxMessageTemplate, wxAsyncMessageTemplate);
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableWxMvc.class.getName(), false));
        this.menuAutoCreate = annotationAttributes.getBoolean("menuAutoCreate");
    }

    /**
     * 本来想用WxMvcConfigurer，但是因为那个配置不能修改returnValueHandlers和argumentResolvers的顺序
     * 所以用了这个
     */
    public static class WxMvcAdapterCustomer implements InitializingBean, BeanFactoryAware {

        private BeanFactory beanFactory;

        @Override
        public void afterPropertiesSet() {
            RequestMappingHandlerAdapter requestMappingHandlerAdapter = this.beanFactory.getBean(RequestMappingHandlerAdapter.class);
            List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
            List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
            if (beanFactory instanceof ConfigurableBeanFactory) {
                argumentResolvers.add(new WxArgumentResolver((ConfigurableBeanFactory) beanFactory));
            } else {
                argumentResolvers.add(new WxArgumentResolver(beanFactory.getBean(WxUserManager.class), beanFactory.getBean(WxUserProvider.class)));
            }
            // 可以换成@Autowired，Spring内部框架就是这样做的
            // returnValueHandlers.add(beanFactory.getBean(WxAsyncMessageReturnValueHandler.class));
            // returnValueHandlers.add(beanFactory.getBean(WxSyncMessageReturnValueHandler.class));
            returnValueHandlers.add(beanFactory.getBean(WxMappingReturnValueHandler.class));
            returnValueHandlers.add(beanFactory.getBean(WxMessageReturnValueHandler.class));
            argumentResolvers.addAll(requestMappingHandlerAdapter.getArgumentResolvers());
            returnValueHandlers.addAll(requestMappingHandlerAdapter.getReturnValueHandlers());
            requestMappingHandlerAdapter.setArgumentResolvers(argumentResolvers);
            requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }

    /**
     * 从WebMvcConfigurerAdapter切到了WebMvcConfigurer
     * 因为在Spring5中，WebMvcConfigurer添加了默认方法，不需要使用Adapter这种过时的东西了
     * 故WebMvcConfigurerAdapter被弃用了，但为了兼容4.x和5.x，我这里只能把WebMvcConfigurerAdapter代码挪过来了
     *
     */
    public static class WxMvcConfigurer implements WebMvcConfigurer, Ordered {

        private HandlerInterceptor wxOAuth2Interceptor;

        private WxProperties wxProperties;

        public WxMvcConfigurer(HandlerInterceptor wxOAuth2Interceptor, WxProperties wxProperties) {
            this.wxOAuth2Interceptor = wxOAuth2Interceptor;
            this.wxProperties = wxProperties;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(wxOAuth2Interceptor)
                    .addPathPatterns(wxProperties.getMvc().getInterceptor().getIncludePatterns().toArray(new String[0]))
                    .excludePathPatterns(wxProperties.getMvc().getInterceptor().getExcludePatterns().toArray(new String[0]));
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE + 1000;
        }


        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addFormatters(FormatterRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addCorsMappings(CorsRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation is empty.
         */
        @Override
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        }

        /**
         * {@inheritDoc}
         * <p>This implementation returns {@code null}.
         */
        @Override
        public Validator getValidator() {
            return null;
        }

        /**
         * {@inheritDoc}
         * <p>This implementation returns {@code null}.
         */
        @Override
        public MessageCodesResolver getMessageCodesResolver() {
            return null;
        }


    }

    /*public static class WxMvcConfigurer extends WebMvcConfigurerAdapter {

        private WxArgumentResolver wxArgumentResolver;

        private WxSyncMessageReturnValueHandler wxAsyncMessageReturnValueHandler;

        /**
         * 之前这里产生循环依赖，因为ConversionService是这个里面生成的，而conversionService又被WxApiExecutor依赖
         * WxApiExecutor -> WxApiService -> WxUserProvider -> WxMvcConfigurer -> ConversionService -> WxAPIExecutor
         * 于是产生了循环依赖
         * 临时处理先把ConversionService的依赖去掉，后期考虑优化依赖关系
         *
         * @param wxUserProvider
         * @param beanFactory
         *
        public WxMvcConfigurer(WxUserProvider wxUserProvider, BeanFactory beanFactory) {
            if (beanFactory instanceof ConfigurableBeanFactory) {
                this.wxArgumentResolver = new WxArgumentResolver((ConfigurableBeanFactory) beanFactory);
            } else {
                this.wxArgumentResolver = new WxArgumentResolver(wxUserProvider);
            }
            this.wxAsyncMessageReturnValueHandler = beanFactory.getBean(WxSyncMessageReturnValueHandler.class);
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(this.wxArgumentResolver);
        }

        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
            returnValueHandlers.add(0, wxAsyncMessageReturnValueHandler);
        }

        /**
         * WebMvcConfigurationSupport添加的MessageConverter不会被添加到SpringBoot全局的HttpMessageConverters中
         */
        /*@Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			converters.add(new WxMediaResourceMessageConverter());
		}*
    }
    */
}
