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
    }

    @RequestMapping("test")
    @ResponseBody
    public String test() {
        return apiInvoker.getCallbackIp();
    }

    @RequestMapping("menu")
    @ResponseBody
    public String menu() {
        return apiInvoker.getMenu();
    }

    @WxButton(group = Button.Group.LEFT, main = true, name = "一级菜单左", key = "left")
    public void a() {
    }

    @WxButton(group = Button.Group.MIDDLE, main = true, name = "一级菜单中", key = "middle")
    public void b() {
    }

    @WxButton(group = Button.Group.MIDDLE, name = "二级菜单中一", key = "middle_1")
    public void b1() {
    }

    @WxButton(group = Button.Group.RIGHT, main = true, name = "一级菜单右", key = "right")
    public void c() {
    }

    @WxButton(group = Button.Group.RIGHT, name = "二级菜单右一", key = "right_1")
    public void c1() {
    }

    @WxButton(group = Button.Group.LEFT, name = "二级菜单左一", key = "left_1")
    public void a1() {
    }

    @WxButton(group = Button.Group.LEFT, name = "二级菜单左二", key = "left_2")
    public void a2() {
    }

    @WxButton(group = Button.Group.LEFT, name = "二级菜单左三", key = "left_3")
    public void a3() {
    }

}
