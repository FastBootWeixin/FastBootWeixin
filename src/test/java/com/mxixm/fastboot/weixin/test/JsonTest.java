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

package com.mxixm.fastboot.weixin.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxixm.fastboot.weixin.module.extend.WxCard;

import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.IOException;

public class JsonTest {

    public enum SS {

        A, B, C

    }

    public static class S {

        private SS ss;

        public SS getSs() {
            return ss;
        }

        public void setSs(SS ss) {
            this.ss = ss;
        }
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper1 = new ObjectMapper();
//        objectMapper1.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        String ss = "{\"ss\":\"a\"}";
        S ab = objectMapper1.readValue(ss, S.class);


        String s = null;
        Test.test(s);
        Test test = new Test();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(test));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String text = "{\n" +
                "    \"errcode\": 0,\n" +
                "    \"errmsg\": \"ok\",\n" +
                "    \"card\": {\n" +
                "        \"card_type\": \"MEMBER_CARD\",\n" +
                "        \"member_card\": {\n" +
                "            \"base_info\": {\n" +
                "                \"id\": \"pi1hxwBxNY9mXj7WMYGwUDHt5ZeM\",\n" +
                "                \"logo_url\": \"http://mmbiz.qpic.cn/mmbiz_jpg/YDfptbsN0Ydm1AQqTsTutyjZiaSj6bT4tHzZS4RFpSk93p62W7mPOZW0g3BsqPrqtc3CP9ibVCCriaCiacTibw3MlGA/0?wx_fmt=jpeg\",\n" +
                "                \"code_type\": \"CODE_TYPE_TEXT\",\n" +
                "                \"brand_name\": \"食荳集市CHO-COLLECTOR\",\n" +
                "                \"title\": \"会员卡\",\n" +
                "                \"date_info\": {\n" +
                "                    \"type\": \"DATE_TYPE_PERMANENT\"\n" +
                "                },\n" +
                "                \"color\": \"#ee903c\",\n" +
                "                \"notice\": \"到店请出示会员卡号\",\n" +
                "                \"description\": \"详情请咨询店内工作人员\\n本店享有最终解释权\",\n" +
                "                \"location_id_list\": [\n" +
                "                    481397789,\n" +
                "                    481397788,\n" +
                "                    481397791\n" +
                "                ],\n" +
                "                \"get_limit\": 1,\n" +
                "                \"can_share\": false,\n" +
                "                \"can_give_friend\": false,\n" +
                "                \"use_custom_code\": false,\n" +
                "                \"status\": \"CARD_STATUS_VERIFY_OK\",\n" +
                "                \"sku\": {\n" +
                "                    \"quantity\": 99999897,\n" +
                "                    \"total_quantity\": 100000000\n" +
                "                },\n" +
                "                \"create_time\": 1505884577,\n" +
                "                \"update_time\": 1505979627,\n" +
                "                \"area_code_list\": []\n" +
                "            },\n" +
                "            \"supply_bonus\": false,\n" +
                "            \"bonus_url\": \"https://cvip.meituan.com/point\",\n" +
                "            \"supply_balance\": false,\n" +
                "            \"balance_url\": \"https://cvip.meituan.com/deposit\",\n" +
                "            \"prerogative\": \"会员卡:\\n消费20.0元可获得1积分\\n得到五元代金券1张 \\n凡食荳集市会员可享受所有菜品八五折优惠（不含饮品）\\n\\n\",\n" +
                "            \"activate_url\": \"\",\n" +
                "            \"custom_cell1\": {\n" +
                "                \"name\": \"立即使用\",\n" +
                "                \"url\": \"https://cvip.meituan.com/member\"\n" +
                "            },\n" +
                "            \"auto_activate\": false,\n" +
                "            \"wx_activate\": true,\n" +
                "            \"wx_activate_after_submit\": false,\n" +
                "            \"wx_activate_after_submit_url\": \"\",\n" +
                "            \"advanced_info\": {\n" +
                "                \"time_limit\": [],\n" +
                "                \"text_image_list\": [],\n" +
                "                \"business_service\": [],\n" +
                "                \"consume_share_card_list\": [],\n" +
                "                \"share_friends\": false\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        WxCard wxCard = objectMapper.readValue(text, WxCard.class);
        System.out.println(wxCard);


    }

    public static class Test {

        @XmlElementWrapper(name = "test")
        private String a;

        @JsonProperty("b")
        private String b;

        public static void test(String... ab) {
            System.out.println(ab);
        }

    }

}
