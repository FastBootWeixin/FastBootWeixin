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

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.*;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * {@link ClientHttpRequest} implementation based on
 * Apache HttpComponents HttpClient.
 *
 * <p>Created via the {@link HttpComponentsClientHttpRequestFactory}.
 *
 * @author Oleg Kalnichevski
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.1
 * @see HttpComponentsClientHttpRequestFactory#createRequest(URI, HttpMethod)
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
		return new WxApiHttpResponse(this.delegate.execute());
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
