package com.mxixm.fastboot.weixin.config.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * wxMvc属性
 */
@Data
@ConfigurationProperties(prefix = "wx.mvc.interceptor")
public final class WxMvcProperties {

    List<String> includePatterns = new ArrayList<>();

    List<String> excludePatterns = new ArrayList<>();

}
