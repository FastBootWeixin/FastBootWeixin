package com.mxixm.fastbootwx.support;

import com.mxixm.fastbootwx.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastbootwx.module.user.WxUser;
import com.mxixm.fastbootwx.util.CacheMap;
import com.mxixm.fastbootwx.web.WxUserManager;

import java.util.HashMap;
import java.util.Map;

/**
 * FastBootWeixin  DefaultWxUserProvider
 *
 * @author Guangshan
 * @summary FastBootWeixin  DefaultWxUserProvider
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/5 21:54
 */
public class DefaultWxUserProvider implements WxUserProvider<WxUser> {

    private WxUserManager wxUserManager;

    public DefaultWxUserProvider(WxUserManager wxUserManager) {
        this.wxUserManager = wxUserManager;
    }

    @Override
    public WxUser getUser(String fromUserName) {
        return this.wxUserManager.getWxUser(fromUserName);
    }

}
