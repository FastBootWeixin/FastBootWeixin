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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mxixm.fastboot.weixin.module.adapter.WxJsonAdapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FastBootWeixin WxCard
 *
 * @author Guangshan
 * @date 2017/09/21 23:29
 * @since 0.1.2
 */
public class WxCard {

    @JsonProperty("card")
    private Card card;

    public Card getCard() {
        return card;
    }

    public static class Card {

        /**
         * 卡券类型
         */
        public enum Type {
            /**
             * 团购券
             */
            GROUPON,
            /**
             * 代金券
             */
            CASH,
            /**
             * 折扣券
             */
            DISCOUNT,
            /**
             * 兑换券
             */
            GIFT,
            /**
             * 优惠券
             */
            GENERAL_COUPON,

            /**
             * 会员卡
             */
            MEMBER_CARD,
            /**
             * 景点门票
             */
            SCENIC_TICKET,
            /**
             * 电影票
             */
            MOVIE_TICKET,
            /**
             * 飞机票
             */
            BOARDING_PASS,
            /**
             * 会议门票
             */
            MEETING_TICKET,
            /**
             * 汽车票
             */
            BUS_TICKET
        }

        @JsonIgnore
        private Body body;

        public Body getBody() {
            switch (cardType) {
                case CASH:
                    return cash;
                case DISCOUNT:
                    return discount;
                case GIFT:
                    return gift;
                case GROUPON:
                    return groupOn;
                case GENERAL_COUPON:
                    return groupOn;
                case MEMBER_CARD:
                    return memberCard;
                default:
                    return null;
            }
        }
        /**
         * 卡券类型。
         */
        @JsonProperty("card_type")
        private Type cardType;

        @JsonProperty("groupon")
        private GroupOn groupOn;

        @JsonProperty("cash")
        private Cash cash;

        @JsonProperty("discount")
        private Discount discount;

        @JsonProperty("gift")
        private Gift gift;

        @JsonProperty("general_coupon")
        private GeneralCoupon generalCoupon;

        @JsonProperty("member_card")
        private MemberCard memberCard;

        public static class Body {

            /**
             * 基本的卡券数据，见下表，所有卡券类型通用。
             */
            @JsonProperty("base_info")
            protected BaseInfo baseInfo;

            @JsonProperty("advanced_info")
            protected AdvancedInfo advancedInfo;

            public BaseInfo getBaseInfo() {
                return baseInfo;
            }

            public void setBaseInfo(BaseInfo baseInfo) {
                this.baseInfo = baseInfo;
            }

            public AdvancedInfo getAdvancedInfo() {
                return advancedInfo;
            }

            public void setAdvancedInfo(AdvancedInfo advancedInfo) {
                this.advancedInfo = advancedInfo;
            }
        }

        /**
         * 团购券
         */
        public static class GroupOn extends Body {

            /**
             * 团购券专用，团购详情。
             */
            @JsonProperty("deal_detail")
            private String dealDetail;

        }

        /**
         * 代金券类型
         */
        public static class Cash extends Body {

            /**
             * 代金券专用，表示起用金额（单位为分）,如果无起用门槛则填0。
             */
            @JsonProperty("least_cost")
            private Integer leaseCost;

            /**
             * 代金券专用，表示减免金额。（单位为分）
             */
            @JsonProperty("reduce_cost")
            private Integer reduceCost;

        }

        /**
         * 折扣券
         */
        public static class Discount extends Body {

            /**
             * 折扣券专用，表示打折额度（百分比）。填30就是七折。
             */
            @JsonProperty("discount")
            private Integer discount;

        }

        /**
         * 兑换券
         */
        public static class Gift extends Body {

            /**
             * 兑换券专用，填写兑换内容的名称。
             */
            @JsonProperty("gift")
            private String gift;

        }

        /**
         * 优惠券
         */
        public static class GeneralCoupon extends Body {

            /**
             * 优惠券专用，填写优惠详情。
             */
            @JsonProperty("deal_detail")
            private String dealDetail;

        }

        /**
         * 会员卡
         */
        public static class MemberCard extends Body {

        }


        /**
         * 基本的卡券数据，见下表，所有卡券通用。
         */
        public static class BaseInfo {

            /**
             * 卡券的唯一ID
             */
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @JsonProperty("id")
            private String id;

