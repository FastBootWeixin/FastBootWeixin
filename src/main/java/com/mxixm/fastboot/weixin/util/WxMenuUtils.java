package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.web.WxRequest;

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
                return button.getPagePath();
            default:
                return button.getKey();
        }
    }

    /**
     * 从请求体中获取PagePath
     * @param body 请求体
     * @return PagePath值
     */
    public static String getPagePathFromBody(WxRequest.Body body) {
        // 只有是MINI_PROGRAM时才可取到EventKey
        if (body.getButtonType() == WxButton.Type.MINI_PROGRAM) {
            return body.getEventKey();
        }
        return null;
    }

    /**
     * 从请求体中获取MediaId
     * @param body 请求体
     * @return MediaId值
     */
    public static String getMediaIdFromBody(WxRequest.Body body) {
        if (body.getButtonType() == WxButton.Type.MEDIA_ID || body.getButtonType() == WxButton.Type.VIEW_LIMITED) {
            return body.getEventKey();
        }
        return null;
    }

    /**
     * 从请求体中获取Url
     * @param body 请求体
     * @return Url值
     */
    public static String getUrlFromBody(WxRequest.Body body) {
        if (body.getButtonType() == WxButton.Type.VIEW) {
            return body.getEventKey();
        }
        return null;
    }

    /**
     * 从请求体中获取Key
     * 其实标准做法应该是排除VIEW、VIEW_LIMITED、MINI_PROGRAM和MEDIA_ID这四种的，这里为了增强兼容性所以这么做
     * @param body 请求体
     * @return Key值
     */
    public static String getKeyFromBody(WxRequest.Body body) {
        if (body.getButtonType() != null) {
            return body.getEventKey();
        }
        return null;
    }

}
