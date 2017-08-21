package com.mxixm.fastbootwx.module.message.processer;

import com.mxixm.fastbootwx.module.WxRequest;
import com.mxixm.fastbootwx.module.media.WxMedia;
import com.mxixm.fastbootwx.module.media.WxMediaManager;
import com.mxixm.fastbootwx.module.message.WxMessage;

import java.io.File;

/**
 * FastBootWeixin  WxCommonMessageProcesser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxCommonMessageProcesser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/20 22:53
 */
public class WxVideoMessageProcesser extends AbstractWxMediaMessageProcesser<WxMessage.Video> {

    public WxVideoMessageProcesser(WxMediaManager wxMediaManager) {
        super(wxMediaManager);
    }

    public WxMessage.Video process(WxRequest wxRequest, WxMessage.Video wxMessage) {
        if (wxMessage == null) {
            return wxMessage;
        }
        processBody(wxRequest, wxMessage.getBody());
        processVideoBody(wxRequest, wxMessage.getBody());
        return wxMessage;
    }

    protected WxMessage.Video.Body processVideoBody(WxRequest wxRequest, WxMessage.Video.Body body) {
        if (body.getThumbMediaId() == null) {
            // 优先使用path
            if (body.getThumbMediaPath() != null) {
                String mediaId = wxMediaManager.addTempMedia(WxMedia.Type.IMAGE, new File(body.getThumbMediaPath()));
                body.setMediaId(mediaId);
            } else if (body.getThumbMediaUrl() != null) {
                String url = WxMediaUrlUtils.processUrl(wxRequest.getRequestUrl().toString(), body.getThumbMediaUrl());
                String mediaId = wxMediaManager.addTempMediaByUrl(WxMedia.Type.IMAGE, url);
                body.setMediaId(mediaId);
            }
        }
        return body;
    }

}
