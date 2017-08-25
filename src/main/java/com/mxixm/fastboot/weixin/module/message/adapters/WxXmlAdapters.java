package com.mxixm.fastboot.weixin.module.message.adapters;

import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * FastBootWeixin  WxXmlAdapters
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxXmlAdapters
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/5 23:43
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
