package com.mxixm.fastbootwx.mvc.annotation;

import com.mxixm.fastbootwx.annotation.WxResponseBody;

import java.lang.annotation.*;

/**
 * FastBootWeixin  WxController
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxController
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WxResponseBody
public @interface WxController {

    String value() default "";

}