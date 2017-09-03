package com.mxixm.fastboot.weixin.module.web.session;

import java.util.Iterator;

/**
 * FastBootWeixin  WxRequest
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxRequest
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/9/2 22:44
 */
public interface WxSession {

    /**
     * session的创建时间
     */
    long getCreationTime();

    /**
     * 设置创建时间
     */
    void setCreationTime(long time);

    /**
     * sessionId
     */
    String getId();

    /**
     * 设置sessionId
     */
    void setId(String id);

    /**
     * 最后一次访问时间
     */
    long getLastAccessedTime();

    /**
     * 最大空闲时间，如果空闲时间超过这个奖杯移除
     */
    void setMaxIdleTime(int maxIdleTime);

    /**
     * 获取最大空闲时间
     */
    int getMaxIdleTime();

    /**
     * 返回空闲时间
     */
    long getIdleTime();

    /**
     * 获取属性
     */
    Object getAttribute(String name);

    /**
     * 获取所有属性名
     */
    Iterator<String> getAttributeNames();

    /**
     * 设置属性，返回被替换的属性
     */
    Object setAttribute(String name, Object value);

    /**
     * 移除属性
     */
    Object removeAttribute(String name);

    /**
     * 无效当前session
     */
    void invalidate();

    /**
     * 获得sessionManager
     */
    WxSessionManager getWxSessionManager();


    /**
     * 设置sessionManager
     */
    void setWxSessionManager(WxSessionManager wxSessionManager);

    /**
     * 设置是否有效
     */
    void setValid(boolean isValid);


    /**
     * 返回当前是否有效
     */
    boolean isValid();

    /**
     * 访问session，其实是修改最后访问时间
     */
    void access();

    /**
     * 不触发异常的无效掉session
     */
    void expire();

}
