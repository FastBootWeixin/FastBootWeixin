/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.service.invoker.executor;

import com.mxixm.fastboot.weixin.exception.WxApiResponseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.List;

/**
 * FastBootWeixin WxApiMessageConverterExtractor
 *
 * @author Guangshan
 * @date 2017/8/13 10:58
 * @since 0.1.2
 */
public class WxApiMessageConverterExtractor<T> implements WxResponseExtractor<T> {

    private final Type responseType;

    private final Class<T> responseClass;

    private final List<HttpMessageConverter<?>> messageConverters;

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    public WxApiMessageConverterExtractor(Class<T> responseType, List<HttpMessageConverter<?>> messageConverters) {
        this((Type) responseType, messageConverters);
    }

    public WxApiMessageConverterExtractor(Type responseType, List<HttpMessageConverter<?>> messageConverters) {
        Assert.notNull(responseType, "'responseType' must not be null");
        Assert.notEmpty(messageConverters, "'messageConverters' must not be empty");
        this.responseType = responseType;
        this.responseClass = (responseType instanceof Class) ? (Class<T>) responseType : null;
        this.messageConverters = messageConverters;
    }

    @Override
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
