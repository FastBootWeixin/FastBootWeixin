/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.module.extend.adapters;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.mxixm.fastboot.weixin.module.extend.WxCard;

/**
 * fastboot-weixin  WxJsonAdapters
 *
 * @author Guangshan
 * @date 2017/9/25 21:12
 * @since ${VERSION}
 */
public class WxJsonAdapters {

    public static class WxStringColorConverter implements Converter<String, WxCard.Card.BaseInfo.Color> {

        @Override
        public WxCard.Card.BaseInfo.Color convert(String value) {
            return WxCard.Card.BaseInfo.Color.of(value);
        }

        /**
         * 这个的作用是提供输入类型，JackSon根据输入类型去找Deserializer，找到后执行Deserializer
         * Deserializer后会把值送给convert
         *
         * @param typeFactory
         * @return dummy
         */
        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(WxCard.Card.BaseInfo.Color.class);
        }
    }

}
