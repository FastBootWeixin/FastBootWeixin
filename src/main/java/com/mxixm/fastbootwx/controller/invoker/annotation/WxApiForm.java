package com.mxixm.fastbootwx.controller.invoker.annotation;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WxApiForm {

    String value() default ValueConstants.DEFAULT_NONE;

}
