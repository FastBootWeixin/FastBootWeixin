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

package com.mxixm.fastboot.weixin.module.message.processer;

import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * FastBootWeixin AbstractWxGroupMediaMessageProcesser
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public abstract class AbstractWxMessageBodyProcesser<B extends WxMessageBody> implements WxMessageProcesser<WxMessage<B>> {

    @Override
    public WxMessage<B> process(WxRequest wxRequest, WxMessage<B> wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        B body = processBody(wxRequest, wxMessage.getBody());
        if (wxMessage.getBody() != body) {
            // TODO: 2017/9/28 返回了不同实例，是否需要替换body？
        }
        return wxMessage;
    }

    protected abstract B processBody(WxRequest wxRequest, B body);

    public boolean supports(WxRequest wxRequest, WxMessage<B> wxMessage) {
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
