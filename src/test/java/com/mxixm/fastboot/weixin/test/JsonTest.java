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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxixm.fastboot.weixin.module.extend.WxCard;

import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.IOException;

public class JsonTest {

    public static void main(String[] args) throws IOException {
        Test test = new Test();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(test));
        String text = "{\"errcode\":0,\"errmsg\":\"ok\",\"card\":{\"card_type\":\"DISCOUNT\",\"discount\":{\"base_info\":{\"id\":\"pKS9_xMBmNqlcWD-uAkD1pOy09Qw\",\"logo_url\":\"http:\\/\\/mmbiz.qpic.cn\\/mmbiz\\/iaL1LJM1mF9aRKPZJkmG8xXhiaHqkKSVMMWeN3hLut7X7hicFNjakmx ibMLGWpXrEXB33367o7zHN0CwngnQY7zb7g\\/0\",\"code_type\":\"CODE_TYPE_TEXT\",\"brand_name\":\"光闪餐厅\",\"title\":\"666元双人火锅套餐\",\"sub_title\":\"周末狂欢必备\",\"date_info\":{\"type\":\"DATE_TYPE_FIX_TERM\",\"fixed_term\":15,\"fixed_begin_term\":0},\"color\":\"#63b359\",\"notice\":\"使用时向服务员出示此券\",\"service_phone\":\"020-88888888\",\"description\":\"不可与其他优惠同享如需团购券发票，请在消时向商户提出店内均可使用，仅限堂食\",\"location_id_list\":[],\"status\":\"CARD_STATUS_NOT_VERIFY\",\"sku\":{\"quantity\":499999,\"total_quantity\":500000},\"create_time\":1506178445,\"update_time\":1506178445,\"custom_url_name\":\"立即使用\",\"custom_url\":\"http:\\/\\/www.qq.com\",\"custom_url_sub_title\":\"6个汉字tips\",\"promotion_url\":\"http:\\/\\/www.qq.com\",\"promotion_url_name\":\"更多优惠\",\"area_code_list\":[]},\"discount\":30,\"advanced_info\":{\"time_limit\":[],\"text_image_list\":[],\"business_service\":[],\"consume_share_card_list\":[],\"share_friends\":false}}}}";
        WxCard wxCard = objectMapper.readValue(text, WxCard.class);
        System.out.println(wxCard);


    }

    public static class Test {

        @XmlElementWrapper(name = "test")
        private String a;

        @JsonProperty("b")
        private String b;

    }

}
