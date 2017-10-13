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

import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;
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
    private WxApiInvokeSpi wxApiInvokeSpi;

    private WxMessageProcesser wxMessageProcesser;

    public WxMessageTemplate(WxApiInvokeSpi wxApiInvokeSpi, WxMessageProcesser wxMessageProcesser) {
        this.wxApiInvokeSpi = wxApiInvokeSpi;
        this.wxMessageProcesser = wxMessageProcesser;
    }

    public void sendMessage(WxMessage wxMessage) {
        if (WxUserMessage.class.isAssignableFrom(wxMessage.getClass())) {
            this.wxApiInvokeSpi.sendUserMessage((WxUserMessage) wxMessage);
        } else if (WxGroupMessage.class.isAssignableFrom(wxMessage.getClass())) {
            this.wxApiInvokeSpi.sendGroupMessage((WxGroupMessage) wxMessage);
        } else if (WxTemplateMessage.class.isAssignableFrom(wxMessage.getClass())) {
            this.wxApiInvokeSpi.sendTemplateMessage((WxTemplateMessage) wxMessage);
        } else {
            logger.error("不能处理的消息类型" + wxMessage);
        }
    }

    public void sendMessage(WxRequest wxRequest, WxMessage wxMessage) {
        this.sendMessage(wxMessageProcesser.process(wxRequest, wxMessage));
    }

    public void sendUserMessage(WxRequest wxRequest, WxMessage wxMessage) {
        this.sendMessage(wxRequest, wxMessage);
    }

    public void sendMessage(WxRequest wxRequest, String wxMessageContent) {
        this.sendMessage(wxRequest, WxMessage.Text.builder().content(wxMessageContent).build());
    }

    public void sendUserMessage(WxRequest wxRequest, String wxMessageContent) {
        this.sendMessage(wxRequest, wxMessageContent);
    }

    public void sendMessage(String toUser, String wxMessageContent) {
        this.sendUserMessage(toUser, WxMessage.Text.builder().content(wxMessageContent).build());
    }

    public void sendUserMessage(String toUser, String wxMessageContent) {
        this.sendMessage(toUser, wxMessageContent);
    }

    public void sendMessage(String toUser, WxMessage wxMessage) {
        WxUserMessage wxUserMessage = wxMessage.toUserMessage();
        wxUserMessage.setToUser(toUser);
        this.sendMessage(wxMessage);
    }

    public void sendUserMessage(String toUser, WxMessage wxMessage) {
        this.sendMessage(toUser, wxMessage);
    }

    public void sendGroupMessage(WxMessage wxMessage) {
        this.sendMessage(wxMessage.toGroupMessage());
    }

    public void sendMessage(int tagId, WxMessage wxMessage) {
        this.sendGroupMessage(tagId, wxMessage);
    }

    public void sendGroupMessage(int tagId, WxMessage wxMessage) {
        WxGroupMessage wxGroupMessage = wxMessage.toGroupMessage();
        wxGroupMessage.filter = new WxGroupMessage.Filter();
        wxGroupMessage.filter.tagId = tagId;
        wxGroupMessage.toUsers = null;
        this.sendMessage(wxMessage);
    }

    public void sendMessage(Collection<String> toUsers, WxMessage wxMessage) {
        this.sendGroupMessage(toUsers, wxMessage);
    }

    public void sendGroupMessage(Collection<String> toUsers, WxMessage wxMessage) {
        WxGroupMessage wxGroupMessage = wxMessage.toGroupMessage();
        wxGroupMessage.filter = null;
        wxGroupMessage.toUsers = toUsers;
        this.sendMessage(wxMessage);
    }

    public void sendTemplateMessage(WxTemplateMessage wxTemplateMessage) {
        this.sendMessage(wxTemplateMessage);
    }

    public void sendTemplateMessage(String toUser, WxTemplateMessage wxTemplateMessage) {
        wxTemplateMessage.setToUser(toUser);
        this.sendMessage(wxTemplateMessage);
    }

}
