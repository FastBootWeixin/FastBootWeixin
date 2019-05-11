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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxixm.fastboot.weixin.exception.WxApiResponseException;
import com.mxixm.fastboot.weixin.exception.WxApiResultException;
import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.credential.WxTokenManager;
import com.mxixm.fastboot.weixin.service.invoker.WxApiMethodInfo;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiBody;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiForm;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiRequest;
import com.mxixm.fastboot.weixin.service.invoker.common.ReaderInputStream;
import com.mxixm.fastboot.weixin.util.WxAppAssert;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin WxApiExecutor
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @date 2017/07/23 17:14
 * @since 0.1.2
 */
public class WxApiExecutor {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private static final String WX_ACCESS_TOKEN_PARAM_NAME = "access_token";

    private final WxApiTemplate wxApiTemplate;

    private final WxTokenManager wxTokenManager;

    private final WxApiResponseExtractor wxApiResponseExtractor;

    private final ObjectMapper jsonConverter = new ObjectMapper();

//    private final ConversionService conversionService;

    public WxApiExecutor(WxApiTemplate wxApiTemplate, WxTokenManager wxTokenManager) {
        this.wxApiTemplate = wxApiTemplate;
        this.wxApiResponseExtractor = new WxApiResponseExtractor(this.wxApiTemplate.getMessageConverters());
        this.wxTokenManager = wxTokenManager;
//        this.conversionService = conversionService;
    }

    public Object execute(WxApiMethodInfo wxApiMethodInfo, Object[] args) {
        return this.execute(wxApiMethodInfo, args, true);
    }

