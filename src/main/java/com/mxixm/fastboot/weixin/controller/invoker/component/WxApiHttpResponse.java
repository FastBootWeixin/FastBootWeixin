package com.mxixm.fastboot.weixin.controller.invoker.component;

import com.mxixm.fastboot.weixin.mvc.WxRequestResponseUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * FastBootWeixin  WxApiHttpResponse
 * 包装AbstractClientHttpResponse，包装InputStream为pushbackInputStream，用于取前几个字符
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiHttpResponse
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/23 22:31
 */
public final class WxApiHttpResponse extends AbstractClientHttpResponse {

	private static final int WX_API_ERROR_CODE_END = 15;

	private final ClientHttpResponse delegate;

	private final ClientHttpRequest request;

	private PushbackInputStream pushbackInputStream;

	public WxApiHttpResponse(ClientHttpResponse delegate, ClientHttpRequest request) {
		this.delegate = delegate;
		this.request = request;
	}

	/**
	 * 你问我为什么要偷梁换柱？当然是因为微信接口返回的是JSON，但是Content-Type却是Text_Pain啦，是否要考虑判断内容？
	 * 暂时不需要，除非有些接口返回XML，也是这个头，那就坑爹了
	 * @return
	 */
	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders headers = this.delegate.getHeaders();
		if (headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
			return headers;
		}
		if (headers.containsKey(HttpHeaders.CONTENT_TYPE) && headers.getContentType().equals(MediaType.TEXT_PLAIN)) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		if (!headers.containsKey(WxRequestResponseUtils.X_WX_REQUEST_URL)) {
			headers.add(WxRequestResponseUtils.X_WX_REQUEST_URL, request.getURI().toString());
		}
		return headers;
	}

	/**
	 * 装饰一下，返回可以重读的InputStream
	 * @return
	 * @throws IOException
	 */
	@Override
	public InputStream getBody() throws IOException {

		InputStream body = this.delegate.getBody();
		if (body == null || body.markSupported() || body instanceof PushbackInputStream) {
			return body;
		} else if (this.pushbackInputStream == null) {
			this.pushbackInputStream = new PushbackInputStream(body, WX_API_ERROR_CODE_END);
		}
		return this.pushbackInputStream;
	}

	@Override
	public HttpStatus getStatusCode() throws IOException {
		return this.delegate.getStatusCode();
	}

	@Override
	public int getRawStatusCode() throws IOException {
		return this.delegate.getRawStatusCode();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.delegate.getStatusText();
	}

	@Override
	public void close() {
		this.delegate.close();
		this.pushbackInputStream = null;
	}

}
