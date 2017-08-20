package com.example.myproject.module.message.processer;

import com.example.myproject.module.WxRequest;
import com.example.myproject.module.media.WxMedia;
import com.example.myproject.module.media.WxMediaManager;
import com.example.myproject.module.message.WxMessage;
import com.example.myproject.module.message.WxMessageProcesser;

import java.io.File;

/**
 * FastBootWeixin  WxCommonMessageProcesser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxCommonMessageProcesser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 22:53
 */
public abstract class AbstractWxMediaMessageProcesser<T extends WxMessage> implements WxMessageProcesser<T> {

    protected WxMediaManager wxMediaManager;

    public AbstractWxMediaMessageProcesser(WxMediaManager wxMediaManager) {
        this.wxMediaManager = wxMediaManager;
    }

    protected WxMessage.MediaBody processBody(WxRequest wxRequest, WxMessage.MediaBody body) {
        if (body.getMediaId() == null) {
            // 优先使用path
            if (body.getMediaPath() != null) {
                String mediaId = wxMediaManager.addTempMedia(WxMedia.Type.IMAGE, new File(body.getMediaPath()));
                body.setMediaId(mediaId);
            } else if (body.getMediaUrl() != null) {
                String url = WxMediaUrlUtils.processUrl(wxRequest.getRequestUrl().toString(), body.getMediaUrl());
                String mediaId = wxMediaManager.addTempMediaByUrl(WxMedia.Type.IMAGE, url);
                body.setMediaId(mediaId);
            }
        }
        return body;
    }

}
