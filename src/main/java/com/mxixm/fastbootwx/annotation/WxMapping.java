package com.mxixm.fastbootwx.annotation;

import java.lang.annotation.*;

/**
 * 标记是微信的Mapping，包括WxButton、WxEventMapping、WxMessageMapping
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WxMapping {
}
