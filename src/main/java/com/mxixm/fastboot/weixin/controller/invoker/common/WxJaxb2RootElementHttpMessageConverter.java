/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

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
