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

package com.mxixm.fastboot.weixin.module.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * fastboot-weixin  WxGroupMessage
 *
 * @author Guangshan
 * @date 2017/9/24 14:25
 * @since 0.1.3
 */
public class WxGroupMessage<T extends WxMessageBody> extends WxMessage<T> {

    private static Map<Type, Class<? extends WxGroupMessage>> classMap = new HashMap<>();

    static {
        classMap.put(Type.TEXT, WxGroupMessage.Text.class);
        classMap.put(Type.IMAGE, WxGroupMessage.Image.class);
        classMap.put(Type.VOICE, WxGroupMessage.Voice.class);
        classMap.put(Type.VIDEO, WxGroupMessage.Video.class);
        classMap.put(Type.MUSIC, WxGroupMessage.Music.class);
        classMap.put(Type.NEWS, WxGroupMessage.News.class);
        classMap.put(Type.MPNEWS, WxGroupMessage.MpNews.class);
        classMap.put(Type.WXCARD, WxGroupMessage.WxCard.class);
    }

    @JsonProperty("touser")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
    protected Collection<String> toUsers;

    @JsonProperty("filter")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Filter filter;

    /**
     * 群发的filter结构
     */
    protected static class Filter {

        @JsonProperty("is_to_all")
        protected boolean isToAll = true;

        @JsonProperty("tag_id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        protected Integer tagId;

    }

    public static class GroupMessageBuilder {

        protected Builder builder;
        protected Collection<String> toUsers = new HashSet<>();
        protected Filter filter = new Filter();

        GroupMessageBuilder(Builder builder) {
            this.builder = builder;
        }

        public GroupMessageBuilder toTag(int tagId) {
            filter.isToAll = false;
            filter.tagId = tagId;
            return this;
        }

        public GroupMessageBuilder toUsers(Collection<String> userList) {
            this.toUsers = userList;
            return this;
        }

        public GroupMessageBuilder toUsers(String... users) {
            return this.toUsers(new ArrayList<>(Arrays.asList(users)));
        }

        public GroupMessageBuilder preview(String user) {
            return this.toUsers(Arrays.asList(user));
        }

        public GroupMessageBuilder addUser(String user) {
            if (this.toUsers == null) {
                this.toUsers = new ArrayList<>();
            }
            this.toUsers.add(user);
            return this;
        }

        public GroupMessageBuilder addUsers(Collection<String> users) {
            if (this.toUsers == null) {
                this.toUsers = new ArrayList<>();
            }
            this.toUsers.addAll(users);
            return this;
        }

        public GroupMessageBuilder addUsers(String... users) {
            return this.addUsers(Arrays.asList(users));
        }

        public WxGroupMessage build() {
            WxGroupMessage wxGroupMessage;
            if (classMap.containsKey(builder.messageType)) {
                wxGroupMessage = BeanUtils.instantiateClass(classMap.get(builder.messageType));
            } else {
                // 如果不存在时，是否有必要抛出异常提示？暂时不加
                wxGroupMessage = new WxGroupMessage();
            }
            wxGroupMessage.setMessageType(builder.messageType);
            wxGroupMessage.setBody(builder.body);
            if (toUsers.isEmpty()) {
                wxGroupMessage.filter = filter;
            } else {
                wxGroupMessage.toUsers = toUsers;
            }
            return wxGroupMessage;
        }

    }

    public static class Text extends WxGroupMessage<WxMessageBody.Text> {

        @JsonProperty("text")
        protected WxMessageBody.Text body;

        @Override
        public WxMessageBody.Text getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.Text body) {
            this.body = body;
        }
    }

    public static class Image extends WxGroupMessage<WxMessageBody.Image> {

        @JsonProperty("image")
        protected WxMessageBody.Image body;

        @Override
        public WxMessageBody.Image getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.Image body) {
            this.body = body;
        }
    }

    public static class Voice extends WxGroupMessage<WxMessageBody.Voice> {

        @JsonProperty("voice")
        protected WxMessageBody.Voice body;

        @Override
        public WxMessageBody.Voice getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.Voice body) {
            this.body = body;
        }
    }

    public static class Video extends WxGroupMessage<WxMessageBody.Video> {

        @JsonProperty("video")
        protected WxMessageBody.Video body;

        @Override
        public WxMessageBody.Video getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.Video body) {
            this.body = body;
        }
    }

    public static class Music extends WxGroupMessage<WxMessageBody.Music> {

        @JsonProperty("music")
        protected WxMessageBody.Music body;

        @Override
        public WxMessageBody.Music getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.Music body) {
            this.body = body;
        }
    }

    /**
     * 图文消息（点击跳转到外链）
     */
    public static class News extends WxGroupMessage<WxMessageBody.News> {

        /**
         * 图文消息个数，限制为8条以内
         */
        @JsonProperty("news")
        protected WxMessageBody.News body;

        @Override
        public WxMessageBody.News getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.News body) {
            this.body = body;
        }
    }

    /**
     * 发送图文消息（点击跳转到图文消息页面）
     */
    public static class MpNews extends WxGroupMessage<WxMessageBody.MpNews> {

        @JsonProperty("mpnews")
        protected WxMessageBody.MpNews body;

        /**
         * 图文消息被判定为转载时，是否继续群发。1为继续群发（转载），0为停止群发。该参数默认为0。
         */
        @JsonProperty("send_ignore_reprint")
        protected int sendIgnoreReprint;

        @Override
        public WxMessageBody.MpNews getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.MpNews body) {
            this.body = body;
            this.sendIgnoreReprint = body.sendIgnoreReprint ? 1 : 0;
        }
    }

    /**
     * 发送卡券
     */
    public static class WxCard extends WxGroupMessage<WxMessageBody.WxCard> {

        @JsonProperty("wxcard")
        protected WxMessageBody.WxCard body;

        @Override
        public WxMessageBody.WxCard getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.WxCard body) {
            this.body = body;
        }
    }

    /**
     * 群发消息结果
     */
    public static class Result {

        @JsonProperty("errcode")
        private Integer errorCode;

        @JsonProperty("errmsg")
        private String errorMessage;

        /**
         * 消息发送任务的ID
         */
        @JsonProperty("msg_id")
        private Long messageId;

        /**
         * 消息的数据ID，，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，
         * 获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍
         */
        @JsonProperty("msg_data_id")
        private Long messageDataId;

        @JsonProperty("msg_status")
        private Status messageStatus;

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Long getMessageId() {
            return messageId;
        }

        public void setMessageId(Long messageId) {
            this.messageId = messageId;
        }

        public Long getMessageDataId() {
            return messageDataId;
        }

        public void setMessageDataId(Long messageDataId) {
            this.messageDataId = messageDataId;
        }

        public Status getMessageStatus() {
            return messageStatus;
        }

        public void setMessageStatus(Status messageStatus) {
            this.messageStatus = messageStatus;
        }

        public enum Status {
            SEND_SUCCESS,
            SENDING,
            SEND_FAIL,
            DELETE
        }

    }

}
