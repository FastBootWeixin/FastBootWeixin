package com.example.myproject.controller.invoker.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * copy from spring
 *
 * Simple implementation of {@link ClientHttpResponse} that reads the response's body
 * into memory, thus allowing for multiple invocations of {@link #getBody()}.
 *
 * @author Arjen Poutsma
 * @since 3.1
 */
public final class WxBufferingInputMessageWrapper implements HttpInputMessage {

	private final HttpInputMessage httpInputMessage;

	private byte[] body;

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
		return new ByteArrayInputStream(this.body);
	}

	/**
	 * 构造后初始化，不想加在构造方法中，一定要记着初始化
	 * @throws IOException
	 */
	public WxBufferingInputMessageWrapper init() throws IOException {
		if (this.body == null) {
			this.body = StreamUtils.copyToByteArray(this.httpInputMessage.getBody());
		}
		return this;
	}

	public byte[] getRawBody() {
		return this.body;
	}

}
