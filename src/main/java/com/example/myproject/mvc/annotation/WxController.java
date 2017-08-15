package com.example.myproject.mvc.annotation;

import com.example.myproject.annotation.WxResponseBody;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/8/4.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@WxResponseBody
public @interface WxController {

    /**
     * The value may indicate a suggestion for a logical component value,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component value, if any
     */
    String value() default "";

}