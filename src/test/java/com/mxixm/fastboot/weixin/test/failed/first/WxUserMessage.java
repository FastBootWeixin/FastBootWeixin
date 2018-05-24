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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.module.adapter.WxXmlAdapters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * fastboot-weixin  WxUserMessage
 *
 * @author Guangshan
 * @date 2017/9/24 14:48
 * @since 0.1.3
 */
public class WxUserMessage extends WxMessage {

    /**
     * 消息的基础字段
     * 开发者微信号
     */
    @XmlElement(name = "ToUserName", required = true)
    @JsonProperty("touser")
    protected String toUserName;

    /**
     * 消息的基础字段
     * 发送方帐号（一个OpenID）
     */
    @XmlElement(name = "FromUserName", required = true)
    @JsonIgnore
    protected String fromUserName;

    /**
     * 消息的基础字段
     * 消息创建时间 （整型）
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
    @XmlElement(name = "CreateTime", required = true)
    @JsonIgnore
    protected Date createTime;

    public String getToUserName() {
        return this.toUserName;
    }

    public String getFromUserName() {
        return this.fromUserName;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    WxUserMessage(String toUserName, String fromUserName, Date createTime, Type messageType) {
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.createTime = createTime != null ? createTime : new Date();
        this.messageType = messageType;
    }

}
