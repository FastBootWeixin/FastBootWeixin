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

package com.mxixm.fastboot.weixin.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.annotation.*;

/**
 * FastBootWeixin WxButton
 * 待支持：参数从变量中取
 *
 * @author Guangshan
 * @date 2017/09/21 23:27
 * @since 0.1.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WxMapping
public @interface WxButton {

    /**
     * 按钮属于哪一组
     */
    Group group();

    /**
     * 显示名称
     *
     * @return dummy
     */
    String name();

    /**
     * 菜单类型
     */
    Type type() default Type.CLICK;

    /**
     * 是否是主菜单(下面的菜单)
     */
    boolean main() default false;

    /**
     * 顺序
     */
    Order order() default Order.FIRST;

    /**
     * 这里可以写一个自动生成key的策略
     */
    String key() default "";

    /**
     * 网页 链接，用户点击菜单可打开链接，不超过1024字节。
     * type为miniprogram时，不支持小程序的老版本客户端将打开本url。
     */
    String url() default "";

    /**
     * media_id类型和view_limited类型必须
     * 调用新增永久素材接口返回的合法media_id
     */
    String mediaId() default "";

    /**
     * miniprogram类型必须，小程序的appid（仅认证公众号可配置）
     */
    String appId() default "";

    /**
     * miniprogram类型必须，小程序的页面路径
     */
    String pagePath() default "";

    /**
     * 哪个按钮组
     */
    enum Group {
        LEFT, MIDDLE, RIGHT
    }

    /**
     * 顺序，最多五个
     */
    enum Order {
        FIRST, SECOND, THIRD, FORTH, FIFTH
    }

    /**
     * 类型
     */
    enum Type {

        /**
         * 点击推事件
         */
        @JsonProperty("click")
        CLICK,

        /**
         * 跳转URL
         */
        @JsonProperty("view")
        VIEW,

        /**
         * 扫码推事件
         */
        @JsonProperty("scancode_push")
        SCANCODE_PUSH,

        /**
         * 扫码推事件且弹出“消息接收中”提示框
         */
        @JsonProperty("scancode_waitmsg")
        SCANCODE_WAITMSG,

        /**
         * 弹出系统拍照发图
         */
        @JsonProperty("pic_sysphoto")
        PIC_SYSPHOTO,

        /**
         * 弹出拍照或者相册发图
         */
        @JsonProperty("pic_photo_or_album")
        PIC_PHOTO_OR_ALBUM,

        /**
         * 弹出微信相册发图器
         */
        @JsonProperty("pic_weixin")
        PIC_WEIXIN,

        /**
         * 弹出地理位置选择器
         */
        @JsonProperty("location_select")
        LOCATION_SELECT,

        /**
         * 下发消息（除文本消息）
         */
        @JsonProperty("media_id")
        MEDIA_ID,

        /**
         * 跳转图文消息URL
         */
        @JsonProperty("view_limited")
        VIEW_LIMITED,

        /**
         * 跳转小程序
         */
        @JsonProperty("miniprogram")
        MINI_PROGRAM

    }

}
