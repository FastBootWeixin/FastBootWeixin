package com.example.myproject.controller.invoker.common;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.List;

/**
 * FastBootWeixin  WxHttpInputMessageConverter
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxHttpInputMessageConverter
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/13 10:53
 */
public class WxHttpInputMessageConverter extends AbstractHttpMessageConverter<HttpInputMessage> {

    public WxHttpInputMessageConverter() {
        super(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return HttpInputMessage.class.isAssignableFrom(clazz);
    }

    @Override
    protected HttpInputMessage readInternal(Class<? extends HttpInputMessage> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // 为何不直接返回inputMessage？因为在请求结束后，原始的inputMessage会被系统关闭，所以做了一层包装
        return new WxBufferingInputMessageWrapper(inputMessage).init();
    }

    @Override
    protected void writeInternal(HttpInputMessage httpInputMessage, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        StreamUtils.copy(httpInputMessage.getBody(), outputMessage.getBody());
    }

}
