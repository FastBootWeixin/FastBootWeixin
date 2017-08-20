package com.example.myproject.module.message.processer;

import com.example.myproject.module.WxRequest;
import com.example.myproject.module.media.WxMedia;
import com.example.myproject.module.media.WxMediaManager;
import com.example.myproject.module.message.WxMessage;
import com.example.myproject.module.message.WxMessageProcesser;

import java.io.File;

/**
 * FastBootWeixin  WxCommonMessageProcesser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxCommonMessageProcesser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 22:53
 */
public class WxVoiceMessageProcesser extends AbstractWxMediaMessageProcesser<WxMessage.Voice> {

    public WxVoiceMessageProcesser(WxMediaManager wxMediaManager) {
        super(wxMediaManager);
    }

    public WxMessage.Voice process(WxRequest wxRequest, WxMessage.Voice wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        processBody(wxRequest, wxMessage.getBody());
        return wxMessage;
    }

}
