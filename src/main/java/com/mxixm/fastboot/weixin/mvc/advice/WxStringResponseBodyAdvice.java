package com.mxixm.fastboot.weixin.mvc.advice;

import com.mxixm.fastboot.weixin.annotation.WxMapping;
import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.WxRequest;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcesser;
import com.mxixm.fastboot.weixin.mvc.WxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
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
public class WxStringResponseBodyAdvice implements ResponseBodyAdvice<String>, Ordered {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private Marshaller xmlConverter;

    private WxMessageProcesser wxMessageProcesser;

    public WxStringResponseBodyAdvice(WxMessageProcesser wxMessageProcesser) {
        this.wxMessageProcesser = wxMessageProcesser;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 30000;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return StringHttpMessageConverter.class.isAssignableFrom(converterType) &&
                CharSequence.class.isAssignableFrom(returnType.getParameterType()) &&
                returnType.hasMethodAnnotation(WxMapping.class);
    }

    @Override
    public String beforeBodyWrite(String body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!(request instanceof ServletServerHttpRequest)) {
            return body;
        }
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequestAttribute(servletRequest);
        WxMessage.Text text = WxMessage.Text.builder().content(body).build();
        return parseXml(wxMessageProcesser.process(wxRequest, text));
    }

    private String parseXml(WxMessage text) {
        try {
            if (xmlConverter == null) {
                JAXBContext jaxbContext = JAXBContext.newInstance(WxMessage.Text.class);
                xmlConverter = jaxbContext.createMarshaller();
            }
            StringWriter writer = new StringWriter();
            xmlConverter.marshal(text, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new WxAppException(e);
        }
    }

}
