package com.mxixm.fastboot.weixin.module.message.adapters;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.Date;

/**
 * FastBootWeixin  WxJsonAdapters
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxJsonAdapters
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/14 22:31
 */
public class WxJsonAdapters {

    /*public static class WxDateDeserializer extends JsonDeserializer<Date> {

        public Date deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            int id = (Integer) ((IntNode) node.get("id")).numberValue();
            String itemName = node.get("itemName").asText();
            int userId = (Integer) ((IntNode) node.get("id")).numberValue();

            return new Date();
        }
    }*/

    public static class WxIntDateConverter implements Converter<Integer, Date> {

        @Override
        public Date convert(Integer value) {
            return new Date(value * 1000l);
        }

        /**
         * 这个的作用是提供输入类型，JackSon根据输入类型去找Deserializer，找到后执行Deserializer
         * Deserializer后会把值送给convert
         * @param typeFactory
         * @return
         */
        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Integer.class);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Date.class);
        }
    }

    public static class WxBooleanIntConverter implements Converter<Boolean, Integer> {

        @Override
        public Integer convert(Boolean value) {
            return value == null ? 0 : value ? 1 : 0;
        }

        /**
         * 这个的作用是提供输入类型，JackSon根据输入类型去找Deserializer，找到后执行Deserializer
         * Deserializer后会把值送给convert
         * @param typeFactory
         * @return
         */
        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Boolean.class);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Integer.class);
        }
    }

}
