package com.mxixm.fastboot.weixin.module.web.session;

import com.mxixm.fastboot.weixin.module.web.WxRequest;

public class DefaultWxSessionIdGenerator implements WxSessionIdGenerator {

    public String generate(WxRequest wxRequest) {
        return wxRequest.getBody().getFromUserName();
    }

}
