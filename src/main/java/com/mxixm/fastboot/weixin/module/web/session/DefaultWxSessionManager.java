package com.mxixm.fastboot.weixin.module.web.session;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.CacheMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin  DefaultWxSessionManager
 *
 * @author Guangshan
 * @summary FastBootWeixin  DefaultWxSessionManager
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/9/3 16:42
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
        if (wxSession == null && create) {
            wxSession = createWxSession(wxRequest);
        }
        wxSession.access();
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
