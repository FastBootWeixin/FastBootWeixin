package com.example.myproject.controller.invoker.annotation;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WxApiForm {

    String name() default ValueConstants.DEFAULT_NONE;

}
