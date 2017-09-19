/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.config.message;

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.message.processer.*;
import com.mxixm.fastboot.weixin.module.message.support.WxAsyncMessageReturnValueHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WxAsyncMessageConfiguration {

    private final WxProperties wxProperties;

    private final WxMediaManager wxMediaManager;

    private final WxApiInvokeSpi wxApiInvokeSpi;

    public WxAsyncMessageConfiguration(
            WxProperties wxProperties, WxMediaManager wxMediaManager, @Lazy WxApiInvokeSpi wxApiInvokeSpi) {
        this.wxProperties = wxProperties;
        this.wxMediaManager = wxMediaManager;
        this.wxApiInvokeSpi = wxApiInvokeSpi;
    }

    @Bean
    @Lazy
    public WxAsyncMessageReturnValueHandler wxAsyncMessageReturnValueHandler() {
        return new WxAsyncMessageReturnValueHandler(wxProperties, wxMessageTemplate());
    }

    @Bean
    public WxMessageTemplate wxMessageTemplate() {
        return new WxMessageTemplate(wxApiInvokeSpi, wxMessageProcesser());
    }

    @Bean
    public WxMessageProcesser wxMessageProcesser() {
        WxMessageProcesseChain wxMessageProcesserChain = new WxMessageProcesseChain();
        wxMessageProcesserChain.addProcessers(getDefaultProcessor(wxMediaManager));
        return wxMessageProcesserChain;
    }

    private List<WxMessageProcesser> getDefaultProcessor(WxMediaManager wxMediaManager) {
        List<WxMessageProcesser> list = new ArrayList<>();
        list.add(new WxCommonMessageProcesser());
        list.add(new WxImageMessageProcesser(wxMediaManager));
        list.add(new WxVoiceMessageProcesser(wxMediaManager));
        list.add(new WxMusicMessageProcesser(wxMediaManager));
        list.add(new WxVideoMessageProcesser(wxMediaManager));
        list.add(new WxNewsMessageProcesser());
        return list;
    }

}
