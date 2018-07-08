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

import com.mxixm.fastboot.weixin.service.WxBaseService;
import org.springframework.beans.factory.InitializingBean;

/**
 * FastBootWeixin WxTokenManager
 * 暂时没有定时任务，懒获取
 *
 * @author Guangshan
 * @date 2017/7/23 18:26
 * @since 0.1.2
 */
public class WxTokenManager extends AbstractWxCredentialManager implements InitializingBean {

    private WxBaseService wxBaseService;

    public WxTokenManager(WxBaseService wxBaseService, WxTokenStore wxTokenStore) {
        super(WxCredential.Type.ACCESS_TOKEN, wxTokenStore);
        this.wxBaseService = wxBaseService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.refresh();
    }

    @Override
    protected WxCredential refreshInternal() {
        return wxBaseService.refreshToken();
    }
}
