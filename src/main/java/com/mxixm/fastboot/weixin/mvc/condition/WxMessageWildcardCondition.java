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
import com.mxixm.fastboot.weixin.util.WildcardUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxMessageWildcardCondition
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public final class WxMessageWildcardCondition extends AbstractRequestCondition<WxMessageWildcardCondition> {

    private Collection<String> wildcards;

    public WxMessageWildcardCondition(String... wildcards) {
        this(Collections.unmodifiableCollection(wildcards != null ? Arrays.asList(wildcards) : Collections.emptyList()));
    }

    public WxMessageWildcardCondition(Collection<String> wildcards) {
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
    public WxMessageWildcardCondition combine(WxMessageWildcardCondition other) {
        List<String> result = new ArrayList<>();
        result.addAll(this.wildcards);
        result.addAll(other.wildcards);
        return new WxMessageWildcardCondition(result);
    }

    @Override
    public WxMessageWildcardCondition getMatchingCondition(HttpServletRequest request) {
        WxRequest.Body wxRequestBody = WxWebUtils.getWxRequestBodyFromRequest(request);
        if (wxRequestBody == null || wxRequestBody.getContent() == null) {
            return null;
        }
        String content = wxRequestBody.getContent();
        if (this.wildcards.isEmpty()) {
            return null;
        }
        List<String> matches = wildcards.stream().filter(w -> WildcardUtils.wildcardMatch(content, w)).collect(Collectors.toList());
        return matches.isEmpty() ? null : new WxMessageWildcardCondition(matches);
    }

    /**
     * 拿通配符的长度判断，以后可以加入权重系统
     *
     * @param other
     * @param request
     * @return dummy
     */
    @Override
    public int compareTo(WxMessageWildcardCondition other, HttpServletRequest request) {
        int thisMax = this.wildcards.stream().sorted(Comparator.comparing(String::length).reversed()).findFirst().orElse("").length();
        int thatMax = other.wildcards.stream().sorted(Comparator.comparing(String::length).reversed()).findFirst().orElse("").length();
        return thisMax - thatMax;
    }

}
