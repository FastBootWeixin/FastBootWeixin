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

package com.mxixm.fastboot.weixin.module.web.session;

import java.util.Iterator;

/**
 * FastBootWeixin WxSession
 *
 * @author Guangshan
 * @date 2017/9/2 22:44
 * @since 0.1.2
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
