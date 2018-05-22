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

package com.mxixm.fastboot.weixin.module.message.processor;

import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;
import com.mxixm.fastboot.weixin.util.WxUrlUtils;
import org.springframework.core.io.FileSystemResource;

/**
 * FastBootWeixin WxGroupVideoMessageProcessor
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public class WxVideoMessageProcessor extends AbstractWxMediaMessageProcessor<WxMessageBody.Video> {

    public WxVideoMessageProcessor(WxMediaManager wxMediaManager) {
        super(wxMediaManager);
    }

    @Override
    protected WxMessageBody.Video processBody(WxMessageParameter WxMessageParameter, WxMessageBody.Video body) {
        super.processBody(WxMessageParameter, body);
        processVideoBody(WxMessageParameter, body);
        return body;
    }

    protected WxMessageBody.Video processVideoBody(WxMessageParameter WxMessageParameter, WxMessageBody.Video body) {
        if (body.getThumbMediaId() == null) {
            // 优先使用path
            if (body.getThumbMediaPath() != null) {
                String mediaId = wxMediaManager.addTempMedia(WxMedia.Type.IMAGE, new FileSystemResource(body.getThumbMediaPath()));
                body.setMediaId(mediaId);
            } else if (body.getThumbMediaUrl() != null) {
                String url = WxUrlUtils.mediaUrl(WxMessageParameter.getRequestUrl(), body.getThumbMediaUrl());
                String mediaId = wxMediaManager.addTempMediaByUrl(WxMedia.Type.IMAGE, url);
                body.setMediaId(mediaId);
            }
        }
        return body;
    }

}
