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

import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;

/**
 * FastBootWeixin WxButtonOrderCondition
 *
 * @author Guangshan
 * @date 2018-9-16 16:14:08
 * @since 0.7.0
 */
public final class WxButtonOrderCondition extends AbstractWxButtonEnumCondition<WxButton.Order> {

    public WxButtonOrderCondition(WxButton.Order... orders) {
        super(orders);
    }

    @Override
    protected WxButton.Order getMatchEnum(WxMenu.Button wxButton) {
        return wxButton.getOrder();
    }

    @Override
    protected AbstractWxEnumCondition instance(WxButton.Order... enums) {
        return new WxButtonOrderCondition(enums);
    }
}
