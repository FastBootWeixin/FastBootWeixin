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

package com.mxixm.fastboot.weixin.mvc.method;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;

/**
 * FastBootWeixin WxMappingHandlerMethodNamingStrategy
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public class WxMappingHandlerMethodNamingStrategy
        implements HandlerMethodMappingNamingStrategy<WxMappingInfo> {

    public static final String SEPARATOR = "#";

    @Override
    public String getName(HandlerMethod handlerMethod, WxMappingInfo mapping) {
        if (StringUtils.hasText(mapping.getName())) {
            return mapping.getName();
        }
        StringBuilder sb = new StringBuilder();
        String simpleTypeName = handlerMethod.getBeanType().getSimpleName();
        for (int i = 0; i < simpleTypeName.length(); i++) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }
        sb.append(SEPARATOR).append(handlerMethod.getMethod().getName());
        return sb.toString();
    }

}
