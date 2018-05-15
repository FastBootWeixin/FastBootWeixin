/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.module.menu;

import com.mxixm.fastboot.weixin.annotation.WxButton;

/**
 * fastboot-weixin  WxButtonEventKeyStrategy
 *
 * @author Guangshan
 * @date 2018/5/15 22:28
 * @since 0.6.0
 */
public interface WxButtonEventKeyStrategy {

    /**
     * eventKey生成策略
     * @param wxButton
     * @return
     */
    String getEventKey(WxButton wxButton);
}
