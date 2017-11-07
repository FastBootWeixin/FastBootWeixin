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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FastBootWeixin WxApiResponseExtractor
 * todo 有必要把所有用到的集合考虑是否换成线程安全的
 *
 * @author Guangshan
 * @date 2017/8/13 11:14
 * @since 0.1.2
 */
public class WxApiResponseExtractor {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final Map<Class, WxApiMessageConverterExtractor> delegates;

    private final List<HttpMessageConverter<?>> converters;

    public WxApiResponseExtractor(List<HttpMessageConverter<?>> converters) {
        this.converters = converters;
        this.delegates = new HashMap<>();
    }

    public <T> T extractData(ResponseEntity<HttpInputMessage> responseEntity, Class<T> returnType) {
        // 本来应该有response数据为空的判断的，其实这里已经被前一步的restTemplate获取中判断过了，这里只用判断body为空即可
        if (returnType == null || void.class == returnType || Void.class == returnType || responseEntity.getBody() == null) {
            return null;
        }
        /* 先不管文件
        if (WxWebUtils.isMutlipart(returnType)) {
            return null;
        }
        不是不管文件，而是可以被messageConverter处理了
        */
        WxApiMessageConverterExtractor<T> delegate = delegates.get(returnType);
        if (delegate == null) {
            delegate = new WxApiMessageConverterExtractor(returnType, converters);
            delegates.put(returnType, delegate);
        }
        // 这里遇到了个坑，很长时间没玩过IO了，这个坑就和IO相关，每次提取数据时都抛出IO异常，IO已关闭
        // 本来以为是我的error判断那里提前读了IO导致后来IO关闭了，调试后发现真正原因，因为
        // ResponseEntity<HttpInputMessage> responseEntity = wxApiInvoker.exchange(requestEntity, HttpInputMessage.class);
        // 上面代码执行结束后，有个finally，就是用于关闭response的，也就是说，一旦执行结束就无法再执行提取数据的操作了
        // 所以我只能把WxHttpInputMessageConverter里返回的inputStream包装一下了
        // 这里还涉及一个问题，是否有必要把所有消息都返回inputStream？当然没有必要，只要特定几种类型返回InputStream
        // 其他类型直接转换即可。
        try {
            return delegate.extractData(responseEntity);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new WxApiResponseException("提取数据时发生IO异常", responseEntity);
        }
    }

}
