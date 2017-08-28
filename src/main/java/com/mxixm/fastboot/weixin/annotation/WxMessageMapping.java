package com.mxixm.fastboot.weixin.annotation;

import com.mxixm.fastboot.weixin.module.message.WxMessage;

import java.lang.annotation.*;

/**
 * 微信消息请求绑定
 * 暂时不想做pattern匹配
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WxMapping
public @interface WxMessageMapping {

    /**
     * 请求的消息类型
     * @return
     */
    WxMessage.Type type();

    /**
     * 通配符
     * @return
     */
    String[] wildcard() default "*";

    /**
     * 匹配模式，正则表达式，暂时不支持
     * @return
     */
    String pattern() default "";

    /**
     * 名称
     * @return
     */
    String name() default "";

}
