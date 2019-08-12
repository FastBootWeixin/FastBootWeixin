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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxixm.fastboot.weixin.annotation.*;
import com.mxixm.fastboot.weixin.module.credential.WxJsTicketManager;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.extend.WxCard;
import com.mxixm.fastboot.weixin.module.extend.WxQrCode;
import com.mxixm.fastboot.weixin.module.extend.WxShortUrl;
import com.mxixm.fastboot.weixin.module.js.WxJsApi;
import com.mxixm.fastboot.weixin.module.js.WxJsConfig;
import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.message.*;
import com.mxixm.fastboot.weixin.module.user.WxTagUser;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.web.WxRequestBody;
import com.mxixm.fastboot.weixin.module.web.session.WxSession;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.service.WxBaseService;
import com.mxixm.fastboot.weixin.service.WxExtendService;
import com.mxixm.fastboot.weixin.util.WxMessageUtils;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import com.mxixm.fastboot.weixin.web.WxUserManager;
import com.mxixm.fastboot.weixin.web.WxWebUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    WxApiService wxApiService;

    @Autowired
    WxMediaManager wxMediaManager;

    @Autowired
    WxMessageTemplate wxMessageTemplate;

    @Autowired
    WxExtendService wxExtendService;

    @Autowired
    WxJsTicketManager wxJsTicketManager;

    @Autowired
    WxUserManager wxUserManager;

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
    @WxButton(group = WxButton.Group.MIDDLE, main = true, name = "中")
    @WxAsyncMessage
    public WxMessage middle(WxUser wxUser) {
        WxQrCode wxQrCode = WxQrCode.builder().permanent(wxUser.getOpenId()).build();
        WxQrCode.Result qrCode = wxExtendService.createQrCode(wxQrCode);
//        List<WxMessage> messages = new ArrayList<>();
//        messages.add(WxMessage.textBuilder().content("消息规则").build());
        return WxMessage.imageBuilder()
                .mediaUrl(qrCode.getShowUrl())
//                .mediaPath("E:/showqrcode2.jpg")
                .build();
    }

    /**
     * 定义微信菜单
     */
    @WxButton(group = WxButton.Group.RIGHT, main = true, name = "右")
    @WxAsyncMessage
    public String right(WxUser wxUser) {
        return wxUser.getNickName() + "haha";
    }

    @WxButton(group = WxButton.Group.RIGHT, name = "右1")
    public WxMessage right1(WxUser wxUser) {
        return WxMessage.miniProgramBuilder().appId("wx286b93c14bbf93aa").pagePath("pages/lunar/index").build();
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
            url = "http://vxyufx.natappfree.cc/wx/test",
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
                .addItem("测试图文消息", "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "https://github.com/LauItachi/WeChatTest")
                .build();
    }

    /**
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FORTH,
            name = "图片消息")
    public WxMessage image() {
        //String mediaId = wxMediaManager.addTempMedia(WxMedia.Type.VIDEO, new FileSystemResource("路径"));
        return WxMessage.imageBuilder()
                .mediaUrl("http://img.zcool.cn/community/01f09e577b85450000012e7e182cf0.jpg@1280w_1l_2o_100sh.jpg")
                .build();
    }

    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIFTH,
            name = "资料")
    @WxAsyncMessage
    public WxUserMessage showQrCode(WxUser wxUser) {
        WxQrCode wxQrCode = WxQrCode.builder().permanent(wxUser.getOpenId()).build();
        WxQrCode.Result qrCode = wxExtendService.createQrCode(wxQrCode);
        String showUrl = qrCode.getShowUrl();
        WxUserMessage message = WxMessage.News.builder()
                .addItem(WxMessageBody.News.Item.builder().title("二维码").description("您的专属二维码")
                        .picUrl(showUrl)
                        .url(showUrl).build()).build();
        return message;
    }

    /**
     * 接受微信事件
     *
     * @param wxRequest
     * @param wxUser
     */
    @WxEventMapping(type = WxEvent.Type.UNSUBSCRIBE)
    public void unsubscribe(WxRequest wxRequest, WxUser wxUser) {
        System.out.println("取消关注" + wxUser.getOpenId());
//        System.out.println(wxUser.getNickName() + "退订了公众号");
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

    @WxEventMapping(type = WxEvent.Type.SCAN)
    public String scan(WxRequest wxRequest, WxUser wxUser) {
        System.out.println("扫描二维码" + wxUser.getOpenId());
        return "触发扫描二维码";
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
     * @return the result
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT)
    @WxAsyncMessage
    public String text(WxRequest wxRequest, String content) {
        WxSession wxSession = wxRequest.getWxSession();
        wxMessageTemplate.sendUserMessage(wxRequest.getBody().getFromUserName(), content);
        if (wxSession != null && wxSession.getAttribute("last") != null) {
            return "上次收到消息内容为" + wxSession.getAttribute("last");
        }
        return "收到消息内容为" + content;
    }

    private String openId;

    private String k = "\uD83D\uDE2C";

    @RequestMapping("doText")
    public WxMessage t(String text) {
        WxMessage wxMessage = WxMessage.text().content(text).toUser(openId).build();
        wxMessageTemplate.sendMessage(wxMessage);
        try {
            System.out.println(new ObjectMapper().writeValueAsString(wxMessage));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return wxMessage;
    }

    /**
     * 接受用户文本消息，异步返回文本消息
     *
     * @param content
     * @return the result
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT, contents = "test")
    public String testS(String fromUserName, String content) {
        openId = fromUserName;
        return "收到消息内容为" + k + content;
    }

    /**
     * 接受用户文本消息，同步返回图文消息
     *
     * @param content
     * @return the result
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
     * @return the result
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "2*")
    @WxAsyncMessage
    public String text2(WxRequestBody.Text text, String content) {
        boolean match = text.getContent().equals(content);
        return "收到消息内容为" + content + "!结果匹配！" + match;
    }

    /**
     * 接受用户文本消息，异步返回文本消息
     *
     * @param content
     * @return the result
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "3*")
    @WxAsyncMessage
    public String text3(WxRequestBody.Text text, String content) {
        return WxMessageUtils.linkBuilder().href("http://baidu.com").text("123123").build();
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
                .toUser(text.getFromUserName())
//                .url("http://www.baidu.com")
                .build();
        wxMessageTemplate.sendTemplateMessage(templateMessage);
        return "模板消息已发送";
    }

    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "卡券*")
    public List<WxMessage> cardMessage(String content) {
        Integer tagId = Integer.parseInt(content.substring("卡券".length()));
        WxUser.PageResult pageResult = wxApiService.listUserByTag(WxTagUser.listUser(tagId));
        return pageResult.getOpenIdList().stream().flatMap(u -> {
            List<WxMessage> l = new ArrayList();
            l.add(WxMessage.WxCard.builder().cardId("pKS9_xMBmNqlcWD-uAkD1pOy09Qw").toUser(u).build());
            l.add(WxMessage.WxCard.builder().cardId("pKS9_xPsM7ZCw7BW1U2lRRN-J2Qg").toUser(u).build());
            return l.stream();
        }).collect(Collectors.toList());
    }

    @RequestMapping("cards")
    public List<WxCard> cards() {
        return wxApiService.getCards(WxCard.PageParam.of(WxCard.Status.CARD_STATUS_NOT_VERIFY))
                .getCardIdList().stream().map(id -> {
            return wxApiService.cardInfo(WxCard.CardSelector.info(id));
        }).collect(Collectors.toList());
    }

    @RequestMapping("card")
    public WxCard card() {
        return wxApiService.cardInfo(WxCard.CardSelector.info("pKS9_xMBmNqlcWD-uAkD1pOy09Qw"));
    }


    @RequestMapping("mediaUpload")
    public String mediaUpload() {
        return wxMediaManager.addTempMedia(WxMedia.Type.IMAGE, new FileSystemResource("E:/test.png"));
    }

    @RequestMapping("send")
    @ResponseBody
    public String testWeb(String openId) {
        WxUserMessage wxUserMessage = WxMessage.imageBuilder().mediaUrl("http://wx3.sinaimg.cn/mw690/007n4kc8gy1fy9gifg8a0j30m60m70tv.jpg").build();
        wxMessageTemplate.sendMessage(openId, wxUserMessage);
        return "";
    }

    @RequestMapping("sendGroup")
    @ResponseBody
    public WxMessage sendGroup(String text) {
        return WxMessage.textBuilder().content(text).toGroup().build();
    }

    @RequestMapping("qrcode")
    @ResponseBody
    public WxQrCode.Result qrcode() {
        return wxExtendService.createQrCode(WxQrCode.builder().temporary(1).build());
    }

    @RequestMapping("shortUrl")
    @ResponseBody
    public String shortUrl() {
        return wxExtendService.createShortUrl(WxShortUrl.builder().longUrl("http://wap.koudaitong.com/v2/showcase/goods?alias=128wi9shh&spm=h56083&redirect_count=1").build());
    }

    @RequestMapping("wx/bind")
    @ResponseBody
    public String login() {
        WxWebUser wxWebUser = WxWebUtils.getWxWebUserFromSession();
        WxUser wxUser = wxUserManager.getWxUserByWxWebUser(wxWebUser);
        return wxWebUser.getOpenId();
    }

    @PostMapping("doError")
    @ResponseBody
    public WxMessage err(String text) {
        return WxMessage.textBuilder().content(text).toGroup("oKS9_xGOW1xJQnIaKhFUaoei_UxU", "oKS9_xBZfDTmA3v6ahWs-hrkAqT4").build();
    }

    @RequestMapping("getWxJsConfig")
    @ResponseBody
    public WxJsConfig wxJsConfig() {
        return wxJsTicketManager.getWxJsConfigFromRequest(WxJsApi.getLocation);
    }

    @RequestMapping("testMessage")
    @ResponseBody
    public WxMessage wxMessage() {
        return WxMessage.musicBuilder().thumbMediaId("aaaaaa")
                .description("aaaaaaaaaaaa")
                .thumbMediaPath("aaaaaaaaa")
                .thumbMediaUrl("aaaaaa")
                .title("aaaaaaaaaa").build();
    }

//    @WxButton(type = WxButton.Type.VIEW,
//            group = WxButton.Group.LEFT,
//            order = WxButton.Order.SECOND,
//            url = "http://vxyufx.natappfree.cc/wx/login",
//            name = "点击链接")
//    @WxAsyncMessage
    public WxMessage testLogin(WxRequest wxRequest) {
        return WxMessage.Text.builder().content("点击了菜单链接").build();
    }

    @GetMapping("wx/login")
    public String loginWx() {
        return "redirect:/页面地址";
    }

    @Autowired
    private WxBaseService wxBaseService;

    @GetMapping("test/base")
    public void base() {
//        wxApiService.deleteMenu();
//        wxBaseService.getWxWebUserByCode("1234");
        String openId = null;
        WxUser.PageResult result;
        while (!StringUtils.isEmpty(openId = (result = wxApiService.listUser(openId)).getNextOpenId())) {
            System.out.println(result.getOpenIdList());
        }
    }


}