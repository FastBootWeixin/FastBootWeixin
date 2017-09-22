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

package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * FastBootWeixin WxCategoryCondition
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public final class WxCategoryCondition extends AbstractWxEnumCondition<Wx.Category> {

    public WxCategoryCondition(Wx.Category... types) {
        super(types);
    }

    protected WxCategoryCondition(Collection<Wx.Category> types) {
        super(Collections.unmodifiableSet(new LinkedHashSet<>(types)));
    }

    @Override
    public WxCategoryCondition combine(AbstractWxEnumCondition other) {
        Set<Wx.Category> set = new LinkedHashSet(this.enums);
        set.addAll(other.enums);
        return new WxCategoryCondition(set);
    }

    @Override
    protected WxCategoryCondition matchEnum(WxRequest.Body wxRequestBody) {
        Wx.Category wxCategory = wxRequestBody.getCategory();
        if (getEnums().contains(wxCategory)) {
            return new WxCategoryCondition(wxCategory);
        }
        return null;
    }

}
