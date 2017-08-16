package com.example.myproject.controller.invoker;

import com.example.myproject.controller.invoker.annotation.WxApiBody;
import com.example.myproject.controller.invoker.annotation.WxApiForm;
import com.example.myproject.controller.invoker.annotation.WxApiParam;
import com.example.myproject.module.media.WxMaterial;
import com.example.myproject.module.media.WxMediaResource;
import com.example.myproject.module.menu.WxMenuManager;
import com.example.myproject.module.user.WxUser;

import java.io.File;

/**
 * FastBootWeixin  WxApiInvokeSpi
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 * 改个名儿，叫SPI高端一点
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiInvokeSpi
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:14
 */
public interface WxApiInvokeSpi {

    String getCallbackIp();

    WxMenuManager.WxMenus getMenu();

    String createMenu(@WxApiBody WxMenuManager.WxMenu menu);

    WxMaterial.MediaResult uploadMedia(@WxApiParam("type") WxMaterial.Type type, @WxApiForm("media") File media);

    WxMediaResource getMedia(@WxApiParam("media_id") String mediaId);

    WxUser getUserInfo(@WxApiParam("openid") String userOpenId);

    WxMaterial.ImageResult uploadImg(@WxApiForm("media") File media);

    WxMaterial.NewsResult addNews(@WxApiBody WxMaterial.News news);

    /**
     * 值返回一个json结果，不管了，如果有错的话会抛出异常的
     * @param news
     */
    void updateNews(@WxApiBody WxMaterial.NewsForUpdate news);

    /**
     * 视频不要传description
     * @param type
     * @param media
     * @param description
     * @return
     */
    WxMaterial.MaterialResult addMaterial(@WxApiParam("type") WxMaterial.Type type, @WxApiForm("media") File media, @WxApiForm("description") WxMaterial.Video description);

    getMaterial(@WxApiBody WxMaterial mediaId);

    
    getVideoMaterial();

    delMaterial();
    getMaterialCount();
    batchGetMaterial();

}
