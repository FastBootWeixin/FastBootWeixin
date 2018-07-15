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

package com.mxixm.fastboot.weixin.config.message;

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.message.WxTemplateMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.WxUserMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.processor.*;
import com.mxixm.fastboot.weixin.module.message.processor.group.WxGroupNewsMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.processor.user.WxUserNewsMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageTemplate;
import com.mxixm.fastboot.weixin.service.WxApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

/**
 * FastBootWeixin WxAsyncMessageConfiguration
 *
 * @author Guangshan
 * @date 2017/09/21 23:32
 * @since 0.1.2
 */
@Configuration
public class WxMessageConfiguration {

    private final WxProperties wxProperties;

    private final WxMediaManager wxMediaManager;

    private final WxApiService wxApiService;

    public WxMessageConfiguration(
            WxProperties wxProperties, WxMediaManager wxMediaManager, @Lazy WxApiService wxApiService) {
        this.wxProperties = wxProperties;
        this.wxMediaManager = wxMediaManager;
        this.wxApiService = wxApiService;
    }

    /**
     * 干掉这两个，用新的替换
    @Bean
    @Lazy
    public WxSyncMessageReturnValueHandler wxSyncMessageReturnValueHandler() {
        return new WxSyncMessageReturnValueHandler(wxMessageTemplate());
    }

    @Bean
    @Lazy
    public WxAsyncMessageReturnValueHandler wxAsyncMessageReturnValueHandler() {
        return new WxAsyncMessageReturnValueHandler(wxAsyncMessageTemplate());
    }
     **/

    @Bean
    public WxMessageTemplate wxMessageTemplate() {
        return new WxMessageTemplate(wxApiService, wxMessageProcessor());
    }

    @Bean
    public WxAsyncMessageTemplate wxAsyncMessageTemplate() {
        return new WxAsyncMessageTemplate(wxProperties, wxMessageTemplate());
    }

    /**
     * todo 如果需要开放给开发者自定义消息处理，则需要把这个改成WxMessageProcessors，类似于HttpMessageConverters
     * @return WxMessageProcessor
     */
    @Bean
    public WxMessageProcessor wxMessageProcessor() {
        WxMessageProcessorChain wxMessageProcessorChain = new WxMessageProcessorChain();
        wxMessageProcessorChain.addProcessors(getDefaultProcessor(wxMediaManager));
        return wxMessageProcessorChain;
    }

    private List<WxMessageProcessor> getDefaultProcessor(WxMediaManager wxMediaManager) {
        List<WxMessageProcessor> list = new ArrayList<>();

        list.add(new WxUserMessageProcessor());
        list.add(new WxTemplateMessageProcessor());

        list.add(new WxImageMessageProcessor(wxMediaManager));
        list.add(new WxVoiceMessageProcessor(wxMediaManager));
        list.add(new WxMusicMessageProcessor(wxMediaManager));
        list.add(new WxVideoMessageProcessor(wxMediaManager));
        list.add(new WxMiniProgramMessageProcessor(wxMediaManager));

        list.add(new WxUserNewsMessageProcessor());
        list.add(new WxGroupNewsMessageProcessor());

        return list;
    }

}
