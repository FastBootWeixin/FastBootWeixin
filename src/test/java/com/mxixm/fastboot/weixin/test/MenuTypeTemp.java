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

public enum MenuTypeTemp {

    点击推事件("click"),
    跳转URL("view"),
    扫码推事件("scancode_push"),
    扫码推事件且弹出消息接收中("scancode_waitmsg"),
    弹出系统拍照发图("pic_sysphoto"),
    弹出拍照或者相册发图("pic_photo_or_album"),
    弹出微信相册发图器("pic_weixin"),
    弹出地理位置选择器("location_select"),
    下发消息除文本消息("media_id"),
    跳转图文消息URL("view_limited");

    public String type;

    private MenuTypeTemp(String type) {
        this.type = type;
    }

}
