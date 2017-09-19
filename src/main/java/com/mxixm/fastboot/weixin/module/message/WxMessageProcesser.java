/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.module.message;

import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public interface WxMessageProcesser<T extends WxMessage> {

    T process(WxRequest wxRequest, T wxMessage);

    default boolean supports(WxRequest wxRequest, T wxMessage) {
        Type[] types = this.getClass().getGenericInterfaces();
        Class userClass = Arrays.stream(types).filter(t -> t instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .filter(t -> t.getRawType().equals(WxMessageProcesser.class))
                .findFirst().map(t -> (Class) t.getActualTypeArguments()[0])
                .orElse(null);
        if (userClass == null) {
            return false;
        }
        return userClass.isAssignableFrom(wxMessage.getClass());
    }

}
