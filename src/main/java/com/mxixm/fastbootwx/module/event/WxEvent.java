package com.mxixm.fastbootwx.module.event;

import com.mxixm.fastbootwx.module.Wx;
import com.mxixm.fastbootwx.annotation.WxButton;

/**
 * Created by Administrator on 2017/8/3.
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
         * 理论上应该拆分个系统Category里面
         * 系统事件：资质认证成功（此时立即获得接口权限）
         */
        QUALIFICATION_VERIFY_SUCCESS(Wx.Category.SYSTEM),

        /**
         * 系统事件：资质认证失败
         */
        QUALIFICATION_VERIFY_FAIL(Wx.Category.SYSTEM),

        /**
         * 系统事件：名称认证成功（即命名成功）
         */
        NAMING_VERIFY_SUCCESS(Wx.Category.SYSTEM),

        /**
         * 名称认证失败（这时虽然客户端不打勾，但仍有接口权限）
         */
        NAMING_VERIFY_FAIL(Wx.Category.SYSTEM),

        /**
         * 年审通知
         */
        ANNUAL_RENEW(Wx.Category.SYSTEM),

        /**
         * 认证过期失效通知
         */
        VERIFY_EXPIRED(Wx.Category.SYSTEM);

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
