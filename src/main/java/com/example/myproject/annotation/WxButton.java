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

	// 按钮属于哪一组
	Button.Group group();

	Button.Type type() default Button.Type.CLICK;

	// 是否是主菜单(下面的菜单)
	boolean main() default false;

	String name() default "";
	
	String key() default "";
	
	String url() default "";
	
	String mediaId() default "";
	
}
