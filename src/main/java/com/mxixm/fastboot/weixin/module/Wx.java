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

package com.mxixm.fastboot.weixin.module;

import com.mxixm.fastboot.weixin.util.WxUrlUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Dictionary;

/**
 * FastBootWeixin Wx 微信常量类
 *
 * @author Guangshan
 * @date 2017/8/5 21:34
 * @since 0.1.2
 */
public class Wx {

    public static final String DICTIONARY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 个人定义的类目
     */
    public enum Category {
        /**
         * 收到用户消息
         */
        MESSAGE,
        /**
         * 包括按钮事件和用户事件(如关注)
         * 后续可考虑分离按钮事件和用户事件
         */
        EVENT,
        /**
         * 用户按钮事件
         */
        BUTTON,
        /**
         * 系统事件
         * 废弃，不再使用此类型，直接使用EVENT，因为不区分比较方便，根据三个不同Category提供三个注解
         */
        @Deprecated
        SYSTEM
    }


    public static class Environment {

        private static Environment instance = new Environment();

        public static Environment instance() {
            return instance;
        }

        private Environment() {
        }

        /**
         * 默认存储路径，在用户目录下的weixin目录
         */
        private String defaultMediaPath = System.getProperty("java.io.tmpdir");
        // "~/weixin/media/";

        private String wxToken;

        private String wxAppId;

        private String wxAppSecret;

        private URI callbackUri;

        private String callbackUrl;

        private boolean encrypt = false;

        private String encodingAesKey;

        public String getCallbackHost() {
            return callbackUri != null ? callbackUri.getHost() : null;
        }

        public void setCallbackUrl(String callbackUrl) {
            if (StringUtils.isEmpty(callbackUrl)) {
                return;
            }
            callbackUrl = callbackUrl.toLowerCase();
            if (!callbackUrl.startsWith(WxUrlUtils.HTTP_PROTOCOL) && !callbackUrl.startsWith(WxUrlUtils.HTTPS_PROTOCOL)) {
                // 默认http协议
                callbackUrl = WxUrlUtils.HTTP_PROTOCOL + WxUrlUtils.RELAX_PROTOCOL + callbackUrl;
            }
            this.callbackUri = URI.create(callbackUrl);
            this.callbackUrl = callbackUrl;
        }

        public URI getCallbackUri() {
            return callbackUri;
        }

        public String getCallbackUrl() {
            return this.callbackUrl;
        }

        public String getWxToken() {
            return wxToken;
        }

        public void setWxToken(String wxToken) {
            this.wxToken = wxToken;
        }

        public String getWxAppId() {
            return wxAppId;
        }

        public void setWxAppId(String wxAppId) {
            this.wxAppId = wxAppId;
        }

        public String getWxAppSecret() {
            return wxAppSecret;
        }

        public void setWxAppSecret(String wxAppSecret) {
            this.wxAppSecret = wxAppSecret;
        }

        public String getDefaultMediaPath() {
            return defaultMediaPath;
        }

        public void setDefaultMediaPath(String defaultMediaPath) {
            this.defaultMediaPath = defaultMediaPath;
        }

        public boolean isEncrypt() {
            return encrypt;
        }

        public void setEncrypt(boolean encrypt) {
            this.encrypt = encrypt;
        }

        public String getEncodingAesKey() {
            return encodingAesKey;
        }

        public void setEncodingAesKey(String encodingAesKey) {
            this.encodingAesKey = encodingAesKey;
        }
    }

}
