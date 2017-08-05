package com.example.myproject.module.message;

import com.example.myproject.module.Wx;
import com.example.myproject.module.message.adapters.WxXmlAdapters;
import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * 所有消息都是通过Msg推送的
 * FastBootWeixin  WxMessage
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMessage
 * 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/2 23:21
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.NONE)
@Getter
public class WxMessage {

    /**
     * 消息的基础字段
     * 开发者微信号
     */
    @XmlElement(name = "ToUserName")
    protected String toUserName;

    /**
     * 消息的基础字段
     * 发送方帐号（一个OpenID）
     */
    @XmlElement(name = "FromUserName")
    protected String fromUserName;

    /**
     * 消息的基础字段
     * 消息创建时间 （整型）
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
    @XmlElement(name = "CreateTime")
    protected Date createTime;

    /**
     * 消息的基础字段
     * 消息类型，event
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.MsgTypeAdaptor.class)
    @XmlElement(name = "MsgType")
    protected Type messageType;

    WxMessage() {
    }

    WxMessage(String toUserName, String fromUserName, Date createTime, Type messageType) {
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.createTime = createTime;
        this.messageType = messageType;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder<T extends Builder<T>> {
        protected String toUserName;
        protected String fromUserName;
        protected Date createTime;
        protected Type messageType;

        Builder() {
        }

        public Builder toUserName(String toUserName) {
            this.toUserName = toUserName;
            return this;
        }

        public Builder fromUserName(String fromUserName) {
            this.fromUserName = fromUserName;
            return this;
        }

        public Builder createTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder msgType(Type msgType) {
            this.messageType = msgType;
            return this;
        }

        public WxMessage build() {
            return new WxMessage(toUserName, fromUserName, createTime, messageType);
        }

        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Builder(toUserName=" + this.toUserName + ", fromUserName=" + this.fromUserName + ", createTime=" + this.createTime + ", messageType=" + this.messageType + ")";
        }
    }

    @XmlRootElement(name = "xml")
    @Getter
    public static class Text extends WxMessage {

        @XmlElement(name = "Content")
        protected String content;

        Text() {
        }

        Text(String toUserName, String fromUserName, Date createTime, Type messageType, String content) {
            super(toUserName, fromUserName, createTime, messageType);
            this.content = content;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends WxMessage.Builder {
            private String content;

            Builder() {
            }

            // 父类如何返回？
            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public Text build() {
                return new Text(toUserName, fromUserName, createTime, messageType, content);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Text.Builder(content=" + this.content + ")";
            }
        }
    }

    public enum Type {

        /**
         * 收到Button事件和普通事件
         */
        EVENT(Wx.Category.EVENT, Wx.Category.BUTTON),

        /**
         * 文本消息
         */
        TEXT(Wx.Category.MESSAGE),

        /**
         * 图片消息
         */
        IMAGE(Wx.Category.MESSAGE),

        /**
         * 语音消息
         */
        VOICE(Wx.Category.MESSAGE),

        /**
         * 视频消息
         */
        VIDEO(Wx.Category.MESSAGE),

        /**
         * 小视频消息
         */
        SHORT_VIDEO(Wx.Category.MESSAGE),

        /**
         * 地理位置消息
         */
        LOCATION(Wx.Category.MESSAGE),

        /**
         * 链接消息
         */
        LINK(Wx.Category.MESSAGE);

        private Wx.Category[] categories;

        Type(Wx.Category... categories) {
            this.categories = categories;
        }

        public Wx.Category[] getCategories() {
            return categories;
        }
    }

}