            /**
             * 卡券的商户logo，建议像素为300*300。
             */
            @JsonProperty("logo_url")
            private String logoUrl;

            /**
             * 码型
             */
            @JsonProperty("code_type")
            private CodeType codeType;

            /**
             * 码型
             */
            public enum CodeType {
                /**
                 * 文本
                 */
                CODE_TYPE_TEXT,
                /**
                 * 一维码
                 */
                CODE_TYPE_BARCODE,
                /**
                 * 二维码
                 */
                CODE_TYPE_QRCODE,
                /**
                 * 二维码无code显示
                 */
                CODE_TYPE_ONLY_QRCODE,
                /**
                 * 一维码无code显示
                 */
                CODE_TYPE_ONLY_BARCODE,
                /**
                 * 不显示code和条形码类型
                 */
                CODE_TYPE_NONE
            }

            /**
             * 商户名字,字数上限为12个汉字。
             */
            @JsonProperty("brand_name")
            private String brandName;

            /**
             * 卡券名，字数上限为9个汉字。(建议涵盖卡券属性、服务及金额)。
             */
            @JsonProperty("title")
            private String title;

            /**
             * 卡券子标题，字数上限为9个汉字。(建议涵盖卡券属性、服务及金额)。
             */
            @JsonProperty("sub_title")
            private String subTitle;

            /**
             * 券颜色。按色彩规范标注填写Color010-Color100。
             */
            @JsonDeserialize(converter = WxJsonAdapters.WxStringColorConverter.class)
            @JsonProperty("color")
            private Color color;

            /**
             * 券颜色。按色彩规范标注填写Color010-Color100。
             * 具体颜色请查看RGB值
             */
            public enum Color {

                Color010("#63b359"),
                Color020("#2c9f67"),
                Color030("#509fc9"),
                Color040("#5885cf"),
                Color050("#9062c0"),
                Color060("#d09a45"),
                Color070("#e4b138"),
                Color080("#ee903c"),
                Color081("#f08500"),
                Color082("#a9d92d"),
                Color090("#dd6549"),
                Color100("#cc463d"),
                Color101("#cf3e36"),
                Color102("#5E6671");

                private String RGB;

                Color(String RGB) {
                    this.RGB = RGB;
                }

                public String getRGB() {
                    return RGB;
                }

                public static Color of(String RGB) {
                    return Arrays.stream(Color.values()).filter(c -> c.RGB.equals(RGB)).findFirst().orElse(null);
                }
            }

            /**
             * 卡券使用提醒，字数上限为16个汉字。
             */
            @JsonProperty("notice")
            private String notice;

            /**
             * 卡券使用说明，字数上限为1024个汉字。
             */
            @JsonProperty("description")
            private String description;

            /**
             * 使用日期，有效期的信息。
             */
            @JsonProperty("date_info")
            private DateInfo dateInfo;


            /**
             * 使用日期，有效期的信息。
             */
            public static class DateInfo {

                /**
                 * 使用时间的类型，旧文档采用的1和2依然生效。
                 */
                @JsonProperty("type")
                private Type type;

                /**
                 * 使用时间的类型，旧文档采用的1和2依然生效。
                 */
                public enum Type {
                    /**
                     * 表示固定日期区间
                     */
                    DATE_TYPE_FIX_TIME_RANGE,
                    /**
                     * 表示固定时长 自领取后按天算。
                     */
                    DATE_TYPE_FIX_TERM,

                    /**
                     *
                     */
                    DATE_TYPE_PERMANENT
                }

                /**
                 * type为DATE_TYPE_FIX_TIME_RANGE时专用，表示起用时间。从1970年1月1日00:00:00至起用时间的秒数，
                 * 最终需转换为字符串形态传入。（东八区时间,UTC+8，单位为秒）
                 */
                @JsonProperty("begin_timestamp")
                private Long beginTimestamp;

                /**
                 * 表示结束时间，建议设置为截止日期的23:59:59过期。（东八区时间,UTC+8，单位为秒）
                 * 也可以用于DATE_TYPE_FIX_TERM时间类型，表示卡券统一过期时间，建议设置为截止日期的23:59:59过期。
                 * （东八区时间,UTC+8，单位为秒），设置了fixed_term卡券，当时间达到end_timestamp时卡券统一过期
                 */
                @JsonProperty("end_timestamp")
                private Long endTimestamp;

