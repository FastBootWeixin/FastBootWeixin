package com.mxixm.fastbootwx.module.message;

import com.mxixm.fastbootwx.module.WxRequest;
import com.mxixm.fastbootwx.support.WxUserProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * FastBootWeixin  WxMessageProcesser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMessageProcesser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 22:24
 */
public interface WxMessageProcesser<T extends WxMessage> {

    T process(WxRequest wxRequest, T wxMessage);

    default boolean supports(WxRequest wxRequest, T wxMessage) {
        Type[] types = this.getClass().getGenericInterfaces();
        Class userClass = Arrays.stream(types).filter(t -> t instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .filter(t -> t.getRawType().equals(WxMessageProcesser.class))
                .findFirst().map(t -> (Class)t.getActualTypeArguments()[0])
                .orElse(null);
        if (userClass == null) {
            return false;
        }
        return userClass.isAssignableFrom(wxMessage.getClass());
    }

}
