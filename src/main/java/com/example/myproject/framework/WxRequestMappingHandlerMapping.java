/*
 * Copyright 2002-2016 the original author or authors.
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

package com.example.myproject.framework;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.servlet.http.HttpServletRequest;

public class WxRequestMappingHandlerMapping extends AbstractHandlerMapping {

    private final String[] WX_VERIFY_PARAMS = new String[]{
            "echostr", "nonce", "signature", "timestamp"
    };

    private final String[] WX_POST_PARAMS = new String[]{
            "openid", "nonce", "signature", "timestamp"
    };

    private final ParamsRequestCondition WX_VERIFY_PARAMS_CONDITION = new ParamsRequestCondition(this.WX_VERIFY_PARAMS);

    private final ParamsRequestCondition WX_POST_PARAMS_CONDITION = new ParamsRequestCondition(this.WX_POST_PARAMS);

    private final ConsumesRequestCondition WX_POST_CONSUMES_CONDITION = new ConsumesRequestCondition(MediaType.TEXT_XML.getType());

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        if (isWxVerifyRequest(request) && isWxPostRequest(request)) {
            return null;
        }
        return null;
    }

    // 判断是否是微信verify请求
    private boolean isWxVerifyRequest(HttpServletRequest request) {
        return "GET".equals(request.getMethod())
                && WX_VERIFY_PARAMS_CONDITION.getMatchingCondition(request) != null;
    }

    // 判断是否是微信POST请求
    private boolean isWxPostRequest(HttpServletRequest request) {
        return "POST".equals(request.getMethod())
                && WX_POST_PARAMS_CONDITION.getMatchingCondition(request) != null
                && WX_POST_CONSUMES_CONDITION.getMatchingCondition(request) != null;
    }

}
