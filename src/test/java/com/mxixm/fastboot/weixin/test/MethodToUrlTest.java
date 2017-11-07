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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mxixm.fastboot.weixin.service.WxBuildinVerifyService;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class MethodToUrlTest {

    public static void main(String[] args) throws JsonProcessingException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://api.wx.com");
        MvcUriComponentsBuilder.fromMethod(builder, WxBuildinVerifyService.class, ClassUtils.getMethod(WxBuildinVerifyService.class, "verify", null), "a", "a", "a", "a");
        System.out.println(builder);
    }

}
