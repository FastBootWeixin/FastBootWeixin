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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FastBootWeixin WxOAuth2Callback
 * 登录校验通过之后执行一些业务逻辑
 *
 * @author Guangshan
 * @date 2017/09/21 23:46
 * @since 0.1.2
 */
public interface WxOAuth2Callback {

    void after(WxOAuth2Context context) throws Exception;

    final class WxOAuth2Context {

        private WxWebUser wxWebUser;
        private String state;
        private HttpServletResponse response;
        private HttpServletRequest request;

        public WxOAuth2Context(WxWebUser wxWebUser, String state,
                               HttpServletResponse response, HttpServletRequest request) {
            super();
            this.state = state;
            this.wxWebUser = wxWebUser;
            this.response = response;
            this.request = request;
        }

        /**
         * 当前登录用户
         *
         * @return the wxWebUser
         */
        public WxWebUser getWxWebUser() {
            return wxWebUser;
        }

        public String getState() {
            return state;
        }

        /**
         * http response
         *
         * @return the response
         */
        public HttpServletResponse getResponse() {
            return response;
        }

        /**
         * http request
         *
         * @return the request
         */
        public HttpServletRequest getRequest() {
            return request;
        }
    }
}