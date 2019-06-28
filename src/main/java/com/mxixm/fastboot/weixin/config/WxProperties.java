/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.config;

import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.web.session.DefaultWxSessionIdGenerator;
import com.mxixm.fastboot.weixin.module.web.session.WxSessionIdGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FastBootWeixin WxProperties
 * 总的配置类
 * Spring的统一用法是注入configuration的类，然后只在这个类中使用，所有属性都用set或者构造方法设置进去
 * 而不是像我的用法一样，直接整个属性放进去了，之后可以尝试这样去重构
 *
 * @author Guangshan
 * @date 2017/09/21 23:33
 * @since 0.1.2
 */
@ConfigurationProperties(prefix = "wx")
public class WxProperties implements InitializingBean {

    private static final int ENCODING_AED_KEY_LENGTH = 43;

    private String token;

    /**
     * 规范化命名
     */
    @Deprecated
    private String appid;

    /**
     * 规范化命名
     */
    private String appId;

    /**
     * 规范化命名
     */
    @Deprecated
    private String appsecret;

    /**
     * 规范化命名
     */
    private String appSecret;

    /**
     * 微信接口配置信息里的path路径
     */
    private String path = "/";

    /**
     * 用户在网页授权页同意授权给公众号后，微信会将授权数据传给一个回调页面，回调页面需在此域名下，以确保安全可靠
     * 在微信公众号请求用户网页授权之前，开发者需要先到公众平台官网中的
     * “开发 - 接口权限 - 网页服务 - 网页帐号 - 网页授权获取用户基本信息”的配置选项中，修改授权回调域名。
     * 请注意，这里填写的是域名（是一个字符串），而不是URL，因此请勿加 http:// 等协议头
     */
    @Deprecated
    private String callbackDomain;

    /**
     * 用于替换上面的属性，以达到菜单使用相对路径的目的
     * 如果只配置callbackDomain则默认使用http协议生成callbackUrl
     */
    private String callbackUrl;

    /**
     * 是否启用消息加解密。
     * 明文模式配置false，兼容模式true或者false均可，加密模式配置为true
     * 注意配置为true时一定要提供EncodingAESKey
     */
    private boolean encrypt = false;

    /**
     * 微信消息加解密使用的aesKey，base64编码，长度为43
     */
    private String encodingAesKey;

    /**
     * 微信接口调用器接口
     */
    private Invoker invoker = new Invoker();

    /**
     * 微信系统配置
     */
    private System system = new System();

    /**
     * 微信服务相关URL配置
     */
    private Url url = new Url();

    /**
     * 发送微信消息相关配置
     */
    private Message message = new Message();

    /**
     * MVC相关配置
     */
    private Mvc mvc = new Mvc();

