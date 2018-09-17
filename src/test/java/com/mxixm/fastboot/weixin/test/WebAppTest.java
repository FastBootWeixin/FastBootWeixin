/**
 *
 */
package com.mxixm.fastboot.weixin.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@SpringBootApplication
@RestController
public class WebAppTest {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebAppTest.class, args);
    }

    @GetMapping("a")
    @PostMapping("a")
    public String a() {
        return "a";
    }

}
