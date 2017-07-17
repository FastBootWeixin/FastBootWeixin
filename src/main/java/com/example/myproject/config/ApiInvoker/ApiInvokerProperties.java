package com.example.myproject.config.ApiInvoker;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wx.api.invoker")
public class ApiInvokerProperties implements InitializingBean {

    /**
     * 是否启用https
     */
    private boolean enableHttps = false;

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
        return enableHttps;
    }

    public void setEnableHttps(boolean enableHttps) {
        this.enableHttps = enableHttps;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public boolean isRequestSentRetryEnabled() {
        return requestSentRetryEnabled;
    }

    public void setRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    @Override
    public void afterPropertiesSet() {

    }
}
