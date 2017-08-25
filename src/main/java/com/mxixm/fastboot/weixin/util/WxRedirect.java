package com.mxixm.fastboot.weixin.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FastBootWeixin  WxRedirect
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxRedirect
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/23 23:38
 */
@Getter
@AllArgsConstructor
public class WxRedirect {

    /**
     * 最后会重定向到redirect_uri/?code=CODE&state=STATE上面
     * baseUrl 请求地址
     */
    private String baseUrl;

    /**
     * url 要跳转的地址
     */
    private String url;

    /**
     * state 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     */
    private String state;

    /**
     * isBase 应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），
     * snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息）
     */
    private boolean isBase;

    public static class Builder {

        private String baseUrl;
        private String url;
        private String state;
        private boolean isBase;

        Builder() {
        }

        Builder(String baseUrl, String url, String state, boolean isBase) {
            this.baseUrl = baseUrl;
            this.url = url;
            this.state = state;
            this.isBase = isBase;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder isBase(boolean isBase) {
            this.isBase = isBase;
            return this;
        }

        public WxRedirect build() {
            return new WxRedirect(baseUrl, url, state, isBase);
        }

    }
}
