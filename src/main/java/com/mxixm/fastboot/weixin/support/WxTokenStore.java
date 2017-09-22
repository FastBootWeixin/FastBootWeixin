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

package com.mxixm.fastboot.weixin.support;

/**
 * FastBootWeixin WxTokenStore
 * 注意考虑分布式存储，或许需要加一个lock，因为获取之后上一个会失效，所以不能完全交给setToken方法自己加锁
 *
 * @author Guangshan
 * @date 2017/7/23 17:08
 * @since 0.1.2
 */
public interface WxTokenStore {

    /**
     * 获取Token
     *
     * @return dummy
     */
    String getToken();

    /**
     * 设置token
     *
     * @param token
     * @param expireTime
     */
    void setToken(String token, long expireTime);

    /**
     * 获取过期时间
     *
     * @return dummy
     */
    long getExpireTime();

    /**
     * 多线程或者分布式时，防止多个同时设置token值，也同时用于防止tokenManage同时多次刷新
     *
     * @return dummy
     */
    boolean lock();

    /**
     * 解锁
     */
    void unlock();

}