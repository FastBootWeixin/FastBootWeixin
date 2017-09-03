package com.mxixm.fastboot.weixin.module.web.session;

import com.mxixm.fastboot.weixin.module.web.WxRequest;

/**
 * 其实只用暴露getWxSession就可以了
 */
public interface WxSessionManager {

    WxSession createWxSession(WxRequest wxRequest);

    WxSession getWxSession(WxRequest wxRequest);

    WxSession getWxSession(WxRequest wxRequest, boolean create);

    void removeWxSession(WxSession wxSession);

}
