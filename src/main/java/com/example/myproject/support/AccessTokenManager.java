package com.example.myproject.support;

import com.example.myproject.config.token.TokenServer;
import com.example.myproject.module.token.AccessToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;

/**
 * FastBootWeixin  AccessTokenManager
 * 暂时没有定时任务，懒获取
 *
 * @author Guangshan
 * @summary FastBootWeixin  AccessTokenManager
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 18:26
 */
public class AccessTokenManager implements InitializingBean {

    private TokenStore tokenStore;

    private TokenServer tokenServer;

    public AccessTokenManager(TokenServer tokenServer, TokenStore tokenStore) {
        this.tokenServer = tokenServer;
        this.tokenStore = tokenStore;
    }

    /**
     * token的冗余时间
     */
    @Value("${wx.verify.token.redundance:10000}")
    private int tokenRedundance;

    private String refrestToken() {
        long now = Instant.now().toEpochMilli();
        if (this.tokenStore.lock()) {
            try {
                // 拿到锁之后再判断一次过期时间，如果过期的话视为还没刷新
                if (tokenStore.getExpireTime() < now) {
                    AccessToken accessToken = tokenServer.refreshToken();
                    tokenStore.setToken(accessToken.getAccessToken(), now + accessToken.getExpiresIn() * 1000);
                    return accessToken.getAccessToken();
                }
            } finally {
                // 如果加锁成功了，一定要解锁
                tokenStore.unlock();
            }
        } else {
            // 加锁失败，直接获取当前token
            // TODO: 2017/7/23 考虑一个更完善的方案，这个方案可能是由问题的
            // 因为如果此时获取了旧的token，但是如果旧的token失效了，那么此时请求会失败
            // 如果设置了请求token失败时重新获取的策略，很有可能造成线程阻塞。
        }
        return tokenStore.getToken();
    }

    public String getToken() {
        long now = Instant.now().toEpochMilli();
        long expireTime = tokenStore.getExpireTime();
        // 如果当前仍在有效期，但是在刷新期内，异步刷新，并返回当前的值
        if (now <= expireTime && expireTime <= now - tokenRedundance) {
            new Thread(() -> this.refrestToken()).start();
            return this.tokenStore.getToken();
        } else if (expireTime < now) {
            return this.refrestToken();
        }
        return this.tokenStore.getToken();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.refrestToken();
    }
}
