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

/**
 * FastBootWeixin WxCredentialStore
 * 微信凭证管理器，包括accessToken、jsticket、cardTicket
 * 注意考虑分布式存储，或许需要加一个lock，因为获取之后上一个会失效，所以不能完全交给setToken方法自己加锁
 * todo 该类放在这儿不太合理，等正式版时挪个位置
 *
 * @author Guangshan
 * @date 2018-5-13 23:09:46
 * @since 0.1.2
 */
public interface WxCredentialStore {

    /**
     * 获取credential
     *
     * @return the credential
     */
    String get();

    /**
     * 设置credential
     *
     * @param credential
     * @param expires
     */
    void store(String credential, long expires);

    /**
     * 获取过期时间
     *
     * @return 过期时间的long
     */
    long expires();

    /**
     * 多线程或者分布式时，防止多个同时设置credential值，也同时用于防止credentialManage同时多次刷新
     *
     * @return 是否加锁成功
     */
    boolean lock();

    /**
     * 解锁
     */
    void unlock();

}