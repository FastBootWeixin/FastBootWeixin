/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.module.message.processer;

import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxRedirectUtils;
import com.mxixm.fastboot.weixin.util.WxUrlUtils;

public class WxNewsMessageProcesser implements WxMessageProcesser<WxMessage.News> {

    public WxMessage.News process(WxRequest wxRequest, WxMessage.News wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        wxMessage.getBody().getArticles().stream().forEach(i -> {
            i.setPicUrl(WxUrlUtils.mediaUrl(wxRequest.getRequestURL().toString(), i.getPicUrl()));
            i.setUrl(WxRedirectUtils.redirect(wxRequest.getRequestURL().toString(), i.getUrl()));
        });
        return wxMessage;
    }

}
