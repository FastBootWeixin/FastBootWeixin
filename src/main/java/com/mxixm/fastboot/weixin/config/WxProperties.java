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

import java.util.ArrayList;
import java.util.List;

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

    private String token;

    private String appid;

    private String appsecret;

    /**
     * 微信接口配置信息里的path路径
     */
    private String path = "/";

    /**
     * 用户在网页授权页同意授权给公众号后，微信会将授权数据传给一个回调页面，回调页面需在此域名下，以确保安全可靠
     */
    private String callbackUrl;

    private Invoker invoker = new Invoker();

    private System system = new System();

    private Url url = new Url();

    private Message message = new Message();

    private Mvc mvc = new Mvc();

    private Server server = new Server();

    public WxProperties() {
    }

    /**
     * 这里还可以设置其他地方要使用的东西
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Wx.Environment.instance().setWxAppId(this.appid);
        Wx.Environment.instance().setWxAppSecret(this.appsecret);
        Wx.Environment.instance().setWxToken(this.token);
        Wx.Environment.instance().setCallbackUrl(this.callbackUrl);
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
        return this.appid;
    }

    public String getAppsecret() {
        return this.appsecret;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
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
        this.appid = appid;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
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

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof WxProperties)) return false;
        final WxProperties other = (WxProperties) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$token = this.getToken();
        final Object other$token = other.getToken();
        if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
        final Object this$appid = this.getAppid();
        final Object other$appid = other.getAppid();
        if (this$appid == null ? other$appid != null : !this$appid.equals(other$appid)) return false;
        final Object this$appsecret = this.getAppsecret();
        final Object other$appsecret = other.getAppsecret();
        if (this$appsecret == null ? other$appsecret != null : !this$appsecret.equals(other$appsecret)) return false;
        final Object this$callbackUrl = this.getCallbackUrl();
        final Object other$callbackUrl = other.getCallbackUrl();
        if (this$callbackUrl == null ? other$callbackUrl != null : !this$callbackUrl.equals(other$callbackUrl))
            return false;
        final Object this$invoker = this.getInvoker();
        final Object other$invoker = other.getInvoker();
        if (this$invoker == null ? other$invoker != null : !this$invoker.equals(other$invoker)) return false;
        final Object this$system = this.getSystem();
        final Object other$system = other.getSystem();
        if (this$system == null ? other$system != null : !this$system.equals(other$system)) return false;
        final Object this$url = this.getUrl();
        final Object other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        final Object this$mvc = this.getMvc();
        final Object other$mvc = other.getMvc();
        if (this$mvc == null ? other$mvc != null : !this$mvc.equals(other$mvc)) return false;
        final Object this$server = this.getServer();
        final Object other$server = other.getServer();
        if (this$server == null ? other$server != null : !this$server.equals(other$server)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $token = this.getToken();
        result = result * PRIME + ($token == null ? 43 : $token.hashCode());
        final Object $appid = this.getAppid();
        result = result * PRIME + ($appid == null ? 43 : $appid.hashCode());
        final Object $appsecret = this.getAppsecret();
        result = result * PRIME + ($appsecret == null ? 43 : $appsecret.hashCode());
        final Object $callbackUrl = this.getCallbackUrl();
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

    public String toString() {
        return "com.mxixm.fastboot.weixin.config.WxProperties(token=" + this.getToken() + ", appid=" + this.getAppid() + ", appsecret=" + this.getAppsecret() + ", callbackUrl=" + this.getCallbackUrl() + ", invoker=" + this.getInvoker() + ", system=" + this.getSystem() + ", url=" + this.getUrl() + ", message=" + this.getMessage() + ", mvc=" + this.getMvc() + ", server=" + this.getServer() + ")";
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
    }


    /**
     * 微信系统属性
     */
    public static class System {
    }

    /**
     * 微信url
     */
    public static class Url {

        private String host = "api.weixin.qq.com";

        private String refreshToken = "cgi-bin/token";

        private String getCallbackIp = "cgi-bin/getcallbackip";

        private String getMenu = "cgi-bin/menu/get";

        private String createMenu = "cgi-bin/menu/create";

        private String getUserAccessTokenByCode = "sns/oauth2/access_token";

        private String getUserInfoByUserAccessToken = "sns/userinfo";

        public String getHost() {
            return this.host;
        }

        public String getRefreshToken() {
            return this.refreshToken;
        }

        public String getGetCallbackIp() {
            return this.getCallbackIp;
        }

        public String getGetMenu() {
            return this.getMenu;
        }

        public String getCreateMenu() {
            return this.createMenu;
        }

        public String getGetUserAccessTokenByCode() {
            return this.getUserAccessTokenByCode;
        }

        public String getGetUserInfoByUserAccessToken() {
            return this.getUserInfoByUserAccessToken;
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
    }

    public static class Mvc {
        /**
         * 网页授权获取用户基本信息
         * 如果为空，则默认为当前的requestUrl
         */
        private String url;

        private Interceptor interceptor = new Interceptor();

        public String getUrl() {
            return this.url;
        }

        public Interceptor getInterceptor() {
            return this.interceptor;
        }

        public static class Interceptor {

            List<String> includePatterns = new ArrayList<>();

            List<String> excludePatterns = new ArrayList<>();

            public List<String> getIncludePatterns() {
                return this.includePatterns;
            }

            public List<String> getExcludePatterns() {
                return this.excludePatterns;
            }
        }
    }

    public static class Server {

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
    }

}
