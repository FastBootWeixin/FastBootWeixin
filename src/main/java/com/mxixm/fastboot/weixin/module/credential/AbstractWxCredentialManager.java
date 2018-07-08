/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.module.credential;

import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.concurrent.*;

/**
 * FastBootWeixin AbstractWxCredentialManager
 * 暂时没有定时任务，懒获取
 * 这里是Wx的authentication authorization相关的父类，用于管理微信的凭证
 *
 * @author Guangshan
 * @date 2018-5-13 14:20:34
 * @since 0.6.0
 */
public abstract class AbstractWxCredentialManager {

    private WxCredential.Type type;

    private WxCredentialStore wxCredentialStore;

    /**
     * 守护线程timer
     */
    private static ExecutorService executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1), Executors.defaultThreadFactory());

    public AbstractWxCredentialManager(WxCredential.Type type, WxCredentialStore wxCredentialStore) {
        this.type = type;
        this.wxCredentialStore = wxCredentialStore;
    }

    /**
     * token的冗余时间
     */
    @Value("${wx.credential.redundance:10000}")
    private int tokenRedundance;

    /**
     * 刷新，会判断时间，如果没过期不会刷新
     * @return 新的凭证
     */
    public String refresh() {
        long now = Instant.now().toEpochMilli();
        if (this.wxCredentialStore.lock(this.type)) {
            try {
                // 拿到锁之后再判断一次过期时间，如果过期的话视为还没刷新
                if (wxCredentialStore.expires(this.type) < now) {
                    WxCredential wxCredential = this.refreshInternal();
                    wxCredentialStore.store(this.type, wxCredential.getCredential(), now + wxCredential.getExpiresIn() * 1000);
                    return wxCredential.getCredential();
                }
            } finally {
                // 如果加锁成功了，一定要解锁
                wxCredentialStore.unlock(this.type);
            }
        } else {
            // 加锁失败，直接获取当前token
            // TODO: 2017/7/23 考虑一个更完善的方案，这个方案可能是有问题的
            // 因为如果此时获取了旧的token，但是如果旧的token失效了，那么此时请求会失败
            // 如果设置了请求token失败时重新获取的策略，很有可能造成线程阻塞。
        }
        return wxCredentialStore.get(this.type);
    }

    /**
     * 强制刷新
     * @return 新的凭证
     */
    public String forceRefresh() {
        wxCredentialStore.store(this.type, null, 0);
        return this.refresh();
    }

    /**
     * 执行刷新操作
     * @return
     */
    protected abstract WxCredential refreshInternal();

    public String get() {
        long now = Instant.now().toEpochMilli();
        long expiresTime = wxCredentialStore.expires(this.type);
        // 如果当前仍在有效期，但是在刷新期内，异步刷新，并返回当前的值
        if (now <= expiresTime && expiresTime <= now - tokenRedundance) {
            executor.execute(() -> this.refresh());
            return this.wxCredentialStore.get(this.type);
        } else if (expiresTime < now) {
            return this.refresh();
        }
        return this.wxCredentialStore.get(this.type);
    }

}
