package com.mxixm.fastbootwx.config.invoker;

import com.mxixm.fastbootwx.module.Wx;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.api.verify")
public class WxVerifyProperties implements InitializingBean {

    private String token;

    private String appid;

    private String appsecret;

    @Override
    public void afterPropertiesSet() throws Exception {
        Wx.Environment.instance().setWxToken(this.token);
        Wx.Environment.instance().setWxAppId(this.appid);
        Wx.Environment.instance().setWxAppSecret(this.appsecret);
    }
}
