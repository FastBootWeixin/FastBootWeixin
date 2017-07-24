package com.example.myproject.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.myproject.module.menu.Button;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WxButton {

	Button parent() default Button.none;
	
	Button.Type type() default Button.Type.click;
	
	String name() default "";
	
	String key() default "";
	
	String url() default "";
	
	String mediaId() default "";
	
}