                /**
                 * type为DATE_TYPE_FIX_TERM时专用，表示自领取后多少天内有效，不支持填写0。
                 */
                @JsonProperty("fixed_term")
                private Integer fixedTerm;

                /**
                 * type为DATE_TYPE_FIX_TERM时专用，表示自领取后多少天开始生效，领取后当天生效填写0。（单位为天）
                 */
                @JsonProperty("fixed_begin_term")
                private Integer fixedBeginTerm;

                public Type getType() {
                    return type;
                }

                public void setType(Type type) {
                    this.type = type;
                }

                public Long getBeginTimestamp() {
                    return beginTimestamp;
                }

                public void setBeginTimestamp(Long beginTimestamp) {
                    this.beginTimestamp = beginTimestamp;
                }

                public Long getEndTimestamp() {
                    return endTimestamp;
                }

                public void setEndTimestamp(Long endTimestamp) {
                    this.endTimestamp = endTimestamp;
                }

                public Integer getFixedTerm() {
                    return fixedTerm;
                }

                public void setFixedTerm(Integer fixedTerm) {
                    this.fixedTerm = fixedTerm;
                }

                public Integer getFixedBeginTerm() {
                    return fixedBeginTerm;
                }

                public void setFixedBeginTerm(Integer fixedBeginTerm) {
                    this.fixedBeginTerm = fixedBeginTerm;
                }
            }

            /**
             * 商品信息。
             */
            @JsonProperty("sku")
            private Sku sku;

            /**
             * 商品信息
             */
            public static class Sku {
                /**
                 * 卡券库存的数量，上限为100000000。
                 */
                @JsonProperty("quantity")
                private Integer quantity;

                public Integer getQuantity() {
                    return quantity;
                }

                public void setQuantity(Integer quantity) {
                    this.quantity = quantity;
                }
            }

            /**
             * 是否自定义Code码 。填写true或false，默认为false。
             * 通常自有优惠码系统的开发者选择
             * 自定义Code码，并在卡券投放时带入
             * Code码，详情见是否自定义Code码。https://mp.weixin.qq.com/wiki?action=doc&id=mp1451025056&t=0.8033017180130944#2.2.2
             * 非必填
             */
            @JsonProperty("use_custom_code")
            private Boolean useCustomCode;

            /**
             * 填入GET_CUSTOM_CODE_MODE_DEPOSIT表示该卡券为预存code模式卡券，
             * 须导入超过库存数目的自定义code后方可投放，填入该字段后，quantity字段须为0,须导入code后再增加库存
             * 非必填
             */
            @JsonProperty("get_custom_code_mode")
            private CodeMode getCustomCodeMode;

            /**
             * 参考上面
             */
            public enum CodeMode {
                GET_CUSTOM_CODE_MODE_DEPOSIT
            }

            /**
             * 客服电话。
             * 非必填
             */
            @JsonProperty("service_phone")
            private String servicePhone;

            /**
             * 每人可领券的数量限制,不填写默认为50。
             * 非必填
             */
            @JsonProperty("get_limit")
            private Integer getLimit;

            /**
             * 每人可核销的数量限制,不填写默认为50。
             * 非必填
             */
            @JsonProperty("use_limit")
            private Integer useLimit;

            /**
             * 是否指定用户领取，填写true或false。默认为false。通常指定特殊用户群体投放卡券或防止刷券时选择指定用户领取。
             * 非必填
             */
            @JsonProperty("bind_openid")
            private Boolean bindOpenId;

            /**
             * 卡券领取页面是否可分享。默认false。
             * 非必填
             */
            @JsonProperty("can_share")
            private Boolean canShare;

            /**
             * 卡券是否可转赠。默认false。
             * 非必填
             */
            @JsonProperty("can_give_friend")
            private Boolean canGiveFriend;

            /**
             * 门店位置poiid。调用POI门店管理接口获取门店位置poiid。具备线下门店的商户为必填。
             * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444378120
             * 非必填
             */
            @JsonProperty("location_id_list")
            private List<Integer> locationIdList;

            /**
             * 设置本卡券支持全部门店，与location_id_list互斥
             * 非必填
             */
            @JsonProperty("use_all_locations")
            private Boolean useAllLocations;

