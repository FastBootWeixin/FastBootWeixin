package com.mxixm.fastboot.weixin.module.web.session;

import com.mxixm.fastboot.weixin.module.web.WxRequest;

public interface WxSessionIdGenerator {

    String generate(WxRequest wxRequest);

}
