package com.example.myproject.module.message.processer;

import com.example.myproject.module.WxRequest;
import com.example.myproject.module.media.WxMediaManager;
import com.example.myproject.module.message.WxMessage;

/**
 * FastBootWeixin  WxCommonMessageProcesser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxCommonMessageProcesser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 22:53
 */
public class WxMusicMessageProcesser extends AbstractWxMediaMessageProcesser<WxMessage.Music> {

    public WxMusicMessageProcesser(WxMediaManager wxMediaManager) {
        super(wxMediaManager);
    }

    public WxMessage.Music process(WxRequest wxRequest, WxMessage.Music wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        processBody(wxRequest, wxMessage.getBody());
        return wxMessage;
    }

}
