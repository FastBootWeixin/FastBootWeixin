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

package com.mxixm.fastboot.weixin.test;

import com.mxixm.fastboot.weixin.web.WxOAuth2Callback;
import org.springframework.stereotype.Service;

/**
 * fastboot-weixin  WxOAuth2CallbackImpl
 *
 * @author Guangshan
 * @date 2017/10/18 21:32
 * @since 0.2.1
 */
@Service
public class WxOAuth2CallbackImpl implements WxOAuth2Callback {
    @Override
    public void after(WxOAuth2Context context) throws Exception {
        System.out.println(context.getWxWebUser());
    }
}
