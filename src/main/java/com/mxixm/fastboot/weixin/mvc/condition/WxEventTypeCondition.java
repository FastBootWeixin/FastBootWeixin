package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * FastBootWeixin  WxEventTypeCondition
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxEventTypeCondition
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
 */
public final class WxEventTypeCondition extends AbstractWxEnumCondition<WxEvent.Type> {


	public WxEventTypeCondition(WxEvent.Type... types) {
		super(types);
	}

	protected WxEventTypeCondition(Collection<WxEvent.Type> types) {
		super(Collections.unmodifiableSet(new LinkedHashSet<>(types)));
	}

	@Override
	public WxEventTypeCondition combine(AbstractWxEnumCondition other) {
		Set<WxEvent.Type> set = new LinkedHashSet(this.enums);
		set.addAll(other.enums);
		return new WxEventTypeCondition(set);
	}

	@Override
	protected WxEventTypeCondition matchEnum(WxRequest.Body wxRequestBody) {
		WxEvent.Type wxEventType = wxRequestBody.getEventType();
		if (getEnums().contains(wxEventType)) {
			return new WxEventTypeCondition(wxEventType);
		}
		return null;
	}

}
