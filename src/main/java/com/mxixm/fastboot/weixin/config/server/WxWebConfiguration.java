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

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.module.web.session.DefaultWxSessionManager;
import com.mxixm.fastboot.weixin.module.web.session.WxSessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxWebConfiguration
 *
 * @author Guangshan
 * @date 2017/09/3 22:58
 * @since 0.1.2
 */
@Configuration
public class WxWebConfiguration {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final WxProperties wxProperties;

    public WxWebConfiguration(WxProperties wxProperties) {
        this.wxProperties = wxProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxSessionManager wxSessionManager() {
        return new DefaultWxSessionManager(wxProperties.getServer().getSessionTimeout(),
                wxProperties.getServer().getMaxActiveLimit(),
                BeanUtils.instantiateClass(wxProperties.getServer().getWxSessionIdGeneratorClass()));
    }

}
