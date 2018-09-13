package com.mxixm.fastboot.weixin.module.menu;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * FastBootWeixin WxMenu
 * 微信多套菜单，因为有个性化菜单
 * 通过接口返回的菜单中信息有限，分四种情况：
 * 1. view事件，返回type、name、url
 * 2. media_id或者view_limited，返回type、name、mediaId
 * 3. miniprogram，返回type、name、appId、appPath、url
 * 4. 其他的都返回type、name和key
 * 同时还有父菜单，返回信息只有name和子菜单信息。
 * 参考示例：
 * {
 *   "menu": {
 *     "button": [
 *       {
 *         "name": "左",
 *         "sub_button": [
 *           {
 *             "type": "click",
 *             "name": "左1",
 *             "key": "LEFT_1"
 *             "sub_button": []
 *           },
 *           {
 *             "type": "view",
 *             "name": "左2",
 *             "url": "http://baidu.com",
 *             "sub_button": []
 *           },
 *           {
 *             "type": "scancode_push",
 *             "name": "左3",
 *             "key": "LEFT_3",
 *             "sub_button": []
 *           },
 *           {
 *             "type": "scancode_waitmsg",
 *             "name": "左4",
 *             "key": "LEFT_4",
 *             "sub_button": []
 *           },
 *           {
 *             "type": "pic_sysphoto",
 *             "name": "左5",
 *             "key": "LEFT_4_2",
 *             "sub_button": []
 *           }
 *         ]
 *       },
 *       {
 *         "name": "中",
 *         "sub_button": [
 *           {
 *             "type": "pic_photo_or_album",
 *             "name": "中1",
 *             "key": "MIDDLE_1",
 *             "sub_button": []
 *           },
 *           {
 *             "type": "pic_weixin",
 *             "name": "中2",
 *             "key": "MIDDLE_2",
 *             "sub_button": []
 *           },
 *           {
 *             "type": "location_select",
 *             "name": "中3",
 *             "key": "MIDDLE_3",
 *             "sub_button": []
 *           },
 *           {
 *             "type": "media_id",
 *             "name": "中4",
 *             "sub_button": [],
 *             "media_id": "3wRH_WkQCjnl8hQcYCxyUukk9I-I1AOxIipo5aYve7A"
 *           }
 *         ]
 *       },
 *       {
 *         "type": "click",
 *         "name": "右",
 *         "key": "RIGHT",
 *         "sub_button": []
 *       }
 *     ]
 *   }
 * }
 *
 * @author Guangshan
 * @date 2018/09/13 23:39
 * @since 0.7.0
 */
public class WxMenus {

    @JsonProperty("menu")
    public WxMenu wxMenu;

    @JsonProperty("conditionalmenu")
    public List<WxMenu> conditionalWxMenu;

    @Override
    public String toString() {
        return "com.mxixm.fastboot.weixin.module.menu.WxMenus(wxMenu=" + this.wxMenu + ", conditionalWxMenu=" + this.conditionalWxMenu + ")";
    }

}
