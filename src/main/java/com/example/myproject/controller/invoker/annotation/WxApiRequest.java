package com.example.myproject.controller.invoker.annotation;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

/**
 * 标记一个类为代理调用类
 * @see com.example.myproject.controller.invoker.WxInvokerProxyFactory
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WxApiRequest {

	/**
	 * 要调用的主机地址
	 * @return
	 */
	String host() default ValueConstants.DEFAULT_NONE;

	/**
	 * 如果以方法名为属性名，通过SPEL表达式获得对应的地址，则prefix需要设置为参数的前缀
	 * @return
	 */
	String prefix() default ValueConstants.DEFAULT_NONE;

	/**
	 * 方法上，如果有path，则优先取path，否则按上面的方式拼接
	 * @return
	 */
	String path() default ValueConstants.DEFAULT_NONE;

	/**
	 * 调用方法
	 * @return
	 */
	Method method() default Method.GET;

	String[] headers() default {};

	enum Method {

		GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE

	}


}
