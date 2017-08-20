package com.example.myproject.module.message.processer;

import com.example.myproject.module.WxRequest;
import com.example.myproject.module.media.WxMediaManager;
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
public class WxNewsMessageProcesser implements WxMessageProcesser<WxMessage.News> {

    public WxMessage.News process(WxRequest wxRequest, WxMessage.News wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        wxMessage.getBody().getArticles().stream().forEach(i -> i.setPicUrl(WxMediaUrlUtils.processUrl(wxRequest.getRequestUrl().toString(), i.getPicUrl())));
        return wxMessage;
    }

}
