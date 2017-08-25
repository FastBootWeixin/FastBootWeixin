package com.mxixm.fastboot.weixin.support;

import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.web.WxUserManager;

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
