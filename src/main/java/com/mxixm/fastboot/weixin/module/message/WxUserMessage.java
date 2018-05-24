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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.module.adapter.WxXmlAdapters;
import org.springframework.beans.BeanUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * fastboot-weixin  WxUserMessage
 *
 * @author Guangshan
 * @date 2017/9/24 14:48
 * @since 0.1.3
 */
public class WxUserMessage<T extends WxMessageBody> extends WxMessage<T> {

    private static Map<Type, Class<? extends WxUserMessage>> classMap = new HashMap<>();

    static {
        classMap.put(Type.TEXT, WxUserMessage.Text.class);
        classMap.put(Type.IMAGE, WxUserMessage.Image.class);
        classMap.put(Type.VOICE, WxUserMessage.Voice.class);
        classMap.put(Type.VIDEO, WxUserMessage.Video.class);
        classMap.put(Type.MUSIC, WxUserMessage.Music.class);
        classMap.put(Type.NEWS, WxUserMessage.News.class);
        classMap.put(Type.MPNEWS, WxUserMessage.MpNews.class);
        classMap.put(Type.WXCARD, WxUserMessage.WxCard.class);
        classMap.put(Type.STATUS, WxUserMessage.Status.class);
        classMap.put(Type.MINI_PROGRAM, WxUserMessage.MiniProgram.class);
    }

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

    /**
     * 消息的基础字段
     * 消息创建时间 （整型）
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
    @XmlElement(name = "CreateTime", required = true)
    @JsonIgnore
    protected Date createTime;

    protected String getToUser() {
        return this.toUser;
    }

    protected String getFromUser() {
        return this.fromUser;
    }

    protected Date getCreateTime() {
        return this.createTime;
    }

    protected void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    protected void setToUser(String toUser) {
        this.toUser = toUser;
    }

    protected void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    WxUserMessage() {
        super();
    }

    WxUserMessage(Type messageType) {
        super(messageType);
    }

    WxUserMessage(Type messageType, T wxMessageBody) {
        super(messageType, wxMessageBody);
    }

    WxUserMessage(Type messageType, T wxMessageBody, String toUser, String fromUser, Date createTime) {
        super(messageType, wxMessageBody);
        this.setToUser(toUser);
        this.setFromUser(fromUser);
        this.setCreateTime(createTime);
    }

    /**
     * 是否有必要加个back返回原构造器？
     * 是否可以泛型化，使得构造出来的对象是WxUserMessage的子类
     */
    public static class UserMessageBuilder {

        protected Builder builder;
        protected String toUser;
        protected String fromUser;
        protected Date createTime;

        UserMessageBuilder(Builder builder) {
            this.builder = builder;
        }

        public UserMessageBuilder toUser(String toUser) {
            this.toUser = toUser;
            return this;
        }

        public UserMessageBuilder fromUser(String fromUser) {
            this.fromUser = fromUser;
            return this;
        }

        public UserMessageBuilder createTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public WxUserMessage build() {
            WxUserMessage wxUserMessage;
            if (classMap.containsKey(builder.messageType)) {
                wxUserMessage = BeanUtils.instantiateClass(classMap.get(builder.messageType));
            } else {
                // 没有这种类型时，是否有必要抛出异常？
                wxUserMessage = new WxUserMessage();
            }
            wxUserMessage.setMessageType(builder.messageType);
            wxUserMessage.setBody(builder.body);
            wxUserMessage.setToUser(toUser);
            wxUserMessage.setFromUser(fromUser);
            wxUserMessage.setCreateTime(createTime);
            return wxUserMessage;
        }

    }

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Text extends WxUserMessage<WxMessageBody.Text> {

        @XmlElement(name = "Content")
        @XmlJavaTypeAdapter(WxXmlAdapters.TextBodyAdaptor.class)
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

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Image extends WxUserMessage<WxMessageBody.Image> {

        @XmlElement(name = "Image", required = true)
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

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Voice extends WxUserMessage<WxMessageBody.Voice> {

        @XmlElement(name = "Voice", required = true)
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

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Video extends WxUserMessage<WxMessageBody.Video> {

        @XmlElement(name = "Video")
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

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class MiniProgram extends WxUserMessage<WxMessageBody.MiniProgram> {

        @XmlElement(name = "Miniprogrampage")
        @JsonProperty("miniprogrampage")
        protected WxMessageBody.MiniProgram body;

        @Override
        public WxMessageBody.MiniProgram getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.MiniProgram body) {
            this.body = body;
        }
    }

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Music extends WxUserMessage<WxMessageBody.Music> {

        @XmlElement(name = "Music")
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
    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class News extends WxUserMessage<WxMessageBody.News> {

        /**
         * 图文消息个数，限制为8条以内
         */
        @XmlElement(name = "ArticleCount", required = true)
        protected Integer articleCount;

        @XmlElement(name = "Articles", required = true)
        @JsonProperty("news")
        protected WxMessageBody.News body;

        @Override
        public WxMessageBody.News getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.News body) {
            this.body = body;
            this.articleCount = body.articles.size();
        }
    }

    /**
     * 发送图文消息（点击跳转到图文消息页面）
     */
    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class MpNews extends WxUserMessage<WxMessageBody.MpNews> {

        @XmlElement(name = "Mpnews", required = true)
        @JsonProperty("mpnews")
        protected WxMessageBody.MpNews body;

        @Override
        public WxMessageBody.MpNews getBody() {
            return body;
        }

        @Override
        public void setBody(WxMessageBody.MpNews body) {
            this.body = body;
        }
    }

    /**
     * 发送卡券
     */
    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class WxCard extends WxUserMessage<WxMessageBody.WxCard> {

        @XmlElement(name = "WxCard", required = true)
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

    public static class Status extends WxUserMessage<WxMessageBody.Status> {
        @JsonIgnore
        protected Type messageType;

        @JsonIgnore
        protected WxMessageBody.Status body;

        @JsonProperty("command")
        protected WxMessageBody.Status.Command command;

        @Override
        public void setBody(WxMessageBody.Status body) {
            this.command = body.isTyping ? WxMessageBody.Status.Command.TYPING : WxMessageBody.Status.Command.CANCEL_TYPING;
        }

    }

}
