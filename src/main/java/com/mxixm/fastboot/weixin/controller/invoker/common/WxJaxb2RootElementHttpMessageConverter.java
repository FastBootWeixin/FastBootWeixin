package com.mxixm.fastboot.weixin.controller.invoker.common;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;

import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.IOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin  WxJaxb2RootElementHttpMessageConverter
 * 用于转化CData，暂时不使用，实在没办法了再使用
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxJaxb2RootElementHttpMessageConverter
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/23 22:31
 */
public class WxJaxb2RootElementHttpMessageConverter extends Jaxb2RootElementHttpMessageConverter {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private static final CDataCharacterEscapeHandler characterEscapeHandler = new CDataCharacterEscapeHandler();

    @Override
    protected void customizeMarshaller(Marshaller marshaller) {
        super.customizeMarshaller(marshaller);
        try {
            marshaller.setProperty("com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler", characterEscapeHandler);
        } catch (PropertyException e) {
            logger.error(e);
        }
    }

    public static class CDataCharacterEscapeHandler implements CharacterEscapeHandler {
        @Override
        public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
            out.write("<![CDATA[");
            out.write(ch, start, length);
            out.write("]]");
        }
    }

}
