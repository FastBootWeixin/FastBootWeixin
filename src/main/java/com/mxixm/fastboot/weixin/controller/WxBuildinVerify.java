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

package com.mxixm.fastboot.weixin.controller;

import com.mxixm.fastboot.weixin.util.CryptUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FastBootWeixin WxBuildinVerify
 * 可以优化成内置的方式
 * 之后可以改造为endPoint方式
 *
 * @author Guangshan
 * @date 2017/7/16 23:37
 * @since 0.1.2
 */
public class WxBuildinVerify {

    private final String token;

    public WxBuildinVerify(String token) {
        this.token = token;
    }

    @ResponseBody
    public String verify(String signature, String timestamp, String nonce, String echostr) {
        String rawString = Stream.of(token, timestamp, nonce).sorted().collect(Collectors.joining());
        if (signature.equals(CryptUtils.encryptSHA1(rawString))) {
            return echostr;
        }
        return null;
    }

}
