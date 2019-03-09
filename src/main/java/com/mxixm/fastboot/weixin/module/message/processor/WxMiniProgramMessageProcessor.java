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
 * FastBootWeixin WxMiniProgramMessageProcessor
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public class WxMiniProgramMessageProcessor extends AbstractWxMessageBodyProcessor<WxMessageBody.MiniProgram> {

    protected WxMediaManager wxMediaManager;

    public WxMiniProgramMessageProcessor(WxMediaManager wxMediaManager) {
        this.wxMediaManager = wxMediaManager;
    }

    @Override
    protected WxMessageBody.MiniProgram processBody(WxMessageParameter WxMessageParameter, WxMessageBody.MiniProgram body) {
        processMiniProgramBody(WxMessageParameter, body);
        return body;
    }

    protected WxMessageBody.MiniProgram processMiniProgramBody(WxMessageParameter WxMessageParameter, WxMessageBody.MiniProgram body) {
        // 为了避免重复代码提示，两个地方用了相同的逻辑的不同写法，todo 待优化
        if (body.getThumbMediaId() == null) {
            // 优先使用path
            if (body.getThumbMediaPath() != null) {
                String thumbMediaId = wxMediaManager.addTempMedia(WxMedia.Type.IMAGE, new FileSystemResource(body.getThumbMediaPath()));
                body.setThumbMediaId(thumbMediaId);
            } else if (body.getThumbMediaUrl() != null) {
                String url = WxUrlUtils.absoluteUrl(WxMessageParameter.getRequestUrl(), body.getThumbMediaUrl());
                String thumbMediaId = wxMediaManager.addTempMediaByUrl(WxMedia.Type.IMAGE, url);
                body.setThumbMediaId(thumbMediaId);
            }
        }
        return body;
    }

}
