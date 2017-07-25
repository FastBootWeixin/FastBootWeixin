package com.example.myproject;

import com.example.myproject.config.ApiInvoker.ApiInvoker;
import com.example.myproject.module.menu.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import com.example.myproject.annotation.WxApplication;
import com.example.myproject.annotation.WxButton;
import com.example.myproject.module.menu.WxMenuManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Hello world!
 */
@WxApplication
@Controller
public class App {

    @Autowired
    ApiInvoker apiInvoker;

    //用mvn命令执行和直接执行该Java是一样的结果，mvn spring-boot:run是找到这个文件的main去执行的
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
        System.out.println(WxMenuManager.getInstance().getMenuJson());
    }

    @RequestMapping("test")
    @ResponseBody
    public String test() {
        return apiInvoker.getCallbackIp();
    }

    @WxButton(group = Button.Group.LEFT, main = true, name = "a", key = "a")
    public void a() {
    }

    @WxButton(group = Button.Group.MIDDLE, main = true, name = "b", key = "b")
    public void b() {
    }

    @WxButton(group = Button.Group.RIGHT, main = true, name = "c", key = "c")
    public void c() {
    }

    @WxButton(group = Button.Group.LEFT, name = "a1", key = "a1")
    public void a1() {
    }

    @WxButton(group = Button.Group.LEFT, name = "a2", key = "a2")
    public void a2() {
    }

    @WxButton(group = Button.Group.LEFT, name = "a3", key = "a3")
    public void a3() {
    }

}
