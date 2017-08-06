package com.example.myproject.mvc.annotation;

import com.example.myproject.module.event.WxEvent;

/**
 * 微信请求绑定
 */
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
