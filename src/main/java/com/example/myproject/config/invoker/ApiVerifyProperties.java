package com.example.myproject.config.invoker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.api.verify")
public class ApiVerifyProperties {

    private String token;

    private String appid;

    private String appsecret;

}
