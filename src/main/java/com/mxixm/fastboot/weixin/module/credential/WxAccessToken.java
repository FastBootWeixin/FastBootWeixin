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
 * FastBootWeixin WxAccessToken
 *
 * @author Guangshan
 * @date 2017/7/23 17:45
 * @since 0.1.2
 */
public class WxAccessToken implements WxCredential {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    public WxAccessToken() {
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public int getExpiresIn() {
        return this.expiresIn;
    }

    @Override
    public String getCredential() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WxAccessToken)) {
            return false;
        }
        final WxAccessToken other = (WxAccessToken) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$accessToken = this.getAccessToken();
        final Object other$accessToken = other.getAccessToken();
        if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) {
            return false;
        }
        if (this.getExpiresIn() != other.getExpiresIn()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $accessToken = this.getAccessToken();
        result = result * PRIME + ($accessToken == null ? 43 : $accessToken.hashCode());
        result = result * PRIME + this.getExpiresIn();
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof WxAccessToken;
    }

    @Override
    public String toString() {
        return "com.mxixm.fastboot.weixin.module.credential.WxAccessToken(accessToken=" + this.getAccessToken() + ", expiresIn=" + this.getExpiresIn() + ")";
    }
}