            /**
             * 卡券顶部居中的按钮，仅在卡券状态正常(可以核销)时显示
             * 非必填
             */
            @JsonProperty("center_title")
            private String centerTitle;

            /**
             * 显示在入口下方的提示语，仅在卡券状态正常(可以核销)时显示。
             * 非必填
             */
            @JsonProperty("center_sub_title")
            private String centerSubTitle;

            /**
             * 顶部居中的url，仅在卡券状态正常(可以核销)时显示。
             * 非必填
             */
            @JsonProperty("center_url")
            private String centerUrl;

            /**
             * 卡券跳转的小程序的user_name，仅可跳转该公众号绑定的小程序。
             * 非必填
             */
            @JsonProperty("center_app_brand_user_name")
            private String centerAppBrandUserName;

            /**
             * 卡券跳转的小程序的path
             * 非必填
             */
            @JsonProperty("center_app_brand_pass")
            private String centerAppBrandPass;

            /**
             * 自定义跳转外链的入口名字。
             * 非必填
             */
            @JsonProperty("custom_url_name")
            private String customUrlName;

            /**
             * 自定义跳转的URL。
             * 非必填
             */
            @JsonProperty("custom_url")
            private String customUrl;

            /**
             * 显示在入口右侧的提示语。
             * 非必填
             */
            @JsonProperty("custom_url_sub_title")
            private String customUrlSubTitle;

            /**
             * 卡券跳转的小程序的user_name，仅可跳转该公众号绑定的小程序。
             * 非必填
             */
            @JsonProperty("custom_app_brand_user_name")
            private String customAppBrandUserName;

            /**
             * 卡券跳转的小程序的path
             * 非必填
             */
            @JsonProperty("custom_app_brand_pass")
            private String customAppBrandPass;

            /**
             * 营销场景的自定义入口名称。
             * 非必填
             */
            @JsonProperty("promotion_url_name")
            private String promotionUrlName;

            /**
             * 入口跳转外链的地址链接。
             * 非必填
             */
            @JsonProperty("promotion_url")
            private String promotionUrl;

            /**
             * 显示在营销入口右侧的提示语。
             */
            @JsonProperty("promotion_url_sub_title")
            private String promotionUrlSubTitle;


            /**
             * 卡券跳转的小程序的user_name，仅可跳转该公众号绑定的小程序。
             * 非必填
             */
            @JsonProperty("promotion_app_brand_user_name")
            private String promotionAppBrandUserName;

            /**
             * 卡券跳转的小程序的path
             * 非必填
             */
            @JsonProperty("promotion_app_brand_pass")
            private String promotionAppBrandPass;

            /**
             * 文档示例中有，文档中没有？？
             */
            // @JsonProperty("source")
            // private String source;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLogoUrl() {
                return logoUrl;
            }

            public void setLogoUrl(String logoUrl) {
                this.logoUrl = logoUrl;
            }

            public CodeType getCodeType() {
                return codeType;
            }

            public void setCodeType(CodeType codeType) {
                this.codeType = codeType;
            }

            public String getBrandName() {
                return brandName;
            }

