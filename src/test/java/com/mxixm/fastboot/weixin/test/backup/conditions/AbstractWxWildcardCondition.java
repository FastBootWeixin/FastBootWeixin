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
import com.mxixm.fastboot.weixin.util.WildcardUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxWildcardRequestCondition
 *
 * @author Guangshan
 * @date 2018-9-16 17:14:02
 * @since 0.7.0
 */
public abstract class AbstractWxWildcardCondition extends AbstractWxRequestCondition<AbstractWxWildcardCondition> {

    private Collection<String> wildcards;

    public AbstractWxWildcardCondition(String... wildcards) {
        this(Collections.unmodifiableCollection(wildcards != null ? Arrays.asList(wildcards) : Collections.emptyList()));
    }

    public AbstractWxWildcardCondition(Collection<String> wildcards) {
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
    public AbstractWxWildcardCondition combine(AbstractWxWildcardCondition other) {
        List<String> result = new ArrayList<>();
        result.addAll(this.wildcards);
        result.addAll(other.wildcards);
        return instance(result.toArray(new String[0]));
    }

    /**
     * 生成子类的实例
     * @param wildcards 构造参数
     * @return 返回实例
     */
    protected abstract AbstractWxWildcardCondition instance(String... wildcards);

    /**
     * 获取用于匹配的目标
     * @param wxRequest 微信请求
     * @return 返回目标文本
     */
    protected abstract String getMatchText(WxRequest wxRequest);

    @Override
    public AbstractWxWildcardCondition getMatchingCondition(WxRequest wxRequest) {
        String text = getMatchText(wxRequest);
        if (this.wildcards.isEmpty() || text == null) {
            return null;
        }
        List<String> matches = wildcards.stream().filter(w -> WildcardUtils.wildcardMatch(text, w)).collect(Collectors.toList());
        return matches.isEmpty() ? null : instance(matches.toArray(new String[0]));
    }

    /**
     * 拿通配符的长度判断，以后可以加入权重系统
     *
     * @param other
     * @param wxRequest
     * @return the result
     */
    @Override
    public int compareTo(AbstractWxWildcardCondition other, WxRequest wxRequest) {
        int thisMax = this.wildcards.stream().sorted(Comparator.comparing(String::length).reversed()).findFirst().orElse("").length();
        int thatMax = other.wildcards.stream().sorted(Comparator.comparing(String::length).reversed()).findFirst().orElse("").length();
        return thisMax - thatMax;
    }

}
