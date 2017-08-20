package com.example.myproject.support;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.module.user.WxUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
