package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.mvc.WxWebUtils;
import com.mxixm.fastboot.weixin.util.WildcardUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FastBootWeixin  WxMessageWildcardCondition
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMessageWildcardCondition
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
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
		WxRequest.Body wxRequestBody = WxWebUtils.getWxRequestBodyFromRequestAttribute(request);
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
