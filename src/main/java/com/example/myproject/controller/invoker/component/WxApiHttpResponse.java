/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.myproject.controller.invoker.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * {@link ClientHttpResponse} implementation based on
 * Apache HttpComponents HttpClient.
 *
 * <p>Created via the {@link org.springframework.http.client.HttpComponentsClientHttpRequest}.
 *
 * @author Oleg Kalnichevski
 * @author Arjen Poutsma
 * @since 3.1
 * @see org.springframework.http.client.HttpComponentsClientHttpRequest#execute()
 */
public final class WxApiHttpResponse extends AbstractClientHttpResponse {

	private static final int WX_API_ERROR_CODE_END = 15;

	private final ClientHttpResponse delegate;

	private PushbackInputStream pushbackInputStream;

	public WxApiHttpResponse(ClientHttpResponse delegate) {
		this.delegate = delegate;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.delegate.getHeaders();
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
	}

}
