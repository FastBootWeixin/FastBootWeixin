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

package com.mxixm.fastboot.weixin.module.extend;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FastBootWeixin  WxShortUrl
 *
 * @author Guangshan
 * @date 2017/9/23 17:47
 * @since 0.1.2
 */
public class WxShortUrl {

    public enum Action {

        @JsonProperty("long2short")
        LONG2SHORT
    }

    @JsonProperty("action")
    private Action action;

    @JsonProperty("long_url")
    private String longUrl;

    WxShortUrl(Action action, String longUrl) {
        this.action = action;
        this.longUrl = longUrl;
    }



    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Action action;
        private String longUrl;

        Builder() {
        }

        public Builder longUrl(String longUrl) {
            this.action = Action.LONG2SHORT;
            this.longUrl = longUrl;
            return this;
        }

        public WxShortUrl build() {
            return new WxShortUrl(action, longUrl);
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.extend.WxShortUrl.WxShortUrlBuilder(action=" + this.action + ", longUrl=" + this.longUrl + ")";
        }
    }

    /**
     * 短链接结果
     */
    public static class Result {

        @JsonProperty("short_url")
        private String shortUrl;

        public String getShortUrl() {
            return shortUrl;
        }

        public void setShortUrl(String shortUrl) {
            this.shortUrl = shortUrl;
        }
    }

}
