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

package com.mxixm.fastboot.weixin.module.card;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * FastBootWeixin WxCard
 *
 * @author Guangshan
 * @date 2017/09/21 23:29
 * @since 0.1.2
 */
public class WxCard {


    /**
     * 设置测试白名单
     *
     */
    public static class WhiteList {

        @JsonProperty("openid")
        private List<String> openIds;

        @JsonProperty("usernames")
        private List<String> usernames;

        WhiteList(List<String> openIds, List<String> usernames) {
            this.openIds = openIds;
            this.usernames = usernames;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private List<String> openIds;
            private List<String> usernames;

            Builder() {
                openIds = new ArrayList<>();
                usernames = new ArrayList<>();
            }

            public Builder addOpenId(List<String> openIds) {
                this.openIds.addAll(openIds);
                return this;
            }

            public Builder addUsername(List<String> usernames) {
                this.usernames.addAll(usernames);
                return this;
            }

            public Builder addOpenId(String openId) {
                this.openIds.add(openId);
                return this;
            }

            public Builder addUsername(String username) {
                this.usernames.add(username);
                return this;
            }

            public WhiteList build() {
                return new WhiteList(openIds, usernames);
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.card.WxCard.WhiteList.Builder(openIds=" + this.openIds + ", usernames=" + this.usernames + ")";
            }
        }
    }

}
