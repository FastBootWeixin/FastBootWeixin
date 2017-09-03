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
 * FastBootWeixin  WxWebConfiguration
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxWebConfiguration
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/9/3 22:58
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
                BeanUtils.instantiate(wxProperties.getServer().getWxSessionIdGeneratorClass()));
    }

}
