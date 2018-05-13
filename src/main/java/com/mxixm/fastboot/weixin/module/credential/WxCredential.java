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

/**
 * fastboot-weixin  WxCredential
 * 微信凭证相关，包括AccessToken，JsTicket，CardTicket
 *
 * @author Guangshan
 * @date 2018/5/13 23:23
 * @since 0.6.0
 */
public interface WxCredential {

    /**
     * 获取有效时长
     * @return 过期时间
     */
    int getExpiresIn();

    /**
     * 获取凭证
     * @return 凭证
     */
    String getCredential();

    enum Type {
        ACCESS_TOKEN, JS_TICKET, CARD_TICKET
    }

}
