package com.example.myproject.controller.invoker;

import com.example.myproject.controller.invoker.annotation.WxApiBody;
import com.example.myproject.controller.invoker.annotation.WxApiForm;
import com.example.myproject.controller.invoker.annotation.WxApiParam;
import com.example.myproject.module.media.WxMedia;
import com.example.myproject.module.menu.WxMenuManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;

/**
 * FastBootWeixin  WxApiInvokeService
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiInvokeService
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:14
 */
public interface WxApiInvokeService {

    String getCallbackIp();

    WxMenuManager.WxMenus getMenu();

    String createMenu(@WxApiBody WxMenuManager.WxMenu menu);

    String uploadMedia(@WxApiParam("type") WxMedia.Type type, @WxApiForm("media") File media);

    InputStreamResource getMedia(@WxApiParam("media_id") String mediaId);

}
