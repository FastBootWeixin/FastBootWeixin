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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * FastBootWeixin DefaultWxSession
 *
 * @author Guangshan
 * @date 2017/9/3 16:42
 * @since 0.1.2
 */
public class DefaultWxSession implements WxSession, Serializable {

    private static final long serialVersionUID = 1L;

    public DefaultWxSession(WxSessionManager wxSessionManager) {
        super();
        this.wxSessionManager = wxSessionManager;
    }

    /**
     * session的属性
     */
    protected ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     * 创建时间
     */
    protected long creationTime = 0L;

    /**
     * sessionId
     */
    protected String id = null;

    /**
     * 最后一次访问时间
     */
    protected long lastAccessedTime = creationTime;

    protected transient WxSessionManager wxSessionManager = null;

    /**
     * 最大空闲时间
     */
    protected volatile int maxIdleTime = 0;

    /**
     * session是否有效
     */
    protected volatile boolean isValid = true;

    /**
     * 设置创建时间
     */
    @Override
    public void setCreationTime(long time) {
        this.creationTime = time;
        this.lastAccessedTime = time;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        // 只能设置一次
        if (this.id != null) {
            return;
        }
        this.id = id;
    }

    @Override
    public long getLastAccessedTime() {
        if (!isValid()) {
            throw new IllegalStateException("session无效");
        }
        return this.lastAccessedTime;
    }

    @Override
    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    @Override
    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * 返回空闲时间
     */
    @Override
    public long getIdleTime() {
        if (!isValid()) {
            throw new IllegalStateException("session无效");
        }
        long timeNow = System.currentTimeMillis();
        return timeNow - lastAccessedTime;
    }

    /**
     * 返回sessionmanager
     */
    @Override
    public WxSessionManager getWxSessionManager() {
        return this.wxSessionManager;
    }

    @Override
    public void setWxSessionManager(WxSessionManager wxSessionManager) {
        this.wxSessionManager = wxSessionManager;
    }


    /**
     * Return the <code>isValid</code> flag for this session.
     */
    @Override
    public boolean isValid() {

        if (!this.isValid) {
            return false;
        }
        if (maxIdleTime > 0) {
            int idleTime = (int) (getIdleTime() / 1000L);
            if (idleTime >= maxIdleTime) {
                expire();
            }
        }
        return this.isValid;
    }


    /**
     * Set the <code>isValid</code> flag for this session.
     *
     * @param isValid The new value for the <code>isValid</code> flag
     */
    @Override
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    @Override
    public void access() {
        this.lastAccessedTime = System.currentTimeMillis();
    }

    /**
     * 过期之，tomcat的session实现是考虑到了正在过期的状态的，我这里以后也要考虑，暂时偷个懒
     */
    @Override
    public void expire() {
        if (!isValid) {
            return;
        }
        synchronized (this) {
            if (!isValid) {
                return;
            }
            if (wxSessionManager == null) {
                return;
            }
            wxSessionManager.removeWxSession(this);
            setValid(false);
        }
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Object getAttribute(String name) {
        if (name == null) {
            return null;
        }
        return attributes.get(name);
    }

    @Override
    public Iterator<String> getAttributeNames() {
        Set<String> names = new HashSet<>();
        names.addAll(attributes.keySet());
        return names.iterator();
    }

    @Override
    public void invalidate() {
        expire();
    }

    @Override
    public Object removeAttribute(String name) {
        if (name == null) {
            return null;
        }
        return attributes.remove(name);
    }

    @Override
    public Object setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name不能为空");
        }
        // 如果value为空意味着删除
        if (value == null) {
            return removeAttribute(name);
        }
        return attributes.put(name, value);
    }

}