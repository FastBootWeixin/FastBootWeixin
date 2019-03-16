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

package com.mxixm.fastboot.weixin.mvc.method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin WxMappingInfo
 * 此类有大量重复代码，还有可优化余地
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public class WxMappingInfos extends WxMappingInfo {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private WxMappingInfo[] wxMappingInfos;

    public WxMappingInfos(WxMappingInfo... wxMappingInfos) {
        super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        this.wxMappingInfos = wxMappingInfos;
    }

    public WxMappingInfo[] getWxMappingInfos() {
        return wxMappingInfos;
    }
}
