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

import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 * FastBootWeixin WxResponseExtractor
 *
 * @author Guangshan
 * @date 2017/8/13 11:14
 * @since 0.1.2
 */
public interface WxResponseExtractor<T> {

    /**
     * Extract data from the given {@code ClientHttpResponse} and return it.
     *
     * @param responseEntity the HTTP response
     * @return the extracted data
     * @throws IOException in case of I/O errors
     */
    T extractData(ResponseEntity<HttpInputMessage> responseEntity) throws IOException;

}
