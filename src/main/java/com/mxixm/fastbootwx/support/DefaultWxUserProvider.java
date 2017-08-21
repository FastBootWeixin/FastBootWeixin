package com.mxixm.fastbootwx.support;

import com.mxixm.fastbootwx.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastbootwx.module.user.WxUser;

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

    private WxApiInvokeSpi wxApiInvokeSpi;

    private Map<String, WxUser> cache = new HashMap<>();

    public DefaultWxUserProvider(WxApiInvokeSpi wxApiInvokeSpi) {
        this.wxApiInvokeSpi = wxApiInvokeSpi;
    }

    @Override
    public WxUser getUser(String fromUserName) {
        WxUser userInfo = cache.get(fromUserName);
        if (userInfo == null) {
            userInfo = wxApiInvokeSpi.getUserInfo(fromUserName);
            cache.put(fromUserName, userInfo);
        }
        return userInfo;
    }

}
