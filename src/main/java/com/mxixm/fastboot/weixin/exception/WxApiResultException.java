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

package com.mxixm.fastboot.weixin.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

/**
 * FastBootWeixin WxApiResultException
 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1433747234
 *
 * @author Guangshan
 * @date 2017/7/23 23:38
 * @since 0.1.2
 */
public class WxApiResultException extends WxApiException {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    public static final String WX_API_RESULT_SUCCESS = "\"errcode\":0,";

    public static final String WX_API_RESULT_ERRCODE = "\"errcode\":";

    public static final String WX_API_RESULT_ERRMSG = "\"errmsg\":";

    public static final String INVALID_ACCESS_TOKEN_MESSAGE = "invalid credential, access_token is invalid or not latest";

    private int code;

    private String errorMessage;

    Code resultCode;

    public WxApiResultException(int code, String errorMessage) {
        super(code + ":" + errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
        this.resultCode = Code.of(code);
        // 因为一个code对应多个可能，所以只能做一个特殊处理
        if (this.resultCode == Code.APPSECRET_ERROR && errorMessage.startsWith(INVALID_ACCESS_TOKEN_MESSAGE)) {
            this.resultCode = Code.INVALID_ACCESS_TOKEN;
        }
    }

    public WxApiResultException(String errorResult) {
        super(errorResult);
        // 设置code和errorMessage
        prepare(errorResult);
        this.resultCode = Code.of(code);
        if (this.resultCode == Code.APPSECRET_ERROR && errorMessage.startsWith(INVALID_ACCESS_TOKEN_MESSAGE)) {
            this.resultCode = Code.INVALID_ACCESS_TOKEN;
        }
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Code getResultCode() {
        return resultCode;
    }

    public void prepare(String errorResult) {
        this.code = Code.UNKNOWN_ERROR.errcode;
        this.errorMessage = Code.UNKNOWN_ERROR.errdesc;
        try {
            int codeStringStart = errorResult.indexOf(WX_API_RESULT_ERRCODE);
            int codeStart = errorResult.indexOf(":", codeStringStart) + 1;
            int codeEnd = errorResult.indexOf(",", codeStart);
            this.code = Integer.parseInt(errorResult.substring(codeStart, codeEnd));
            int messageStringStart = errorResult.indexOf(WX_API_RESULT_ERRMSG);
            int messageStart = errorResult.indexOf(":", messageStringStart) + 2;
            int messageEnd = errorResult.indexOf("\"", messageStart);
            this.errorMessage = errorResult.substring(messageStart, messageEnd);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static boolean hasException(String result) {
        if (StringUtils.hasLength(result)) {
            // 包含错误码但是错误码不是0
            if (result.contains(WX_API_RESULT_ERRCODE) && !result.contains(WX_API_RESULT_SUCCESS)) {
                return true;
            }
        }
        return false;
    }

    public enum Code {

        SYSTEM_BUSY(-1, "系统繁忙，此时请开发者稍候再试"),

        INVALID_ACCESS_TOKEN(40000, "不合法的认证信息，access_token不合法或者不是最新的"),

        APPSECRET_ERROR(40001, "获取access_token时AppSecret错误，或者access_token无效。请开发者认真比对AppSecret的正确性，或查看是否正在为恰当的公众号调用接口"),

        GRANT_TYPE_ERROR(40002, "不合法的凭证类型，请确保grant_type字段值为client_credential"),

        ILLEGAL_OPENID(40003, "不合法的OpenID，请开发者确认OpenID（该用户）是否已关注公众号，或是否是其他公众号的OpenID"),

        ILLEGAL_MEDIA_TYPE(40004, "不合法的媒体文件类型"),

        ILLEGAL_FILE_TYPE(40005, "不合法的文件类型"),

        ILLEGAL_FILE_SIZE(40006, "不合法的文件大小"),

        ILLEGAL_MEDIA_ID(40007, "不合法的媒体文件id"),

        ILLEGAL_MESSAGE_TYPE(40008, "不合法的消息类型"),

        ILLEGAL_IMAGE_FILE_SIZE(40009, "不合法的图片文件大小"),

        ILLEGAL_VOICE_FILE_SIZE(40010, "不合法的语音文件大小"),

        ILLEGAL_VIDEO_FILE_SIZE(40011, "不合法的视频文件大小"),

        ILLEGAL_THUMB_FILE_SIZE(40012, "不合法的缩略图文件大小"),

        ILLEGAL_APPID(40013, "不合法的AppID，请开发者检查AppID的正确性，避免异常字符，注意大小写"),

        ILLEGAL_ACCESS_TOKEN(40014, "不合法的access_token，请开发者认真比对access_token的有效性（如是否过期），或查看是否正在为恰当的公众号调用接口"),

        ILLEGAL_MENU_TYPE(40015, "不合法的菜单类型"),

        ILLEGAL_MAIN_BUTTON_COUNT(40016, "不合法的父按钮个数"),

        ILLEGAL_BUTTON_COUNT(40017, "不合法的按钮个数"),

        ILLEGAL_BUTTON_NAME_LENGTH(40018, "不合法的按钮名字长度"),

        ILLEGAL_BUTTON_KEY_LENGTH(40019, "不合法的按钮KEY长度"),

        ILLEGAL_BUTTON_URL_LENGTH(40020, "不合法的按钮URL长度"),

        ILLEGAL_MENU_VERSION(40021, "不合法的菜单版本号"),

        ILLEGAL_SUB_MENU_LEVEL(40022, "不合法的子菜单级数"),

        ILLEGAL_SUB_MENU_COUNT(40023, "不合法的子菜单按钮个数"),

        ILLEGAL_SUB_MENU_TYPE(40024, "不合法的子菜单按钮类型"),

        ILLEGAL_SUB_MENU_NAME_LENGTH(40025, "不合法的子菜单按钮名字长度"),

        ILLEGAL_SUB_MENU_KEY_LENGTH(40026, "不合法的子菜单按钮KEY长度"),

        ILLEGAL_SUB_MENU_URL_LENGTH(40027, "不合法的子菜单按钮URL长度"),

        ILLEGAL_CONDITION_MENU_USER(40028, "不合法的自定义菜单使用用户"),

        ILLEGAL_OAUTH_CODE(40029, "不合法的oauth_code"),

        ILLEGAL_REFRESH_TOKEN(40030, "不合法的refresh_token"),

        ILLEGAL_OPENID_LIST(40031, "不合法的openid列表"),

        ILLEGAL_OPENID_LIST_SIZE(40032, "不合法的openid列表长度"),

        ILLEGAL_REQUEST_CHAR(40033, "不合法的请求字符，不能包含\\uxxxx格式的字符"),

        ILLEGAL_REQUEST_PARAM(40035, "不合法的参数"),

        ILLEGAL_REQUEST_FORMAT(40038, "不合法的请求格式"),

        ILLEGAL_URL_LENGTH(40039, "不合法的URL长度"),

        ILLEGAL_GROUP_ID(40050, "不合法的分组id"),

        ILLEGAL_GROUP_NAME(40051, "分组名字不合法"),

        ILLEGAL_ARTICLE_ID(40060, "删除单篇图文时，指定的 article_idx 不合法"),

        ILLEGAL_GROUP_NAME_1(40117, "分组名字不合法"),

        ILLEGAL_MEDIA_ID_SIZE(40118, "media_id大小不合法"),

        ILLEGAL_BUTTON_TYPE(40119, "button类型错误"),

        ILLEGAL_BUTTON_TYPE_1(40120, "button类型错误"),

        ILLEGAL_MEDIA_ID_TYPE(40121, "不合法的media_id类型"),

        ILLEGAL_WEIXIN_ACCOUNT(40132, "微信号不合法"),

        UNSUPPORT_IMAGE_TYPE(40137, "不支持的图片格式"),

        NO_USE_OTHER_INDEX(40155, "请勿添加其他公众号的主页链接"),

        MISSING_ACCESS_TOKEN(41001, "缺少access_token参数"),

        MISSING_APPID(41002, "缺少appid参数"),

        MISSING_REFRESH_TOKEN(41003, "缺少refresh_token参数"),

        MISSING_SECRET(41004, "缺少secret参数"),

        MISSING_MEDIA_FILE_DATA(41005, "缺少多媒体文件数据"),

        MISSING_MEDIA_ID(41006, "缺少media_id参数"),

        MISSING_SUB_MENU_DATA(41007, "缺少子菜单数据"),

        MISSING_OAUTH_CODE(41008, "缺少oauth code"),

        MISSION_OPENID(41009, "缺少openid"),

        EXPIRED_ACCESS_TOKEN(42001, "access_token超时，请检查access_token的有效期，请参考基础支持-获取access_token中，对access_token的详细机制说明"),

        EXPIRED_REFRESH_TOKEN(42002, "refresh_token超时"),

        EXPIRED_OAUTH_CODE(42003, "oauth_code超时"),

        USER_CHANGE_PASSWORD(42007, "用户修改微信密码，accesstoken和refreshtoken失效，需要重新授权"),

        NEED_GET_REQUEST(43001, "需要GET请求"),

        NEED_POST_REQUEST(43002, "需要POST请求"),

        NEED_HTTPS_REQUEST(43003, "需要HTTPS请求"),

        NEED_RECEIVER_SUBSCRIBE(43004, "需要接收者关注"),

        NEED_FRIEND(43005, "需要好友关系"),

        NEED_OUTOF_BLACKLIST(43019, "需要将接收者从黑名单中移除"),

        EMPTY_MEDIA_FILE(44001, "多媒体文件为空"),

        EMPTY_POST_DATA(44002, "POST的数据包为空"),

        EMPTY_NEWS_CONTENT(44003, "图文消息内容为空"),

        EMPTY_TEXT_CONTENT(44004, "文本消息内容为空"),

        LIMITED_MEDIA_FILE_SIZE(45001, "多媒体文件大小超过限制"),

        LIMITED_TEXT_CONTENT(45002, "消息内容超过限制"),

        LIMITED_TITLE(45003, "标题字段超过限制"),

        LIMITED_DESCRIPTION(45004, "描述字段超过限制"),

        LIMITED_URL(45005, "链接字段超过限制"),

        LIMITED_IMGURL(45006, "图片链接字段超过限制"),

        LIMITED_VOICE_DURATION(45007, "语音播放时间超过限制"),

        LIMITED_NEWS(45008, "图文消息超过限制"),

        LIMITED_API_INVOKE(45009, "接口调用超过限制"),

        LIMITED_MENU_COUNT(45010, "创建菜单个数超过限制"),

        LIMITED_API(45011, "API调用太频繁，请稍候再试"),

        LIMITED_REPLY(45015, "回复时间超过限制"),

        SYSTEM_GROUP(45016, "系统分组，不允许修改"),

        LIMITED_GROUP_NAME(45017, "分组名字过长"),

        LIMITED_GROUP_COUNT(45018, "分组数量超过上限"),

        LIMITED_CUSTOME_MESSAGE(45047, "客服接口下行条数超过上限"),

        NOT_FOUND_MEDIA_DATA(46001, "不存在媒体数据"),

        NOT_FOUND_MENU_VERSION(46002, "不存在的菜单版本"),

        NOT_FOUND_MENU_DATA(46003, "不存在的菜单数据"),

        NOT_FOUND_USER(46004, "不存在的用户"),

        ERROR_JSON_XML(47001, "解析JSON/XML内容错误"),

        API_UNAUTHED(48001, "api功能未授权，请确认公众号已获得该接口，可以在公众平台官网-开发者中心页中查看接口权限"),

        MESSAGE_REJEST(48002, "粉丝拒收消息（粉丝在公众号选项中，关闭了“接收消息”）"),

        API_FORBIDDEN(48004, "api接口被封禁，请登录mp.weixin.qq.com查看详情"),

        NOT_ALLOW_DELETE_THIS_MEDIA(48005, "api禁止删除被自动回复和自定义菜单引用的素材"),

        NOT_ALLOW_API_RESET(48006, "api禁止清零调用次数，因为清零次数达到上限"),

        USER_UNAUTHED_API(50001, "用户未授权该api"),

        LIMITED_USER(50002, "用户受限，可能是违规后接口被封禁"),

        INVALID_PARAMETER(61451, "参数错误(invalid parameter)"),

        INVALID_KF_ACCOUNT(61452, "无效客服账号(invalid kf_account)"),

        KF_ACCOUNT_EXSITED(61453, "客服帐号已存在(kf_account exsited)"),

        INVALID_KF_ACOUNT_LENGTH(61454, "客服帐号名长度超过限制(仅允许10个英文字符，不包括@及@后的公众号的微信号)(invalid kf_acount (length)"),

        ILLEGAL_CHARACTER_IN_KF_ACCOUNT(61455, "客服帐号名包含非法字符(仅允许英文+数字)(illegal character in kf_account)"),

        KF_ACCOUNT_COUNT_EXCEEDED(61456, " 客服帐号个数超过限制(10个客服账号)(kf_account count exceeded)"),

        INVALID_FILE_TYPE(61457, "无效头像文件类型(invalid file type)"),

        SYSTEM_ERROR(61450, "系统错误(system error)"),

        ILLEGAL_DATE_FORMAT(61500, "日期格式错误"),

        NOT_FOUND_CONDITION_MENU(65301, "不存在此menuid对应的个性化菜单"),

        NOT_FOUND_THIS_USER(65302, "没有相应的用户"),

        CONDITION_CREATE_BEFORE_DEFAULT(65303, "没有默认菜单，不能创建个性化菜单"),

        MATCH_RULE_EMPTY(65304, "MatchRule信息为空"),

        LIMITED_CONDITION_MENU_COUNT(65305, "个性化菜单数量受限"),

        UNSUPPORT_CONDITION_MENU_ACCOUNT(65306, "不支持个性化菜单的帐号"),

        EMPTY_CONDITION_MENU(65307, "个性化菜单信息为空"),

        CONTAIN_NO_RESPONSE_BUTTON(65308, "包含没有响应类型的button"),

        CONDITION_SWITCH_OFF(65309, "个性化菜单开关处于关闭状态"),

        EMPTY_CONTRY_INFO(65310, "填写了省份或城市信息，国家信息不能为空"),

        EMPTY_PROVINCE_INFO(65311, "填写了城市信息，省份信息不能为空"),

        ILLEGAL_CONTRY_INFO(65312, "不合法的国家信息"),

        ILLEGAL_PROVINCE_INFO(65313, "不合法的省份信息"),

        ILLEGAL_CITY_INFO(65314, "不合法的城市信息"),

        OVERFLOW_CORS_URL(65316, "该公众号的菜单设置了过多的域名外跳（最多跳转到3个域名的链接）"),

        ILLEGAL_URL(65317, "不合法的URL"),

        ILLEGAL_POST_DATA(9001001, "POST数据参数不合法"),

        UNAVAILABLE_REMOTE_SERVICE(9001002, "远端服务不可用"),

        ILLEGAL_TICKET(9001003, "Ticket不合法"),

        SHAKE_INFO_FAILED(9001004, "获取摇周边用户信息失败"),

        COMMERCIAL_INFO_FAILED(9001005, "获取商户信息失败"),

        GET_OPENID_FAILED(9001006, "获取OpenID失败"),

        UPLOAD_FILE_BROKEN(9001007, "上传文件缺失"),

        ILLEGAL_UPLOAD_MEDIA_TYPE(9001008, "上传素材的文件类型不合法"),

        ILLEGAL_UPLOAD_MEDIA_SIZE(9001009, "上传素材的文件尺寸不合法"),

        UPLOAD_FAILED(9001010, "上传失败"),

        ILLEGAL_ACCOUNT(9001020, "帐号不合法"),

        TOO_LOW_ACTIVE(9001021, "已有设备激活率低于50%，不能新增设备"),

        ILLEGAL_DEVICE_COUNT(9001022, "设备申请数不合法，必须为大于0的数字"),

        EXISTED_DEVICE_ID(9001023, "已存在审核中的设备ID申请"),

        TOO_MANY_DEVICE_ID(9001024, "一次查询设备ID数量不能超过50"),

        ILLEGAL_DEVICE_ID(9001025, "设备ID不合法"),

        ILLEGAL_PAGE_ID(9001026, "页面ID不合法"),

        ILLEGAL_PAGE_PARAM(9001027, "页面参数不合法"),

        TOO_MANY_DELETE_PAGE(9001028, "一次删除页面ID数量不能超过10"),

        EXISTED_PAGE_DEVICE(9001029, "页面已应用在设备中，请先解除应用关系再删除"),

        TOO_MANY_QUERY_PAGE_ID(9001030, "一次查询页面ID数量不能超过50"),

        ILLEGAL_DATE_PERIOD(9001031, "时间区间不合法"),

        DEVICE_PAGE_BIND_ERROR(9001032, "保存设备与页面的绑定关系参数错误"),

        ILLEGAL_STORE_ID(9001033, "门店ID不合法"),

        DEVICE_REMARK_TOO_LARGE(9001034, "设备备注信息过长"),

        ILLEGAL_DEVICE_REGIST(9001035, "设备申请参数不合法"),

        ILLEGAL_BEGIN(9001036, "查询起始值begin不合法"),

        SUCCESS(0, "请求成功"),

        INVALID_IP_ERROR(40164, "调用接口的IP地址不在白名单中，请在接口IP白名单中进行设置"),

        UNKNOWN_ERROR(9999999, "未知错误，请查看message");

        int errcode;

        String errdesc;

        Code(int errcode, String errdesc) {
            this.errcode = errcode;
            this.errdesc = errdesc;
        }

        public static Code of(int errcode) {
            return Arrays.stream(Code.values()).filter(v -> v.errcode == errcode).findFirst().orElse(UNKNOWN_ERROR);
        }

    }

}
