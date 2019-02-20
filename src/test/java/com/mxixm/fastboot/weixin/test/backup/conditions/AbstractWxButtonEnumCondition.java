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

package com.mxixm.fastboot.weixin.test.backup.conditions;

import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.web.WxRequest;

/**
 * FastBootWeixin AbstractWxButtonEnumCondition
 *
 * @author Guangshan
 * @date 2018-9-16 17:09:16
 * @since 0.7.0
 */
public abstract class AbstractWxButtonEnumCondition<T extends Enum<T>> extends AbstractWxEnumCondition<T> {

    public AbstractWxButtonEnumCondition(T... enums) {
        super(enums);
    }

    /**
     * 获取用于匹配的目标
     * @return 返回目标枚举
     */
    @Override
    protected T getMatchEnum(WxRequest wxRequest) {
        if (wxRequest.getButton() != null) {
            return getMatchEnum(wxRequest.getButton());
        }
        return null;
    }

    /**
     * 获取用于匹配的目标
     * @param wxButton 微信按钮
     * @return 返回目标枚举
     */
    protected abstract T getMatchEnum(WxMenu.Button wxButton);

}
