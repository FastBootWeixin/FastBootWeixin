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

import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxEnumRequestCondition
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public abstract class AbstractWxEnumCondition<T extends Enum<T>> extends AbstractWxRequestCondition<AbstractWxEnumCondition<T>> {

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

    public Set<String> getEnumStrings() {
        return this.enums.stream().map(Enum::name).collect(Collectors.toSet());
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
    public AbstractWxEnumCondition getMatchingCondition(WxRequest wxRequest) {
        T t = getMatchEnum(wxRequest);
        if (t != null && this.getEnums().contains(t)) {
            return instance(t);
        }
        return null;
    }

    @Override
    public AbstractWxEnumCondition combine(AbstractWxEnumCondition other) {
        Set<T> set = new LinkedHashSet(this.enums);
        set.addAll(other.enums);
        return instance((T[]) set.toArray());
    }

    /**
     * 获取用于匹配的目标
     * @return 返回目标枚举
     */
    protected abstract T getMatchEnum(WxRequest wxRequest);

    /**
     * 生成子类的实例
     * @param enums 构造参数
     * @return 返回实例
     */
    protected abstract AbstractWxEnumCondition instance(T... enums);

    @Override
    public int compareTo(AbstractWxEnumCondition<T> other, WxRequest wxRequest) {
        if (other.enums.size() != this.enums.size()) {
            return other.enums.size() - this.enums.size();
        } else if (this.enums.size() == 1) {
            // 以第一个元素算
            return this.enums.stream().findFirst().get().ordinal() - other.enums.stream().sorted().findFirst().map(e -> e.ordinal()).orElse(-1);
        }
        return 0;
    }

}
