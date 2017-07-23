package com.example.myproject.controller;

import com.example.myproject.config.ApiInvoker.ApiVerifyProperties;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FastBootWeixin  /
 * 之后可以改造为endPoint方式
 *
 * @author Guangshan
 * @summary FastBootWeixin  WXVerifyController
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/16 23:37
 */
@RestController
public class WXVerifyController {

    @Autowired
    private ApiVerifyProperties apiVerifyProperties;

    @GetMapping(params = {"signature", "timestamp", "nonce", "echostr"})
    public String verifyServer(@RequestParam(value = "signature", required = true) String signature,
                               @RequestParam(value = "timestamp", required = true) String timestamp,
                               @RequestParam(value = "nonce", required = true) String nonce,
                               @RequestParam(value = "echostr", required = true) String echostr) {
        String rawString = Stream.of(apiVerifyProperties.getToken(), timestamp, nonce).sorted().collect(Collectors.joining());
        if (signature.equals(DigestUtils.sha1Hex(rawString))) {
            return echostr;
        }
        return null;
    }

}
