package com.example.myproject.module.message.support;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.module.media.WxMediaManager;
import com.example.myproject.module.message.WxMessageProcesser;
import com.example.myproject.module.message.WxMessageTemplate;
import com.example.myproject.module.message.processer.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * change log的配置
 *
 * @author Huisman (SE)
 * @Copyright (c) 2017, Lianjia Group All Rights Reserved.
 */
@Configuration
@EnableConfigurationProperties({WxAsyncMessageProperties.class})
public class WxAsyncMessageConfiguration {

    private final WxAsyncMessageProperties wxAsyncMessageProperties;

    private final WxMediaManager wxMediaManager;

    private final WxApiInvokeSpi wxApiInvokeSpi;

    public WxAsyncMessageConfiguration(
            WxAsyncMessageProperties wxAsyncMessageProperties, WxMediaManager wxMediaManager, WxApiInvokeSpi wxApiInvokeSpi) {
        this.wxAsyncMessageProperties = wxAsyncMessageProperties;
        this.wxMediaManager = wxMediaManager;
        this.wxApiInvokeSpi = wxApiInvokeSpi;

    }

    @Bean
    public WxAsyncMessageReturnValueHandler wxAsyncMessageReturnValueHandler() {
        return new WxAsyncMessageReturnValueHandler(wxAsyncMessageProperties, wxMessageTemplate());
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