            public void setBrandName(String brandName) {
                this.brandName = brandName;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public Color getColor() {
                return color;
            }

            public void setColor(Color color) {
                this.color = color;
            }

            public String getNotice() {
                return notice;
            }

            public void setNotice(String notice) {
                this.notice = notice;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public DateInfo getDateInfo() {
                return dateInfo;
            }

            public void setDateInfo(DateInfo dateInfo) {
                this.dateInfo = dateInfo;
            }

            public Sku getSku() {
                return sku;
            }

            public void setSku(Sku sku) {
                this.sku = sku;
            }

            public Boolean getUseCustomCode() {
                return useCustomCode;
            }

            public void setUseCustomCode(Boolean useCustomCode) {
                this.useCustomCode = useCustomCode;
            }

            public CodeMode getGetCustomCodeMode() {
                return getCustomCodeMode;
            }

            public void setGetCustomCodeMode(CodeMode getCustomCodeMode) {
                this.getCustomCodeMode = getCustomCodeMode;
            }

            public String getServicePhone() {
                return servicePhone;
            }

            public void setServicePhone(String servicePhone) {
                this.servicePhone = servicePhone;
            }

            public Integer getGetLimit() {
                return getLimit;
            }

            public void setGetLimit(Integer getLimit) {
                this.getLimit = getLimit;
            }

            public Integer getUseLimit() {
                return useLimit;
            }

            public void setUseLimit(Integer useLimit) {
                this.useLimit = useLimit;
            }

            public Boolean getBindOpenId() {
                return bindOpenId;
            }

            public void setBindOpenId(Boolean bindOpenId) {
                this.bindOpenId = bindOpenId;
            }

            public Boolean getCanShare() {
                return canShare;
            }

            public void setCanShare(Boolean canShare) {
                this.canShare = canShare;
            }

            public Boolean getCanGiveFriend() {
                return canGiveFriend;
            }

            public void setCanGiveFriend(Boolean canGiveFriend) {
                this.canGiveFriend = canGiveFriend;
            }

            public List<Integer> getLocationIdList() {
                return locationIdList;
            }

            public void setLocationIdList(List<Integer> locationIdList) {
                this.locationIdList = locationIdList;
            }

            public Boolean getUseAllLocations() {
                return useAllLocations;
            }

            public void setUseAllLocations(Boolean useAllLocations) {
                this.useAllLocations = useAllLocations;
            }

            public String getCenterTitle() {
                return centerTitle;
            }

            public void setCenterTitle(String centerTitle) {
                this.centerTitle = centerTitle;
            }

            public String getCenterSubTitle() {
                return centerSubTitle;
            }

            public void setCenterSubTitle(String centerSubTitle) {
                this.centerSubTitle = centerSubTitle;
            }

            public String getCenterUrl() {
                return centerUrl;
            }

            public void setCenterUrl(String centerUrl) {
                this.centerUrl = centerUrl;
            }

            public String getCenterAppBrandUserName() {
                return centerAppBrandUserName;
            }

            public void setCenterAppBrandUserName(String centerAppBrandUserName) {
                this.centerAppBrandUserName = centerAppBrandUserName;
            }

            public String getCenterAppBrandPass() {
                return centerAppBrandPass;
            }

            public void setCenterAppBrandPass(String centerAppBrandPass) {
                this.centerAppBrandPass = centerAppBrandPass;
            }

            public String getCustomUrlName() {
                return customUrlName;
            }

            public void setCustomUrlName(String customUrlName) {
                this.customUrlName = customUrlName;
            }

            public String getCustomUrl() {
                return customUrl;
            }

            public void setCustomUrl(String customUrl) {
                this.customUrl = customUrl;
            }

            public String getCustomUrlSubTitle() {
                return customUrlSubTitle;
            }

            public void setCustomUrlSubTitle(String customUrlSubTitle) {
                this.customUrlSubTitle = customUrlSubTitle;
            }

            public String getCustomAppBrandUserName() {
                return customAppBrandUserName;
            }

            public void setCustomAppBrandUserName(String customAppBrandUserName) {
                this.customAppBrandUserName = customAppBrandUserName;
            }

            public String getCustomAppBrandPass() {
                return customAppBrandPass;
            }

            public void setCustomAppBrandPass(String customAppBrandPass) {
                this.customAppBrandPass = customAppBrandPass;
            }

            public String getPromotionUrlName() {
                return promotionUrlName;
            }

            public void setPromotionUrlName(String promotionUrlName) {
                this.promotionUrlName = promotionUrlName;
            }

            public String getPromotionUrl() {
                return promotionUrl;
            }

            public void setPromotionUrl(String promotionUrl) {
                this.promotionUrl = promotionUrl;
            }

            public String getPromotionUrlSubTitle() {
                return promotionUrlSubTitle;
            }

            public void setPromotionUrlSubTitle(String promotionUrlSubTitle) {
                this.promotionUrlSubTitle = promotionUrlSubTitle;
            }

            public String getPromotionAppBrandUserName() {
                return promotionAppBrandUserName;
            }

            public void setPromotionAppBrandUserName(String promotionAppBrandUserName) {
                this.promotionAppBrandUserName = promotionAppBrandUserName;
            }

            public String getPromotionAppBrandPass() {
                return promotionAppBrandPass;
            }

            public void setPromotionAppBrandPass(String promotionAppBrandPass) {
                this.promotionAppBrandPass = promotionAppBrandPass;
            }

            public String getSubTitle() {
                return subTitle;
            }

            public void setSubTitle(String subTitle) {
                this.subTitle = subTitle;
            }
        }

        /**
         * 创建优惠券特有的高级字段
         * 非必填
         * 1.高级字段为商户额外展示信息字段，非必填,但是填入某些结构体后，须填充完整方可显示：
         * 如填入text_image_list结构体时，须同时传入image_url和text，否则也会报错；
         * 2.填入时间限制字段（time_limit）,只控制显示，不控制实际使用逻辑，不填默认不显示；
         * 3.创建卡券时，开发者填入的时间戳须注意时间戳溢出时间，设置的时间戳须早于2038年1月19日；
         * 4.预存code模式的卡券须设置quantity为0，导入code后方可增加库存；
         * 5.卡券自定义cell跳转小程序支持的最低微信客户端版本为6.5.8，低版本用户仍然会跳转url，高版本会跳转小程序；
         */
        public static class AdvancedInfo {

            /**
             * 使用门槛（条件）字段，若不填写使用条件则在券面拼写：无最低消费限制，全场通用，不限品类；
             * 并在使用说明显示：可与其他优惠共享
             * 非必填
             */
            @JsonProperty("use_condition")
            private UseCondition useCondition;

            public static class UseCondition {

                /**
                 * 指定可用的商品类目，仅用于代金券类型，填入后将在券面拼写适用于xxx
                 * 非必填
                 */
                @JsonProperty("accept_category")
                private String acceptCategory;

                /**
                 * 指定不可用的商品类目，仅用于代金券类型，填入后将在券面拼写不适用于xxxx
                 * 非必填
                 */
                @JsonProperty("reject_category")
                private String rejectCategory;

                /**
                 * 不可以与其他类型共享门槛，填写false时系统将在使用须知里拼写“不可与其他优惠共享”，
                 * 填写true时系统将在使用须知里拼写“可与其他优惠共享”，默认为true
                 */
                @JsonProperty("can_use_with_other_discount")
                private Boolean canUseWithOtherDiscount;

                public String getAcceptCategory() {
                    return acceptCategory;
                }

                public void setAcceptCategory(String acceptCategory) {
                    this.acceptCategory = acceptCategory;
                }

                public String getRejectCategory() {
                    return rejectCategory;
                }

                public void setRejectCategory(String rejectCategory) {
                    this.rejectCategory = rejectCategory;
                }

                public Boolean getCanUseWithOtherDiscount() {
                    return canUseWithOtherDiscount;
                }

                public void setCanUseWithOtherDiscount(Boolean canUseWithOtherDiscount) {
                    this.canUseWithOtherDiscount = canUseWithOtherDiscount;
                }
            }


            /**
             * 满减门槛字段，可用于兑换券和代金券，填入后将在全面拼写消费满xx元可用。
             * 非必填
             */
            @JsonProperty("least_cost")
            private Integer leastCost;

            /**
             * 购买xx可用类型门槛，仅用于兑换，填入后自动拼写购买xxx可用。
             * 非必填
             */
            @JsonProperty("object_use_for")
            private String objectUseFor;

            /**
             * 封面摘要结构体名称
             * 非必填
             */
            @JsonProperty("abstract")
            private Abstracts abstracts;

            public static class Abstracts {

                /**
                 * 封面摘要简介。
                 * 非必填
                 */
                @JsonProperty("abstract")
                private String abstracts;

                /**
                 * 封面图片列表，仅支持填入一个封面图片链接，上传图片接口上传获取图片获得链接，
                 * 填写非CDN链接会报错，并在此填入。建议图片尺寸像素850*350
                 * 非必填
                 */
                @JsonProperty("icon_url_list")
                private List<String> iconUrlList;

                public String getAbstracts() {
                    return abstracts;
                }

                public void setAbstracts(String abstracts) {
                    this.abstracts = abstracts;
                }

                public List<String> getIconUrlList() {
                    return iconUrlList;
                }

                public void setIconUrlList(List<String> iconUrlList) {
                    this.iconUrlList = iconUrlList;
                }
            }


            /**
             * 图文列表，显示在详情内页，优惠券券开发者须至少传入一组图文列表
             * 非必填
             */
            @JsonProperty("text_image_list")
            private List<TextImage> textImageList;

            public static class TextImage {

                /**
                 * 图片链接，必须调用上传图片接口上传图片获得链接，并在此填入，否则报错
                 * 非必填
                 */
                @JsonProperty("image_url")
                private String imageUrl;

                /**
                 * 图文描述
                 * 非必填
                 */
                @JsonProperty("text")
                private String text;

                public String getImageUrl() {
                    return imageUrl;
                }

                public void setImageUrl(String imageUrl) {
                    this.imageUrl = imageUrl;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }
            }

            /**
             * 商家服务类型
             * 非必填
             */
            @JsonProperty("business_service")
            private List<BusinessService> businessService;

            public enum BusinessService {
                /**
                 * 外卖服务
                 */
                BIZ_SERVICE_DELIVER,
                /**
                 * 停车位
                 */
                BIZ_SERVICE_FREE_PARK,
                /**
                 * 可带宠物
                 */
                BIZ_SERVICE_WITH_PET,
                /**
                 * 免费wifi，可多选
                 */
                BIZ_SERVICE_FREE_WIFI

            }

            /**
             * 使用时段限制，包含以下字段
             * 非必填
             */
            @JsonProperty("time_limit")
            private List<TimeLimit> timeLimits;

            public UseCondition getUseCondition() {
                return useCondition;
            }

            public void setUseCondition(UseCondition useCondition) {
                this.useCondition = useCondition;
            }

            public Integer getLeastCost() {
                return leastCost;
            }

            public void setLeastCost(Integer leastCost) {
                this.leastCost = leastCost;
            }

            public String getObjectUseFor() {
                return objectUseFor;
            }

            public void setObjectUseFor(String objectUseFor) {
                this.objectUseFor = objectUseFor;
            }

            public Abstracts getAbstracts() {
                return abstracts;
            }

            public void setAbstracts(Abstracts abstracts) {
                this.abstracts = abstracts;
            }

            public List<TextImage> getTextImageList() {
                return textImageList;
            }

            public void setTextImageList(List<TextImage> textImageList) {
                this.textImageList = textImageList;
            }

            public List<BusinessService> getBusinessService() {
                return businessService;
            }

            public void setBusinessService(List<BusinessService> businessService) {
                this.businessService = businessService;
            }

            public List<TimeLimit> getTimeLimits() {
                return timeLimits;
            }

            public void setTimeLimits(List<TimeLimit> timeLimits) {
                this.timeLimits = timeLimits;
            }

            public static class TimeLimit {

                /**
                 * 限制类型枚举值：支持填入
                 * @see Type
                 * 此处只控制显示， 不控制实际使用逻辑，不填默认不显示
                 * 非必填
                 */
                @JsonProperty("type")
                private String type;

                /**
                 * 限制类型枚举值：支持填入
                 */
                public enum Type {
                    MONDAY,
                    TUESDAY,
                    WEDNESDAY,
                    THURSDAY,
                    FRIDAY,
                    SATURDAY,
                    SUNDAY,
                    HOLIDAY
                }

                /**
                 * 当前type类型下的起始时间（小时），如当前结构体内填写了MONDAY，
                 * 此处填写了10，则此处表示周一 10:00可用
                 * 非必填
                 */
                @JsonProperty("begin_hour")
                private Integer beginHour;
                /**
                 * 当前type类型下的起始时间（分钟），如当前结构体内填写了MONDAY，
                 * begin_hour填写10，此处填写了59，则此处表示周一 10:59可用
                 * 非必填
                 */
                @JsonProperty("begin_minute")
                private Integer beginMinute;
                /**
                 * 当前type类型下的结束时间（小时），如当前结构体内填写了MONDAY，
                 * 此处填写了20，则此处表示周一 10:00-20:00可用
                 * 非必填
                 */
                @JsonProperty("end_hour")
                private Integer endHour;
                /**
                 * 当前type类型下的结束时间（分钟），如当前结构体内填写了MONDAY，begin_hour填写10，此处填写了59，
                 * 则此处表示周一 10:59-00:59可用
                 * 非必填
                 */
                @JsonProperty("end_minute")
                private Integer endMinute;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public Integer getBeginHour() {
                    return beginHour;
                }

                public void setBeginHour(Integer beginHour) {
                    this.beginHour = beginHour;
                }

                public Integer getBeginMinute() {
                    return beginMinute;
                }

                public void setBeginMinute(Integer beginMinute) {
                    this.beginMinute = beginMinute;
                }

                public Integer getEndHour() {
                    return endHour;
                }

                public void setEndHour(Integer endHour) {
                    this.endHour = endHour;
                }

                public Integer getEndMinute() {
                    return endMinute;
                }

                public void setEndMinute(Integer endMinute) {
                    this.endMinute = endMinute;
                }
            }
        }

    }

