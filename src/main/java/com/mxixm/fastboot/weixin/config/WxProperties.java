package com.mxixm.fastboot.weixin.config;

import com.mxixm.fastboot.weixin.module.Wx;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * FastBootWeixin  WxProperties
 * 总的配置类
 * Spring的统一用法是注入configuration的类，然后只在这个类中使用，所有属性都用set或者构造方法设置进去
 * 而不是像我的用法一样，直接整个属性放进去了，之后可以尝试这样去重构
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxProperties
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/27 20:37
 */
@Data
@ConfigurationProperties(prefix = "wx")
public class WxProperties implements InitializingBean {

    private String token;

    private String appid;

    private String appsecret;

    /**
     * 用户在网页授权页同意授权给公众号后，微信会将授权数据传给一个回调页面，回调页面需在此域名下，以确保安全可靠
     */
    private String callbackUrl;

    private Invoker invoker = new Invoker();

    private System system = new System();

    private Url url = new Url();

    private Message message = new Message();

    private Mvc mvc = new Mvc();

    /**
     * 这里还可以设置其他地方要使用的东西
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Wx.Environment.instance().setWxAppId(this.appid);
        Wx.Environment.instance().setWxAppSecret(this.appsecret);
        Wx.Environment.instance().setWxToken(this.token);
        Wx.Environment.instance().setCallbackUrl(this.callbackUrl);
    }

    @Getter
    public static class Invoker {

        /**
         * 是否启用https
         * 不启用会报43003...
         */
        private boolean enableHttps = true;

        /**
         * TTL
         */
        private int timeToLive = 30;

        /**
         * 最大连接
         */
        private int maxTotal = 200;

        /**
         * 每个route最大连接，每个url最大连接数
         */
        private int maxPerRoute = 200;

        /**
         * 是否启用重试
         */
        private boolean requestSentRetryEnabled = false;

        /**
         * 重试次数
         */
        private int retryCount = 2;

        /**
         * 连接超时
         */
        private int connectTimeout = 5000;

        /**
         * 读取超时
         */
        private int readTimeout = 5000;

        /**
         * 连接池获取超时
         */
        private int connectionRequestTimeout = 200;

    }


    /**
     * 微信系统属性
     */
    @Getter
    public static class System {
    }

    /**
     * 微信url
     */
    @Getter
    public static class Url {

        private String host = "api.weixin.qq.com";

        private String refreshToken = "cgi-bin/token";

        private String getCallbackIp = "cgi-bin/getcallbackip";

        private String getMenu = "cgi-bin/menu/get";

        private String createMenu = "cgi-bin/menu/create";

        private String getUserAccessTokenByCode = "sns/oauth2/access_token";

        private String getUserInfoByUserAccessToken = "sns/userinfo";
    }

    @Getter
    public static class Message {
        /**
         * 核心线程的大小
         */
        private int poolCoreSize = 3;
        /**
         * 最大线程数
         */
        private int poolMaxSize = 6;
        /**
         * 线程存活时间
         */
        private int poolKeepAliveInSeconds = 80;
        /**
         * 最大队列大小
         */
        private int maxQueueSize = 10000;
    }

    @Getter
    public static class Mvc {
        /**
         * 网页授权获取用户基本信息
         * 如果为空，则默认为当前的requestUrl
         */
        private String url;

        private Interceptor interceptor = new Interceptor();

        @Getter
        public static class Interceptor {

            List<String> includePatterns = new ArrayList<>();

            List<String> excludePatterns = new ArrayList<>();
        }
    }

}
