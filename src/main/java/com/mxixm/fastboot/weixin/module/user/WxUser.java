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

package com.mxixm.fastboot.weixin.module.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mxixm.fastboot.weixin.module.adapter.WxJsonAdapters;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * FastBootWeixin WxUser
 *
 * @author Guangshan
 * @date 2017/8/5 22:29
 * @since 0.1.2
 */
public class WxUser implements Serializable {

    public enum SubscribeScene {
        /**
         * 公众号搜索
         */
        ADD_SCENE_SEARCH,
        /**
         * 公众号迁移
         */
        ADD_SCENE_ACCOUNT_MIGRATION,
        /**
         * 名片分享
         */
        ADD_SCENE_PROFILE_CARD,
        /**
         * 扫描二维码
         */
        ADD_SCENE_QR_CODE,
        /**
         * LINK 图文页内名称点击
         */
        ADD_SCENEPROFILE,
        /**
         * 图文页右上角菜单
         */
        ADD_SCENE_PROFILE_ITEM,
        /**
         * 支付后关注
         */
        ADD_SCENE_PAID,
        /**
         * 其他
         */
        ADD_SCENE_OTHERS
    }

    /**
     * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
     */
    @JsonProperty("subscribe")
    private Integer subscribe;

    /**
     * 用户的标识，对当前公众号唯一
     */
    @JsonProperty("openid")
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

    /**
     * subscribe_scene
     * 返回用户关注的渠道来源
     * ADD_SCENE_SEARCH 公众号搜索
     * ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移
     * ADD_SCENE_PROFILE_CARD 名片分享
     * ADD_SCENE_QR_CODE 扫描二维码
     * ADD_SCENEPROFILE LINK 图文页内名称点击
     * ADD_SCENE_PROFILE_ITEM 图文页右上角菜单
     * ADD_SCENE_PAID 支付后关注
     * ADD_SCENE_OTHERS 其他
     */
    @JsonProperty("subscribe_scene")
    private String subscribeScene;

    /**
     * 二维码扫码场景（开发者自定义）
     */
    @JsonProperty("qr_scene")
    private String qrScene;

    /**
     * 二维码扫码场景描述（开发者自定义）
     */
    @JsonProperty("qr_scene_str")
    private String qrSceneStr;

    public WxUser() {
    }

    public Integer getSubscribe() {
        return this.subscribe;
    }

    public String getOpenId() {
        return this.openId;
    }

    public String getNickName() {
        return this.nickName;
    }

    public Integer getSex() {
        return this.sex;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public String getProvince() {
        return this.province;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getHeadImgUrl() {
        return this.headImgUrl;
    }

    public Date getSubscribeTime() {
        return this.subscribeTime;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public String getRemark() {
        return this.remark;
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public List<String> getPrivileges() {
        return this.privileges;
    }

    public List<Integer> getTagIdList() {
        return this.tagIdList;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public void setTagIdList(List<Integer> tagIdList) {
        this.tagIdList = tagIdList;
    }

    public String getSubscribeScene() {
        return subscribeScene;
    }

    public SubscribeScene getSubscribeSceneEnum() {
        return SubscribeScene.valueOf(this.getSubscribeScene());
    }

    public void setSubscribeScene(String subscribeScene) {
        this.subscribeScene = subscribeScene;
    }

    public String getQrScene() {
        return qrScene;
    }

    public void setQrScene(String qrScene) {
        this.qrScene = qrScene;
    }

    public String getQrSceneStr() {
        return qrSceneStr;
    }

    public void setQrSceneStr(String qrSceneStr) {
        this.qrSceneStr = qrSceneStr;
    }

    @Override
    public String toString() {
        return "WxUser{" +
                "subscribe=" + subscribe +
                ", openId='" + openId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sex=" + sex +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", language='" + language + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", subscribeTime=" + subscribeTime +
                ", unionId='" + unionId + '\'' +
                ", remark='" + remark + '\'' +
                ", groupId=" + groupId +
                ", privileges=" + privileges +
                ", tagIdList=" + tagIdList +
                ", subscribeScene='" + subscribeScene + '\'' +
                ", qrScene='" + qrScene + '\'' +
                ", qrSceneStr='" + qrSceneStr + '\'' +
                '}';
    }

    public static class PageResult {

        @JsonProperty("count")
        private Integer count;

        @JsonProperty("total")
        private Integer total;

        @JsonProperty("data")
        private Data data;

        @JsonProperty("next_openid")
        private String nextOpenId;

        public static class Data {
            @JsonProperty("openid")
            private List<String> openIdList;

            public List<String> getOpenIdList() {
                return openIdList;
            }
        }

        public Integer getCount() {
            return count;
        }

        public Data getData() {
            return data;
        }

        public String getNextOpenId() {
            return nextOpenId;
        }

        public List<String> getOpenIdList() {
            return data.openIdList;
        }

        public Integer getTotal() {
            return total;
        }

    }

}
