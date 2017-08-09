package com.example.myproject.config.invoker;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx.api.invoker")
public class WxInvokerProperties implements InitializingBean {

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

    @Override
    public void afterPropertiesSet() {

    }
}
