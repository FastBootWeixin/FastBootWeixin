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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * fastboot-weixin  WxTicket
 * 包括WxJsApiTicket和WxCardApiTicket
 *
 * @author Guangshan
 * @date 2018/5/7 22:16
 * @since 0.6.0
 */
public class WxTicket implements WxCredential {

    @JsonProperty("ticket")
    private String ticket;

    @JsonProperty("expires_in")
    private int expiresIn;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public int getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getCredential() {
        return ticket;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public enum Type {
        JS_API {
            @Override
            public String toString() {
                return "jsapi";
            }
        },
        WX_CARD {
            @Override
            public String toString() {
                return "wx_card";
            }
        }
    }

}
