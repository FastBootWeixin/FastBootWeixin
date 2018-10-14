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

package com.mxixm.fastboot.weixin.module.message;

import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;

/**
 * FastBootWeixin WxTemplateMessageProcessor
 *
 * @author Guangshan
 * @date 2017-10-13 11:17:00
 * @since 0.2.1
 */
public class WxTemplateMessageProcessor implements WxMessageProcessor<WxTemplateMessage> {

    @Override
    public WxTemplateMessage process(WxMessageParameter wxMessageParameter, WxTemplateMessage wxMessage) {
        // 这个重复逻辑可以使用processInternal处理
        if (wxMessage == null) {
            return null;
        }
        if (wxMessage.getToUser() == null) {
            wxMessage.setToUser(wxMessageParameter.getToUser());
        }
        return wxMessage;
    }

}
