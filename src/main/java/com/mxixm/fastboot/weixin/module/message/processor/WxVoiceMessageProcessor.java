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

import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;

/**
 * FastBootWeixin WxGroupVoiceMessageProcessor
 *
 * @author Guangshan
 * @date 2017/8/20 22:53
 * @since 0.1.2
 */
public class WxVoiceMessageProcessor extends AbstractWxMediaMessageProcessor<WxMessageBody.Voice> {

    public WxVoiceMessageProcessor(WxMediaManager wxMediaManager) {
        super(wxMediaManager);
    }

    @Override
    protected WxMessageBody.Voice processBody(WxMessageParameter wxMessageParameter, WxMessageBody.Voice body) {
        super.processBody(wxMessageParameter, body);
        return body;
    }

}
