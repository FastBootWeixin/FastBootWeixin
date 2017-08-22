package com.mxixm.fastbootwx.module.user;

import com.mxixm.fastbootwx.module.message.adapters.WxJsonAdapters;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * FastBootWeixin  WxUser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxUser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/5 22:29
 */
@Data
public class WxUser {

    /**
     * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
     */
    @JsonProperty("subscribe")
    private String subscribe;

    /**
     * 用户的标识，对当前公众号唯一
     */
    @JsonProperty("openId")
    private String openId;

    /**
     * 用户的昵称
     */
    @JsonProperty("nickname")
    private String nickName;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    @JsonProperty("sex")
    private Integer sex;
    /**
     *
     * 用户所在城市
     */
    @JsonProperty("city")
    private String city;

    /**
     * 用户所在国家
     */
    @JsonProperty("country")
    private String country;

    /**
     * 用户所在省份
     */
    @JsonProperty("province")
    private String province;

    /**
     * 用户的语言，简体中文为zh_CN
     */
    @JsonProperty("language")
    private String language;

    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），
     * 用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    @JsonProperty("headimgurl")
    private String headImgUrl;

    /**
     * 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
     */
    @JsonDeserialize(converter = WxJsonAdapters.WxIntDateConverter.class)
    @JsonProperty("subscribe_time")
    private Date subscribeTime;

    /**
     * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    @JsonProperty("unionid")
    private String unionId;

    /**
     * 公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
     */
    @JsonProperty("remark")
    private String remark;

    /**
     * 用户所在的分组ID（兼容旧的用户分组接口）
     */
    @JsonProperty("groupid")
    private Integer groupId;

    /**
     * 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     */
    @JsonProperty("privilege")
    private List<String> privileges;

    /**
     * 用户被打上的标签ID列表
     * 理论上是个list，暂时偷懒不写转换器了
     */
    @JsonProperty("tagid_list")
    private List<Integer> tagIdList;
}
