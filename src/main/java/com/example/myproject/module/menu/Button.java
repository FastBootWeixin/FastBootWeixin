package com.example.myproject.module.menu;

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

        CLICK("点击推事件"),

        VIEW("跳转URL"),

        SCANCODE_PUSH("扫码推事件"),

        SCANCODE_WAITMSG("扫码推事件且弹出“消息接收中”提示框"),

        PIC_SYSPHOTO("弹出系统拍照发图"),

        PIC_PHOTO_OR_ALBUM("弹出拍照或者相册发图"),

        PIC_WEIXIN("弹出微信相册发图器"),

        LOCATION_SELECT("弹出地理位置选择器"),

        MEDIA_ID("下发消息（除文本消息）"),

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
