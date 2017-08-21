package com.mxixm.fastbootwx.controller.invoker.executor;

import com.mxixm.fastbootwx.exception.WxApiResponseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.List;

/**
 * copy from spring
 *
 * Response extractor that uses the given {@linkplain HttpMessageConverter entity converters}
 * to convert the response into a type {@code T}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see RestTemplate
 */
public class WxApiMessageConverterExtractor<T> implements WxResponseExtractor<T> {

	private final Type responseType;

	private final Class<T> responseClass;

	private final List<HttpMessageConverter<?>> messageConverters;

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	/**
	 * Create a new instance of the {@code HttpMessageConverterExtractor} with the given response
	 * type and message converters. The given converters must support the response type.
	 */
	public WxApiMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
		this((Type) responseType, messageConverters);
	}

	/**
	 * Creates a new instance of the {@code HttpMessageConverterExtractor} with the given response
	 * type and message converters. The given converters must support the response type.
	 */
	public WxApiMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
		Assert.notNull(responseType, "'responseType' must not be null");
		Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
		this.responseType = responseType;
		this.responseClass = (responseType instanceof Class) ? (Class<T>) responseType : null;
		this.messageConverters = messageConverters;
	}

	public T extractData(ResponseEntity<HttpInputMessage> responseEntity) throws IOException {
		MediaType contentType = getContentType(responseEntity);
		for (HttpMessageConverter<?> messageConverter : this.messageConverters) {
			if (messageConverter instanceof GenericHttpMessageConverter) {
				GenericHttpMessageConverter<?> genericMessageConverter =
						(GenericHttpMessageConverter<?>) messageConverter;
				if (genericMessageConverter.canRead(this.responseType, null, contentType)) {
					return (T) genericMessageConverter.read(this.responseType, null, responseEntity.getBody());
				}
			}
			if (this.responseClass != null) {
				if (messageConverter.canRead(this.responseClass, contentType)) {
					return (T) messageConverter.read((Class) this.responseClass, responseEntity.getBody());
				}
			}
		}

		throw new WxApiResponseException("不能转换相应数据为类型：" + this.responseType, responseEntity);
	}

	private MediaType getContentType(ResponseEntity<HttpInputMessage> responseEntity) {
		MediaType contentType = responseEntity.getHeaders().getContentType();
		if (contentType == null) {
			if (logger.isTraceEnabled()) {
				logger.trace("No Content-Type header found, defaulting to application/octet-stream");
			}
			contentType = MediaType.APPLICATION_OCTET_STREAM;
		}
		return contentType;
	}

}
