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
import com.mxixm.fastboot.weixin.controller.WxBuildinVerify;
import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.controller.invoker.common.WxMediaResourceMessageConverter;
import com.mxixm.fastboot.weixin.module.menu.WxMenuManager;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageReturnValueHandler;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageTemplate;
import com.mxixm.fastboot.weixin.module.web.session.WxSessionManager;
import com.mxixm.fastboot.weixin.mvc.advice.WxMediaResponseBodyAdvice;
import com.mxixm.fastboot.weixin.mvc.advice.WxMessageResponseBodyAdvice;
import com.mxixm.fastboot.weixin.mvc.advice.WxStringResponseBodyAdvice;
import com.mxixm.fastboot.weixin.mvc.annotation.WxMappingHandlerMapping;
import com.mxixm.fastboot.weixin.mvc.param.WxArgumentResolver;
import com.mxixm.fastboot.weixin.module.user.WxUserProvider;
import com.mxixm.fastboot.weixin.web.WxOAuth2Interceptor;
import com.mxixm.fastboot.weixin.web.WxUserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
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
public class WxBuildinMvcConfiguration implements ImportAware {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final WxProperties wxProperties;

    private final BeanFactory beanFactory;

    private final WxMessageProcesser wxMessageProcesser;

    private final WxApiInvokeSpi wxApiInvokeSpi;

    private boolean menuAutoCreate = true;

    public WxBuildinMvcConfiguration(WxProperties wxProperties, BeanFactory beanFactory, @Lazy WxMessageProcesser wxMessageProcesser, @Lazy WxApiInvokeSpi wxApiInvokeSpi) {
        this.wxProperties = wxProperties;
        this.beanFactory = beanFactory;
        this.wxMessageProcesser = wxMessageProcesser;
        this.wxApiInvokeSpi = wxApiInvokeSpi;
    }

    @Bean
    public WxBuildinVerify wxBuildinVerify() {
        return new WxBuildinVerify(wxProperties.getToken());
    }

    @Bean
    public WxMappingHandlerMapping wxRequestMappingHandlerMapping(@Lazy WxSessionManager wxSessionManager, @Lazy WxAsyncMessageTemplate wxAsyncMessageTemplate) {
        WxMappingHandlerMapping wxMappingHandlerMapping = new WxMappingHandlerMapping(wxProperties.getPath(), wxBuildinVerify(), wxMenuManager(), wxSessionManager, wxAsyncMessageTemplate);
        wxMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return wxMappingHandlerMapping;
    }

    @Bean
    public WxMenuManager wxMenuManager() {
        return new WxMenuManager(wxApiInvokeSpi, menuAutoCreate);
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

    @Bean
    public WxMessageResponseBodyAdvice wxMessageResponseBodyAdvice() {
        return new WxMessageResponseBodyAdvice(wxMessageProcesser);
    }

    @Bean
    public WxStringResponseBodyAdvice wxStringResponseBodyAdvice() {
        return new WxStringResponseBodyAdvice(wxMessageProcesser);
    }

    @Bean
    public WxMediaResponseBodyAdvice wxMediaResponseBodyAdvice() {
        return new WxMediaResponseBodyAdvice();
    }

    @Bean
    public WxOAuth2Interceptor wxOAuth2Interceptor() {
        return new WxOAuth2Interceptor();
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
        public void afterPropertiesSet() throws Exception {
            RequestMappingHandlerAdapter requestMappingHandlerAdapter = this.beanFactory.getBean(RequestMappingHandlerAdapter.class);
            List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
            List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
            if (beanFactory instanceof ConfigurableBeanFactory) {
                argumentResolvers.add(new WxArgumentResolver((ConfigurableBeanFactory) beanFactory));
            } else {
                argumentResolvers.add(new WxArgumentResolver(beanFactory.getBean(WxUserManager.class), beanFactory.getBean(WxUserProvider.class)));
            }
            returnValueHandlers.add(beanFactory.getBean(WxAsyncMessageReturnValueHandler.class));
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

    public static class WxMvcConfigurer extends WebMvcConfigurerAdapter implements Ordered {

        private HandlerInterceptor wxOAuth2Interceptor;

        private WxProperties wxProperties;

        public WxMvcConfigurer(HandlerInterceptor wxOAuth2Interceptor, WxProperties wxProperties) {
            this.wxOAuth2Interceptor = wxOAuth2Interceptor;
            this.wxProperties = wxProperties;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(wxOAuth2Interceptor)
                    .addPathPatterns(wxProperties.getMvc().getInterceptor().getIncludePatterns().toArray(new String[wxProperties.getMvc().getInterceptor().getIncludePatterns().size()]))
                    .excludePathPatterns(wxProperties.getMvc().getInterceptor().getExcludePatterns().toArray(new String[wxProperties.getMvc().getInterceptor().getExcludePatterns().size()]));
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE + 1000;
        }
    }

    /*public static class WxMvcConfigurer extends WebMvcConfigurerAdapter {

        private WxArgumentResolver wxArgumentResolver;

        private WxAsyncMessageReturnValueHandler wxAsyncMessageReturnValueHandler;

        /**
         * 之前这里产生循环依赖，因为ConversionService是这个里面生成的，而conversionService又被WxApiExecutor依赖
         * WxApiExecutor -> WxApiInvokeSpi -> WxUserProvider -> WxMvcConfigurer -> ConversionService -> WxAPIExecutor
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
            this.wxAsyncMessageReturnValueHandler = beanFactory.getBean(WxAsyncMessageReturnValueHandler.class);
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
