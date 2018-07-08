/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.module.credential;

import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.js.WxJsApi;
import com.mxixm.fastboot.weixin.module.js.WxJsConfig;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxJsTicketManager
 * 暂时没有定时任务，懒获取
 *
 * @author Guangshan
 * @date 2018-5-7 23:35:38
 * @since 0.6.0
 */
public class WxJsTicketManager extends AbstractWxCredentialManager {

    private String appId;

    private final WxApiService wxApiService;

    private final WxJsTicketPart wxJsTicketPart;

    public WxJsTicketManager(String appId, WxJsTicketPart wxJsTicketPart, WxJsTicketStore wxJsTicketStore, WxApiService wxApiService) {
        super(WxCredential.Type.JS_TICKET, wxJsTicketStore);
        this.appId = appId;
        this.wxJsTicketPart = wxJsTicketPart;
        this.wxApiService = wxApiService;
    }

    @Override
    protected WxCredential refreshInternal() {
        return wxApiService.getTicket(WxTicket.Type.JS_API);
    }

    public WxJsConfig getWxJsConfig(String url, WxJsApi... wxJsApis) {
        return getWxJsConfig(false, url, wxJsApis);
    }

    public WxJsConfig getWxJsConfig(boolean debug, String url, WxJsApi... wxJsApis) {
        return this.getWxJsConfig(debug, url, enumsToStrings(wxJsApis));
    }

    public WxJsConfig getWxJsConfig(String url, String... wxJsApis) {
        return this.getWxJsConfig(false, url, wxJsApis);
    }

    public WxJsConfig getWxJsConfig(boolean debug, String url, String... wxJsApis) {
        return this.getWxJsConfigBuilder()
                .debug(debug)
                .jsApiList(wxJsApis)
                .url(url).build();
    }

    /**
     * 可以覆盖默认属性，但是一定要添加url，jsApiList之后才能进行构造
     * @return
     */
    public WxJsConfig.Builder getWxJsConfigBuilder() {
        return WxJsConfig.builder()
                .appId(appId)
                .debug(false)
                .nonceStr(wxJsTicketPart.nonce())
                .timestamp(wxJsTicketPart.timestamp())
                .ticket(this.get());
    }

    /**
     * url从referer中构造，默认关闭debug，既然已经这么精简了，那么debug可以视为不需要的
     * @param wxJsApis
     * @return
     */
    public WxJsConfig getWxJsConfigFromReferer(String... wxJsApis) {
        HttpServletRequest request = WxWebUtils.getHttpServletRequest();
        String refererUrl = request.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isEmpty(refererUrl)) {
            throw new WxAppException("尝试从referer中获取url失败，请手动传入url");
        }
        return this.getWxJsConfig(refererUrl, wxJsApis);
    }

    public WxJsConfig getWxJsConfigFromReferer(WxJsApi... wxJsApis) {
        return this.getWxJsConfigFromReferer(enumsToStrings(wxJsApis));
    }

    /**
     * url从request中构造，默认关闭debug，既然已经这么精简了，那么debug可以视为不需要的
     * @param wxJsApis
     * @return
     */
    public WxJsConfig getWxJsConfigFromRequest(String... wxJsApis) {
        HttpServletRequest request = WxWebUtils.getHttpServletRequest();
        String requestUrl = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            requestUrl = requestUrl + "&" + queryString;
        }
        return this.getWxJsConfig(requestUrl, wxJsApis);
    }

    public WxJsConfig getWxJsConfigFromRequest(WxJsApi... wxJsApis) {
        return this.getWxJsConfigFromRequest(enumsToStrings(wxJsApis));
    }

    private String[] enumsToStrings(WxJsApi... wxJsApis) {
        return Arrays.stream(wxJsApis).map(WxJsApi::name).collect(Collectors.toList()).toArray(new String[wxJsApis.length]);
    }

}
