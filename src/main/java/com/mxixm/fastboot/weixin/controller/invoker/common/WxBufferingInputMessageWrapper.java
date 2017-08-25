package com.mxixm.fastboot.weixin.controller.invoker.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * FastBootWeixin  WxBufferingInputMessageWrapper
 * 数据全部放入buffer
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxBufferingInputMessageWrapper
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/13 10:53
 */
public final class WxBufferingInputMessageWrapper implements HttpInputMessage, Closeable {

	private final HttpInputMessage httpInputMessage;

	private byte[] body;

	private ByteArrayInputStream byteArrayInputStream;

	public WxBufferingInputMessageWrapper(HttpInputMessage httpInputMessage) {
		this.httpInputMessage = httpInputMessage;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.httpInputMessage.getHeaders();
	}

	@Override
	public InputStream getBody() throws IOException {
		this.init();
		if (byteArrayInputStream == null) {
			byteArrayInputStream = new ByteArrayInputStream(this.body);
		}
		return byteArrayInputStream;
	}

	/**
	 * 构造后初始化，不想加在构造方法中，一定要记着初始化
	 * @throws IOException
	 */
	public WxBufferingInputMessageWrapper init() throws IOException {
		if (this.body != null) {
			return this;
		}
		if (httpInputMessage instanceof WxBufferingInputMessageWrapper) {
			this.body = ((WxBufferingInputMessageWrapper) httpInputMessage).getRawBody();
		} else {
			this.body = StreamUtils.copyToByteArray(httpInputMessage.getBody());
		}
		return this;
	}

	public byte[] getRawBody() {
		return this.body;
	}

	@Override
	public void close() throws IOException {
		if (this.httpInputMessage != null && this.httpInputMessage.getBody() != null) {
			try {
				this.httpInputMessage.getBody().close();
			} catch (IOException e) {
				// ignore it
			}
		}
	}
}
