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

package com.mxixm.fastboot.weixin.module.message.processer;

import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxMessageProcesseChain
 *
 * @author Guangshan
 * @date 2017/08/20 23:39
 * @since 0.1.2
 */
public class WxMessageProcesseChain implements WxMessageProcesser<WxMessage> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final List<WxMessageProcesser> wxMessageProcessers = new ArrayList<>();

    public List<WxMessageProcesser> getProcessers() {
        return Collections.unmodifiableList(this.wxMessageProcessers);
    }

    @Override
    public WxMessage process(WxRequest wxRequest, WxMessage wxMessage) {
        for (WxMessageProcesser processer : getSupportedProcessers(wxRequest, wxMessage)) {
            wxMessage = processer.process(wxRequest, wxMessage);
        }
        return wxMessage;
    }

    @Override
    public boolean supports(WxRequest wxRequest, WxMessage wxMessage) {
        return true;
    }

    private List<WxMessageProcesser> getSupportedProcessers(WxRequest wxRequest, WxMessage wxMessage) {
        return wxMessageProcessers.stream().filter(p -> p.supports(wxRequest, wxMessage)).collect(Collectors.toList());
    }

    public WxMessageProcesseChain addProcesser(WxMessageProcesser processer) {
        this.wxMessageProcessers.add(processer);
        return this;
    }

    public WxMessageProcesseChain addProcessers(List<? extends WxMessageProcesser> processers) {
        if (processers != null) {
            for (WxMessageProcesser processer : processers) {
                this.wxMessageProcessers.add(processer);
            }
        }
        return this;
    }

}
