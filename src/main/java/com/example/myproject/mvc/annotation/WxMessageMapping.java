package com.example.myproject.mvc.annotation;

import com.example.myproject.module.message.receive.WxMessage;

/**
 * 微信请求绑定
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

}
