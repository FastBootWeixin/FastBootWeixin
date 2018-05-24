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
import com.mxixm.fastboot.weixin.module.credential.WxTokenManager;
import com.mxixm.fastboot.weixin.module.user.WxUserProvider;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.service.WxBaseService;
import com.mxixm.fastboot.weixin.service.WxExtendService;
import com.mxixm.fastboot.weixin.service.invoker.WxInvokerProxyFactoryBean;
import com.mxixm.fastboot.weixin.service.invoker.common.WxHttpInputMessageConverter;
import com.mxixm.fastboot.weixin.service.invoker.common.WxMediaResourceMessageConverter;
import com.mxixm.fastboot.weixin.service.invoker.component.WxApiHttpRequestFactory;
import com.mxixm.fastboot.weixin.service.invoker.executor.WxApiExecutor;
import com.mxixm.fastboot.weixin.service.invoker.executor.WxApiTemplate;
import com.mxixm.fastboot.weixin.service.invoker.handler.WxResponseErrorHandler;
import com.mxixm.fastboot.weixin.support.DefaultWxUserProvider;
import com.mxixm.fastboot.weixin.util.WxContextUtils;
import com.mxixm.fastboot.weixin.web.WxUserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

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

    private final WxMediaResourceMessageConverter wxMediaResourceMessageConverter;

    public WxInvokerConfiguration(WxProperties wxProperties, WxMediaResourceMessageConverter wxMediaResourceMessageConverter) {
        this.wxProperties = wxProperties;
        this.wxMediaResourceMessageConverter = wxMediaResourceMessageConverter;
    }

    @Bean
    public WxContextUtils wxApplicationContextUtils() {
        return new WxContextUtils();
    }

    /**
     * 是否有必要模仿Spring不提供RestTemplate，只提供RestTemplateBuilder
     *
     * @return the result
     */
    @Bean(name = WxBeans.WX_API_TEMPLATE_NAME)
    public WxApiTemplate wxApiTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        // 兼容SB2.0的RestTemplateBuilder
        Method method = ReflectionUtils.findMethod(RestTemplateBuilder.class, "requestFactory", Supplier.class);
        Object arg;
        if (method == null) {
            method = ReflectionUtils.findMethod(RestTemplateBuilder.class, "requestFactory", ClientHttpRequestFactory.class);
            arg = new WxApiHttpRequestFactory(wxProperties);
        } else {
            arg = (Supplier<ClientHttpRequestFactory>) () -> new WxApiHttpRequestFactory(wxProperties);
        }
        builder = (RestTemplateBuilder) ReflectionUtils.invokeMethod(method, builder, arg);

        builder = builder.errorHandler(new WxResponseErrorHandler());
        // 加入默认转换
        // 为了兼容SB2.0，暂时不考虑用户自定义的转换器，因为SB2.0修改了HttpMessageConverters的包地址，不能直接依赖进来了。
        builder = builder.messageConverters(Collections.unmodifiableList(getDefaultWxMessageConverters()));
        return new WxApiTemplate(builder.build());
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
	|  defaultWxUserProvider (field private com.example.myproject.controller.invoker.WxApiService com.example.myproject.support.DefaultWxUserProvider.wxApiInvokeSpi)
	└─────┘
	 */

    /**
     * 这里之前引用了conversionService，这个conversionService是在WxMvcConfigurer时初始化的
     * 于是产生了循环依赖
     *
     * @param wxTokenManager
     * @return the result
     */
    @Bean
    public WxApiExecutor wxApiExecutor(WxTokenManager wxTokenManager) {
        return new WxApiExecutor(wxApiTemplate(), wxTokenManager);
    }

    @Bean
    public WxInvokerProxyFactoryBean<WxApiService> wxInvokerProxyFactory(WxApiExecutor wxApiExecutor) {
        return new WxInvokerProxyFactoryBean(WxApiService.class, wxProperties, wxApiExecutor);
    }

    @Bean
    public WxExtendService wxExtendService(@Lazy WxApiService wxApiService) {
        return new WxExtendService(wxApiService);
    }

    @Bean
    @ConditionalOnMissingBean
    public WxUserProvider userProvider(WxUserManager wxUserManager) {
        return new DefaultWxUserProvider(wxUserManager);
    }

    @Bean
    public WxUserManager wxUserManager(@Lazy WxBaseService wxBaseService, @Lazy WxApiService wxApiService) {
        return new WxUserManager(wxBaseService, wxApiService);
    }

    /**
     * 只考虑微信的消息转换，后期可以优化
     * 其实这里完全可以使用系统的Bean，但是这里我想特殊处理，只对微信消息做转换，所以定制化了几个converter
     * 重新启用，因为Spring2.0重构了MessageConverters，修改了包地址，故不能直接使用了
     * 带来了一点影响，使用者自定义的转换器将不能自动注入到WxApiTemplate。还有其他解决方式，暂时不考虑。
     * 保持和默认的一致
     *
     * @return list
     */
    private List<HttpMessageConverter<?>> getDefaultWxMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        List<HttpMessageConverter<?>> partConverters = new ArrayList<>();

        converters.add(new WxHttpInputMessageConverter());

        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        converters.add(byteArrayHttpMessageConverter);
        partConverters.add(byteArrayHttpMessageConverter);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        converters.add(stringHttpMessageConverter);
        partConverters.add(stringHttpMessageConverter);

        stringHttpMessageConverter = new StringHttpMessageConverter();
        converters.add(stringHttpMessageConverter);
        partConverters.add(stringHttpMessageConverter);

        converters.add(this.wxMediaResourceMessageConverter);
        partConverters.add(this.wxMediaResourceMessageConverter);

        AllEncompassingFormHttpMessageConverter allEncompassingFormHttpMessageConverter = new AllEncompassingFormHttpMessageConverter();
//        allEncompassingFormHttpMessageConverter.setMultipartCharset(Charset.defaultCharset());
        converters.add(allEncompassingFormHttpMessageConverter);

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        converters.add(mappingJackson2HttpMessageConverter);
        partConverters.add(mappingJackson2HttpMessageConverter);

        Jaxb2RootElementHttpMessageConverter jaxb2RootElementHttpMessageConverter = new Jaxb2RootElementHttpMessageConverter();
        converters.add(jaxb2RootElementHttpMessageConverter);
        partConverters.add(jaxb2RootElementHttpMessageConverter);

        ResourceHttpMessageConverter resourceHttpMessageConverter = new ResourceHttpMessageConverter();
        converters.add(resourceHttpMessageConverter);
        partConverters.add(resourceHttpMessageConverter);
        allEncompassingFormHttpMessageConverter.setPartConverters(partConverters);

        return converters;
    }

}
