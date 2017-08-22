package com.mxixm.fastbootwx.mvc.annotation;

import com.mxixm.fastbootwx.annotation.WxResponseBody;
import org.springframework.stereotype.Controller;

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
@Controller
@WxResponseBody
public @interface WxController {

    String value() default "";

}