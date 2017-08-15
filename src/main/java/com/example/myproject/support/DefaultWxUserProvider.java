package com.example.myproject.support;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.module.user.WxUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public DefaultWxUserProvider(WxApiInvokeSpi wxApiInvokeSpi) {
        this.wxApiInvokeSpi = wxApiInvokeSpi;
    }

    @Override
    public WxUser getUser(String fromUserName) {
        return wxApiInvokeSpi.getUserInfo(fromUserName);
    }

}
