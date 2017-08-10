package com.example.myproject.support;

/**
 * FastBootWeixin  DefaultWxUserProvider
 *
 * @author Guangshan
 * @summary FastBootWeixin  DefaultWxUserProvider
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/5 21:54
 */
public class DefaultWxUserProvider implements WxUserProvider<WxUser> {

    @Override
    public WxUser getUser(String fromUserName, String toUserName) {
        return new WxUser(fromUserName, toUserName);
    }

    @Override
    public WxUser getFromUser(String fromUserName) {
        return new WxUser(fromUserName, null);
    }

    @Override
    public WxUser getToUser(String toUserName) {
        return new WxUser(null, toUserName);
    }
}
