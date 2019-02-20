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

package com.mxixm.fastboot.weixin.service;

import com.mxixm.fastboot.weixin.util.CryptUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FastBootWeixin WxVerifyHttpHandler
 * 更换内置校验方式，暂时不启用
 *
 * @author Guangshan
 * @date 2018-12-2 16:19:10
 * @since 0.7.0
 */
public class WxVerifyHttpHandler implements HttpRequestHandler {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final String token;

    public WxVerifyHttpHandler(String token) {
        this.token = token;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        logger.info("======verify start======");
        logger.info("signature:" + signature + "," + "timestamp:" + timestamp + "," + "nonce:" + nonce + "," + "echostr:" + echostr);
        String rawString = Stream.of(token, timestamp, nonce).sorted().collect(Collectors.joining());
        if (signature.equals(CryptUtils.encryptSHA1(rawString))) {
            logger.info("======verify success end======");
            response.getOutputStream().print(echostr);
            response.flushBuffer();
        }
        logger.info("======verify failed end, and before sha1 string is " + rawString + " ======");
    }
}
