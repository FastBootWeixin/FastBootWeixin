package com.example.myproject.mvc.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/8/4.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface WxController {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any
     */
    String value() default "";

}