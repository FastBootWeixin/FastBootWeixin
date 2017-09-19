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

import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxUrlUtils;

import java.io.File;

public class WxVideoMessageProcesser extends AbstractWxMediaMessageProcesser<WxMessage.Video> {

    public WxVideoMessageProcesser(WxMediaManager wxMediaManager) {
        super(wxMediaManager);
    }

    public WxMessage.Video process(WxRequest wxRequest, WxMessage.Video wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        processBody(wxRequest, wxMessage.getBody());
        processVideoBody(wxRequest, wxMessage.getBody());
        return wxMessage;
    }

    protected WxMessage.Video.Body processVideoBody(WxRequest wxRequest, WxMessage.Video.Body body) {
        if (body.getThumbMediaId() == null) {
            // 优先使用path
            if (body.getThumbMediaPath() != null) {
                String mediaId = wxMediaManager.addTempMedia(WxMedia.Type.IMAGE, new File(body.getThumbMediaPath()));
                body.setMediaId(mediaId);
            } else if (body.getThumbMediaUrl() != null) {
                String url = WxUrlUtils.mediaUrl(wxRequest.getRequestURL().toString(), body.getThumbMediaUrl());
                String mediaId = wxMediaManager.addTempMediaByUrl(WxMedia.Type.IMAGE, url);
                body.setMediaId(mediaId);
            }
        }
        return body;
    }

}
