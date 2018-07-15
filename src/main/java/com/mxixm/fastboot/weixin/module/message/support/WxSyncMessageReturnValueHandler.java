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

package com.mxixm.fastboot.weixin.module.message.support;

import com.mxixm.fastboot.weixin.annotation.WxAsyncMessage;
import com.mxixm.fastboot.weixin.annotation.WxMapping;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageTemplate;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;

/**
 * FastBootWeixin WxSyncMessageReturnValueHandler
 * 其实@WxAsyncMessage并不是通过这个类发送的，这里应该叫做CustomerMessage，这里的消息不需要异步发送，直接调用发送即可
 * todo 按照上面的逻辑重构
 * done 已重构
 * 该类不处理声明为被动返回的消息，即返回XML消息，其他类型都通过本类处理
 * 而对于消息的发送，都采用异步的方式我觉得没有问题，如果同步发送，报错之后响应给微信，微信会返回给用户服务器故障，这是不友好的，所以这里固定异步发送。
 * 但是有点区别，如果是普通的@RequestMapping呢，可能就不需要异步发送了
 * todo 分离出来两种，WxMappingReturnValueHandler和WxMessageReturnValueHandler
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @update 2018-5-24 17:24:21
 * @since 0.6.1
 */
@Deprecated
public class WxSyncMessageReturnValueHandler extends AbstractWxMessageReturnValueHandler {

    private WxMessageTemplate wxMessageTemplate;

    public WxSyncMessageReturnValueHandler(WxMessageTemplate wxMessageTemplate) {
        this.wxMessageTemplate = wxMessageTemplate;
    }

    @Override
    protected boolean supportsReturnTypeInternal(MethodParameter returnType) {
        // 是否显式声明为async了，如果是不从这里发送
        boolean isAsyncMessage = returnType.hasMethodAnnotation(WxAsyncMessage.class);
        // 是否是WxMapping，如果声明为wxMapping，则不通过这里而是直接异步发送
        // 理论上可控制顺序来控制这里的判断减少，但是稳妥起见多判断一次
        boolean isWxMapping = returnType.hasMethodAnnotation(WxMapping.class);
        Class realType = getGenericType(returnType);
        // 对于非WxMapping的handler，判断返回类型是否是WxMessage，如果是则同步发送消息
        boolean isWxMessage = WxMessage.class.isAssignableFrom(realType);
        return !isAsyncMessage && !isWxMapping && isWxMessage;
    }

    @Override
    protected void handlReturnValueInternal(Object returnValue) {
        WxMessageParameter wxMessageParameter = WxWebUtils.getWxMessageParameter();
        wxMessageTemplate.sendMessage(wxMessageParameter, returnValue);
    }

}
