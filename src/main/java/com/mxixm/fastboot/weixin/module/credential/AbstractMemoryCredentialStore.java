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

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * fastboot-weixin  AbstractMemoryCredentialStore
 *
 * @author Guangshan
 * @date 2018/5/13 23:48
 * @since 0.6.0
 */
public abstract class AbstractMemoryCredentialStore implements WxCredentialStore.Single {

    /**
     * 锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 多线程或者分布式时，防止多个同时设置token值，也同时用于防止tokenManage同时多次刷新
     *
     * @return the result
     */
    @Override
    public boolean lock() {
        this.lock.lock();
        long now = Instant.now().toEpochMilli();
        // 如果在有效期内，则说明加锁失败，获得锁的时候已经被别人刷新了
        if (now < this.expires()) {
            this.unlock();
            return false;
        }
        return true;
    }

    /**
     * 多线程或者分布式时，防止多个同时设置token值，也同时用于防止tokenManage同时多次刷新
     */
    @Override
    public void unlock() {
        this.lock.unlock();
    }

}
