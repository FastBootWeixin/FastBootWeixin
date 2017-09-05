package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * FastBootWeixin  WxButtonTypeCondition
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxButtonTypeCondition
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
 */
public final class WxButtonTypeCondition extends AbstractWxEnumCondition<WxButton.Type> {


	public WxButtonTypeCondition(WxButton.Type... types) {
		super(types);
	}

	protected WxButtonTypeCondition(Collection<WxButton.Type> types) {
		super(Collections.unmodifiableSet(new LinkedHashSet<>(types)));
	}

	@Override
	public WxButtonTypeCondition combine(AbstractWxEnumCondition other) {
		Set<WxButton.Type> set = new LinkedHashSet(this.enums);
		set.addAll(other.enums);
		return new WxButtonTypeCondition(set);
	}

	@Override
	protected WxButtonTypeCondition matchEnum(WxRequest.Body wxRequestBody) {
		WxButton.Type wxButtonType = wxRequestBody.getButtonType();
		if (getEnums().contains(wxButtonType)) {
			return new WxButtonTypeCondition(wxButtonType);
		}
		return null;
	}

}
