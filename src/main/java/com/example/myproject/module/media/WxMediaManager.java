package com.example.myproject.module.media;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.controller.invoker.annotation.WxApiForm;
import com.example.myproject.controller.invoker.annotation.WxApiParam;

import java.io.File;

/**
 * FastBootWeixin  WxMediaManager
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMediaManager
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 21:05
 */
public class WxMediaManager {

    private WxApiInvokeSpi wxApiInvokeSpi;

    private WxMediaStore wxMediaStore;

    public WxMediaManager(WxApiInvokeSpi wxApiInvokeSpi, WxMediaStore wxMediaStore) {
        this.wxApiInvokeSpi = wxApiInvokeSpi;
        this.wxMediaStore = wxMediaStore;
    }

    public WxMedia.TempMediaResult uploadTempMedia(WxMedia.Type type, File media) {
        WxMedia.TempMediaResult result = wxMediaStore.getTempMedia(media.getPath());
        if (result != null) {
            return result;
        }
        result = wxApiInvokeSpi.uploadTempMedia(type, media);
        wxMediaStore.storeTempMedia(type, media, result);
        return result;
    }

}
