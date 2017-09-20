/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.test;

import com.mxixm.fastboot.weixin.annotation.*;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.web.WxRequestBody;
import com.mxixm.fastboot.weixin.module.web.session.WxSession;
import org.springframework.boot.SpringApplication;

@WxApplication
@WxController
public class WxApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WxApp.class, args);
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
    @WxButton(group = WxButton.Group.RIGHT, main = true, name = "右")
    public String right() {
        return "点击右边菜单";
    }

    /**
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIRST,
            name = "文本消息")
    public String leftFirst(WxRequest wxRequest, WxUser wxUser) {
        return "测试文本消息";
    }

    /**
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.VIEW,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.SECOND,
            url = "http://baidu.com",
            name = "点击链接")
    @WxAsyncMessage
    public WxMessage link() {
        return WxMessage.Text.builder().content("点击了菜单链接").build();
    }

    /**
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.THIRD,
            name = "图文消息")
    public WxMessage news() {
        return WxMessage.News.builder()
                .addItem("测试图文消息", "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "http://mxixm.com")
                .addItem("测试图文消息", "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "http://smc24f.natappfree.cc/vendor/82")
                .addItem("测试图文消息", "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2a0e54054e2fb7c0&redirect_uri=http://smc24f.natappfree.cc/vendor/82&response_type=code&scope=snsapi_base&state#wechat_redirect")
                .build();
    }

    /**
     * 接受微信事件
     *
     * @param wxRequest
     * @param wxUser
     */
    @WxEventMapping(type = WxEvent.Type.UNSUBSCRIBE)
    public void unsubscribe(WxRequest wxRequest, WxUser wxUser) {
        System.out.println(wxUser.getNickName() + "退订了公众号");
    }

    /**
     * 接受用户文本消息，异步返回文本消息
     *
     * @param content
     * @return dummy
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT)
    @WxAsyncMessage
    public String text(WxRequest wxRequest, String content) {
        WxSession wxSession = wxRequest.getWxSession();
        if (wxSession != null && wxSession.getAttribute("last") != null) {
            return "上次收到消息内容为" + wxSession.getAttribute("last");
        }
        return "收到消息内容为" + content;
    }

    /**
     * 接受用户文本消息，同步返回图文消息
     *
     * @param content
     * @return dummy
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "1*")
    public String message(WxSession wxSession, String content) {
        wxSession.setAttribute("last", content);
        return "收到文本内容为" + content;
    }

    /**
     * 接受用户文本消息，异步返回文本消息
     *
     * @param content
     * @return dummy
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "2*")
    @WxAsyncMessage
    public String text2(WxRequestBody.Text text, String content) {
        boolean match = text.getContent().equals(content);
        return "收到消息内容为" + content + "!结果匹配！" + match;
    }
}