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

package com.mxixm.fastboot.weixin.module.user;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * FastBootWeixin WxUserProvider
 * 用户提供器接口
 * 关于fromUser和toUser可以再考虑考虑
 *
 * @author Guangshan
 * @date 2017/8/5 21:50
 * @since 0.1.2
 */
public interface WxUserProvider<T> {

    T getUser(String userName);

    /**
     * 可能会有bug，当这个类的实现类有多个泛型时\
     * 没bug啦，改进了
     *
     * @param clazz
     * @return the result
     */
    default boolean isMatch(Class<?> clazz) {
        Type[] types = this.getClass().getGenericInterfaces();
        Class userClass = Arrays.stream(types).filter(t -> t instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .filter(t -> t.getRawType().equals(WxUserProvider.class))
                .findFirst().map(t -> (Class) t.getActualTypeArguments()[0])
                .orElse(null);
        if (userClass == null) {
            return false;
        }
        return clazz.isAssignableFrom(userClass);
    }

}
