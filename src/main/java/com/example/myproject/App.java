package com.example.myproject;

import com.example.myproject.config.ApiInvoker.ApiInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import com.example.myproject.annotation.WxApplication;
import com.example.myproject.annotation.WxButton;
import com.example.myproject.module.menu.WXMenuManager;
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
        System.out.println(WXMenuManager.getInstance().getMenuJson());
    }

    @RequestMapping("test")
    @ResponseBody
    public String test() {
        return apiInvoker.getCallbackIp();
    }

    @WxButton(name = "a", key = "a")
    public void a() {
    }

    @WxButton(name = "b", key = "b")
    public void b() {
    }

    @WxButton(name = "c", key = "c")
    public void c() {
    }

    @WxButton(name = "a1", key = "a1")
    public void a1() {
    }

    @WxButton(name = "a2", key = "a2")
    public void a2() {
    }

    @WxButton(name = "a3", key = "a3")
    public void a3() {
    }

}
