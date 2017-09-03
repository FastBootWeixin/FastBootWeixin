package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.message.WxMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * FastBootWeixin  WxMessageTypeCondition
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMessageTypeCondition
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
 */
public final class WxMessageTypeCondition extends AbstractWxEnumCondition<WxMessage.Type> {


	public WxMessageTypeCondition(WxMessage.Type... types) {
		super(types);
	}

	protected WxMessageTypeCondition(Collection<WxMessage.Type> types) {
		super(Collections.unmodifiableSet(new LinkedHashSet<>(types)));
	}

	/**
	 * Returns a new instance with a union of the HTTP request types
	 * from "this" and the "other" instance.
	 */
	@Override
	public WxMessageTypeCondition combine(AbstractWxEnumCondition other) {
		Set<WxMessage.Type> set = new LinkedHashSet(this.enums);
		set.addAll(other.enums);
		return new WxMessageTypeCondition(set);
	}

	@Override
	protected WxMessageTypeCondition matchEnum(WxRequest.Body wxRequestBody) {
		WxMessage.Type wxMessageType = wxRequestBody.getMessageType();
		if (getEnums().contains(wxMessageType)) {
			return new WxMessageTypeCondition(wxMessageType);
		}
		return null;
	}

}
