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

package com.mxixm.fastboot.weixin.config.invoker;

import com.mxixm.fastboot.weixin.common.WxBeans;
import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.controller.invoker.WxInvokerProxyFactory;
import com.mxixm.fastboot.weixin.controller.invoker.common.WxHttpInputMessageConverter;
import com.mxixm.fastboot.weixin.controller.invoker.component.WxApiHttpRequestFactory;
import com.mxixm.fastboot.weixin.controller.invoker.executor.WxApiExecutor;
import com.mxixm.fastboot.weixin.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastboot.weixin.controller.invoker.handler.WxResponseErrorHandler;
import com.mxixm.fastboot.weixin.module.token.WxTokenServer;
import com.mxixm.fastboot.weixin.support.DefaultWxUserProvider;
import com.mxixm.fastboot.weixin.support.WxAccessTokenManager;
import com.mxixm.fastboot.weixin.module.user.WxUserProvider;
import com.mxixm.fastboot.weixin.util.WxContextUtils;
import com.mxixm.fastboot.weixin.web.WxUserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FastBootWeixin WxInvokerConfiguration
 *
 * @author Guangshan
 * @date 2017/09/21 23:31
 * @since 0.1.2
 */
@Configuration
@ConditionalOnClass(RestTemplate.class)
public class WxInvokerConfiguration {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final WxProperties wxProperties;

    private final ObjectProvider<HttpMessageConverters> messageConverters;

    public WxInvokerConfiguration(
            WxProperties wxProperties,
            ObjectProvider<HttpMessageConverters> messageConverters) {
        this.wxProperties = wxProperties;
        this.messageConverters = messageConverters;
    }

    @Bean
    public WxContextUtils wxApplicationContextUtils() {
        return new WxContextUtils();
    }

    /**
     * 是否有必要模仿Spring不提供RestTemplate，只提供RestTemplateBuilder
     *
     * @return dummy
     */
    @Bean(name = WxBeans.WX_API_INVOKER_NAME)
    public WxApiInvoker wxApiInvoker() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder = builder.requestFactory(new WxApiHttpRequestFactory(wxProperties))
                .errorHandler(new WxResponseErrorHandler());
        HttpMessageConverters converters = this.messageConverters.getIfUnique();
        List<HttpMessageConverter<?>> converterList = new ArrayList<>();
        // 加入默认转换
        converterList.add(new WxHttpInputMessageConverter());
        if (converters != null) {
            converterList.addAll(converters.getConverters());
            builder = builder.messageConverters(Collections.unmodifiableList(converterList));
        }
        return new WxApiInvoker(builder.build());
    }

	/*
        ┌─────┐
	|  wxInvokerProxyFactory defined in class path resource [com/example/myproject/config/invoker/WxInvokerConfiguration.class]
	↑     ↓
	|  wxApiExecutor defined in class path resource [com/example/myproject/config/invoker/WxInvokerConfiguration.class]
	↑     ↓
	|  org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration$EnableWebMvcConfiguration
	↑     ↓
	|  wxButtonArgumentResolver defined in class path resource [com/example/myproject/config/server/WxBuildinMvcConfiguration.class]
	↑     ↓
	|  defaultWxUserProvider (field private com.example.myproject.controller.invoker.WxApiInvokeSpi com.example.myproject.support.DefaultWxUserProvider.wxApiInvokeSpi)
	└─────┘
	 */

    /**
     * 这里之前引用了conversionService，这个conversionService是在WxMvcConfigurer时初始化的
     * 于是产生了循环依赖
     *
     * @param wxAccessTokenManager
     * @return dummy
     */
    @Bean
    public WxApiExecutor wxApiExecutor(WxAccessTokenManager wxAccessTokenManager) {
        return new WxApiExecutor(wxApiInvoker(), wxAccessTokenManager);
    }

    @Bean
    public WxInvokerProxyFactory<WxApiInvokeSpi> wxInvokerProxyFactory(WxApiExecutor wxApiExecutor) {
        return new WxInvokerProxyFactory(WxApiInvokeSpi.class, wxProperties, wxApiExecutor);
    }


    @Bean
    @ConditionalOnMissingBean
    public WxUserProvider userProvider(WxUserManager wxUserManager) {
        return new DefaultWxUserProvider(wxUserManager);
    }

    @Bean
    public WxUserManager wxUserManager(@Lazy WxTokenServer wxTokenServer, @Lazy WxApiInvokeSpi wxApiInvokeSpi) {
        return new WxUserManager(wxTokenServer, wxApiInvokeSpi);
    }

    /**
     * 只考虑微信的消息转换，后期可以优化
     * 其实这里完全可以使用系统的Bean，但是这里我想特殊处理，只对微信消息做转换，所以定制化了几个converter
     *
     * @return dummy
     */
    private HttpMessageConverters getDefaultWxMessageConverters() {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        Jaxb2RootElementHttpMessageConverter xmlConverter = new Jaxb2RootElementHttpMessageConverter();
        AllEncompassingFormHttpMessageConverter formConverter = new AllEncompassingFormHttpMessageConverter();
        ResourceHttpMessageConverter resourceConverter = new ResourceHttpMessageConverter();
        HttpMessageConverters wxMessageConverters = new HttpMessageConverters(stringConverter, jsonConverter, xmlConverter, formConverter, resourceConverter);
        return wxMessageConverters;
    }

}
