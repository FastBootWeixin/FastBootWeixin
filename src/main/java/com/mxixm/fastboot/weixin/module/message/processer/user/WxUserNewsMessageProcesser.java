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

package com.mxixm.fastboot.weixin.module.message.processer.user;

import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.module.message.processer.AbstractWxMessageBodyProcesser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxRedirectUtils;
import com.mxixm.fastboot.weixin.util.WxUrlUtils;

/**
 * FastBootWeixin WxGroupNewsMessageProcesser
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public class WxUserNewsMessageProcesser extends AbstractWxMessageBodyProcesser<WxMessageBody.News> {

    @Override
    protected WxMessageBody.News processBody(WxRequest wxRequest, WxMessageBody.News body) {
        body.getArticles().stream().forEach(i -> {
            i.setPicUrl(WxUrlUtils.mediaUrl(wxRequest.getRequestURL().toString(), i.getPicUrl()));
            i.setUrl(WxRedirectUtils.redirect(wxRequest.getRequestURL().toString(), i.getUrl()));
        });
        return body;
    }

}
