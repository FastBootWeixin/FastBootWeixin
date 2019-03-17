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
import com.mxixm.fastboot.weixin.util.WildcardUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxWildcardRequestCondition
 *
 * @author Guangshan
 * @date 2018-9-16 17:14:02
 * @since 0.7.0
 */
public class WxWildcardRequestCondition extends AbstractWxRequestCondition<WxWildcardRequestCondition> {

    private Collection<String> wildcards;

    private Function<WxRequest, String> fun;

    public WxWildcardRequestCondition(WxRequestCondition.Type type, Function<WxRequest, String> fun, String... wildcards) {
        this(type, fun, convertContent(wildcards));
    }

    public WxWildcardRequestCondition(WxRequestCondition.Type type, Function<WxRequest, String> fun, Collection<String> wildcards) {
        super(type);
        this.fun = fun;
        this.wildcards = Collections.unmodifiableCollection(wildcards);
    }

    @Override
    protected Collection<String> getContent() {
        return wildcards;
    }

    public Collection<String> getWildcards() {
        return this.wildcards;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    @Override
    public WxWildcardRequestCondition combine(WxWildcardRequestCondition other) {
        Assert.isTrue(this.getType() == other.getType(), "合并条件类型必须相同");
        List<String> result = new ArrayList<>();
        result.addAll(this.wildcards);
        result.addAll(other.wildcards);
        return new WxWildcardRequestCondition(this.type, fun, result.toArray(new String[0]));
    }

    @Override
    public WxWildcardRequestCondition getMatchingCondition(WxRequest wxRequest) {
        if (this.isEmpty()) {
            return this;
        }
        String text = fun.apply(wxRequest);
        if (text == null) {
            return null;
        }
        // todo 字符前后有空格，暂时这么处理，待确定原因
        List<String> matches = wildcards.stream().filter(w -> WildcardUtils.wildcardMatch(text.trim(), w)).collect(Collectors.toList());
        return matches.isEmpty() ? null : new WxWildcardRequestCondition(this.type, fun, matches.toArray(new String[0]));
    }

    /**
     * 拿通配符的长度判断，以后可以加入权重系统，判断通配符长度
     *
     * @param other
     * @param wxRequest
     * @return the result
     */
    @Override
    public int compareTo(WxWildcardRequestCondition other, WxRequest wxRequest) {
        Assert.isTrue(this.getType() == other.getType(), "比较类型必须相同");
        int thisMax = this.wildcards.stream().sorted(Comparator.comparing(String::length).reversed()).findFirst().orElse("").length();
        int thatMax = other.wildcards.stream().sorted(Comparator.comparing(String::length).reversed()).findFirst().orElse("").length();
        return thatMax - thisMax;
    }

}
