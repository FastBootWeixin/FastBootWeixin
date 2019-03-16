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

package com.mxixm.fastboot.weixin.module.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.Wx;

/**
 * FastBootWeixin WxEvent
 *
 * @author Guangshan
 * @date 2017/7/23 23:38
 * @since 0.1.2
 */
public class WxEvent {

    public enum Type {

        /**
         * 按钮事件中的点击推事件
         * see {@link WxButton.Type}
         */
        CLICK(Wx.Category.BUTTON),

        /**
         * 按钮事件中的跳转URL事件
         * see {@link WxButton.Type}
         */
        VIEW(Wx.Category.BUTTON),

        /**
         * 按钮事件中的扫码推事件
         * see {@link WxButton.Type}
         */
        SCANCODE_PUSH(Wx.Category.BUTTON),

        /**
         * 按钮事件中的扫码推事件且弹出“消息接收中”提示框
         * see {@link WxButton.Type}
         */
        SCANCODE_WAITMSG(Wx.Category.BUTTON),

        /**
         * 按钮事件中的弹出拍照或者相册发图
         * see {@link WxButton.Type}
         */
        PIC_PHOTO_OR_ALBUM(Wx.Category.BUTTON),

        /**
         * 按钮事件中的弹出拍照或者相册发图
         * see {@link WxButton.Type}
         */
        PIC_SYSPHOTO(Wx.Category.BUTTON),

        /**
         * 按钮事件中的微信相册发图器
         * see {@link WxButton.Type}
         */
        PIC_WEIXIN(Wx.Category.BUTTON),

        /**
         * 按钮事件中的地理位置选择器
         * see {@link WxButton.Type}
         */
        LOCATION_SELECT(Wx.Category.BUTTON),

        /**
         * 下发消息（除文本消息），不推送事件，也可能是官方文档没写
         */
        MEDIA_ID(Wx.Category.BUTTON),

        /**
         * 跳转图文消息URL，不推送事件，也可能是官方文档没写
         */
        VIEW_LIMITED(Wx.Category.BUTTON),

        /**
         * 点击查看小程序
         * see {@link WxButton.Type}
         */
        VIEW_MINIPROGRAM(Wx.Category.BUTTON),

        /**
         * 订阅(关注)
         */
        SUBSCRIBE(Wx.Category.EVENT),

        /**
         * 取消订阅(关注)
         */
        UNSUBSCRIBE(Wx.Category.EVENT),

        /**
         * 上报地理位置事件
         */
        LOCATION(Wx.Category.EVENT),

        /**
         * 扫描带参数二维码事件
         */
        SCAN(Wx.Category.EVENT),

        /**
         * 非系统的事件
         * 当用户未关注时，扫描二维码会变为subscribe事件，同时相关信息也会放在Subscribe的事件体中
         */
        SCAN_AND_SUBSCRIBE(Wx.Category.EVENT),

        /**
         * 在模版消息发送任务完成后，微信服务器会将是否送达成功作为通知，发送到开发者中心中填写的服务器配置地址中。
         */
        TEMPLATESENDJOBFINISH(Wx.Category.EVENT),

        /**
         * 在群发消息发送任务完成后，微信服务器会将是否送达成功作为通知，发送到开发者中心中填写的服务器配置地址中。
         * 注意群发消息成功的回调中，还有很多其他信息，暂时未提供支持，后续加入
         */
        MASSSENDJOBFINISH(Wx.Category.EVENT),

        /**
         * 理论上应该拆分个系统Category里面
         * 系统事件：资质认证成功（此时立即获得接口权限）
         */
        QUALIFICATION_VERIFY_SUCCESS(Wx.Category.EVENT),

        /**
         * 系统事件：资质认证失败
         */
        QUALIFICATION_VERIFY_FAIL(Wx.Category.EVENT),

        /**
         * 系统事件：名称认证成功（即命名成功）
         */
        NAMING_VERIFY_SUCCESS(Wx.Category.EVENT),

        /**
         * 名称认证失败（这时虽然客户端不打勾，但仍有接口权限）
         */
        NAMING_VERIFY_FAIL(Wx.Category.EVENT),

        /**
         * 年审通知
         */
        ANNUAL_RENEW(Wx.Category.EVENT),

        /**
         * 认证过期失效通知
         */
        VERIFY_EXPIRED(Wx.Category.EVENT),

        /**
         * 不认识的类型，可能是微信接口里有，但是没有被发现的
         */
        UNKNOWN(Wx.Category.EVENT);

        /**
         * 消息类别
         */
        private Wx.Category category;

        Type(Wx.Category category) {
            this.category = category;
        }

        public Wx.Category getCategory() {
            return category;
        }
    }

}
