package com.mxixm.fastbootwx.config.message;

import com.mxixm.fastbootwx.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastbootwx.module.media.WxMediaManager;
import com.mxixm.fastbootwx.module.message.WxMessageProcesser;
import com.mxixm.fastbootwx.module.message.WxMessageTemplate;
import com.mxixm.fastbootwx.module.message.processer.*;
import com.mxixm.fastbootwx.module.message.support.WxAsyncMessageReturnValueHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

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
            WxAsyncMessageProperties wxAsyncMessageProperties, WxMediaManager wxMediaManager, @Lazy WxApiInvokeSpi wxApiInvokeSpi) {
        this.wxAsyncMessageProperties = wxAsyncMessageProperties;
        this.wxMediaManager = wxMediaManager;
        this.wxApiInvokeSpi = wxApiInvokeSpi;
    }

    @Bean
    @Lazy
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
