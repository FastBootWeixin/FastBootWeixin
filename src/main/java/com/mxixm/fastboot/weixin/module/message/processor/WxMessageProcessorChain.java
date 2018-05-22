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

import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxMessageProcessorChain
 *
 * @author Guangshan
 * @date 2017/08/20 23:39
 * @since 0.1.2
 */
public class WxMessageProcessorChain implements WxMessageProcessor<WxMessage> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final List<WxMessageProcessor> wxMessageProcessors = new ArrayList<>();

    public List<WxMessageProcessor> getProcessors() {
        return Collections.unmodifiableList(this.wxMessageProcessors);
    }

    @Override
    public WxMessage process(WxMessageParameter wxMessageParameter, WxMessage wxMessage) {
        for (WxMessageProcessor processor : getSupportedProcessors(wxMessageParameter, wxMessage)) {
            wxMessage = processor.process(wxMessageParameter, wxMessage);
        }
        return wxMessage;
    }

    @Override
    public boolean supports(WxMessageParameter wxMessageParameter, WxMessage wxMessage) {
        return true;
    }

    private List<WxMessageProcessor> getSupportedProcessors(WxMessageParameter wxMessageParameter, WxMessage wxMessage) {
        return wxMessageProcessors.stream().filter(p -> p.supports(wxMessageParameter, wxMessage)).collect(Collectors.toList());
    }

    public WxMessageProcessorChain addProcessor(WxMessageProcessor processor) {
        this.wxMessageProcessors.add(processor);
        return this;
    }

    public WxMessageProcessorChain addProcessors(List<? extends WxMessageProcessor> processors) {
        if (processors != null) {
            for (WxMessageProcessor processor : processors) {
                this.wxMessageProcessors.add(processor);
            }
        }
        return this;
    }

}
