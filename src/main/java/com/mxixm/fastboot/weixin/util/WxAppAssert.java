package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.exception.WxAppException;

/**
 * FastBootWeixin  WxAppAssert
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxAppAssert
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:51
 */
public abstract class WxAppAssert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new WxAppException(message);
        }
    }

}
