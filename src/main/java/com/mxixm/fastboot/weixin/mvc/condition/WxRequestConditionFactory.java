package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.util.WxMenuUtils;

import java.util.Optional;

/**
 * FastBootWeixin WxRequestConditionFactory
 * 静态工厂方法，理论上应该放到AbstractWxEnumCondition中的，或者这里换成实例工厂方法，暂时先不修改
 *
 * @author Guangshan
 * @date 2018-9-17 10:03:14
 * @since 0.7.0
 */
public class WxRequestConditionFactory {

    public static WxEnumRequestCondition createWxCategoriesCondition(Wx.Category... categories) {
        return new WxEnumRequestCondition(WxRequestCondition.Type.CATEGORY, wxRequest ->
                wxRequest.getBody().getCategory(), categories);
    }

    public static WxEnumRequestCondition createWxButtonTypesCondition(WxButton.Type... buttonTypes) {
        return new WxEnumRequestCondition(WxRequestCondition.Type.BUTTON_TYPE, wxRequest ->
                wxRequest.getBody().getButtonType(), buttonTypes);
    }

    public static WxEnumRequestCondition createWxButtonGroupsCondition(WxButton.Group... buttonGroups) {
        return new WxEnumRequestCondition(WxRequestCondition.Type.BUTTON_KEY, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getGroup).orElse(null), buttonGroups);
    }

    public static WxEnumRequestCondition createWxButtonOrdersCondition(WxButton.Order... buttonOrders) {
        return new WxEnumRequestCondition(WxRequestCondition.Type.BUTTON_ORDER, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getOrder).orElse(null), buttonOrders);
    }

    public static WxEnumRequestCondition createWxButtonLevelsCondition(WxButton.Level... buttonLevels) {
        return new WxEnumRequestCondition(WxRequestCondition.Type.BUTTON_LEVEL, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getLevel).orElse(null), buttonLevels);
    }

    public static WxWildcardRequestCondition createWxButtonKeysCondition(String... buttonKeys) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.BUTTON_KEY, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getKey)
                        .orElseGet(() -> WxMenuUtils.getKeyFromBody(wxRequest.getBody())), buttonKeys);
    }

    public static WxWildcardRequestCondition createWxButtonNamesCondition(String... buttonNames) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.BUTTON_NAME, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getName).orElse(null), buttonNames);
    }

    public static WxWildcardRequestCondition createWxButtonUrlsCondition(String... buttonUrls) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.BUTTON_URL, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getUrl)
                        .orElseGet(() -> WxMenuUtils.getUrlFromBody(wxRequest.getBody())), buttonUrls);
    }

    public static WxWildcardRequestCondition createWxButtonMediaIdsCondition(String... buttonMediaIds) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.BUTTON_MEDIA_ID, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getMediaId)
                        .orElseGet(() -> WxMenuUtils.getMediaIdFromBody(wxRequest.getBody())), buttonMediaIds);
    }

    public static WxWildcardRequestCondition createWxButtonAppIdsCondition(String... buttonAppIds) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.BUTTON_APP_ID, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getAppId).orElse(null), buttonAppIds);
    }

    public static WxWildcardRequestCondition createWxButtonPagePathsCondition(String... buttonPagePaths) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.BUTTON_PAGE_PATH, wxRequest ->
                Optional.ofNullable(wxRequest.getButton()).map(WxMenu.Button::getPagePath)
                        .orElseGet(() -> WxMenuUtils.getPagePathFromBody(wxRequest.getBody())), buttonPagePaths);
    }
    public static WxEnumRequestCondition createWxMessageTypesCondition(WxMessage.Type... messageTypes) {
        return new WxEnumRequestCondition(WxRequestCondition.Type.MESSAGE_TYPE, wxRequest ->
                wxRequest.getBody().getMessageType(), messageTypes);
    }

    public static WxWildcardRequestCondition createWxMessageContentsCondition(String... messageContents) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.MESSAGE_CONTENT, wxRequest ->
                wxRequest.getBody().getContent(), messageContents);
    }

    public static WxEnumRequestCondition createWxEventTypesCondition(WxEvent.Type... eventTypes) {
        return new WxEnumRequestCondition(WxRequestCondition.Type.EVENT_TYPE, wxRequest ->
                wxRequest.getBody().getEventType(), eventTypes);
    }

    public static WxWildcardRequestCondition createWxEventScenesCondition(String... eventScenes) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.EVENT_SCENE, wxRequest ->
                wxRequest.getBody().getScene(), eventScenes);
    }

    public static WxWildcardRequestCondition createWxEventKeysCondition(String... eventKeys) {
        return new WxWildcardRequestCondition(WxRequestCondition.Type.EVENT_KEY, wxRequest ->
                wxRequest.getBody().getEventKey(), eventKeys);
    }

}
