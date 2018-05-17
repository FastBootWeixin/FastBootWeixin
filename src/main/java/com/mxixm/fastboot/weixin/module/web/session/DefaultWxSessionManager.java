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

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.CacheMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin DefaultWxSessionManager
 *
 * @author Guangshan
 * @date 2017/9/3 16:42
 * @since 0.1.2
 */
public class DefaultWxSessionManager implements WxSessionManager, InitializingBean {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    protected WxSessionIdGenerator wxSessionIdGenerator = null;

    /**
     * sesison超时
     */
    protected int sessionTimeout;

    /**
     * 最大活跃session数
     */
    protected int maxActiveLimit;

    public DefaultWxSessionManager(int sessionTimeout, int maxActiveLimit, WxSessionIdGenerator wxSessionIdGenerator) {
        this.sessionTimeout = sessionTimeout;
        this.maxActiveLimit = maxActiveLimit;
        this.setWxSessionIdGenerator(wxSessionIdGenerator);
    }

    /**
     * 可以参考Tomcat自己的sessionManager，自己内部维护一个Map，这里为了省事用了我自己的cacheMap
     * 缺点是lastAccessTime就只能在get的时候设置了。对于获取属性的操作并不会影响这个值。
     */
    // protected Map<String, WxSession> sessions = new ConcurrentHashMap<>();
    protected CacheMap<String, WxSession> sessions;

    public WxSessionIdGenerator getWxSessionIdGenerator() {
        return wxSessionIdGenerator;
    }

    public void setWxSessionIdGenerator(WxSessionIdGenerator wxSessionIdGenerator) {
        this.wxSessionIdGenerator = wxSessionIdGenerator;
    }

    public void add(WxSession wxSession) {
        sessions.put(wxSession.getId(), wxSession);
        int size = sessions.size();
        if (maxActiveLimit > 0 && size > maxActiveLimit) {
            logger.info("session数量超限，将触发内存清理");
        }
    }

    @Override
    public WxSession createWxSession(WxRequest wxRequest) {
        WxSession wxSession = createEmptySession();
        wxSession.setValid(true);
        wxSession.setCreationTime(System.currentTimeMillis());
        wxSession.setMaxIdleTime(sessionTimeout);
        wxSession.setId(wxSessionIdGenerator.generate(wxRequest));
        add(wxSession);
        return wxSession;
    }


    public WxSession createEmptySession() {
        return getNewWxSession();
    }

    @Override
    public WxSession getWxSession(WxRequest wxRequest) {
        return this.getWxSession(wxRequest, true);
    }

    @Override
    public WxSession getWxSession(WxRequest wxRequest, boolean create) {
        if (wxRequest == null) {
            return null;
        }
        WxSession wxSession = sessions.get(wxSessionIdGenerator.generate(wxRequest));
        if (wxSession != null) {
            wxSession.access();
        } else if (create) {
            wxSession = createWxSession(wxRequest);
        }
        return wxSession;
    }

    protected WxSession getNewWxSession() {
        return new DefaultWxSession(this);
    }

    public WxSession[] findWxSessions() {
        return sessions.values().toArray(new WxSession[0]);
    }

    @Override
    public void removeWxSession(WxSession wxSession) {
        if (wxSession.getId() != null) {
            sessions.remove(wxSession.getId());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WxSessionIdGenerator wxSessionIdGenerator = getWxSessionIdGenerator();
        if (wxSessionIdGenerator == null) {
            setWxSessionIdGenerator(new DefaultWxSessionIdGenerator());
        }
        sessions = CacheMap.<String, WxSession>builder()
                .cacheName("DefaultWxSessionManager")
                .cacheTimeout(sessionTimeout)
                .refreshOnRead()
                .maxSize(maxActiveLimit).build();
    }
}
