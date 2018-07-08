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

package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.module.message.WxUserMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * FastBootWeixin WxMessageUtils
 * 消息相关的工具类
 *
 * @author Guangshan
 * @date 2018-5-24 17:03:28
 * @since 0.6.1
 */
public class WxMessageUtils {

    private static Set<Class<? extends WxUserMessage>> xmlResponseTypes = new HashSet<>(8);

    static {
        xmlResponseTypes.add(WxUserMessage.Text.class);
        xmlResponseTypes.add(WxUserMessage.Image.class);
        xmlResponseTypes.add(WxUserMessage.Voice.class);
        xmlResponseTypes.add(WxUserMessage.Music.class);
        xmlResponseTypes.add(WxUserMessage.Video.class);
        xmlResponseTypes.add(WxUserMessage.News.class);
    }

    /**
     * 是否支持xml方式回复消息
     * @param type
     * @return result
     */
    public static boolean supportsXmlResponse(Class<?> type) {
        return xmlResponseTypes.contains(type) || CharSequence.class.isAssignableFrom(type);
    }

}
