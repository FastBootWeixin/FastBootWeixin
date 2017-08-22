package com.mxixm.fastbootwx.web;

import com.mxixm.fastbootwx.module.user.WxUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 员工登录校验通过之后执行一些业务逻辑
 *
 * @Copyright (c) 2016, Lianjia Group All Rights Reserved.
 */
public interface WxOAuth2Callback {
    /**
     * 员工登录校验通过之后，会将当前登录的员工放在LoginExecutionContext中，</br>
     * 客户端可以进一步执行一些业务逻辑，比如根据登录员工查找权限，组织等，保存在session(已废弃，推荐保存在缓存中)中。</br>
     * <p>
     * <p>
     * 警告: <BR>
     * 新版本的SSO登录已弃用了Session，方便使用Nginx Round-Robin负载均衡，<br>
     * 也就是说，之前的员工登录之后，肯定就创建Session了，但是新版本SSO登录不再自动创建Session，<br>
     * 如果你的项目使用Session，一定要在Callback里手动创建Session，否则可能出现session为null。
     * </p>
     *
     * @param context
     * @author huisman
     * @updateAt 2017年02月21日
     * @since 2016年8月30日
     */
    void after(WxOAuth2ExecutionContext context) throws Exception;

    public final class WxOAuth2ExecutionContext {

        private WxUser wxUser;
        private HttpServletResponse response;
        private HttpServletRequest request;

        public WxOAuth2ExecutionContext(WxUser wxUser,
                                        HttpServletResponse response, HttpServletRequest request) {
            super();
            this.wxUser = wxUser;
            this.response = response;
            this.request = request;
        }

        /**
         * 当前登录用户
         *
         * @return the wxUser
         */
        public WxUser getWxUser() {
            return wxUser;
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