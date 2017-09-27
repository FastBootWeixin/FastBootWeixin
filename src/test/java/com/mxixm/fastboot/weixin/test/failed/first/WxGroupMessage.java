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

package com.mxixm.fastboot.weixin.test.failed.first;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * fastboot-weixin  WxGroupMessage
 *
 * @author Guangshan
 * @date 2017/9/24 14:25
 * @since 0.1.3
 */
public class WxGroupMessage {

    public boolean isGroupMessage() {
        return filter != null;
    }

    public Filter getFilter() {
        return filter;
    }

    /**
     * 群发消息的过滤器
     * 是否有必要再抽象一层？
     */
    @JsonProperty("filter")
    protected Filter filter;

    /**
     * 群发消息的filter
     */
    public static class Filter {

        /**
         * 是否发送给全部
         */
        @JsonProperty("is_to_all")
        protected Boolean isToAll;

        /**
         * 要发送到的tagId
         */
        @JsonProperty("tag_id")
        protected String tagId;

        public Boolean getToAll() {
            return isToAll;
        }

        public void setToAll(Boolean toAll) {
            isToAll = toAll;
        }

        public String getTagId() {
            return tagId;
        }

        public void setTagId(String tagId) {
            this.tagId = tagId;
        }
    }

}
