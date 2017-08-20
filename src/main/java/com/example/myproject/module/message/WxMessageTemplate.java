package com.example.myproject.module.message;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.module.WxRequest;
import com.example.myproject.module.media.WxMediaManager;

/**
 * FastBootWeixin  WxMessageTemplate
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMessageTemplate
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 20:20
 */
public class WxMessageTemplate {

    /**
     * 暂时不想加入客服系统，要加入的话可以写个EnableWxCustomer
     */
    private WxApiInvokeSpi wxApiInvokeSpi;

    private WxMessageProcesser wxMessageProcesser;

    public WxMessageTemplate(WxApiInvokeSpi wxApiInvokeSpi, WxMessageProcesser wxMessageProcesser) {
        this.wxApiInvokeSpi = wxApiInvokeSpi;
        this.wxMessageProcesser = wxMessageProcesser;
    }

    public void sendMessage(WxRequest wxRequest, WxMessage wxMessage) {
        this.sendMessage(wxMessageProcesser.process(wxRequest, wxMessage));
    }

    public void sendMessage(WxRequest wxRequest, String wxMessageContent) {
        this.sendMessage(wxRequest, WxMessage.Text.builder().content(wxMessageContent).build());
    }

    public void sendMessage(WxMessage wxMessage) {
        this.wxApiInvokeSpi.sendMessage(wxMessage);
    }

}
