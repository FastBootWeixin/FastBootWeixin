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

package com.mxixm.fastboot.weixin.module.message.adapters;

import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class WxXmlAdapters {

    /**
     * 日期转换
     */
    public static class CreateTimeAdaptor extends XmlAdapter<Long, Date> {
        @Override
        public Date unmarshal(Long i) throws Exception {
            return new Date(i * 1000);
        }

        @Override
        public Long marshal(Date d) throws Exception {
            return d.getTime() / 1000;
        }
    }


    /**
     * 类型转换
     */
    public static class MsgTypeAdaptor extends XmlAdapter<String, WxMessage.Type> {
        @Override
        public WxMessage.Type unmarshal(String s) throws Exception {
            return WxMessage.Type.valueOf(s.toUpperCase());
        }

        @Override
        public String marshal(WxMessage.Type type) throws Exception {
            return type.name().toLowerCase();
        }
    }

    /**
     * 类型转换
     */
    public static class EventAdaptor extends XmlAdapter<String, WxEvent.Type> {
        @Override
        public WxEvent.Type unmarshal(String s) throws Exception {
            return WxEvent.Type.valueOf(s.toUpperCase());
        }

        @Override
        public String marshal(WxEvent.Type type) throws Exception {
            return type.name().toUpperCase();
        }
    }

}
