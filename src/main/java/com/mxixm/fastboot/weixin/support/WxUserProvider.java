package com.mxixm.fastboot.weixin.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * FastBootWeixin  WxUserProvider
 * 用户提供器接口
 * 关于fromUser和toUser可以再考虑考虑
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxUserProvider
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/5 21:50
 */
public interface WxUserProvider<T> {

    T getUser(String userName);

    /**
     * 可能会有bug，当这个类的实现类有多个泛型时\
     * 没bug啦，改进了
     * @param clazz
     * @return
     */
    default boolean isMatch(Class<?> clazz) {
        Type[] types = this.getClass().getGenericInterfaces();
        Class userClass = Arrays.stream(types).filter(t -> t instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .filter(t -> t.getRawType().equals(WxUserProvider.class))
                .findFirst().map(t -> (Class)t.getActualTypeArguments()[0])
                .orElse(null);
        if (userClass == null) {
            return false;
        }
        return clazz.isAssignableFrom(userClass);
    }

}
