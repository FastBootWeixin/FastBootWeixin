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
 * FastBootWeixin WxJaxb2RootElementHttpMessageConverter
 * 用于转化CData，暂时不使用，实在没办法了再使用
 * 这里其实还引出了一个大问题，因为我这里使用了CharacterEscapeHandler接口，这个是在rt.jar包中的
 * com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler，因为是rt.jar，所以在编译时并不会把这个包加进去
 * 所以一直编译错误，强行加入编译成功了，但是这种方式是不推荐使用的，推荐替换成其他公用实现，而不是rt.jar中sun开头的私有实现
 * 如maven仓库中的 jaxb-impl-2.3.0.jar，就是这个的相同实现，包名中去掉internal即可。
 * 如果有一天真的要启用这个类，那么可以参考spring的ConditionOnClass来判断两种实现是否存在，如果存在则使用。
 *
 * @author Guangshan
 * @date 2017/08/23 22:31
 * @since 0.1.2
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
