package com.example.myproject.mvc.annotation;

import com.example.myproject.module.message.WxMessage;

/**
 * 微信消息请求绑定
 * 暂时不想做pattern匹配
 */
public @interface WxMessageMapping {

    /**
     * 请求的消息类型
     * @return
     */
    WxMessage.Type type();

    /**
     * 匹配模式
     * @return
     */
    String pattern() default "";

    /**
     * 名称
     * @return
     */
    String name() default "";

}
