package com.example.myproject.config.ApiInvoker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.api.url")
public class ApiUrlProperties {

    private String host = "api.weixin.qq.com";

    private String refreshToken = "cgi-bin/token?grant_type=client_credential&appid={appid}&secret={appsecret}";

    private String getCallbackIp = "cgi-bin/getcallbackip?access_token={accessToken}";

    private String getMenu = "cgi-bin/wxMenu/get?access_token={accessToken}";

    private String createMenu = "cgi-bin/wxMenu/builder?access_token={accessToken}";

}
