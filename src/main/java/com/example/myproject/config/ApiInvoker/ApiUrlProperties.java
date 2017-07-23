package com.example.myproject.config.ApiInvoker;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wx.api.url")
public class ApiUrlProperties {

    private String host = "/api.weixin.qq.com";

    private String refreshToken = "/cgi-bin/token?grant_type=client_credential&appid={appid}&secret={appsecret}";

}
