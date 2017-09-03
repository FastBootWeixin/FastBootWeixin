package com.mxixm.fastboot.weixin.module.message.processer;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;

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
        // 这个重复逻辑可以使用processInternal处理
        if (wxMessage == null) {
            return wxMessage;
        }
        if (wxMessage.getToUserName() == null) {
            wxMessage.setToUserName(wxRequest.getBody().getFromUserName());
        }
        if (wxMessage.getFromUserName() == null) {
            wxMessage.setFromUserName(wxRequest.getBody().getToUserName());
        }
        return wxMessage;
    }

}