    /**
     * 服务相关配置
     */
    private Server server = new Server();

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey;
    }

    public WxProperties() {
    }

    /**
     * 这里还可以设置其他地方要使用的东西
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(!this.encrypt ||
                (this.encodingAesKey != null && this.encodingAesKey.length() == ENCODING_AED_KEY_LENGTH),
                "当加密配置为true时，必须存在encodingAesKey，且长度必须为43，请检查配置");
        Wx.Environment.instance().setWxAppId(this.appId);
        Wx.Environment.instance().setWxAppSecret(this.appSecret);
        Wx.Environment.instance().setWxToken(this.token);
        Wx.Environment.instance().setEncrypt(this.encrypt);
        Wx.Environment.instance().setEncodingAesKey(this.encodingAesKey);
        // 优先使用callbackUrl
        Wx.Environment.instance().setCallbackUrl(StringUtils.isEmpty(this.callbackUrl) ? this.callbackDomain : this.callbackUrl);
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getToken() {
        return this.token;
    }

    public String getAppid() {
        return this.appId;
    }

    public String getAppsecret() {
        return this.appSecret;
    }

    public String getCallbackDomain() {
        return this.callbackDomain;
    }

    public Invoker getInvoker() {
        return this.invoker;
    }

    public System getSystem() {
        return this.system;
    }

    public Url getUrl() {
        return this.url;
    }

    public Message getMessage() {
        return this.message;
    }

    public Mvc getMvc() {
        return this.mvc;
    }

    public Server getServer() {
        return this.server;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAppid(String appid) {
        this.appId = appid;
    }

    public void setAppsecret(String appsecret) {
        this.appSecret = appsecret;
    }

    public void setCallbackDomain(String callbackDomain) {
        this.callbackDomain = callbackDomain;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setMvc(Mvc mvc) {
        this.mvc = mvc;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WxProperties)) {
            return false;
        }
        final WxProperties other = (WxProperties) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$token = this.getToken();
        final Object other$token = other.getToken();
        if (this$token == null ? other$token != null : !this$token.equals(other$token)) {
            return false;
        }
        final Object this$appid = this.getAppid();
        final Object other$appid = other.getAppid();
        if (this$appid == null ? other$appid != null : !this$appid.equals(other$appid)) {
            return false;
        }
        final Object this$appsecret = this.getAppsecret();
        final Object other$appsecret = other.getAppsecret();
        if (this$appsecret == null ? other$appsecret != null : !this$appsecret.equals(other$appsecret)) {
            return false;
        }
        final Object this$callbackUrl = this.getCallbackDomain();
        final Object other$callbackUrl = other.getCallbackDomain();
        if (this$callbackUrl == null ? other$callbackUrl != null : !this$callbackUrl.equals(other$callbackUrl)) {
            return false;
        }
        final Object this$invoker = this.getInvoker();
        final Object other$invoker = other.getInvoker();
        if (this$invoker == null ? other$invoker != null : !this$invoker.equals(other$invoker)) {
            return false;
        }
        final Object this$system = this.getSystem();
        final Object other$system = other.getSystem();
        if (this$system == null ? other$system != null : !this$system.equals(other$system)) {
            return false;
        }
        final Object this$url = this.getUrl();
        final Object other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
            return false;
        }
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        final Object this$mvc = this.getMvc();
        final Object other$mvc = other.getMvc();
        if (this$mvc == null ? other$mvc != null : !this$mvc.equals(other$mvc)) {
            return false;
        }
        final Object this$server = this.getServer();
        final Object other$server = other.getServer();
        if (this$server == null ? other$server != null : !this$server.equals(other$server)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $token = this.getToken();
        result = result * PRIME + ($token == null ? 43 : $token.hashCode());
        final Object $appid = this.getAppid();
        result = result * PRIME + ($appid == null ? 43 : $appid.hashCode());
        final Object $appsecret = this.getAppsecret();
        result = result * PRIME + ($appsecret == null ? 43 : $appsecret.hashCode());
        final Object $callbackUrl = this.getCallbackDomain();
        result = result * PRIME + ($callbackUrl == null ? 43 : $callbackUrl.hashCode());
        final Object $invoker = this.getInvoker();
        result = result * PRIME + ($invoker == null ? 43 : $invoker.hashCode());
        final Object $system = this.getSystem();
        result = result * PRIME + ($system == null ? 43 : $system.hashCode());
        final Object $url = this.getUrl();
        result = result * PRIME + ($url == null ? 43 : $url.hashCode());
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        final Object $mvc = this.getMvc();
        result = result * PRIME + ($mvc == null ? 43 : $mvc.hashCode());
        final Object $server = this.getServer();
        result = result * PRIME + ($server == null ? 43 : $server.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof WxProperties;
    }

    @Override
    public String toString() {
        return "com.mxixm.fastboot.weixin.config.WxProperties(token=" + this.getToken() + ", appid=" + this.getAppid() + ", appsecret=" + this.getAppsecret() + ", callbackDomain=" + this.getCallbackDomain() + ", invoker=" + this.getInvoker() + ", system=" + this.getSystem() + ", url=" + this.getUrl() + ", message=" + this.getMessage() + ", mvc=" + this.getMvc() + ", server=" + this.getServer() + ")";
    }

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

        public boolean isEnableHttps() {
            return this.enableHttps;
        }

        public int getTimeToLive() {
            return this.timeToLive;
        }

        public int getMaxTotal() {
            return this.maxTotal;
        }

        public int getMaxPerRoute() {
            return this.maxPerRoute;
        }

        public boolean isRequestSentRetryEnabled() {
            return this.requestSentRetryEnabled;
        }

        public int getRetryCount() {
            return this.retryCount;
        }

        public int getConnectTimeout() {
            return this.connectTimeout;
        }

        public int getReadTimeout() {
            return this.readTimeout;
        }

        public int getConnectionRequestTimeout() {
            return this.connectionRequestTimeout;
        }

        public void setEnableHttps(boolean enableHttps) {
            this.enableHttps = enableHttps;
        }

        public void setTimeToLive(int timeToLive) {
            this.timeToLive = timeToLive;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        public void setMaxPerRoute(int maxPerRoute) {
            this.maxPerRoute = maxPerRoute;
        }

        public void setRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
            this.requestSentRetryEnabled = requestSentRetryEnabled;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }
    }


    /**
     * 微信系统属性
     */
    public static class System {

        // 默认60分钟，菜单刷新时间
        private int menuRefreshIntervalMs = 60 * 60 * 1000;

        public int getMenuRefreshIntervalMs() {
            return menuRefreshIntervalMs;
        }

        public void setMenuRefreshIntervalMs(int menuRefreshIntervalMs) {
            this.menuRefreshIntervalMs = menuRefreshIntervalMs;
        }
    }

    /**
     * 微信url
     */
    public static class Url {

        private String host = "api.weixin.qq.com";

        private String refreshToken = "cgi-bin/token";

        private String getUserAccessTokenByCode = "sns/oauth2/access_token";

        private String verifyUserAccessToken = "sns/auth";

        private String getUserInfoByUserAccessToken = "sns/userinfo";

        private String getUserAccessTokenByRefreshToken = "sns/oauth2/refresh_token";

        public String getHost() {
            return this.host;
        }

        public String getRefreshToken() {
            return this.refreshToken;
        }

        public String getGetUserAccessTokenByCode() {
            return this.getUserAccessTokenByCode;
        }

        public String getGetUserInfoByUserAccessToken() {
            return this.getUserInfoByUserAccessToken;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public void setGetUserAccessTokenByCode(String getUserAccessTokenByCode) {
            this.getUserAccessTokenByCode = getUserAccessTokenByCode;
        }

        public void setGetUserInfoByUserAccessToken(String getUserInfoByUserAccessToken) {
            this.getUserInfoByUserAccessToken = getUserInfoByUserAccessToken;
        }

        public String getGetUserAccessTokenByRefreshToken() {
            return getUserAccessTokenByRefreshToken;
        }

        public void setGetUserAccessTokenByRefreshToken(String getUserAccessTokenByRefreshToken) {
            this.getUserAccessTokenByRefreshToken = getUserAccessTokenByRefreshToken;
        }

        public String getVerifyUserAccessToken() {
            return verifyUserAccessToken;
        }

        public void setVerifyUserAccessToken(String verifyUserAccessToken) {
            this.verifyUserAccessToken = verifyUserAccessToken;
        }
    }

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

        public int getPoolCoreSize() {
            return this.poolCoreSize;
        }

        public int getPoolMaxSize() {
            return this.poolMaxSize;
        }

        public int getPoolKeepAliveInSeconds() {
            return this.poolKeepAliveInSeconds;
        }

        public int getMaxQueueSize() {
            return this.maxQueueSize;
        }

        public void setPoolCoreSize(int poolCoreSize) {
            this.poolCoreSize = poolCoreSize;
        }

        public void setPoolMaxSize(int poolMaxSize) {
            this.poolMaxSize = poolMaxSize;
        }

        public void setPoolKeepAliveInSeconds(int poolKeepAliveInSeconds) {
            this.poolKeepAliveInSeconds = poolKeepAliveInSeconds;
        }

        public void setMaxQueueSize(int maxQueueSize) {
            this.maxQueueSize = maxQueueSize;
        }
    }

    public static class Mvc {
        /**
         * 网页授权获取用户基本信息
         * 如果为空，则默认为当前的requestUrl
         */
        private String url;

        /**
         * 拦截器相关配置
         */
        private Interceptor interceptor = new Interceptor();

        public String getUrl() {
            return this.url;
        }

        public Interceptor getInterceptor() {
            return this.interceptor;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setInterceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
        }

        public static class Interceptor {

            List<String> includePatterns = Stream.of("/wx/**").collect(Collectors.toList());

            List<String> excludePatterns = new ArrayList<>();

            public List<String> getIncludePatterns() {
                return this.includePatterns;
            }

            public List<String> getExcludePatterns() {
                return this.excludePatterns;
            }

            public void setIncludePatterns(List<String> includePatterns) {
                this.includePatterns = includePatterns;
            }

            public void setExcludePatterns(List<String> excludePatterns) {
                this.excludePatterns = excludePatterns;
            }
        }
    }

    public static class Server {

        /**
         * SessionId生成器
         */
        private Class<? extends WxSessionIdGenerator> wxSessionIdGeneratorClass = DefaultWxSessionIdGenerator.class;

        /**
         * sesison超时，默认四个小时
         */
        private int sessionTimeout = 4 * 60 * 60 * 1000;

        /**
         * 最大活跃session数
         */
        private int maxActiveLimit = 0;

        public Class<? extends WxSessionIdGenerator> getWxSessionIdGeneratorClass() {
            return this.wxSessionIdGeneratorClass;
        }

        public int getSessionTimeout() {
            return this.sessionTimeout;
        }

        public int getMaxActiveLimit() {
            return this.maxActiveLimit;
        }

        public void setWxSessionIdGeneratorClass(Class<? extends WxSessionIdGenerator> wxSessionIdGeneratorClass) {
            this.wxSessionIdGeneratorClass = wxSessionIdGeneratorClass;
        }

        public void setSessionTimeout(int sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        public void setMaxActiveLimit(int maxActiveLimit) {
            this.maxActiveLimit = maxActiveLimit;
        }
    }

}
