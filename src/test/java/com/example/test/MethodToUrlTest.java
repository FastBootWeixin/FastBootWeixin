package com.example.test;

import com.example.myproject.controller.WxVerifyController;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.annotation.XmlElementWrapper;

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
        MvcUriComponentsBuilder.fromMethod(builder, WxVerifyController.class, ClassUtils.getMethod(WxVerifyController.class, "verify", null), "a", "a", "a", "a");
        System.out.println(builder);
    }

}
