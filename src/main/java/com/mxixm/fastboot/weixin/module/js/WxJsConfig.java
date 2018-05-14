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

package com.mxixm.fastboot.weixin.module.js;

import com.mxixm.fastboot.weixin.util.CryptUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * fastboot-weixin  WxJsConfig
 *
 * @author Guangshan
 * @date 2018/5/7 22:10
 * @since 0.6.0
 */
public class WxJsConfig {

    /**
     * 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
     */
    private boolean debug;

    /**
     * 必填，公众号的唯一标识
     */
    private String appId;

    /**
     * 必填，生成签名的时间戳
     */
    private long timestamp;

    /**
     * 必填，生成签名的随机串
     */
    private String nonceStr;

    /**
     * 必填，签名
     */
    private String signature;

    /**
     * 必填，需要使用的JS接口列表
     */
    private List<String> jsApiList;

    public WxJsConfig() {
    }

    public WxJsConfig(boolean debug, String appId, long timestamp, String nonceStr, String signature, List<String> jsApiList) {
        this.debug = debug;
        this.appId = appId;
        this.timestamp = timestamp;
        this.nonceStr = nonceStr;
        this.signature = signature;
        this.jsApiList = jsApiList;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<String> getJsApiList() {
        return jsApiList;
    }

    public void setJsApiList(List<String> jsApiList) {
        this.jsApiList = jsApiList;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean debug;
        private String appId;
        private long timestamp;
        private String nonceStr;
        private String url;
        private String ticket;
        private List<String> jsApiList;

        Builder() {
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder nonceStr(String nonceStr) {
            this.nonceStr = nonceStr;
            return this;
        }

        public Builder url(String url) {
            Assert.notNull(url, "url不能为空");
            int index = url.indexOf('#');
            this.url = index >= 0 ? url.substring(0, index) : url;
            return this;
        }

        public Builder ticket(String ticket) {
            this.ticket = ticket;
            return this;
        }

        public Builder jsApiList(List<String> jsApiList) {
            return addJsApi(jsApiList);
        }

        public Builder jsApiList(String... jsApis) {
            return addJsApi(jsApis);
        }

        public Builder jsApiList(WxJsApi... jsApis) {
            return addJsApi(jsApis);
        }

        public Builder addJsApi(List<String> jsApiList) {
            if (this.jsApiList == null) {
                this.jsApiList = new ArrayList<>(jsApiList);
            } else {
                this.jsApiList.addAll(jsApiList);
            }
            return this;
        }

        public Builder addJsApi(String... jsApis) {
            return addJsApi(Arrays.asList(jsApis));
        }

        public Builder addJsApi(WxJsApi... jsApis) {
            return addJsApi(Arrays.stream(jsApis).map(WxJsApi::name).collect(Collectors.toList()));
        }

        public WxJsConfig build() {
            String signature = signature();
            return new WxJsConfig(debug, appId, timestamp, nonceStr, signature, jsApiList);
        }

        private String signature() {
            Assert.notNull(ticket, "ticket不能为空");
            Assert.notNull(nonceStr, "nonceStr不能为空");
            Assert.isTrue(timestamp > 0 && timestamp < 10000000000L, "timestamp必须是1到10位数字");
            Assert.notNull(url, "url不能为空");
            StringBuilder sb = new StringBuilder();
            String raw = sb.append("jsapi_ticket=").append(ticket).append('&')
                    .append("noncestr=").append(nonceStr).append('&')
                    .append("timestamp=").append(timestamp).append('&')
                    .append("url=").append(url).toString();
            return CryptUtils.encryptSHA1(raw);
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.js.WxJsConfig.Builder(debug=" + this.debug + ", appId=" + this.appId + ", timestamp=" + this.timestamp + ", nonceStr=" + this.nonceStr + ", url=" + this.url + ", jsApiList=" + this.jsApiList + ")";
        }
    }
}
