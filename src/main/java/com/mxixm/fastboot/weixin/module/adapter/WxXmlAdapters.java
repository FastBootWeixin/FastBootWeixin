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

package com.mxixm.fastboot.weixin.module.adapter;

import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.util.EnumUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;
import java.util.EnumMap;

/**
 * FastBootWeixin WxXmlAdapters
 *
 * @author Guangshan
 * @date 2017/8/5 23:43
 * @since 0.1.2
 */
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
     * 类型转换，注意枚举的valueOf不可能为null，如果不存在则会抛出枚举异常，这里需要避免这种情况
     */
    public static class MsgTypeAdaptor extends XmlAdapter<String, WxMessage.Type> {
        @Override
        public WxMessage.Type unmarshal(String s) throws Exception {
            return EnumUtils.valueOf(WxMessage.Type.class, s.toUpperCase().trim());
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
            WxEvent.Type type = EnumUtils.valueOf(WxEvent.Type.class, s.toUpperCase().trim());
            return type == null ? WxEvent.Type.UNKNOWN : type;
        }

        @Override
        public String marshal(WxEvent.Type type) throws Exception {
            return type.name().toUpperCase();
        }
    }

    /**
     * 文本消息体转换器
     */
    public static class TextBodyAdaptor extends XmlAdapter<String, WxMessageBody.Text> {

        @Override
        public WxMessageBody.Text unmarshal(String v) throws Exception {
            return new WxMessageBody.Text(v);
        }

        @Override
        public String marshal(WxMessageBody.Text v) throws Exception {
            return v.getContent();
        }
    }

}