    /**
     * 设置测试白名单
     *
     */
    public static class WhiteList {

        @JsonProperty("openid")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> openIds;

        @JsonProperty("usernames")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> usernames;

        WhiteList(List<String> openIds, List<String> usernames) {
            this.openIds = openIds;
            this.usernames = usernames;
        }

        public List<String> getOpenIds() {
            return openIds;
        }

        public void setOpenIds(List<String> openIds) {
            this.openIds = openIds;
        }

        public List<String> getUsernames() {
            return usernames;
        }

        public void setUsernames(List<String> usernames) {
            this.usernames = usernames;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private List<String> openIds;
            private List<String> usernames;

            Builder() {
                openIds = new ArrayList<>();
                usernames = new ArrayList<>();
            }

            public Builder addOpenId(List<String> openIds) {
                this.openIds.addAll(openIds);
                return this;
            }

            public Builder addUsername(List<String> usernames) {
                this.usernames.addAll(usernames);
                return this;
            }

            public Builder addOpenId(String openId) {
                this.openIds.add(openId);
                return this;
            }

            public Builder addUsername(String username) {
                this.usernames.add(username);
                return this;
            }

            public WhiteList build() {
                return new WhiteList(openIds, usernames);
            }

            @Override
            public String toString() {
                return "com.mxixm.fastboot.weixin.module.card.WxCard.WhiteList.Builder(openIds=" + this.openIds + ", usernames=" + this.usernames + ")";
            }
        }
    }

