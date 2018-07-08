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
 * 2018年7月8日 本想为顶级接口加上无参方法，但是不符合设计模式，也不符合预期，故移除。
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
    String get(WxCredential.Type type);

    /**
     * 根据类型设置credential
     */
    void store(WxCredential.Type type, String credential, long expires);

    /**
     * 获取过期时间
     *
     * @return 过期时间的long
     */
    long expires(WxCredential.Type type);

    /**
     * 多线程或者分布式时，防止多个同时设置credential值，也同时用于防止credentialManage同时多次刷新
     *
     * @return 是否加锁成功
     */
    boolean lock(WxCredential.Type type);

    /**
     * 解锁
     */
    void unlock(WxCredential.Type type);

    /**
     * 只支持单个类型的存储，所有方法上都不需要参数type了
     * 不校验类型，直接调用无参方法
     * 本来还想加个类型校验，但是因为WxTicket使用的是相同的类型，所以这里加不了类型校验，直接调用
     */
    interface Single extends WxCredentialStore {
        /**
         * 获取credential
         *
         * @return the credential
         */
        String get();

        /**
         * 根据类型设置credential
         * @param credential 值
         * @param expires 时间
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

        @Override
        default String get(WxCredential.Type type) {
            return get();
        }

        @Override
        default void store(WxCredential.Type type, String credential, long expires) {
            store(credential, expires);
        }

        @Override
        default long expires(WxCredential.Type type) {
            return expires();
        }

        @Override
        default boolean lock(WxCredential.Type type) {
            return lock();
        }

        @Override
        default void unlock(WxCredential.Type type) {
            unlock();
        }

        class Adapter implements Single {

            private WxCredential.Type type;

            private WxCredentialStore wxCredentialStore;

            public Adapter(WxCredential.Type type, WxCredentialStore wxCredentialStore) {
                this.type = type;
                this.wxCredentialStore = wxCredentialStore;
            }

            @Override
            public String get() {
                return wxCredentialStore.get(type);
            }

            @Override
            public void store(String credential, long expires) {
                wxCredentialStore.store(type, credential, expires);
            }

            @Override
            public long expires() {
                return wxCredentialStore.expires(type);
            }

            @Override
            public boolean lock() {
                return wxCredentialStore.lock(type);
            }

            @Override
            public void unlock() {
                wxCredentialStore.unlock(type);
            }
        }

    }

}