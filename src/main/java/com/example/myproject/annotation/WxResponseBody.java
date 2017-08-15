package com.example.myproject.annotation;

import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/8/15.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
public @interface WxResponseBody {
}
