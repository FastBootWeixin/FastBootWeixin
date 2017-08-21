package com.mxixm.fastbootwx.mvc.annotation;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * FastBootWeixin  WxAppAssert
 *
 * @author WxController
 * @summary FastBootWeixin  WxController
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface WxController {

    /**
     * The value may indicate a suggestion for a logical component value,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component value, if any
     */
    String value() default "";

}