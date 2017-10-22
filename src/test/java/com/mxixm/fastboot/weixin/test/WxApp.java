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
import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.extend.WxCard;
import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.message.WxTemplateMessage;
import com.mxixm.fastboot.weixin.module.user.WxTagUser;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.web.WxRequestBody;
import com.mxixm.fastboot.weixin.module.web.session.WxSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxApp
 *
 * @author Guangshan
 * @date 2017/09/21 23:47
 * @since 0.1.2
 */
@WxApplication
@WxController
public class WxApp {

    @Autowired
    WxApiInvokeSpi wxApiInvokeSpi;

    @Autowired
    WxMediaManager wxMediaManager;

    @Autowired
    WxMessageTemplate wxMessageTemplate;

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
    public WxMessage leftFirst(WxRequest wxRequest, WxUser wxUser) {
        return WxMessage.Text.builder().content("测试文本消息").build();
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
    public WxMessage link(WxRequest wxRequest) {
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
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.THIRD,
            name = "图片消息")
    public WxMessage imgae() {
        return WxMessage.imageBuilder()
                .mediaUrl("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png")
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
     * 接受微信事件
     *
     * @param wxRequest
     * @param wxUser
     */
    @WxEventMapping(type = WxEvent.Type.SUBSCRIBE)
    public String subscribe(WxRequest wxRequest, WxUser wxUser) {
        return "欢迎您关注本公众号，本公众号使用FastBootWeixin框架开发，简单极速开发微信公众号，你值得拥有";
    }

    /**
     * 接受微信事件
     *
     */
    @WxEventMapping(type = WxEvent.Type.LOCATION)
    public WxMessage location(WxRequestBody.LocationReport location) {
        return WxMessage.News.builder()
                .addItem("接受到您的地理位置", "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "http://mxixm.com")
                .addItem("纬度" + location.getLatitude(), "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "http://smc24f.natappfree.cc/vendor/82")
                .addItem("经度" + location.getLongitude(), "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx2a0e54054e2fb7c0&redirect_uri=http://smc24f.natappfree.cc/vendor/82&response_type=code&scope=snsapi_base&state#wechat_redirect")
                .build();
    }


    /**
     * 接受微信事件
     *
     * @param wxUser
     */
    @WxEventMapping(type = WxEvent.Type.TEMPLATESENDJOBFINISH)
    public void template(WxRequestBody.Template template, WxUser wxUser) {
        // 模板消息发送完成的回调
        System.out.println(template.toString());
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

    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "群发*")
    @WxAsyncMessage
    public WxMessage groupMessage(String content) {
        String tagId = content.substring("群发".length());
        return WxMessage.Text.builder().content("pKS9_xJ6hvk4uLPOsHNPmnVRw0vE").toGroup(Integer.parseInt(tagId)).build();
    }


    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "模板*")
    public String templateMessage(WxRequestBody.Text text) {
        WxTemplateMessage templateMessage = WxMessage.templateBuilder()
                .data("keynote1", "1324.76", "#FF0000")
                .data("keynote2", "2017-10-25", "#0000FF")
                .templateId("IIXwm9TJ5F-tAXPdqP7D4xL6rRK-lVwpNWlVRIsZ9Wo")
                .toUser("oKS9_xLsaQsSmvhSZI5EHgUHorLs")
                .url("http://www.baidu.com")
                .build();
        wxMessageTemplate.sendTemplateMessage(templateMessage);
        return "模板消息已发送";
    }

    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "卡券*")
    public List<WxMessage> cardMessage(String content) {
        Integer tagId = Integer.parseInt(content.substring("卡券".length()));
        WxTagUser.UserList userList = wxApiInvokeSpi.listUserByTag(WxTagUser.listUser(tagId));
        return userList.getOpenIdList().stream().flatMap(u -> {
            List<WxMessage> l = new ArrayList();
            l.add(WxMessage.WxCard.builder().cardId("pKS9_xMBmNqlcWD-uAkD1pOy09Qw").toUser(u).build());
            l.add(WxMessage.WxCard.builder().cardId("pKS9_xPsM7ZCw7BW1U2lRRN-J2Qg").toUser(u).build());
            return l.stream();
        }).collect(Collectors.toList());
    }

    @RequestMapping("cards")
    public List<WxCard> cards() {
        return wxApiInvokeSpi.getCards(WxCard.ListSelector.of(WxCard.Status.CARD_STATUS_NOT_VERIFY))
                .getCardIdList().stream().map(id -> {
            return wxApiInvokeSpi.cardInfo(WxCard.CardSelector.info(id));
        }).collect(Collectors.toList());
    }

    @RequestMapping("card")
    public WxCard card() {
        return wxApiInvokeSpi.cardInfo(WxCard.CardSelector.info("pKS9_xMBmNqlcWD-uAkD1pOy09Qw"));
    }


    @RequestMapping("mediaUpload")
    public String mediaUpload() {
        return wxMediaManager.addMedia(WxMedia.Type.IMAGE, new FileSystemResource("E:/test.png"));
    }

}