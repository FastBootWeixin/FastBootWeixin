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

import com.mxixm.fastboot.weixin.exception.WxApiResultException;
import com.mxixm.fastboot.weixin.exception.WxException;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.service.WxBaseService;
import com.mxixm.fastboot.weixin.util.CacheMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.Objects;

/**
 * FastBootWeixin WxUserManager
 *
 * @author Guangshan
 * @date 2017/8/22 23:34
 * @since 0.1.2
 */
public class WxUserManager {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

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

    private WxBaseService wxBaseService;

    private WxApiService wxApiService;

    public WxUserManager(WxBaseService wxBaseService, WxApiService wxApiService) {
        this.wxBaseService = wxBaseService;
        this.wxApiService = wxApiService;
    }

    /**
     * 这里对code做缓存，会带来安全漏洞，其他人可以把自己的code发给别人
     * 打开方打开后session中其实不是自己的，在上面的所有操作都不是自己的，做一些充值动作，其实是给别人充值。
     * @param code
     * @return
     */
    public WxWebUser getWxWebUser(String code) {
         WxWebUser wxWebUser = null;
        try {
            wxWebUser = wxBaseService.getWxWebUserByCode(code);
        } catch (WxApiResultException e) {
            // 拦截异常，统一返回null
            logger.error(e.getErrorMessage(), e);
        }
        return wxWebUser;
    }

    public WxUser getWxUser(String openId) {
        WxUser wxUser = openIdUserCache.get(openId);
        if (wxUser == null) {
            wxUser = wxApiService.getUserInfo(openId);
            if (!Objects.equals(0, wxUser.getSubscribe())) {
                openIdUserCache.put(openId, wxUser);
            }
        }
        return wxUser;
    }

    /**
     * 只针对未认证的情况
     *
     * @param wxWebUser
     * @return the result
     */
    public WxUser getWxUserByWxWebUser(WxWebUser wxWebUser) {
        WxUser wxUser = this.getWxUser(wxWebUser.getOpenId());
        if (wxUser != null && wxUser.getSubscribe() != null && !wxUser.getSubscribe().equals(0)) {
            return wxUser;
        }
        wxUser = tokenUserCache.get(wxWebUser.getRefreshToken());
        if (wxUser == null) {
            try {
                wxUser = wxBaseService.getWxUserByWxWebUser(wxWebUser);
            } catch (WxException e) {
                WxWebUser newWxWebUser = wxBaseService.getWxWebUserByRefreshToken(wxWebUser.getRefreshToken());
                wxWebUser.setAccessToken(newWxWebUser.getAccessToken());
                wxWebUser.setExpiresIn(newWxWebUser.getExpiresIn());
                wxWebUser.setCreateTime(new Date());
                wxUser = wxBaseService.getWxUserByWxWebUser(wxWebUser);
            }
            tokenUserCache.put(wxWebUser.getRefreshToken(), wxUser);
            openIdUserCache.put(wxWebUser.getOpenId(), wxUser);
        }
        return wxUser;
    }

}
