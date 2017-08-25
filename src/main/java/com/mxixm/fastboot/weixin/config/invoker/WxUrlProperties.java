package com.mxixm.fastboot.weixin.config.invoker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.api.url")
public class WxUrlProperties {

    private String host = "api.weixin.qq.com";

    private String refreshToken = "cgi-bin/token";

    private String getCallbackIp = "cgi-bin/getcallbackip";

    private String getMenu = "cgi-bin/menu/get";

    private String createMenu = "cgi-bin/menu/create";

    private String getUserAccessTokenByCode = "sns/oauth2/access_token";

    private String getUserInfoByUserAccessToken = "sns/userinfo";

}
