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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxEnumRequestCondition
 * 最开始时，这里还有个枚举类型的泛型
 * public class WxEnumRequestCondition<T extends Enum<T>> extends AbstractWxRequestCondition<WxEnumRequestCondition<T>>
 * 此时会导致父类的T getMatchingCondition(WxRequest wxRequest)的类型推断失效，推断出的T并不是WxEnumCondition<T>而是AbstractRequestCondition
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public class WxEnumRequestCondition extends AbstractWxRequestCondition<WxEnumRequestCondition> {

    private final Set<Enum> enums;

    /**
     * 获取用于匹配的目标的函数
     */
    private Function<WxRequest, Enum> fun;

    public WxEnumRequestCondition(WxRequestCondition.Type type, Function<WxRequest, Enum> fun, Enum... enums) {
        this(type, fun, convertContent(enums));
    }

    protected WxEnumRequestCondition(WxRequestCondition.Type type, Function<WxRequest, Enum> fun, Collection<Enum> enums) {
        super(type);
        this.fun = fun;
        this.enums = Collections.unmodifiableSet(new LinkedHashSet<>(enums));
    }

    public Set<Enum> getEnums() {
        return this.enums;
    }

    public Set<String> getEnumStrings() {
        return this.enums.stream().map(Enum::name).collect(Collectors.toSet());
    }

    @Override
    protected Collection<Enum> getContent() {
        return this.enums;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    @Override
    public WxEnumRequestCondition getMatchingCondition(WxRequest wxRequest) {
        if (this.isEmpty()) {
            return this;
        }
        if (fun == null) {
            return null;
        }
        Enum e = fun.apply(wxRequest);
        if (e != null && this.getEnums().contains(e)) {
            return new WxEnumRequestCondition(this.type, fun, e);
        }
        return null;
    }

    @Override
    public WxEnumRequestCondition combine(WxEnumRequestCondition other) {
        Set<Enum> set = new LinkedHashSet(this.enums);
        set.addAll(other.enums);
        return new WxEnumRequestCondition(this.type, fun, set.toArray(new Enum[0]));
    }

    @Override
    public int compareTo(WxEnumRequestCondition other, WxRequest wxRequest) {
        if (other.enums.size() != this.enums.size()) {
            return other.enums.size() - this.enums.size();
        } else if (this.enums.size() == 1) {
            // 以第一个元素算
            return this.enums.stream().findFirst().get().ordinal() - other.enums.stream().sorted().findFirst().map(e -> e.ordinal()).orElse(-1);
        }
        return 0;
    }

}
