package com.mxixm.fastboot.weixin.common;

import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.menu.WxMenuManager;
import com.mxixm.fastboot.weixin.support.WxAccessTokenManager;
import com.mxixm.fastboot.weixin.util.WxContextUtils;
import com.mxixm.fastboot.weixin.web.WxUserManager;

/**
 * FastBootWeixin  WxBeans
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxBeans
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:08
 */
public class WxBeans {

    public static final String WX_API_INVOKER_NAME = "WxApiInvoker";

    public static WxUserManager wxUserManager() {
        return WxContextUtils.getBean(WxUserManager.class);
    }

    public static WxAccessTokenManager wxAccessTokenManager() {
        return WxContextUtils.getBean(WxAccessTokenManager.class);
    }

    public static WxMenuManager wxMenuManager() {
        return WxContextUtils.getBean(WxMenuManager.class);
    }

    public static WxMediaManager wxMediaManager() {
        return WxContextUtils.getBean(WxMediaManager.class);
    }

}
