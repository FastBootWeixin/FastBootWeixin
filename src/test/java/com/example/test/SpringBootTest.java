package com.example.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FastBootWeixin  SpringBootTest
 *
 * @author Guangshan
 * @summary FastBootWeixin  SpringBootTest
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/10 20:54
 */
@SpringBootApplication
public class SpringBootTest {

    @Value("${test:aaa}")
    String v;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class, args);
    }

}
