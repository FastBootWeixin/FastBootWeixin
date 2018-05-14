/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.config.credential;

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.module.credential.*;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.service.WxBaseService;
import com.mxixm.fastboot.weixin.service.invoker.executor.WxApiTemplate;
import com.mxixm.fastboot.weixin.support.MemoryWxJsTicketStore;
import com.mxixm.fastboot.weixin.support.MemoryWxTokenStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxTicketConfiguration
 *
 * @author Guangshan
 * @date 2018-5-8 00:10:14
 * @since 0.6.0
 */
@Configuration
public class WxCredentialConfiguration {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final WxProperties wxProperties;

    public WxCredentialConfiguration(WxProperties wxProperties) {
        this.wxProperties = wxProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxTokenStore wxTokenStore() {
        return new MemoryWxTokenStore();
    }

    @Bean
    public WxBaseService wxBaseService(WxApiTemplate wxApiTemplate) {
        return new WxBaseService(wxApiTemplate, wxProperties);
    }

    @Bean
    public WxTokenManager wxTokenManager(WxBaseService wxBaseService, WxTokenStore wxTokenStore) {
        return new WxTokenManager(wxBaseService, wxTokenStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public WxJsTicketStore wxJsTicketStore() {
        return new MemoryWxJsTicketStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public WxJsTicketPart wxJsTicketPart() {
        return new DefaultWxJsTicketPart();
    }

    @Bean
    public WxJsTicketManager wxJsTicketManager(WxJsTicketPart wxJsTicketPart, WxJsTicketStore wxJsTicketStore, WxApiService wxApiService) {
        return new WxJsTicketManager(this.wxProperties.getAppid(), wxJsTicketPart, wxJsTicketStore, wxApiService);
    }

}
