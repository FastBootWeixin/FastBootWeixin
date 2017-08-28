package com.example.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * FastBootWeixin  JsonTest
 *
 * @author Guangshan
 * @summary FastBootWeixin  JsonTest
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/8 23:43
 */
public class JsonTest {

    public static void main(String[] args) throws JsonProcessingException {
        Test test = new Test("1", "2");
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(test));
    }

    @AllArgsConstructor
    public static class Test {

        @XmlElementWrapper(name = "test")
        private String a;

        @JsonProperty("b")
        private String b;

    }

}
