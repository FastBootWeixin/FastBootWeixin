package com.example.myproject.controller.invoker.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WxMethod {

	String path() default ValueConstants.DEFAULT_NONE;

	Type type() default Type.GET;

	String[] headers() default {};

	enum Type {

		GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE

	}


}
