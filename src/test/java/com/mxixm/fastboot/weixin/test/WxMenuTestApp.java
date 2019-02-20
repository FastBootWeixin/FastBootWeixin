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

package com.mxixm.fastboot.weixin.test;

import com.mxixm.fastboot.weixin.annotation.*;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import org.springframework.boot.SpringApplication;
import org.springframework.util.AntPathMatcher;

/**
 * FastBootWeixin WxApp
 *
 * @author Guangshan
 * @date 2017/09/21 23:47
 * @since 0.1.2
 */
//@WxApplication
//@WxController
public class WxMenuTestApp {

    public static void main(String[] args) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.match("{a:[a-z]}{b:[1-9]}", "a3");
        SpringApplication.run(WxMenuTestApp.class, args);
    }

    /**
     * 定义微信菜单
     */
    @WxButton(group = WxButton.Group.LEFT, main = true, name = "左")
    public void left() {
    }

    /**
     * 定义微信菜单
     */
    @WxButton(group = WxButton.Group.MIDDLE, main = true, name = "中")
    public void middle(WxUser wxUser) {
    }

    /**
     * 定义微信菜单
     */
    @WxButton(group = WxButton.Group.RIGHT, main = true, name = "右")
    public String right(WxUser wxUser) {
        return "欢迎" + wxUser.getNickName();
    }

    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIRST,
            name = "点击")
    public String left1(String content) {
        return content;
    }

    /**
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.VIEW,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.SECOND,
            url = "http://vxyufx.natappfree.cc/wx/test",
            name = "跳转")
    public String left2() {
        return "左2";
    }

    @WxButton(type = WxButton.Type.SCANCODE_PUSH,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.THIRD,
            name = "扫码")
    public String left3() {
        return "左3";
    }

    @WxButton(type = WxButton.Type.SCANCODE_WAITMSG,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FORTH,
            name = "选图")
    public String left4() {
        return "左4";
    }

    @WxButton(type = WxButton.Type.PIC_SYSPHOTO,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIFTH,
            name = "位置")
    public String left5() {
        return "左5";
    }

    @WxButton(type = WxButton.Type.PIC_PHOTO_OR_ALBUM,
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.FIRST,
            name = "中1")
    public String middle1() {
        return "中1";
    }

    @WxButton(type = WxButton.Type.PIC_WEIXIN,
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.SECOND,
            name = "中2")
    public String middle2() {
        return "中2";
    }

    @WxButton(type = WxButton.Type.LOCATION_SELECT,
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.THIRD,
            name = "中3")
    public String middle3() {
        return "中3";
    }

    @WxButton(type = WxButton.Type.MEDIA_ID,
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.FORTH,
            mediaId = "3wRH_WkQCjnl8hQcYCxyUukk9I-I1AOxIipo5aYve7A",
            name = "中4")
    public String middle4() {
        return "中4";
    }

//    @WxButton(type = WxButton.Type.VIEW_LIMITED,
//            group = WxButton.Group.MIDDLE,
//            order = WxButton.Order.FIFTH,
//            mediaId = "3wRH_WkQCjnl8hQcYCxyUukk9I-I1AOxIipo5aYve7A",
//            name = "中5")
//    public String middle5() {
//        return "中5";
//    }

}