    public static class CardSelector {

        @JsonProperty("card_id")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String cardId;

        /**
         * 卡券Code码。一张卡券的唯一标识，核销卡券时使用此串码，支持商户自定义。
         */
        @JsonProperty("code")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String code;

        /**
         * 简单工厂方法
         * @param code
         * @return Comsume
         */
        public static CardSelector consume(String code) {
            CardSelector cardSelector = new CardSelector();
            cardSelector.code = code;
            return cardSelector;
        }

        /**
         * 简单工厂方法
         * @param cardId
         * @return Comsume
         */
        public static CardSelector info(String cardId) {
            CardSelector cardSelector = new CardSelector();
            cardSelector.cardId = cardId;
            return cardSelector;
        }

    }

    /**
     * 用于查询卡券
     */
    public static class PageParam {

        @JsonProperty("offset")
        private int offset;

        @JsonProperty("count")
        private int count;

        @JsonProperty("status_list")
        private List<Status> statusList;

        public static PageParam of(int offset, int count, Status... statuses) {
            PageParam pageParam = new PageParam();
            pageParam.offset = offset;
            pageParam.count = count;
            pageParam.statusList = Arrays.asList(statuses);
            return pageParam;
        }

        public static PageParam of(Status... statuses) {
            return of(0, 50, statuses);
        }

    }

    /**
     * 卡券状态
     */
    public enum Status {
        /**
         * 待审核
         */
        CARD_STATUS_NOT_VERIFY,
        /**
         * 审核失败
         */
        CARD_STATUS_VERIFY_FAIL,
        /**
         * 通过审核
         */
        CARD_STATUS_VERIFY_OK,
        /**
         * 卡券被商户删除
         */
        CARD_STATUS_DELETE,
        /**
         * 在公众平台投放过的卡券
         */
        CARD_STATUS_DISPATCH
    }

    /**
     * 卡券列表
     */
    public static class PageResult {

        @JsonProperty("errcode")
        private Integer errorCode;

        @JsonProperty("errmsg")
        private String errorMessage;

        @JsonProperty("card_id")
        private String cardId;

        @JsonProperty("card_id_list")
        private List<String> cardIdList;

        @JsonProperty("total_num")
        private Integer totalNum;

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public List<String> getCardIdList() {
            return cardIdList;
        }

        public void setCardIdList(List<String> cardIdList) {
            this.cardIdList = cardIdList;
        }

        public Integer getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(Integer totalNum) {
            this.totalNum = totalNum;
        }
    }

}
