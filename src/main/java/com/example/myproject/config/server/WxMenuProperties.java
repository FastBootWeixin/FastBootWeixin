package com.example.myproject.config.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.menu")
public class WxMenuProperties {

    private boolean autoCreate = true;

}
