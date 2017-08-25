package com.mxixm.fastboot.weixin.controller.invoker.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WxApiPath {

	String value() default "";

}
