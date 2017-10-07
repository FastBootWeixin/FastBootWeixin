/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.module.media;

import com.mxixm.fastboot.weixin.controller.invoker.WxApiInvokeSpi;
import com.mxixm.fastboot.weixin.controller.invoker.executor.WxApiInvoker;
import com.mxixm.fastboot.weixin.exception.WxApiException;
import com.mxixm.fastboot.weixin.util.WxMediaUtils;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Date;

/**
 * FastBootWeixin WxMediaManager
 *
 * @author Guangshan
 * @date 2017/8/12 21:05
 * @since 0.1.2
 */
public class WxMediaManager {

    private WxApiInvokeSpi wxApiInvokeSpi;

    private WxMediaStore WxMediaStore;

    private WxApiInvoker wxApiInvoker;

    public WxMediaManager(WxApiInvokeSpi wxApiInvokeSpi, WxApiInvoker wxApiInvoker, WxMediaStore WxMediaStore) {
        this.wxApiInvokeSpi = wxApiInvokeSpi;
        this.wxApiInvoker = wxApiInvoker;
        this.WxMediaStore = WxMediaStore;
    }

    public String addTempMedia(WxMedia.Type type, Resource resource) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, true, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        WxMedia.TempMediaResult result = wxApiInvokeSpi.uploadTempMedia(type, resource);
        store(resourcePath, null, type, true,
                result.getMediaId(), null, result.getCreatedAt(), modifiedTime);
        return result.getMediaId();
    }

    public String addTempMediaByUrl(WxMedia.Type type, String url) {
        MediaEntity mediaEntity = query(null, url, true, null, null);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        Resource resource = wxApiInvoker.getForObject(url, Resource.class);
        WxMedia.TempMediaResult result = wxApiInvokeSpi.uploadTempMedia(type, resource);
        store(null, url, type, true,
                result.getMediaId(), null, result.getCreatedAt(), null);
        return result.getMediaId();
    }

    public String addMedia(WxMedia.Type type, Resource resource) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, false, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        WxMedia.MediaResult result = wxApiInvokeSpi.uploadMedia(type, resource, null);
        store(resourcePath, null, type, false,
                result.getMediaId(), result.getUrl(), new Date(), modifiedTime);
        return result.getMediaId();
    }

    /**
     * 本来应该再给get加个缓存的，但是我又偷懒了，get的时候不加缓存了，直接拿微信api的结果吧
     *
     * @param resource
     * @param video
     * @return dummy
     */
    public String addVideoMedia(Resource resource, WxMedia.Video video) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, false, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        WxMedia.MediaResult result = wxApiInvokeSpi.uploadMedia(WxMedia.Type.VIDEO, resource, video);
        store(resourcePath, null, WxMedia.Type.VIDEO, false,
                result.getMediaId(), result.getUrl(), new Date(), modifiedTime);
        return result.getMediaId();
    }

    public WxMedia.Video getVideoMedia(String mediaId) {
        return wxApiInvokeSpi.getVideoMedia(WxMedia.of(mediaId));
    }

    public Resource getTempMedia(String mediaId) {
        MediaEntity mediaEntity = query(null, null, true, mediaId, null);
        if (mediaEntity != null && mediaEntity.getResource() != null) {
            return mediaEntity.getResource();
        }
        WxMediaResource wxMediaResource = wxApiInvokeSpi.getTempMedia(mediaId);
        return storeResource(wxMediaResource, null, true, mediaId, null, null, null);
    }

    public Resource getMedia(String mediaId) {
        MediaEntity mediaEntity = query(null, null, false, mediaId, null);
        if (mediaEntity != null && mediaEntity.getResource() != null) {
            return mediaEntity.getResource();
        }
        WxMediaResource wxMediaResource = wxApiInvokeSpi.getMedia(WxMedia.of(mediaId));
        return storeResource(wxMediaResource, null, false, mediaId, null, null, null);
    }

    public String addImg(Resource resource) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, false, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaUrl() != null) {
            return mediaEntity.getMediaUrl();
        }
        WxMedia.ImageResult imageResult = wxApiInvokeSpi.uploadImg(resource);
        store(resourcePath, null, WxMedia.Type.IMAGE, false,
                null, imageResult.getUrl(), new Date(), modifiedTime);
        return imageResult.getUrl();
    }

    public String addImgByUrl(String url) {
        MediaEntity mediaEntity = query(null, url, false, null, null);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaUrl();
        }
        Resource resource = wxApiInvoker.getForObject(url, Resource.class);
        WxMedia.ImageResult imageResult = wxApiInvokeSpi.uploadImg(resource);
        store(null, url, WxMedia.Type.IMAGE, false,
                null, imageResult.getUrl(), new Date(), null);
        return imageResult.getUrl();
    }

    public String getImgByUrl(String imgUrl) {
        MediaEntity mediaEntity = query(null, imgUrl, false, null, null);
        if (mediaEntity != null) {
            return mediaEntity.getMediaUrl();
        }
        return null;
    }

    private MediaEntity query(String resourcePath, String resourceUrl, boolean isTemp, String mediaId, Date modifiedTime) {
        MediaQuery mediaQuery = MediaQuery.builder()
                .resourcePath(resourcePath)
                .resourceUrl(resourceUrl)
                .isTemp(isTemp)
                .mediaId(mediaId)
                .modifiedTime(modifiedTime != null ? modifiedTime.getTime() : null)
                .build();
        return WxMediaStore.query(mediaQuery);
    }

    private MediaEntity store(String resourcePath, String resourceUrl, WxMedia.Type type, boolean isTemp, String mediaId, String mediaUrl, Date createdTime, Date modifiedTime) {
        MediaEntity mediaEntity = (MediaEntity) MediaEntity.builder()
                .resourcePath(resourcePath)
                .resourceUrl(resourceUrl)
                .type(type)
                .isTemp(isTemp)
                .mediaId(mediaId)
                .mediaUrl(mediaUrl)
                .createdTime(createdTime != null ? createdTime.getTime() : System.currentTimeMillis())
                .modifiedTime(modifiedTime != null ? modifiedTime.getTime() : (createdTime == null ? System.currentTimeMillis() : createdTime.getTime()))
                .build();
        return WxMediaStore.store(mediaEntity);
    }

    private Resource storeResource(Resource resource, WxMedia.Type type, boolean isTemp, String mediaId, String mediaUrl, Date createdTime, Date modifiedTime) {
        MediaEntity mediaEntity = (MediaEntity) MediaEntity.builder()
                .resource(resource)
                .type(type)
                .isTemp(isTemp)
                .mediaId(mediaId)
                .mediaUrl(mediaUrl)
                .createdTime(createdTime != null ? createdTime.getTime() : System.currentTimeMillis())
                .modifiedTime(modifiedTime != null ? modifiedTime.getTime() : (createdTime == null ? System.currentTimeMillis() : createdTime.getTime()))
                .build();
        try {
            return WxMediaStore.storeResource(mediaEntity);
        } catch (IOException e) {
            throw new WxApiException("获取媒体文件失败", e);
        }
    }

    /**
     * 这个怎么存呢？是否有必要存一个映射关系？
     *
     * @param news
     * @return dummy
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
     * @return dummy
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
