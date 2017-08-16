package com.example.myproject;

import com.example.myproject.annotation.WxApplication;
import com.example.myproject.annotation.WxButton;
import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.module.WxRequest;
import com.example.myproject.module.event.WxEvent;
import com.example.myproject.module.media.WxMedia;
import com.example.myproject.module.media.WxMediaResource;
import com.example.myproject.module.menu.WxMenuManager;
import com.example.myproject.module.message.WxMessage;
import com.example.myproject.module.user.WxUser;
import com.example.myproject.mvc.annotation.WxController;
import com.example.myproject.mvc.annotation.WxEventMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

/**
 * Hello world!
 */
@WxApplication
@WxController
public class WxApp {

    @Autowired
    WxApiInvokeSpi wxApiInvokeSpi;

    //用mvn命令执行和直接执行该Java是一样的结果，mvn spring-boot:run是找到这个文件的main去执行的
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WxApp.class, args);
    }

    @RequestMapping("test")
    @ResponseBody
    public String test() {
        return wxApiInvokeSpi.getCallbackIp();
    }

    @RequestMapping("test1")
    @ResponseBody
    public WxMedia.TempMediaResult test1() {
        return wxApiInvokeSpi.uploadTempMedia(WxMedia.Type.IMAGE, new File("E:/test.png"));
    }

    @RequestMapping("test2")
    @ResponseBody
    public WxMediaResource test2(String mediaId) {
        return wxApiInvokeSpi.getTempMedia(mediaId);
    }

    @RequestMapping("menu")
    @ResponseBody
    public WxMenuManager.WxMenus menu() {
        return wxApiInvokeSpi.getMenu();
    }

    /**
     * 一次选择位置共推送了以下事件：
     * 1、messageType:event-eventType:location_select事件，菜单主动触发
     * 2、messageType:location类型，主动推送，用户发送的消息
     * 3、messageType:event-eventType:location事件 系统事件推送
     */
    @WxButton(group = WxButton.Group.LEFT, main = true, name = "左")
    public String left() {
        return "";
    }

    /**
     * 回复消息一定要有fromUser，且是gh_930e3941e6f7
     */
    @WxButton(group = WxButton.Group.MIDDLE, main = true, name = "中")
    public void middle() {
        System.out.println(1);
    }

    @WxButton(group = WxButton.Group.RIGHT, main = true, name = "右")
    public void right() {
    }

    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIRST,
            name = "左一")
    public WxMessage click(WxRequest wxRequest, String fromUser, String toUser, WxUser wxUser) {
        System.out.println(wxApiInvokeSpi.getMenu());
        return WxMessage.News.builder()
                .toUserName(wxUser.getOpenId())
                .firstItem("我是一条图文测试消息", "测试哈哈哈哈",
                        "qipei.mxixm.com/upload/image/1472608640783.jpg",
                        "qipei.mxixm.com/vendor/5")
                .addItem("我是二条图文测试消息", "测试哈哈哈哈",
                        "qipei.mxixm.com/upload/image/1472608640783.jpg",
                        "qipei.mxixm.com/vendor/5")
                .addItem("我是三条图文测试消息", "测试哈哈哈哈",
                        "qipei.mxixm.com/upload/image/1472608640783.jpg",
                        "qipei.mxixm.com/vendor/5")
                .addItem("我是四条图文测试消息", "测试哈哈哈哈",
                        "qipei.mxixm.com/upload/image/1472608640783.jpg",
                        "qipei.mxixm.com/vendor/5")
                .addItem(WxMessage.News.Item.builder()
                        .title("我是五条图文测试消息")
                        .description("测试哈哈哈哈")
                        .picUrl("qipei.mxixm.com/upload/image/1472608640783.jpg")
                        .url("qipei.mxixm.com/vendor/5").build())
                .addItem(WxMessage.News.Item.builder()
                        .title("我是六条图文测试消息")
                        .description("测试哈哈哈哈")
                        .picUrl("qipei.mxixm.com/upload/image/1472608640783.jpg")
                        .url("qipei.mxixm.com/vendor/5").build())
                .addItem(WxMessage.News.Item.builder()
                        .title("我是七条图文测试消息")
                        .description("测试哈哈哈哈")
                        .picUrl("qipei.mxixm.com/upload/image/1472608640783.jpg")
                        .url("qipei.mxixm.com/vendor/5").build())
                .addItem(WxMessage.News.Item.builder()
                        .title("我是八条图文测试消息")
                        .description("测试哈哈哈哈")
                        .picUrl("qipei.mxixm.com/upload/image/1472608640783.jpg")
                        .url("qipei.mxixm.com/vendor/5").build())
                .addItem(WxMessage.News.Item.builder()
                        .title("我是九条图文测试消息")
                        .description("测试哈哈哈哈")
                        .picUrl("qipei.mxixm.com/upload/image/1472608640783.jpg")
                        .url("qipei.mxixm.com/vendor/5").build())
                .build();
//        return WxMessage.Text.builder()
//                .fromUserName(wxUser.getToUserName())
//                .toUserName(wxUser.getFromUserName())
//                .content("我是一条文本测试消息")
//                .build();
    }

    @WxButton(type = WxButton.Type.LOCATION_SELECT,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.SECOND,
            name = "左二")
    public void location() {
    }

    //    @WxButton(type = WxButton.Method.MEDIA_ID, mediaId = "1", group = WxButton.Group.LEFT, value = "二级菜单左三", key = "left_3")
    public void media() {
    }

    @WxButton(type = WxButton.Type.PIC_PHOTO_OR_ALBUM,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FORTH,
            name = "二级菜单左四")
    public void pic() {
    }

    @WxButton(type = WxButton.Type.PIC_SYSPHOTO,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIFTH,
            name = "二级菜单左五")
    public void picSys() {
    }

    @WxButton(type = WxButton.Type.PIC_WEIXIN,
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.FIRST,
            name = "二级菜单中一")
    public void picWeixin() {
    }

    @WxButton(type = WxButton.Type.SCANCODE_PUSH,
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.SECOND,
            name = "二级菜单中二")
    public void scanCode() {
    }

    @WxButton(type = WxButton.Type.SCANCODE_WAITMSG,
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.THIRD,
            name = "二级菜单中三")
    public void scanCodeWait() {
    }

    @WxButton(type = WxButton.Type.VIEW,
            url = "http://baidu.com",
            group = WxButton.Group.MIDDLE,
            order = WxButton.Order.FORTH,
            name = "二级菜单中四")
    public void view() {
    }

    //    @WxButton(type = WxButton.Method.VIEW_LIMITED, group = WxButton.Group.MIDDLE, value = "二级菜单中五", key = "middle_5")
    public void viewLimited() {
    }

    @WxButton(group = WxButton.Group.RIGHT,
            name = "二级菜单右一")
    public void c1() {
    }

    @WxEventMapping(type = WxEvent.Type.UNSUBSCRIBE)
    public void unsubscribe(WxRequest wxRequest, String fromUser, String toUser, WxUser wxUser) {
        System.out.println(wxRequest);
    }
}