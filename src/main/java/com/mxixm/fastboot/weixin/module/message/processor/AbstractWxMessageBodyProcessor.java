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

package com.mxixm.fastboot.weixin.module.message.processor;

import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * FastBootWeixin AbstractWxGroupMediaMessageProcessor
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public abstract class AbstractWxMessageBodyProcessor<B extends WxMessageBody> implements WxMessageProcessor<WxMessage<B>> {

    @Override
    public WxMessage<B> process(WxMessageParameter wxMessageParameter, WxMessage<B> wxMessage) {
        if (wxMessage == null) {
            return null;
        }
        B body = processBody(wxMessageParameter, wxMessage.getBody());
        if (wxMessage.getBody() != body) {
            // TODO: 2017/9/28 返回了不同实例，是否需要替换body？
        }
        return wxMessage;
    }

    /**
     * 处理消息体
     * @param parameter 消息参数
     * @param body      消息体
     * @return 处理后的消息体
     */
    protected abstract B processBody(WxMessageParameter parameter, B body);

    @Override
    public boolean supports(WxMessageParameter wxMessageParameter, WxMessage<B> wxMessage) {
        Type type = this.getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class userClass = (Class) (parameterizedType.getActualTypeArguments()[0]);
        if (userClass == null) {
            return false;
        }
        return userClass.isAssignableFrom(wxMessage.getBody().getClass());
    }

}
