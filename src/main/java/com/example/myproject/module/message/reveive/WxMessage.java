package com.example.myproject.module.message.reveive;

import com.example.myproject.annotation.WxButton;

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
public class WxMessage {

    /**
     * 消息的基础字段
     * 开发者微信号
     */
    protected String toUserName;

    /**
     * 消息的基础字段
     * 发送方帐号（一个OpenID）
     */
    protected String fromUserName;

    /**
     * 消息的基础字段
     * 消息创建时间 （整型）
     */
    protected Date createTime;

    /**
     * 消息的基础字段
     * 消息类型，event
     */
    protected Type msgType;


    public static class ButtonMessage extends WxMessage {

        protected WxButton.Type event;

    }

    /**
     * 个人定义的类目
     */
    enum Category {
        /**
         * 收到用户消息
         */
        MESSAGE,
        /**
         * 包括按钮事件和用户事件(如关注)
         * 后续可考虑分离按钮事件和用户时间
         */
        EVENT,
        /**
         * 系统事件
         */
        SYSTEM
    }

    enum Type {

        /**
         * 收到Button事件
         */
        EVENT(Category.EVENT),

        /**
         * 文本消息
         */
        TEXT(Category.MESSAGE),

        /**
         * 图片消息
         */
        IMAGE(Category.MESSAGE),

        /**
         * 语音消息
         */
        VOICE(Category.MESSAGE),

        /**
         * 视频消息
         */
        VIDEO(Category.MESSAGE),

        /**
         * 小视频消息
         */
        SHORT_VIDEO(Category.MESSAGE),

        /**
         * 地理位置消息
         */
        LOCATION(Category.MESSAGE),

        /**
         * 链接消息
         */
        LINK(Category.MESSAGE);

        private Category category;

        Type(Category category) {
            this.category = category;
        }

        public Category getCategory() {
            return category;
        }
    }

}
