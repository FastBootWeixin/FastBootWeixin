package com.mxixm.fastboot.weixin.module.message.processer;

import com.mxixm.fastboot.weixin.module.WxRequest;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.util.WxRedirectUtils;
import com.mxixm.fastboot.weixin.util.WxUrlUtils;

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
        wxMessage.getBody().getArticles().stream().forEach(i -> {
            i.setPicUrl(WxUrlUtils.mediaUrl(wxRequest.getRequestUrl().toString(), i.getPicUrl()));
            i.setUrl(WxRedirectUtils.redirect(wxRequest.getRequestUrl().toString(), i.getUrl()));
        });
        return wxMessage;
    }

}
