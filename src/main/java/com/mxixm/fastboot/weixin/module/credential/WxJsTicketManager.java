/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.module.credential;

import com.mxixm.fastboot.weixin.module.js.WxJsApi;
import com.mxixm.fastboot.weixin.module.js.WxJsConfig;
import com.mxixm.fastboot.weixin.service.WxApiService;

import java.util.Random;

/**
 * FastBootWeixin WxJsTicketManager
 * 暂时没有定时任务，懒获取
 *
 * @author Guangshan
 * @date 2018-5-7 23:35:38
 * @since 0.6.0
 */
public class WxJsTicketManager extends AbstractWxCredentialManager {

    private String appId;

    private WxApiService wxApiService;

    public WxJsTicketManager(String appId, WxApiService wxApiService, WxJsTicketStore wxJsTicketStore) {
        super(wxJsTicketStore);
        this.appId = appId;
        this.wxApiService = wxApiService;
    }

    @Override
    public WxCredential refreshInternal() {
        return wxApiService.getTicket(WxTicket.Type.JS_API);
    }

    public WxJsConfig getWxJsConfig(String url, WxJsApi... wxJsApis) {
        return getWxJsConfig(false, url, wxJsApis);
    }

    public WxJsConfig getWxJsConfig(boolean debug, String url, WxJsApi... wxJsApis) {
        return WxJsConfig.builder()
                .appId(appId)
                .debug(debug)
                .nonceStr(generateNonce())
                .timestamp(getTimestamp())
                .jsApiList(wxJsApis)
                .url(url)
                .ticket(this.get()).build();
    }

    public WxJsConfig getWxJsConfig(String url, String... wxJsApis) {
        return this.getWxJsConfig(false, url, wxJsApis);
    }

    public WxJsConfig getWxJsConfig(boolean debug, String url, String... wxJsApis) {
        return WxJsConfig.builder()
                .appId(appId)
                .debug(debug)
                .nonceStr(generateNonce())
                .timestamp(getTimestamp())
                .jsApiList(wxJsApis)
                .url(url)
                .ticket(this.get()).build();
    }

    private final static String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 这两个方法单独抽出来一个类
     */
    private String generateNonce() {
        Random random = new Random();//随机类初始化
        StringBuffer sb = new StringBuffer();//StringBuffer类生成，为了拼接字符串
        for (int i = 0; i < 10; ++i) {
            int number = random.nextInt(62);// [0,62)
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
