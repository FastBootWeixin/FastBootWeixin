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

package com.mxixm.fastboot.weixin.module.message;

import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;
import com.mxixm.fastboot.weixin.module.message.parameter.WxRequestMessageParameter;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collection;

/**
 * FastBootWeixin WxMessageTemplate
 *
 * @author Guangshan
 * @date 2017/8/20 20:20
 * @since 0.1.2
 */
public class WxMessageTemplate {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    /**
     * 暂时不想加入客服系统，要加入的话可以写个EnableWxCustomer
     */
    private WxApiService wxApiService;

    private WxMessageProcessor wxMessageProcessor;

    public WxMessageTemplate(WxApiService wxApiService, WxMessageProcessor wxMessageProcessor) {
        this.wxApiService = wxApiService;
        this.wxMessageProcessor = wxMessageProcessor;
    }

    private void sendMessageInternal(WxMessage wxMessage) {
        if (wxMessage == null) {
            return;
        }
        if (WxUserMessage.class.isAssignableFrom(wxMessage.getClass())) {
            this.wxApiService.sendUserMessage((WxUserMessage) wxMessage);
        } else if (WxGroupMessage.class.isAssignableFrom(wxMessage.getClass())) {
            // 群发消息根据参数不同调用不同的接口
            WxGroupMessage wxGroupMessage = (WxGroupMessage) wxMessage;
            if (CollectionUtils.isEmpty(wxGroupMessage.toUsers)) {
                this.wxApiService.sendGroupMessage((WxGroupMessage) wxMessage);
            } else if (wxGroupMessage.toUsers.size() > 1) {
                this.wxApiService.sendUsersMessage((WxGroupMessage) wxMessage);
            } else {
                this.wxApiService.previewGroupMessage((WxGroupMessage) wxMessage);
            }
        } else if (WxTemplateMessage.class.isAssignableFrom(wxMessage.getClass())) {
            this.wxApiService.sendTemplateMessage((WxTemplateMessage) wxMessage);
        } else {
            logger.error("不能处理的消息类型" + wxMessage);
        }
    }

    public void sendMessage(WxMessage wxMessage) {
        this.sendMessage(WxWebUtils.getWxMessageParameter(), wxMessage);
    }

    public void sendMessage(WxMessageParameter wxMessageParameter, WxMessage wxMessage) {
        this.sendMessageInternal(wxMessageProcessor.process(wxMessageParameter, wxMessage));
    }

    /**
     * 总的发送逻辑，支持所有类型的value
     * @param wxMessageParameter 参数
     * @param value 值。支持WxMessage类型、迭代器类型，数组类型和CharSequence类型，其他的都按toString处理为字符串再发送
     */
    public void sendMessage(WxMessageParameter wxMessageParameter, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof WxMessage) {
            this.sendMessage(wxMessageParameter, (WxMessage) value);
        } else if (value instanceof CharSequence) {
            this.sendMessage(wxMessageParameter, value.toString());
        } else if (value instanceof Iterable) {
            ((Iterable) value).forEach(v -> sendMessage(wxMessageParameter, v));
        } else if (value.getClass().isArray()) {
            // 这里应该要判断下数组中元素类型不是原始类型才能进行强制转换
            Arrays.stream((Object[]) value).forEach(v -> sendMessage(wxMessageParameter, v));
        } else {
            // 其他情况调用toString()发送，开发者要注意
            this.sendMessage(wxMessageParameter, value.toString());
        }
    }

    public void sendMessage(WxRequest wxRequest, WxMessage wxMessage) {
        this.sendMessage(new WxRequestMessageParameter(wxRequest), wxMessage);
    }

    public void sendUserMessage(WxRequest wxRequest, WxMessage wxMessage) {
        this.sendMessage(wxRequest, wxMessage);
    }

    public void sendMessage(WxRequest wxRequest, String wxMessageContent) {
        this.sendMessage(wxRequest, WxMessage.textBuilder().content(wxMessageContent).build());
    }

    public void sendMessage(WxMessageParameter wxMessageParameter, String wxMessageContent) {
        this.sendMessage(wxMessageParameter, WxMessage.textBuilder().content(wxMessageContent).build());
    }

    public void sendUserMessage(WxRequest wxRequest, String wxMessageContent) {
        this.sendMessage(wxRequest, wxMessageContent);
    }

    public void sendMessage(String toUser, WxMessage wxMessage) {
        WxUserMessage wxUserMessage = wxMessage.toUserMessage();
        WxMessageParameter wxMessageParameter = WxWebUtils.getWxMessageParameter();
        wxMessageParameter.setToUser(toUser);
        this.sendMessage(wxMessageParameter, wxUserMessage);
    }

    public void sendUserMessage(String toUser, WxMessage wxMessage) {
        this.sendMessage(toUser, wxMessage);
    }

    public void sendMessage(String toUser, String wxMessageContent) {
        this.sendUserMessage(toUser, WxMessage.Text.builder().content(wxMessageContent).build());
    }

    public void sendUserMessage(String toUser, String wxMessageContent) {
        this.sendMessage(toUser, wxMessageContent);
    }

    public void sendGroupMessage(WxMessage wxMessage) {
        this.sendMessage(WxWebUtils.getWxMessageParameter(), wxMessage.toGroupMessage());
    }

    public void sendGroupMessage(int tagId, WxMessage wxMessage) {
        WxGroupMessage wxGroupMessage = wxMessage.toGroupMessage();
        wxGroupMessage.filter = new WxGroupMessage.Filter();
        wxGroupMessage.filter.tagId = tagId;
        wxGroupMessage.toUsers = null;
        this.sendGroupMessage(wxMessage);
    }

    public void sendMessage(int tagId, WxMessage wxMessage) {
        this.sendGroupMessage(tagId, wxMessage);
    }

    public void sendGroupMessage(Collection<String> toUsers, WxMessage wxMessage) {
        WxGroupMessage wxGroupMessage = wxMessage.toGroupMessage();
        wxGroupMessage.filter = null;
        wxGroupMessage.toUsers = toUsers;
        this.sendGroupMessage(wxMessage);
    }

    public void sendMessage(Collection<String> toUsers, WxMessage wxMessage) {
        this.sendGroupMessage(toUsers, wxMessage);
    }

    public void sendTemplateMessage(WxTemplateMessage wxTemplateMessage) {
        this.sendMessage(WxWebUtils.getWxMessageParameter(), wxTemplateMessage);
    }

    public void sendTemplateMessage(String toUser, WxTemplateMessage wxTemplateMessage) {
        WxMessageParameter wxMessageParameter = WxWebUtils.getWxMessageParameter();
        wxMessageParameter.setToUser(toUser);
        this.sendMessage(wxMessageParameter, wxTemplateMessage);
    }

}
