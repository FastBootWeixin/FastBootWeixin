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
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * fastboot-weixin  DefaultWxButtonEventKeyStrategy
 *
 * @author Guangshan
 * @date 2018/5/15 22:29
 * @since 0.6.0
 */
public class DefaultWxButtonEventKeyStrategy implements WxButtonEventKeyStrategy {
    private Map<String, Integer> nameMap = new HashMap<>();

    @Override
    public String getEventKey(WxButton wxButton) {
        if (wxButton.type() == WxButton.Type.VIEW) {
            return wxButton.url();
        }
        if (!StringUtils.isEmpty(wxButton.key())) {
            return wxButton.key();
        }
        if (wxButton.main()) {
            return wxButton.group().name();
        } else {
            String key = wxButton.group().name() + "_" + (wxButton.order().ordinal() + 1);
            if (nameMap.containsKey(key)) {
                int count = nameMap.get(key) + 1;
                nameMap.put(key, count);
                return key + "_" + count;
            } else {
                nameMap.put(key, 1);
                return key;
            }
        }
    }
}
