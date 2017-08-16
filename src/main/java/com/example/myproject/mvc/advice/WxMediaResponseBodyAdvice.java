package com.example.myproject.mvc.advice;

import com.example.myproject.exception.WxAppException;
import com.example.myproject.module.WxRequest;
import com.example.myproject.module.media.WxMediaResource;
import com.example.myproject.module.message.WxMessage;
import com.example.myproject.mvc.WxRequestResponseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * ResponseBodyAdvice Spring 4.1以上才支持。
 * 这个作用是为响应自动添加fromUser
 * 不加这个注解会有问题@ControllerAdvice，不识别
 *
 * @author Guangshan
 * @since 2017年8月15日
 */
@ControllerAdvice
public class WxMediaResponseBodyAdvice implements ResponseBodyAdvice<WxMediaResource>, Ordered {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100000;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return ResourceHttpMessageConverter.class.isAssignableFrom(converterType) &&
                WxMediaResource.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public WxMediaResource beforeBodyWrite(WxMediaResource body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!body.isUrlMedia()) {
            return body;
        }
        try {
            response.getHeaders().setLocation(body.getURI());
            response.setStatusCode(HttpStatus.FOUND);
            return null;
        } catch (IOException e) {
            throw new WxAppException("系统异常");
        }
    }

}
