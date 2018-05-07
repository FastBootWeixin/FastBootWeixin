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

package com.mxixm.fastboot.weixin.support;

import com.mxixm.fastboot.weixin.module.js.WxJsApi;
import com.mxixm.fastboot.weixin.module.js.WxJsConfig;
import com.mxixm.fastboot.weixin.module.ticket.WxTicket;
import com.mxixm.fastboot.weixin.service.WxApiService;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.*;

/**
 * FastBootWeixin WxJsTicketManager
 * 暂时没有定时任务，懒获取
 *
 * @author Guangshan
 * @date 2018-5-7 23:35:38
 * @since 0.6.0
 */
public class WxJsTicketManager {

    private String appId;

    private WxJsTicketStore wxJsTicketStore;

    private WxApiService wxApiService;

    /**
     * 守护线程timer
     */
    private static ExecutorService executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1), Executors.defaultThreadFactory());

    public WxJsTicketManager(String appId, WxApiService wxApiService, WxJsTicketStore wxJsTicketStore) {
        this.appId = appId;
        this.wxApiService = wxApiService;
        this.wxJsTicketStore = wxJsTicketStore;
    }

    /**
     * token的冗余时间
     */
    @Value("${wx.verify.ticket.redundance:10000}")
    private int ticketRedundance;

    private String refrestToken() {
        long now = Instant.now().toEpochMilli();
        if (this.wxJsTicketStore.lock()) {
            try {
                // 拿到锁之后再判断一次过期时间，如果过期的话视为还没刷新
                if (wxJsTicketStore.getExpireTime() < now) {
                    WxTicket wxTicket = wxApiService.getTicket(WxTicket.Type.jsapi);
                    wxJsTicketStore.setTicket(wxTicket.getTicket(), now + wxTicket.getExpiresIn() * 1000);
                    return wxTicket.getTicket();
                }
            } finally {
                // 如果加锁成功了，一定要解锁
                wxJsTicketStore.unlock();
            }
        } else {
            // 加锁失败，直接获取当前token
            // TODO: 2017/7/23 考虑一个更完善的方案，这个方案可能是有问题的
            // 因为如果此时获取了旧的token，但是如果旧的token失效了，那么此时请求会失败
            // 如果设置了请求token失败时重新获取的策略，很有可能造成线程阻塞。
        }
        return wxJsTicketStore.getTicket();
    }

    public String getTicket() {
        long now = Instant.now().toEpochMilli();
        long expireTime = wxJsTicketStore.getExpireTime();
        // 如果当前仍在有效期，但是在刷新期内，异步刷新，并返回当前的值
        if (now <= expireTime && expireTime <= now - ticketRedundance) {
            executor.execute(() -> this.refrestToken());
            return this.wxJsTicketStore.getTicket();
        } else if (expireTime < now) {
            return this.refrestToken();
        }
        return this.wxJsTicketStore.getTicket();
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
                .ticket(this.getTicket()).build();
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
