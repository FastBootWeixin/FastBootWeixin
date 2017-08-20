package com.example.myproject.module.message.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步线程池的配置
 */
@ConfigurationProperties(prefix = "wx.async.message")
public final class WxAsyncMessageProperties {
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

    public WxAsyncMessageProperties() {
        super();
    }

    public WxAsyncMessageProperties(int poolCoreSize, int poolMaxSize, int poolKeepAliveInSeconds, int maxQueueSize) {
        super();
        this.poolCoreSize = poolCoreSize;
        this.poolMaxSize = poolMaxSize;
        this.poolKeepAliveInSeconds = poolKeepAliveInSeconds;
        this.maxQueueSize = maxQueueSize;
    }

    /**
     * @return the 核心线程的大小
     */
    public int getPoolCoreSize() {
        return this.poolCoreSize;
    }

    /**
     * @return the 最大线程数
     */
    public int getPoolMaxSize() {
        return this.poolMaxSize;
    }

    /**
     * @return the 线程存活时间
     */
    public int getPoolKeepAliveInSeconds() {
        return this.poolKeepAliveInSeconds;
    }

    /**
     * @return the 最大队列大小
     */
    public int getMaxQueueSize() {
        return this.maxQueueSize;
    }

}
