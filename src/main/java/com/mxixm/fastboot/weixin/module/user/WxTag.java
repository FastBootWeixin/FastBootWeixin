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

package com.mxixm.fastboot.weixin.module.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * fastboot-weixin  WxTag
 *
 * @author Guangshan
 * @date 2017/9/24 22:14
 * @since 0.1.3
 */
public class WxTag {

    @JsonProperty("tag")
    private Tag tag;

    public Tag getTag() {
        return tag;
    }

    public static WxTag delete(Integer id) {
        WxTag wxTag = new WxTag();
        wxTag.tag = new Tag();
        wxTag.tag.id = id;
        return wxTag;
    }

    public static WxTag update(Integer id, String name) {
        WxTag wxTag = new WxTag();
        wxTag.tag = new Tag();
        wxTag.tag.id = id;
        wxTag.tag.name = name;
        return wxTag;
    }

    public static WxTag create(String name) {
        WxTag wxTag = new WxTag();
        wxTag.tag = new Tag();
        wxTag.tag.name = name;
        return wxTag;
    }

    public static class Tag {

        @JsonProperty("name")
        private String name;

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("count")
        private Integer count;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    public static class TagList {
        @JsonProperty("tags")
        private List<Tag> tags;

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }

}
