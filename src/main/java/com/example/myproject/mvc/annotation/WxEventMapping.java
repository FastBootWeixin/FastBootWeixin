package com.example.myproject.mvc.annotation;

import com.example.myproject.module.event.WxEvent;

import java.lang.annotation.*;

/**
 * 微信请求绑定
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WxEventMapping {

    /**
     * 请求事件的类型
     * @return
     */
    WxEvent.Type type();

    /**
     * 名称
     * @return
     */
    String name() default "";

}
