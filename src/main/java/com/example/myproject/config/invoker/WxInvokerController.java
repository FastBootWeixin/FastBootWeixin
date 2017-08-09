package com.example.myproject.config.invoker;

import com.example.myproject.support.AccessTokenManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

/**
 * FastBootWeixin  WxInvokerController
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxInvokerController
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:14
 */
public interface WxInvokerController {

    @RequestMapping("${wx.api.url.getCallbackIp}")
    public String getCallbackIp();

    @RequestMapping("${wx.api.url.getMenu}")
    public String getMenu();

    @RequestMapping("${wx.api.url.crteteMenu}")
    public String createMenu(@RequestBody String menuJson);

}
