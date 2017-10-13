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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

/**
 * fastboot-weixin  WxGroupMessage
 *
 * @author Guangshan
 * @date 2017/9/24 14:25
 * @since 0.1.3
 */
public class WxTemplateMessage extends WxMessage<WxMessageBody.Template> {

    /**
     * 消息的基础字段
     * 开发者微信号
     */
    @XmlElement(name = "ToUserName", required = true)
    @JsonProperty("touser")
    protected String toUser;

    /**
     * 消息的基础字段
     * 发送方帐号（一个OpenID）
     */
    @XmlElement(name = "FromUserName", required = true)
    @JsonIgnore
    protected String fromUser;


    @XmlElement(name = "TemplateId", required = true)
    @JsonProperty("template_id")
    protected String templateId;


    @JsonProperty("url")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String url;


    @JsonProperty("miniprogram")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Miniprogram miniprogram;

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

    /**
     * 消息模板链接的小程序
     */
    protected static class Miniprogram{
        @JsonProperty("appid" )
        protected String appId;
        @JsonProperty("pagepath")
        protected String pagePath;

        public Miniprogram(String appId, String pagePath) {
            this.appId = appId;
            this.pagePath = pagePath;
        }

        public Miniprogram() {
        }
    }

    public static class TemplateMessageBuilder {

        protected TemplateBuilder builder;//
        protected String toUser;
        protected String templateId;
        protected String url;
        protected Miniprogram miniprogram ;

        TemplateMessageBuilder(TemplateBuilder builder) {
            this.builder =  builder;
        }

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

        public TemplateMessageBuilder miniprogram(String appId,String pagePath) {
            this.miniprogram = new Miniprogram(appId,pagePath);
            return this;
        }



        public WxTemplateMessage build() {
            WxTemplateMessage templateMessage = new WxTemplateMessage();
            templateMessage.setBody( builder.body);
            templateMessage.toUser = toUser;
            templateMessage.templateId = templateId;
            templateMessage.url = url;
            templateMessage.miniprogram = miniprogram;
            return templateMessage;
        }

    }



}
