package com.example.myproject;

import org.springframework.boot.SpringApplication;

import com.example.myproject.annotation.WXApplication;
import com.example.myproject.annotation.WXMenu;
import com.example.myproject.module.menu.WXMenuManager;

/**
 * Hello world!
 *
 */
@WXApplication
public class App {

    //用mvn命令执行和直接执行该Java是一样的结果，mvn spring-boot:run是找到这个文件的main去执行的
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
        System.out.println(WXMenuManager.getInstance().getMenuJson());
    }
    
    @WXMenu(name = "a", subMenu = {"a1", "a2", "a3"})
    public void a() {}
    
    @WXMenu(name = "b")
    public void b() {}
    
    @WXMenu(name = "c")
    public void c() {}
    
    @WXMenu(name = "a1")
    public void a1() {}
    
    @WXMenu(name = "a2")
    public void a2() {}
    
    @WXMenu(name = "a3")
    public void a3() {}

}