package com.mxixm.fastboot.weixin.module.message.processer;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WxMessageProcesseChain implements WxMessageProcesser<WxMessage> {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final List<WxMessageProcesser> wxMessageProcessers = new ArrayList<>();

	public List<WxMessageProcesser> getProcessers() {
		return Collections.unmodifiableList(this.wxMessageProcessers);
	}

	@Override
	public WxMessage process(WxRequest wxRequest, WxMessage wxMessage) {
		for (WxMessageProcesser processer : getSupportedProcessers(wxRequest, wxMessage)) {
			wxMessage = processer.process(wxRequest, wxMessage);
		}
		return wxMessage;
	}

	@Override
	public boolean supports(WxRequest wxRequest, WxMessage wxMessage) {
		return true;
	}

	private List<WxMessageProcesser> getSupportedProcessers(WxRequest wxRequest, WxMessage wxMessage) {
		return wxMessageProcessers.stream().filter(p -> p.supports(wxRequest, wxMessage)).collect(Collectors.toList());
	}

	public WxMessageProcesseChain addProcesser(WxMessageProcesser processer) {
		this.wxMessageProcessers.add(processer);
		return this;
	}

	public WxMessageProcesseChain addProcessers(List<? extends WxMessageProcesser> processers) {
		if (processers != null) {
			for (WxMessageProcesser processer : processers) {
				this.wxMessageProcessers.add(processer);
			}
		}
		return this;
	}

}
