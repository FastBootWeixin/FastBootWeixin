package com.example.myproject.controller.invoker;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    String getCallbackIp();

    @RequestMapping("${wx.api.url.getMenu}")
    String getMenu();

    @RequestMapping("${wx.api.url.crteteMenu}")
    String createMenu(@RequestBody String menuJson);

}
