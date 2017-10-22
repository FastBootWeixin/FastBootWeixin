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

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * FastBootWeixin AbstractWxEnumCondition
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public abstract class AbstractWxEnumCondition<T extends Enum<T>> extends AbstractRequestCondition<AbstractWxEnumCondition<T>> {

    protected final Set<T> enums;

    public AbstractWxEnumCondition(T... enums) {
        this(enums != null ? Arrays.asList(enums) : Collections.emptyList());
    }

    protected AbstractWxEnumCondition(Collection<T> enums) {
        this.enums = Collections.unmodifiableSet(new LinkedHashSet<>(enums));
    }

    public Set<T> getEnums() {
        return this.enums;
    }

    @Override
    protected Collection<T> getContent() {
        return this.enums;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    @Override
    public abstract AbstractWxEnumCondition combine(AbstractWxEnumCondition other);

    @Override
    public AbstractWxEnumCondition getMatchingCondition(HttpServletRequest request) {
        return matchEnum(WxWebUtils.getWxRequestBodyFromRequest(request));
    }

    protected abstract AbstractWxEnumCondition matchEnum(WxRequest.Body wxRequestBody);

    @Override
    public int compareTo(AbstractWxEnumCondition<T> other, HttpServletRequest request) {
        if (other.enums.size() != this.enums.size()) {
            return other.enums.size() - this.enums.size();
        } else if (this.enums.size() == 1) {
            // 以第一个元素算
            return this.enums.stream().findFirst().get().ordinal() - other.enums.stream().sorted().findFirst().map(e -> e.ordinal()).orElse(-1);
        }
        return 0;
    }

}
