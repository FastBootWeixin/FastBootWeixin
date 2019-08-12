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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * fastboot-weixin  WxTagUser
 * todo 待重构，修改成Param和Result的风格
 *
 * @author Guangshan
 * @date 2017/9/23 22:57
 * @since 0.1.2
 */
public class WxTagUser {

    @JsonProperty("tagid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer tagId;

    @JsonProperty("openid_list")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> openIdList;

    @JsonProperty("openid")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String openId;

    @JsonProperty("next_openid")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String nextOpenId;

    /**
     * tagging与untagging使用
     * @param tagId
     * @param openIdList
     * @return WxTagUser
     */
    public static WxTagUser tagUser(Integer tagId, List<String> openIdList) {
        WxTagUser wxTagUser = new WxTagUser();
        wxTagUser.tagId = tagId;
        wxTagUser.openIdList = openIdList;
        return wxTagUser;
    }

    /**
     * 获取tag下用户列表使用
     * @param tagId
     * @return WxTagUser
     */
    public static WxTagUser listUser(Integer tagId) {
        WxTagUser wxTagUser = new WxTagUser();
        wxTagUser.tagId = tagId;
        return wxTagUser;
    }

    /**
     * 获取tag下用户列表使用
     * @param tagId
     * @return WxTagUser
     */
    public static WxTagUser listUser(Integer tagId, String nextOpenId) {
        WxTagUser wxTagUser = new WxTagUser();
        wxTagUser.tagId = tagId;
        wxTagUser.nextOpenId = nextOpenId;
        return wxTagUser;
    }

    /**
     * 获取openId的tag使用
     * @param openId
     * @return WxTagUser
     */
    public static WxTagUser listTag(String openId) {
        WxTagUser wxTagUser = new WxTagUser();
        wxTagUser.openId = openId;
        return wxTagUser;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public List<String> getOpenIdList() {
        return openIdList;
    }

    public void setOpenIdList(List<String> openIdList) {
        this.openIdList = openIdList;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getNextOpenId() {
        return nextOpenId;
    }

    public void setNextOpenId(String nextOpenId) {
        this.nextOpenId = nextOpenId;
    }

    public static class TagIdList {

        @JsonProperty("tagid_list")
        private List<Integer> tagIdList;

        public List<Integer> getTagIdList() {
            return tagIdList;
        }
    }

}
