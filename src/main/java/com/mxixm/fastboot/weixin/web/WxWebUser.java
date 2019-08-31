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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * FastBootWeixin WxWebUser
 *
 * @author Guangshan
 * @date 2017/09/21 23:47
 * @since 0.1.2
 */
public class WxWebUser implements Serializable {

    /**
     * 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * access_token接口调用凭证超时时间，单位（秒）
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * 用户刷新access_token
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     */
    @JsonProperty("openid")
    private String openId;

    /**
     * 用户授权的作用域，使用逗号（,）分隔
     */
    @JsonProperty("scope")
    private String scope;

    /**
     * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    @JsonProperty("unionid")
    private String unionId;

    @JsonIgnore
    private Date createTime = new Date();

    public WxWebUser() {
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public Integer getExpiresIn() {
        return this.expiresIn;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getOpenId() {
        return this.openId;
    }

    public String getScope() {
        return this.scope;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WxWebUser wxWebUser = (WxWebUser) o;
        return Objects.equals(getAccessToken(), wxWebUser.getAccessToken()) &&
                Objects.equals(getExpiresIn(), wxWebUser.getExpiresIn()) &&
                Objects.equals(getRefreshToken(), wxWebUser.getRefreshToken()) &&
                Objects.equals(getOpenId(), wxWebUser.getOpenId()) &&
                Objects.equals(getScope(), wxWebUser.getScope()) &&
                Objects.equals(getUnionId(), wxWebUser.getUnionId()) &&
                Objects.equals(getCreateTime(), wxWebUser.getCreateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccessToken(), getExpiresIn(), getRefreshToken(), getOpenId(), getScope(), getUnionId(), getCreateTime());
    }

    @Override
    public String toString() {
        return "WxWebUser{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", openId='" + openId + '\'' +
                ", scope='" + scope + '\'' +
                ", unionId='" + unionId + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
