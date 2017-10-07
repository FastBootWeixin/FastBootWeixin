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

package com.mxixm.fastboot.weixin.config.media;

import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastboot.weixin.module.media.WxMediaStore;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.support.MapDbWxMediaStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxMediaConfiguration
 *
 * @author Guangshan
 * @date 2017/09/21 23:31
 * @since 0.1.2
 */
@Configuration
public class WxMediaConfiguration {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

//	private final WxApiInvokeSpi wxApiInvokeSpi;
//
//	private final WxApiInvoker wxApiInvoker;
//
//	public WxMediaConfiguration(WxApiInvokeSpi wxApiInvokeSpi, WxApiInvoker wxApiInvoker) {
//		this.wxApiInvokeSpi = wxApiInvokeSpi;
//		this.wxApiInvoker = wxApiInvoker;
//	}

    @Bean
    @ConditionalOnMissingBean
    public WxMediaStore wxMediaStore() {
        return new MapDbWxMediaStore();
    }

    @Bean
    public WxMediaManager wxMediaManager(@Lazy WxApiInvokeSpi wxApiInvokeSpi, @Lazy WxApiInvoker wxApiInvoker) {
        return new WxMediaManager(wxApiInvokeSpi, wxApiInvoker, wxMediaStore());
    }

}
