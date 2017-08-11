package com.example.myproject.controller.invoker.annotation;

import java.lang.annotation.*;

/**
 * 是否有必要再加一个WxApiFile，传入路径参数？
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WxApiBody {

    Type type() default Type.JSON;

    enum Type {
        JSON, XML
    }

}
