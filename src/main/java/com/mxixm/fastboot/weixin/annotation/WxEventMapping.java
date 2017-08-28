package com.mxixm.fastboot.weixin.annotation;

import com.mxixm.fastboot.weixin.annotation.WxAsyncMessage;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.annotation.WxMapping;

import java.lang.annotation.*;

/**
 * 微信请求绑定
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WxAsyncMessage
@WxMapping
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
