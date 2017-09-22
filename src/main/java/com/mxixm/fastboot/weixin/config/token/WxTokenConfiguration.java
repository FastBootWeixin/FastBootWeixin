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

package com.mxixm.fastboot.weixin.config.token;

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastboot.weixin.module.token.WxTokenServer;
import com.mxixm.fastboot.weixin.support.MemoryWxTokenStore;
import com.mxixm.fastboot.weixin.support.WxAccessTokenManager;
import com.mxixm.fastboot.weixin.support.WxTokenStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxTokenConfiguration
 *
 * @author Guangshan
 * @date 2017/09/21 23:33
 * @since 0.1.2
 */
@Configuration
public class WxTokenConfiguration {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final WxProperties wxProperties;

    public WxTokenConfiguration(
            WxProperties wxProperties) {
        this.wxProperties = wxProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxTokenStore wxTokenStore() {
        return new MemoryWxTokenStore();
    }

    @Bean
    public WxTokenServer wxTokenServer(WxApiInvoker wxApiInvoker) {
        return new WxTokenServer(wxApiInvoker, wxProperties);
    }

    @Bean
    public WxAccessTokenManager wxAccessTokenManager(WxTokenServer wxTokenServer, WxTokenStore wxTokenStore) {
        return new WxAccessTokenManager(wxTokenServer, wxTokenStore);
    }

}
