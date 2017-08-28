package com.mxixm.fastboot.weixin.web;

import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.module.token.WxTokenServer;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.util.CacheMap;

import java.util.Date;

/**
 * FastBootWeixin  WxUserManager
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxUserManager
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/22 23:34
 */
public class WxUserManager {

    private final CacheMap<String, WxWebUser> webUserCache = new CacheMap<>("WxWebUserCache", 5 * 60 * 1000);

    private final CacheMap<String, WxUser> tokenUserCache = new CacheMap<>("TokenWxUserCache", 24 * 60 * 60 * 1000);

    private final CacheMap<String, WxUser> openIdUserCache = new CacheMap<>("OpenIdWxUserCache", 24 * 60 * 60 * 1000);

    private WxTokenServer wxTokenServer;

    private WxApiInvokeSpi wxApiInvokeSpi;

    public WxUserManager(WxTokenServer wxTokenServer, WxApiInvokeSpi wxApiInvokeSpi) {
        this.wxTokenServer = wxTokenServer;
        this.wxApiInvokeSpi = wxApiInvokeSpi;
    }

    public WxWebUser getWxWebUser(String code) {
        WxWebUser wxWebUser = webUserCache.get(code);
        if (wxWebUser == null) {
            wxWebUser = wxTokenServer.getWxWebUserByCode(code);
            webUserCache.put(code, wxWebUser);
        }
        return wxWebUser;
    }

    public WxUser getWxUser(String openId) {
        WxUser wxUser = openIdUserCache.get(openId);
        if (wxUser == null) {
            wxUser = wxApiInvokeSpi.getUserInfo(openId);
            openIdUserCache.put(openId, wxUser);
        }
        return wxUser;
    }

    /**
     * 只针对未认证的情况
     * @param wxWebUser
     * @return
     */
    public WxUser getWxUserByWxWebUser(WxWebUser wxWebUser) {
        WxUser wxUser = this.getWxUser(wxWebUser.getOpenId());
        if (wxUser != null && !wxUser.getSubscribe().equals(0)) {
            return wxUser;
        }
        wxUser = tokenUserCache.get(wxWebUser.getRefreshToken());
        if (wxUser == null) {
            if (!wxTokenServer.isVerifyUserAccessToken(wxWebUser)) {
                WxWebUser newWxWebUser = wxTokenServer.getWxWebUserByRefreshToken(wxWebUser.getRefreshToken());
                wxWebUser.setAccessToken(newWxWebUser.getAccessToken());
                wxWebUser.setExpiresIn(newWxWebUser.getExpiresIn());
                wxWebUser.setCreateTime(new Date());
            }
            wxUser = wxTokenServer.getWxUserByWxWebUser(wxWebUser);
            tokenUserCache.put(wxWebUser.getRefreshToken(), wxUser);
            openIdUserCache.put(wxWebUser.getOpenId(), wxUser);
        }
        return wxUser;
    }

}
