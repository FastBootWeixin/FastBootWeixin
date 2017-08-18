package com.example.myproject.module.media;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.exception.WxApiException;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

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

    public String addTempMedia(WxMedia.Type type, File media) {
        String mediaId = wxMediaStore.findTempMediaIdByFile(media);
        if (mediaId != null) {
            return mediaId;
        }
        WxMedia.TempMediaResult result = wxApiInvokeSpi.uploadTempMedia(type, media);
        wxMediaStore.storeFileToTempMedia(type, media, result);
        return result.getMediaId();
    }

    public String addMedia(WxMedia.Type type, File media) {
        String mediaId = wxMediaStore.findMediaIdByFile(media);
        if (mediaId != null) {
            return mediaId;
        }
        WxMedia.MediaResult result = wxApiInvokeSpi.addMedia(type, media, null);
        wxMediaStore.storeFileToMedia(type, media, result);
        return result.getMediaId();
    }

    /**
     * 本来应该再给get加个缓存的，但是我又偷懒了，get的时候不加缓存了，直接拿微信api的结果吧
     * @param video
     * @param description
     * @return
     */
    public String addVideoMedia(File video, WxMedia.Video description) {
        String mediaId = wxMediaStore.findMediaIdByFile(video);
        if (mediaId != null) {
            return mediaId;
        }
        WxMedia.MediaResult result = wxApiInvokeSpi.addMedia(WxMedia.Type.VIDEO, video, description);
        wxMediaStore.storeFileToMedia(WxMedia.Type.VIDEO, video, result);
        return result.getMediaId();
    }

    public WxMedia.Video getVideoMedia(String mediaId) {
        return wxApiInvokeSpi.getVideoMedia(WxMedia.of(mediaId));
    }

    public Resource getTempMedia(String mediaId) {
        File tempMediaFile = wxMediaStore.findFileByTempMediaId(mediaId);
        if (tempMediaFile != null) {
            return new WxMediaResource(tempMediaFile);
        }
        WxMediaResource wxMediaResource = wxApiInvokeSpi.getTempMedia(mediaId);
        try {
            wxMediaStore.storeTempMediaToFile(mediaId, wxMediaResource);
        } catch (IOException e) {
            throw new WxApiException("获取媒体文件失败", e);
        }
        return wxMediaResource;
    }

    public Resource getMedia(String mediaId) {
        File mediaFile = wxMediaStore.findFileByMediaId(mediaId);
        if (mediaFile != null) {
            return new WxMediaResource(mediaFile);
        }
        WxMediaResource wxMediaResource = wxApiInvokeSpi.getMedia(WxMedia.of(mediaId));
        try {
            wxMediaStore.storeMediaToFile(mediaId, wxMediaResource);
        } catch (IOException e) {
            throw new WxApiException("获取媒体文件失败", e);
        }
        return wxMediaResource;
    }

    public String addImg(File file) {
        String url = wxMediaStore.findUrlByFile(file);
        if (url != null) {
            return url;
        }
        WxMedia.ImageResult imageResult = wxApiInvokeSpi.uploadImg(file);
        wxMediaStore.storeFileToUrl(file, imageResult);
        return imageResult.getUrl();
    }

    public Resource getImg(String url) {
        File file = wxMediaStore.findFileByUrl(url);
        if (file != null) {
            return new WxMediaResource(file);
        } else {
            // 如果为空的时候是否可以直接通过url获取source呢？
            return null;
        }
    }

    /**
     * 这个怎么存呢？是否有必要存一个映射关系？
     *
     * @param news
     * @return
     */
    public WxMedia.NewsResult storeNews(WxMedia.News news) {
        return wxApiInvokeSpi.addNews(news);
    }

    /**
     * 只返回一个json结果，不管了，如果有错的话会抛出异常的
     *
     * @param news
     */
    public void updateNews(WxMedia.New news) {
        wxApiInvokeSpi.updateNews(news);
    }

    /**
     * 主要限制是同一个接口相同的参数可能得到的是不同的结果
     *
     * @param mediaId
     * @return
     */
    public WxMedia.News getNews(String mediaId) {
        return wxApiInvokeSpi.getNewsMedia(WxMedia.of(mediaId));
    }

    public void delMedia(String mediaId) {
        wxApiInvokeSpi.delMedia(WxMedia.of(mediaId));

    }

    public WxMedia.Count getMediaCount() {
        return wxApiInvokeSpi.getMediaCount();
    }

}
