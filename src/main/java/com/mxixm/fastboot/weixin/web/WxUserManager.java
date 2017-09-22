/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.web;

import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.module.token.WxTokenServer;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.util.CacheMap;

import java.util.Date;

/**
 * FastBootWeixin WxUserManager
 *
 * @author Guangshan
 * @date 2017/8/22 23:34
 * @since 0.1.2
 */
public class WxUserManager {

    private final CacheMap<String, WxWebUser> webUserCache = CacheMap.<String, WxWebUser>builder()
            .cacheName("WxWebUserCache")
            .cacheTimeout(5 * 60 * 1000)
            .refreshOnRead().build();

    private final CacheMap<String, WxUser> tokenUserCache = CacheMap.<String, WxUser>builder()
            .cacheName("TokenWxUserCache")
            .cacheTimeout(24 * 60 * 60 * 1000)
            .refreshOnRead().build();

    private final CacheMap<String, WxUser> openIdUserCache = CacheMap.<String, WxUser>builder()
            .cacheName("OpenIdWxUserCache")
            .cacheTimeout(24 * 60 * 60 * 1000)
            .refreshOnRead().build();

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
     *
     * @param wxWebUser
     * @return dummy
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
