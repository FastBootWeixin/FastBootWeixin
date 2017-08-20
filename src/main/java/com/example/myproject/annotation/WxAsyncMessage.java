package com.example.myproject.annotation;

import java.lang.annotation.*;

/**
 * 标记异步发送消息
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WxAsyncMessage {

}
