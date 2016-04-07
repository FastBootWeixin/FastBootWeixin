package com.example.myproject.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.myproject.module.menu.MenuType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WXMenu {

	String[] subMenu() default {};
	
	MenuType type() default MenuType.click;
	
	String name() default "";
	
	String key() default "";
	
	String url() default "";
	
	String mediaId() default "";
	
}
