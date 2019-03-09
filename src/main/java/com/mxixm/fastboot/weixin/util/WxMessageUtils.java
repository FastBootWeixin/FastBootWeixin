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

package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.module.message.WxUserMessage;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * FastBootWeixin WxMessageUtils
 * 消息相关的工具类
 *
 * @author Guangshan
 * @date 2018-5-24 17:03:28
 * @since 0.6.1
 */
public class WxMessageUtils {

    private static Set<Class<? extends WxUserMessage>> xmlResponseTypes = new HashSet<>(8);

    static {
        xmlResponseTypes.add(WxUserMessage.Text.class);
        xmlResponseTypes.add(WxUserMessage.Image.class);
        xmlResponseTypes.add(WxUserMessage.Voice.class);
        xmlResponseTypes.add(WxUserMessage.Music.class);
        xmlResponseTypes.add(WxUserMessage.Video.class);
        xmlResponseTypes.add(WxUserMessage.News.class);
    }

    /**
     * 是否支持xml方式回复消息
     * @param type
     * @return result
     */
    public static boolean supportsXmlResponse(Class<?> type) {
        return xmlResponseTypes.contains(type) || CharSequence.class.isAssignableFrom(type);
    }

    public static Link.Builder linkBuilder() {
        return new Link.Builder();
    }

    /**
     * 小程序元素，用于返回文本内容使用小程序。文本消息只支持a标签
     * appid对应的小程序必须与公众号有绑定关系
     */
    public static class Link {

        private static final String linkTemplate = "<a href=\"%s\" %s>%s</a>";

        private static final String miniProgramTemplate = "data-miniprogram-appid=\"%s\" data-miniprogram-path=\"%s\"";

        /**
         * 对于不支持data-miniprogram-appid 项的客户端版本，如果有herf项，则仍然保持跳href中的网页链接
         */
        private String href;

        /**
         * 填写小程序appid，则表示该链接跳小程序
         */
        private String appId;

        /**
         * 填写小程序路径，路径与app.json中保持一致，可带参数
         */
        private String path;

        /**
         * 显示文本内容
         */
        private String text;

        Link(String href, String appId, String path, String text) {
            this.href = href;
            this.appId = appId;
            this.path = path;
            this.text = text;
        }

        @Override
        public String toString() {
            String extendAttribute = "";
            if (!StringUtils.isEmpty(appId)) {
                extendAttribute = String.format(miniProgramTemplate, appId, path);
            }
            return String.format(linkTemplate, href, extendAttribute, text);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String href = "#";
            private String appId = "";
            private String path = "";
            private String text = "";

            Builder() {
            }

            public Builder href(String href) {
                this.href = WxUrlUtils.absoluteUrl(WxWebUtils.getWxMessageParameter().getRequestUrl(), href);
                return this;
            }

            public Builder appId(String appId) {
                this.appId = appId;
                return this;
            }

            public Builder path(String path) {
                this.path = path;
                return this;
            }

            public Builder text(String text) {
                this.text = text;
                return this;
            }

            public String build() {
                return new Link(href, appId, path, text).toString();
            }

            @Override
            public String toString() {
                return "com.mxixm.fastboot.weixin.util.WxMessageUtils.Link.Builder(href=" + this.href + ", appId=" + this.appId + ", path=" + this.path + ", text=" + this.text + ")";
            }
        }
    }


}
