package com.example.myproject.controller.invoker.component;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

	/**
	 * 你问我为什么要偷梁换柱？当然是因为微信接口返回的是JSON，但是Content-Type却是Text_Pain啦，是否要考虑判断内容？
	 * 暂时不需要，除非有些接口返回XML，也是这个头，那就坑爹了
	 * @return
	 */
	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders headers = this.delegate.getHeaders();
		if (headers.getContentType().equals(MediaType.TEXT_PLAIN)) {
			headers.setContentType(MediaType.APPLICATION_JSON);
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
