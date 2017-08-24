package com.mxixm.fastbootwx.mvc.annotation;

import com.mxixm.fastbootwx.annotation.WxAsyncMessage;
import com.mxixm.fastbootwx.annotation.WxMapping;
import com.mxixm.fastbootwx.module.event.WxEvent;

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
