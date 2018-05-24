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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * fastboot-weixin  WxGroupMessage
 *
 * @author LGP
 * @date 2017/9/24 14:25
 * @since 0.2.1
 */
public class WxTemplateMessage extends WxMessage<WxMessageBody.Template> {

    /**
     * 消息的基础字段
     * 发送方帐号（一个OpenID）
     */
    @JsonProperty("touser")
    protected String toUser;

    /**
     * 模板ID
     */
    @JsonProperty("template_id")
    protected String templateId;

    /**
     * 模板跳转链接
     */
    @JsonProperty("url")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String url;

    /**
     * 跳小程序所需数据，不需跳小程序可不用传该数据
     */
    @JsonProperty("miniProgram")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected MiniProgram miniProgram;

    /**
     * 模板数据
     */
    @JsonProperty("data")
    protected WxMessageBody.Template body;

    @Override
    public WxMessageBody.Template getBody() {
        return this.body;
    }

    @Override
    public void setBody(WxMessageBody.Template body) {
        this.body = body;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    /**
     * 消息模板链接的小程序
     * 在消息体中也有MiniProgram类型，虽然两个可以复用，但字段有点小区别，暂时不考虑复用
     */
    protected static class MiniProgram {
        /**
         * 所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系）
         */
        @JsonProperty("appid")
        protected String appId;
        /**
         * 所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar）
         */
        @JsonProperty("pagepath")
        protected String pagePath;

        public MiniProgram(String appId, String pagePath) {
            this.appId = appId;
            this.pagePath = pagePath;
        }

    }

    public static class TemplateMessageBuilder {

        protected WxMessageBody.Template body = new WxMessageBody.Template();
        protected String toUser;
        protected String templateId;
        protected String url;
        protected MiniProgram miniProgram;

        public TemplateMessageBuilder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public TemplateMessageBuilder toUser(String toUser) {
            this.toUser = toUser;
            return this;
        }

        public TemplateMessageBuilder url(String url) {
            this.url = url;
            return this;
        }

        public TemplateMessageBuilder miniProgram(String appId, String pagePath) {
            this.miniProgram = new MiniProgram(appId, pagePath);
            return this;
        }

        public TemplateMessageBuilder data(String name, String value, String color) {
            WxMessageBody.Template.TemplateData data = new WxMessageBody.Template.TemplateData(value, color);
            this.body.put(name, data);
            return this;
        }

        public TemplateMessageBuilder data(String name, String value) {
            WxMessageBody.Template.TemplateData data = new WxMessageBody.Template.TemplateData(value);
            this.body.put(name, data);
            return this;
        }

        public WxTemplateMessage build() {
            WxTemplateMessage templateMessage = new WxTemplateMessage();
            templateMessage.setBody(body);
            templateMessage.toUser = toUser;
            templateMessage.templateId = templateId;
            templateMessage.url = url;
            templateMessage.miniProgram = miniProgram;
            return templateMessage;
        }

    }

    /**
     * 模板消息结果
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
    }

}
