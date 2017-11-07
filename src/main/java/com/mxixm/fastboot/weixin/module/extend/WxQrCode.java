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

package com.mxixm.fastboot.weixin.module.extend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FastBootWeixin WxQrCode
 *
 * @author Guangshan
 * @date 2017/9/23 17:15
 * @since 0.1.2
 */
public class WxQrCode {

    public final static String QR_SCENE_SUFFIX = "qrscene_";

    WxQrCode(int expireSeconds, Action action, ActionInfo actionInfo) {
        this.expireSeconds = expireSeconds;
        this.action = action;
        this.actionInfo = actionInfo;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
     * QR_CARD为二维码卡券
     */
    public enum Action {
        QR_SCENE, QR_STR_SCENE, QR_LIMIT_SCENE, QR_LIMIT_STR_SCENE, QR_CARD
    }

    /**
     * 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。
     */
    @JsonProperty("expire_seconds")
    private int expireSeconds;

    /**
     * @see Action
     */
    @JsonProperty("action_name")
    private Action action;

    /**
     * 二维码详细信息
     */
    @JsonProperty("action_info")
    private ActionInfo actionInfo;


    public static class ActionInfo {

        @JsonProperty("scene")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Scene scene;

        @JsonProperty("card")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Card card;

        public static class Card {

            @JsonProperty("card_id")
            private String cardId;

        }

        public static class Scene {
            /**
             * 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
             */
            @JsonProperty("scene_id")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Integer sceneId;

            /**
             * 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
             */
            @JsonProperty("scene_str")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String sceneStr;
        }

    }

    public static class Builder {

        private int expireSeconds;

        private Action action;

        private ActionInfo actionInfo = new ActionInfo();

        Builder() {
        }

        /**
         * 临时二维码
         * @param expireSeconds
         * @return Builder
         */
        public Builder temporary(int expireSeconds, Integer sceneId) {
            this.expireSeconds = expireSeconds;
            this.action = Action.QR_SCENE;
            this.actionInfo.scene = new ActionInfo.Scene();
            this.actionInfo.scene.sceneId = sceneId;
            return this;
        }

        /**
         * 临时二维码
         * @param expireSeconds
         * @return Builder
         */
        public Builder temporary(int expireSeconds, String sceneStr) {
            this.expireSeconds = expireSeconds;
            this.action = Action.QR_STR_SCENE;
            this.actionInfo.scene = new ActionInfo.Scene();
            this.actionInfo.scene.sceneStr = sceneStr;
            return this;
        }

        /**
         * 临时二维码
         * 默认最长时间
         * @return Builder
         */
        public Builder temporary(Integer sceneId) {
            return temporary(30 * 24 * 60 * 60, sceneId);
        }

        /**
         * 临时二维码
         * 默认最长时间
         * @return Builder
         */
        public Builder temporary(String sceneStr) {
            return temporary(30 * 24 * 60 * 60, sceneStr);
        }

        /**
         * 永久二维码
         * @param sceneId
         * @return Builder
         */
        public Builder permanent(Integer sceneId) {
            this.action = Action.QR_LIMIT_SCENE;
            this.actionInfo.scene = new ActionInfo.Scene();
            this.actionInfo.scene.sceneId = sceneId;
            return this;
        }

        /**
         * 永久二维码
         * @param sceneStr
         * @return Builder
         */
        public Builder permanent(String sceneStr) {
            this.action = Action.QR_LIMIT_STR_SCENE;
            this.actionInfo.scene = new ActionInfo.Scene();
            this.actionInfo.scene.sceneStr = sceneStr;
            return this;
        }

        /**
         * 卡券二维码
         * @param cardId
         * @return Builder
         */
        public Builder card(String cardId) {
            this.action = Action.QR_CARD;
            this.actionInfo.card = new ActionInfo.Card();
            this.actionInfo.card.cardId = cardId;
            return this;
        }

        public WxQrCode build() {
            return new WxQrCode(expireSeconds, action, actionInfo);
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.extend.WxQrCode.WxQrCodeBuilder(expireSeconds=" + this.expireSeconds + ", action=" + this.action + ", actionInfo=" + this.actionInfo + ")";
        }
    }


    /**
     * 二维码请求结果
     */
    public static class Result {

        /**
         * 获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
         */
        @JsonProperty("ticket")
        private String ticket;

        /**
         * 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。
         */
        @JsonProperty("expire_seconds")
        private int expireSeconds;

        /**
         * 二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
         * 这个字段不知道是干哈的，网上也没查到
         */
        @JsonProperty("url")
        private String url;

        /**
         * 展示二维码用的图片
         */
        private String showUrl;

        public String getShowUrl() {
            return showUrl;
        }

        public void setShowUrl(String showUrl) {
            this.showUrl = showUrl;
        }

        public String getTicket() {
            return ticket;
        }

        public int getExpireSeconds() {
            return expireSeconds;
        }

        public String getUrl() {
            return url;
        }
    }

}
