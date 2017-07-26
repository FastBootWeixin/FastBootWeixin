package com.example.myproject.module.menu;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Button {

    // 哪个按钮组
    enum Group {
        LEFT, MIDDLE, RIGHT
    }

    // 顺序，最多五个
    enum Order {
        FIRST, SECOND, THIRD, FORTH, FIFTH
    }

    // 类型
    enum Type {

        @JsonProperty("click")
        CLICK("点击推事件"),

        @JsonProperty("view")
        VIEW("跳转URL"),

        @JsonProperty("scancode_push")
        SCANCODE_PUSH("扫码推事件"),

        @JsonProperty("scancode_waitmsg")
        SCANCODE_WAITMSG("扫码推事件且弹出“消息接收中”提示框"),

        @JsonProperty("pic_sysphoto")
        PIC_SYSPHOTO("弹出系统拍照发图"),

        @JsonProperty("pic_photo_or_album")
        PIC_PHOTO_OR_ALBUM("弹出拍照或者相册发图"),

        @JsonProperty("pic_weixin")
        PIC_WEIXIN("弹出微信相册发图器"),

        @JsonProperty("location_select")
        LOCATION_SELECT("弹出地理位置选择器"),

        @JsonProperty("media_id")
        MEDIA_ID("下发消息（除文本消息）"),

        @JsonProperty("view_limited")
        VIEW_LIMITED("跳转图文消息URL");

        public String descript;

        private Type(String descript) {
            this.descript = descript;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
