package com.example.myproject.controller.invoker.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

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
