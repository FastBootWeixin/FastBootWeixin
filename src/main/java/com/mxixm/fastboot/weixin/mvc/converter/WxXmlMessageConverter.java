/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.mvc.converter;

import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.message.WxEncryptMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageProcessor;
import com.mxixm.fastboot.weixin.module.message.parameter.WxMessageParameter;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.service.WxXmlCryptoService;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/**
 * FastBootWeixin WxXmlMessageConverter
 * 用于转化CData，暂时不使用，实在没办法了再使用
 * 这里其实还引出了一个大问题，因为我这里使用了CharacterEscapeHandler接口，这个是在rt.jar包中的
 * com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler，因为是rt.jar，所以在编译时并不会把这个包加进去
 * 所以一直编译错误，强行加入编译成功了，但是这种方式是不推荐使用的，推荐替换成其他公用实现，而不是rt.jar中sun开头的私有实现
 * 如maven仓库中的 jaxb-impl-2.3.0.jar，就是这个的相同实现，包名中去掉internal即可。
 * 如果有一天真的要启用这个类，那么可以参考spring的ConditionOnClass来判断两种实现是否存在，如果存在则使用。
 *
 * todo 消息的处理和消息的转换是否可以分开。即wxMessageProcessor是否应该放在这里面做？
 *
 * @author Guangshan
 * @date 2017/08/23 22:31
 * @since 0.6.2
 */
public class WxXmlMessageConverter extends Jaxb2RootElementHttpMessageConverter {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private static final CDataCharacterEscapeHandler characterEscapeHandler = new CDataCharacterEscapeHandler();

    private final WxMessageProcessor wxMessageProcessor;

    private final WxXmlCryptoService wxXmlCryptoService;

    public WxXmlMessageConverter(WxMessageProcessor wxMessageProcessor, WxXmlCryptoService wxXmlCryptoService) {
        this.wxMessageProcessor = wxMessageProcessor;
        this.wxXmlCryptoService = wxXmlCryptoService;
    }

    /**
     * 只读WxRequest.Body这种类型，且必须在Wx请求的上下文重
     *
     * @param clazz     传入的Class
     * @param mediaType 媒体类型
     * @return 是否支持读
     */
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return super.canRead(clazz, mediaType) && WxRequest.Body.class.isAssignableFrom(clazz) &&
                WxWebUtils.getWxRequestFromRequest() != null;
    }

    public WxRequest.Body read(HttpServletRequest request) throws IOException {
        WxRequest.Body body = (WxRequest.Body) super.read(WxRequest.Body.class, new ServletServerHttpRequest(request));
        return body;
    }

    @Override
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) throws IOException {
        Assert.isAssignable(WxRequest.Body.class, clazz, "错误的使用了消息转化器");
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest();
        WxRequest.Body body = (WxRequest.Body) super.readFromSource(clazz, headers, source);
        if (!wxRequest.isEncrypted()) {
            return body;
        }
        if (StringUtils.isEmpty(body.getEncrypt()) && !StringUtils.isEmpty(body.getFromUserName())) {
            return body;
        }
        String decryptedMessage = wxXmlCryptoService.decrypt(wxRequest, body.getEncrypt());
        return super.readFromSource(clazz, headers, new StreamSource(new ByteArrayInputStream(decryptedMessage.getBytes(StandardCharsets.UTF_8))));
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        // 同时支持字符串返回值
        return (CharSequence.class.isAssignableFrom(clazz) || WxMessage.class.isAssignableFrom(clazz))
                && WxWebUtils.getWxRequestFromRequest() != null;
    }

    @Override
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws IOException {
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest();
        WxMessage wxMessage = processObject(o);
        if (!wxRequest.isEncrypted()) {
            super.writeToResult(wxMessage, headers, result);
        } else {
            StreamResult rawResult = new StreamResult(new StringWriter(256));
            super.writeToResult(wxMessage, headers, rawResult);
            WxEncryptMessage wxEncryptMessage = wxXmlCryptoService.encrypt(wxRequest, rawResult.getWriter().toString());
            super.writeToResult(wxEncryptMessage, headers, result);
        }
    }

    private WxMessage processObject(Object o) {
        WxMessage wxMessage;
        WxMessageParameter wxMessageParameter = WxWebUtils.getWxMessageParameter();
        if (o instanceof CharSequence) {
            wxMessage = WxMessage.textBuilder().content(o.toString()).build();
        } else if (o instanceof WxMessage) {
            wxMessage = (WxMessage) o;
        } else {
            throw new WxAppException("错误的消息类型");
        }
        return wxMessageProcessor.process(wxMessageParameter, wxMessage);
    }

    @Override
    protected void customizeMarshaller(Marshaller marshaller) {
        super.customizeMarshaller(marshaller);
        try {
            // 强制指定编码为UTF-8，微信仅支持UTF-8，也可在WxMappingHandlerMapping.handleMatch返回编码中指定为UTF-8，但是不能引用常量，故在此指定
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.displayName());
            marshaller.setProperty("com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler", characterEscapeHandler);
        } catch (PropertyException e) {
            logger.error(e);
        }
    }

    /**
     * 是否应该按照com.sun.xml.bind.v2.runtime.output.Encoded中的setEscape方法执行？这里简单处理了一下
     */
    public static class CDataCharacterEscapeHandler implements CharacterEscapeHandler {

        @Override
        public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
            out.write("<![CDATA[");
            int limit = start+length;
            for (int i = start; i < limit; i++) {
                switch (ch[i]) {
                    case '&':
                        out.write("&amp;");
                        break;
                    case '<':
                        out.write("&lt;");
                        break;
                    case '>':
                        out.write("&gt;");
                        break;
                    case '\"':
                        if (isAttVal) {
                            out.write("&quot;");
                        } else {
                            out.write('\"');
                        }
                        break;
                    default:
                        out.write(ch[i]);
                }
            }
            out.write("]]>");
        }
    }

}