    public Object execute(WxApiMethodInfo wxApiMethodInfo, Object[] args, boolean retry) {
        RequestEntity requestEntity = buildHttpRequestEntity(wxApiMethodInfo, args);
        // 后续这里可以区分情况，只有对于stream类型才使用extract，因为如果先执行转为HttpInputMessage
        // 其实是转为了byte放在了内存中，相当于多转了一层，大文件还是会多耗费点内存的，但是这里为了用更多的技术，就这样玩了。
        ResponseEntity<HttpInputMessage> responseEntity = null;
        try {
            responseEntity = wxApiTemplate.exchange(requestEntity, HttpInputMessage.class);
        } catch (WxApiResultException e) {
            // access_token错误，刷新后重试
            if (retry && e.getResultCode() == WxApiResultException.Code.INVALID_ACCESS_TOKEN) {
                wxTokenManager.forceRefresh();
                return execute(wxApiMethodInfo, args, false);
            }
            throw e;
        }
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new WxApiResponseException(responseEntity);
        }
        return wxApiResponseExtractor.extractData(responseEntity, wxApiMethodInfo.getReturnType());
    }

    private RequestEntity buildHttpRequestEntity(WxApiMethodInfo wxApiMethodInfo, Object[] args) {
        UriComponentsBuilder builder = wxApiMethodInfo.fromArgs(args);
        // 替换accessToken
        builder.replaceQueryParam(WX_ACCESS_TOKEN_PARAM_NAME, wxTokenManager.get());
        HttpHeaders httpHeaders = null;
        Object body = null;
        if (wxApiMethodInfo.getRequestMethod() == WxApiRequest.Method.JSON) {
            httpHeaders = buildJsonHeaders();
            // 由于内置的AbstractJackson2HttpMessageConverter中writeInternal方法，对json的解析使用默认的UTF-8编码
            // 导致对于多字节的UTF8编码EMOJI表情进行了转义，产生了不正确的结果
            // 但是内置功能无法被简单修改，所以这里自己转了一下
            body = getStringBody(wxApiMethodInfo, args);
//            body = getObjectBody(wxApiMethodInfo, args);
        } else if (wxApiMethodInfo.getRequestMethod() == WxApiRequest.Method.XML) {
            httpHeaders = buildXmlHeaders();
            // 暂时不支持xml转换。。。
            body = getObjectBody(wxApiMethodInfo, args);
        } else if (wxApiMethodInfo.getRequestMethod() == WxApiRequest.Method.FORM) {
            body = getFormBody(wxApiMethodInfo, args);
        }
        return new RequestEntity(body, httpHeaders, wxApiMethodInfo.getRequestMethod().getHttpMethod(), builder.build().toUri());
    }

    private Object getObjectBody(WxApiMethodInfo wxApiMethodInfo, Object[] args) {
        MethodParameter methodParameter = wxApiMethodInfo.getMethodParameters().stream()
                .filter(p -> !BeanUtils.isSimpleValueType(p.getParameterType()) || p.hasParameterAnnotation(WxApiBody.class))
                .findFirst().orElse(null);
        if (methodParameter == null) {
            throw new WxAppException("没有可处理的参数");
        }
        // 不是简单类型
        if (!BeanUtils.isSimpleValueType(methodParameter.getParameterType())) {
            // 暂时只支持json
            return args[methodParameter.getParameterIndex()];
        }
        if (args[methodParameter.getParameterIndex()] != null) {
            return args[methodParameter.getParameterIndex()].toString();
        } else {
            return "";
        }
    }

    private String getStringBody(WxApiMethodInfo wxApiMethodInfo, Object[] args) {
        MethodParameter methodParameter = wxApiMethodInfo.getMethodParameters().stream()
                .filter(p -> !BeanUtils.isSimpleValueType(p.getParameterType()) || p.hasParameterAnnotation(WxApiBody.class))
                .findFirst().orElse(null);
        if (methodParameter == null) {
            throw new WxAppException("没有可处理的参数");
        }
        // 不是简单类型
        if (!BeanUtils.isSimpleValueType(methodParameter.getParameterType())) {
            try {
                // 暂时只支持json
                return jsonConverter.writeValueAsString(args[methodParameter.getParameterIndex()]);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (args[methodParameter.getParameterIndex()] != null) {
            return args[methodParameter.getParameterIndex()].toString();
        } else {
            return "";
        }
    }

    /**
     * 要发送文件，使用这种方式，请查看源码：FormHttpMessageConverter
     *
     * @param wxApiMethodInfo
     * @param args
     * @return the result
     */
    private Object getFormBody(WxApiMethodInfo wxApiMethodInfo, Object[] args) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        wxApiMethodInfo.getMethodParameters().stream()
                .filter(p -> !BeanUtils.isSimpleValueType(p.getParameterType()) || p.hasParameterAnnotation(WxApiForm.class) || p.hasParameterAnnotation(WxApiBody.class))
                .forEach(p -> {
                    // 为空则直接返回不加这个参数
                    if (args[p.getParameterIndex()] == null) {
                        return;
                    }
                    WxApiForm wxApiForm = p.getParameterAnnotation(WxApiForm.class);
                    String paramName;
                    Object param;
                    if (wxApiForm == null || ValueConstants.DEFAULT_NONE.equals(wxApiForm.value())) {
                        paramName = p.getParameterName();
                    } else {
                        paramName = wxApiForm.value();
                    }
                    // 加入Assert
                    WxAppAssert.notNull(paramName, "请添加编译器的-parameter或者为参数添加注解名称");
                    if (WxWebUtils.isMutlipart(p.getParameterType())) {
                        param = getFormResource(args[p.getParameterIndex()]);
                    } else {
                        param = args[p.getParameterIndex()];
                    }
                    params.add(paramName, param);
                });
        return params;
    }

    private Resource getFormResource(Object arg) {
        if (byte[].class == arg.getClass()) {
            return new ByteArrayResource((byte[]) arg);
        } else if (Resource.class.isAssignableFrom(arg.getClass())) {
            return (Resource) arg;
        } else if (InputStreamSource.class.isAssignableFrom(arg.getClass())) {
            try {
                return new InputStreamResource(((InputStreamResource) arg).getInputStream());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new WxAppException("处理IO转换异常", e);
            }
        } else if (File.class.isAssignableFrom(arg.getClass())) {
            return new FileSystemResource((File) arg);
        } else if (InputStream.class.isAssignableFrom(arg.getClass())) {
            return new InputStreamResource((InputStream) arg);
        } else if (Reader.class.isAssignableFrom(arg.getClass())) {
            Reader reader = (Reader) arg;
            ReaderInputStream readerInputStream = new ReaderInputStream(reader, StandardCharsets.UTF_8);
            return new InputStreamResource(readerInputStream);
        }
        throw new WxAppException("不支持的Resource类型");
    }

    /**
     * 获取一个application/json头
     *
     * @return the result
     */
    private HttpHeaders buildJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * text/xml头
     *
     * @return the result
     */
    private HttpHeaders buildXmlHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        return headers;
    }

}
