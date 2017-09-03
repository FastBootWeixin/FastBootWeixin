package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * FastBootWeixin  WxCategoryCondition
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxCategoryCondition
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
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
