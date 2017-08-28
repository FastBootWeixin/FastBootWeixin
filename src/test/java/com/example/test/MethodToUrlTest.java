package com.example.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mxixm.fastboot.weixin.controller.WxBuildinVerify;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * FastBootWeixin  JsonTest
 *
 * @author Guangshan
 * @summary FastBootWeixin  JsonTest
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/8 23:43
 */
public class MethodToUrlTest {

    public static void main(String[] args) throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://api.wx.com");
        MvcUriComponentsBuilder.fromMethod(builder, WxBuildinVerify.class, ClassUtils.getMethod(WxBuildinVerify.class, "verify", null), "a", "a", "a", "a");
        System.out.println(builder);
    }

}
