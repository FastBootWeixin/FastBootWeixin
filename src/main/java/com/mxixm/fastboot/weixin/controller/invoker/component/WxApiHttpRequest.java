package com.mxixm.fastboot.weixin.controller.invoker.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * FastBootWeixin  WxApiHttpRequest
 * 包装ClientHttpRequest，用于生成包装过的ClientHttpResponse
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiHttpRequest
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/23 22:31
 */
public final class WxApiHttpRequest implements ClientHttpRequest {

	private ClientHttpRequest delegate;

	public WxApiHttpRequest(ClientHttpRequest delegate) {
		this.delegate = delegate;
	}

	@Override
	public HttpMethod getMethod() {
		return this.delegate.getMethod();
	}

	@Override
	public URI getURI() {
		return this.delegate.getURI();
	}

	@Override
	public ClientHttpResponse execute() throws IOException {
		return new WxApiHttpResponse(this.delegate.execute(), this);
	}

	@Override
	public OutputStream getBody() throws IOException {
		return this.delegate.getBody();
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.delegate.getHeaders();
	}
}
