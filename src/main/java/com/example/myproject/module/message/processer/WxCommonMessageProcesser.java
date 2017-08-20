package com.example.myproject.module.message.processer;

import com.example.myproject.module.WxRequest;
import com.example.myproject.module.message.WxMessage;
import com.example.myproject.module.message.WxMessageProcesser;

/**
 * FastBootWeixin  WxCommonMessageProcesser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxCommonMessageProcesser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 22:53
 */
public class WxCommonMessageProcesser implements WxMessageProcesser<WxMessage> {

    public WxMessage process(WxRequest wxRequest, WxMessage wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        if (wxMessage.getToUserName() == null) {
            wxMessage.setToUserName(wxRequest.getFromUserName());
        }
        if (wxMessage.getFromUserName() == null) {
            wxMessage.setFromUserName(wxRequest.getToUserName());
        }
        return wxMessage;
    }

}
