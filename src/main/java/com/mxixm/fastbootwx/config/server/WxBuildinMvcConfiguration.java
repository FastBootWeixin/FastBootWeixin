package com.mxixm.fastbootwx.config.server;

import com.mxixm.fastbootwx.config.invoker.WxVerifyProperties;
import com.mxixm.fastbootwx.controller.WxVerifyController;
import com.mxixm.fastbootwx.controller.invoker.common.WxMediaResourceMessageConverter;
import com.mxixm.fastbootwx.module.message.WxMessageProcesser;
import com.mxixm.fastbootwx.module.message.support.WxAsyncMessageReturnValueHandler;
import com.mxixm.fastbootwx.mvc.advice.WxMediaResponseBodyAdvice;
import com.mxixm.fastbootwx.mvc.advice.WxMessageResponseBodyAdvice;
import com.mxixm.fastbootwx.mvc.advice.WxStringResponseBodyAdvice;
import com.mxixm.fastbootwx.mvc.annotation.WxMappingHandlerMapping;
import com.mxixm.fastbootwx.mvc.param.WxArgumentResolver;
import com.mxixm.fastbootwx.support.WxUserProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Configuration
public class WxBuildinMvcConfiguration {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final WxVerifyProperties wxVerifyProperties;

    private final BeanFactory beanFactory;

    private final WxMessageProcesser wxMessageProcesser;

    public WxBuildinMvcConfiguration(WxVerifyProperties wxVerifyProperties, BeanFactory beanFactory, @Lazy WxMessageProcesser wxMessageProcesser) {
        this.wxVerifyProperties = wxVerifyProperties;
        this.beanFactory = beanFactory;
        this.wxMessageProcesser = wxMessageProcesser;
    }

    @Bean
    public WxVerifyController wxVerifyController() {
        return new WxVerifyController(wxVerifyProperties);
    }

    @Bean
    public WxMappingHandlerMapping wxRequestMappingHandlerMapping() {
        WxMappingHandlerMapping wxMappingHandlerMapping = new WxMappingHandlerMapping(wxVerifyController());
        wxMappingHandlerMapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return wxMappingHandlerMapping;
    }

    @Bean
    public WxMediaResourceMessageConverter wxMediaResourceMessageConverter() {
        return new WxMediaResourceMessageConverter();
    }

    @Bean
    public WxMvcConfigurer wxMvcConfigurer(WxUserProvider wxUserProvider) {
        return new WxMvcConfigurer(wxUserProvider, beanFactory);
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

    public static class WxMvcConfigurer extends WebMvcConfigurerAdapter {

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
         */
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
		}*/
    }

}
