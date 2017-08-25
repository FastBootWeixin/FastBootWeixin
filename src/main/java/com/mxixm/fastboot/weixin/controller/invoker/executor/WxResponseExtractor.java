package com.mxixm.fastboot.weixin.controller.invoker.executor;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 * FastBootWeixin  WxResponseExtractor
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxResponseExtractor
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/13 11:14
 */
public interface WxResponseExtractor<T> {

	/**
	 * Extract data from the given {@code ClientHttpResponse} and return it.
	 * @param responseEntity the HTTP response
	 * @return the extracted data
	 * @throws IOException in case of I/O errors
	 */
	T extractData(ResponseEntity<HttpInputMessage> responseEntity) throws IOException;

}
