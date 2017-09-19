/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.controller;

import com.mxixm.fastboot.weixin.util.CryptUtils;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
