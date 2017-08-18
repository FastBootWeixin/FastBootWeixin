package com.example.myproject.module.media;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;

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

    public String storeTempMedia(WxMedia.Type type, File media) {
        String mediaId = wxMediaStore.findTempMediaIdByFile(media);
        if (mediaId != null) {
            return mediaId;
        }
        WxMedia.TempMediaResult result = wxApiInvokeSpi.uploadTempMedia(type, media);
        wxMediaStore.storeFileToTempMedia(type, media, result);
        return result.getMediaId();
    }

    public String storeMedia(WxMedia.Type type, File media) {
        String mediaId = wxMediaStore.findMediaIdByFile(media);
        if (mediaId != null) {
            return mediaId;
        }
        WxMedia.MediaResult result = wxApiInvokeSpi.addMedia(type, media, null);
        wxMediaStore.storeFileToMedia(type, media, result);
        return result.getMediaId();
    }

}
