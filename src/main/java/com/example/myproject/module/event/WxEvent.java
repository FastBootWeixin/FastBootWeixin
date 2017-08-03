package com.example.myproject.module.event;

/**
 * Created by Administrator on 2017/8/3.
 */
public class WxEvent {

    public enum Type {

        /**
         * 按钮事件中的点击推事件
         * see {@link com.example.myproject.annotation.WxButton.Type}
         */
        CLICK,

        /**
         * 按钮事件中的跳转URL事件
         * see {@link com.example.myproject.annotation.WxButton.Type}
         */
        VIEW,

        /**
         * 按钮事件中的扫码推事件
         * see {@link com.example.myproject.annotation.WxButton.Type}
         */
        SCANCODE_PUSH,

        /**
         * 按钮事件中的扫码推事件且弹出“消息接收中”提示框
         * see {@link com.example.myproject.annotation.WxButton.Type}
         */
        SCANCODE_WAITMSG,

        /**
         * 按钮事件中的弹出拍照或者相册发图
         * see {@link com.example.myproject.annotation.WxButton.Type}
         */
        PIC_PHOTO_OR_ALBUM,

        /**
         * 按钮事件中的微信相册发图器
         * see {@link com.example.myproject.annotation.WxButton.Type}
         */
        PIC_WEIXIN,

        /**
         * 按钮事件中的地理位置选择器
         * see {@link com.example.myproject.annotation.WxButton.Type}
         */
        LOCATION_SELECT,

        /**
         * 订阅(关注)
         */
        SUBSCRIBE,

        /**
         * 取消订阅(关注)
         */
        UNSUBSCRIBE,

        /**
         * 上报地理位置事件
         */
        LOCATION,

        /**
         * 扫描带参数二维码事件
         */
        SCAN,

        /**
         * 非系统的事件
         * 当用户未关注时，扫描二维码会变为subscribe事件，同时相关信息也会放在Subscribe的事件体中
         */
        SCAN_AND_SUBSCRIBE

    }

}
