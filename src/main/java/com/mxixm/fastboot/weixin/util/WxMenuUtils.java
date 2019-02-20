package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.module.menu.WxMenu;

/**
 * FastBootWeixin WxMenuUtils
 * 按钮工具类
 *
 * @author Guangshan
 * @date 2018/9/13 22:47
 * @since 0.7.0
 */
public class WxMenuUtils {

    /**
     * 返回按钮对应的eventKey，因为微信映射方式不同，所有有这么些不同的情况
     * 小程序的不确定，求个测试
     * @param button 微信按钮
     * @return 返回eventKey
     */
    public static String getKey(WxMenu.Button button) {
        if (button.getType() == null) {
            return button.getKey();
        }
        switch (button.getType()) {
            case VIEW:
                return button.getUrl();
            case MEDIA_ID:
            case VIEW_LIMITED:
                return button.getMediaId();
            case MINI_PROGRAM:
                return button.getAppId();
            default:
                return button.getKey();
        }
    }

}
