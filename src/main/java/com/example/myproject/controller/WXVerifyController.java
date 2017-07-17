package com.example.myproject.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

// 之后可以改造为endPoint方式
@RestController
public class WXVerifyController {

    @Value("${wx.security.token}")
    private String token;

    @GetMapping(params = {"signature", "timestamp", "nonce", "echostr"})
    public String verifyServer(@RequestParam(value = "signature", required = true) String signature,
                               @RequestParam(value = "timestamp", required = true) String timestamp,
                               @RequestParam(value = "nonce", required = true) String nonce,
                               @RequestParam(value = "echostr", required = true) String echostr) {
        String rawString = Stream.of(token, timestamp, nonce).sorted().collect(Collectors.joining());
        if (signature.equals(DigestUtils.sha1Hex(rawString))) {
            return echostr;
        }
        return null;
    }

}
