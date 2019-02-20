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

import com.mxixm.fastboot.weixin.exception.WxApiException;
import com.mxixm.fastboot.weixin.module.media.news.WxNews;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.service.invoker.executor.WxApiTemplate;
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

    private WxApiService wxApiService;

    private WxMediaStore wxMediaStore;

    private WxApiTemplate wxApiTemplate;

    public WxMediaManager(WxApiService wxApiService, WxApiTemplate wxApiTemplate, WxMediaStore wxMediaStore) {
        this.wxApiService = wxApiService;
        this.wxApiTemplate = wxApiTemplate;
        this.wxMediaStore = wxMediaStore;
    }

    public String addTempMedia(WxMedia.Type type, Resource resource) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, WxMediaStore.Type.TEMP, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        WxMedia.TempMediaResult result = wxApiService.uploadTempMedia(type, resource);
        store(resourcePath, null, type, WxMediaStore.Type.TEMP,
                result.getMediaId(), null, result.getCreatedAt(), modifiedTime);
        return result.getMediaId();
    }

    public String addTempMediaByUrl(WxMedia.Type type, String url) {
        MediaEntity mediaEntity = query(null, url, WxMediaStore.Type.TEMP, null, null);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        Resource resource = wxApiTemplate.getForObject(url, Resource.class);
        WxMedia.TempMediaResult result = wxApiService.uploadTempMedia(type, resource);
        store(null, url, type, WxMediaStore.Type.TEMP,
                result.getMediaId(), null, result.getCreatedAt(), null);
        return result.getMediaId();
    }

    public String addMedia(WxMedia.Type type, Resource resource) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, WxMediaStore.Type.MATERIAL, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        WxMedia.MediaResult result = wxApiService.uploadMedia(type, resource, null);
        store(resourcePath, null, type, WxMediaStore.Type.MATERIAL,
                result.getMediaId(), result.getUrl(), new Date(), modifiedTime);
        return result.getMediaId();
    }

    /**
     * 本来应该再给get加个缓存的，但是我又偷懒了，get的时候不加缓存了，直接拿微信api的结果吧
     *
     * @param resource
     * @param video
     * @return the result
     */
    public String addVideo(Resource resource, WxMedia.Video video) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, WxMediaStore.Type.MATERIAL, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaId();
        }
        WxMedia.MediaResult result = wxApiService.uploadMedia(WxMedia.Type.VIDEO, resource, video);
        store(resourcePath, null, WxMedia.Type.VIDEO, WxMediaStore.Type.MATERIAL,
                result.getMediaId(), result.getUrl(), new Date(), modifiedTime);
        return result.getMediaId();
    }

    public WxMedia.Video getVideo(String mediaId) {
        return wxApiService.getVideo(WxMedia.of(mediaId));
    }

    public Resource getTempMedia(String mediaId) {
        MediaEntity mediaEntity = query(null, null, WxMediaStore.Type.TEMP, mediaId, null);
        if (mediaEntity != null && mediaEntity.getResource() != null) {
            return mediaEntity.getResource();
        }
        WxMediaResource wxMediaResource = wxApiService.getTempMedia(mediaId);
        return storeResource(wxMediaResource, null, WxMediaStore.Type.TEMP, mediaId, null, null, null);
    }

    public Resource getMedia(String mediaId) {
        MediaEntity mediaEntity = query(null, null, WxMediaStore.Type.MATERIAL, mediaId, null);
        if (mediaEntity != null && mediaEntity.getResource() != null) {
            return mediaEntity.getResource();
        }
        WxMediaResource wxMediaResource = wxApiService.getMedia(WxMedia.of(mediaId));
        return storeResource(wxMediaResource, null, WxMediaStore.Type.MATERIAL, mediaId, null, null, null);
    }

    public String addImg(Resource resource) {
        String resourcePath = WxMediaUtils.resourcePath(resource);
        Date modifiedTime = WxMediaUtils.resourceModifiedTime(resource);
        MediaEntity mediaEntity = query(resourcePath, null, WxMediaStore.Type.IMAGE, null, modifiedTime);
        if (mediaEntity != null && mediaEntity.getMediaUrl() != null) {
            return mediaEntity.getMediaUrl();
        }
        WxMedia.ImageResult imageResult = wxApiService.uploadImg(resource);
        store(resourcePath, null, WxMedia.Type.IMAGE, WxMediaStore.Type.IMAGE,
                null, imageResult.getUrl(), new Date(), modifiedTime);
        return imageResult.getUrl();
    }

    public String addImgByUrl(String url) {
        MediaEntity mediaEntity = query(null, url, WxMediaStore.Type.IMAGE, null, null);
        if (mediaEntity != null && mediaEntity.getMediaId() != null) {
            return mediaEntity.getMediaUrl();
        }
        Resource resource = wxApiTemplate.getForObject(url, Resource.class);
        WxMedia.ImageResult imageResult = wxApiService.uploadImg(resource);
        store(null, url, WxMedia.Type.IMAGE, WxMediaStore.Type.IMAGE,
                null, imageResult.getUrl(), new Date(), null);
        return imageResult.getUrl();
    }

    public String getImgByUrl(String imgUrl) {
        MediaEntity mediaEntity = query(null, imgUrl, WxMediaStore.Type.IMAGE, null, null);
        if (mediaEntity != null) {
            return mediaEntity.getMediaUrl();
        }
        return null;
    }

    private MediaEntity query(String resourcePath, String resourceUrl, WxMediaStore.Type storeType, String mediaId, Date modifiedTime) {
        MediaQuery mediaQuery = MediaQuery.builder()
                .resourcePath(resourcePath)
                .resourceUrl(resourceUrl)
                .storeType(storeType)
                .mediaId(mediaId)
                .modifiedTime(modifiedTime != null ? modifiedTime.getTime() : null)
                .build();
        return wxMediaStore.query(mediaQuery);
    }

    private MediaEntity store(String resourcePath, String resourceUrl, WxMedia.Type type, WxMediaStore.Type storeType, String mediaId, String mediaUrl, Date createdTime, Date modifiedTime) {
        MediaEntity mediaEntity = (MediaEntity) MediaEntity.builder()
                .resourcePath(resourcePath)
                .resourceUrl(resourceUrl)
                .type(type)
                .storeType(storeType)
                .mediaId(mediaId)
                .mediaUrl(mediaUrl)
                .createdTime(createdTime != null ? createdTime.getTime() : System.currentTimeMillis())
                .modifiedTime(modifiedTime != null ? modifiedTime.getTime() : (createdTime == null ? System.currentTimeMillis() : createdTime.getTime()))
                .build();
        return wxMediaStore.store(mediaEntity);
    }

    private Resource storeResource(Resource resource, WxMedia.Type type, WxMediaStore.Type storeType, String mediaId, String mediaUrl, Date createdTime, Date modifiedTime) {
        MediaEntity mediaEntity = (MediaEntity) MediaEntity.builder()
                .resource(resource)
                .type(type)
                .storeType(storeType)
                .mediaId(mediaId)
                .mediaUrl(mediaUrl)
                .createdTime(createdTime != null ? createdTime.getTime() : System.currentTimeMillis())
                .modifiedTime(modifiedTime != null ? modifiedTime.getTime() : (createdTime == null ? System.currentTimeMillis() : createdTime.getTime()))
                .build();
        try {
            return wxMediaStore.storeResource(mediaEntity);
        } catch (IOException e) {
            throw new WxApiException("获取媒体文件失败", e);
        }
    }

    /**
     * 这个怎么存呢？是否有必要存一个映射关系？
     *
     * @param news
     * @return the result
     */
    public WxNews.Result storeNews(WxNews news) {
        return wxApiService.addNews(news);
    }

    /**
     * 只返回一个json结果，不管了，如果有错的话会抛出异常的
     *
     * @param news
     */
    public void updateNews(WxNews news) {
        wxApiService.updateNews(news);
    }

    /**
     * 主要限制是同一个接口相同的参数可能得到的是不同的结果
     *
     * @param mediaId
     * @return the result
     */
    public WxNews getNews(String mediaId) {
        return wxApiService.getNews(WxMedia.of(mediaId));
    }

    public void delMedia(String mediaId) {
        wxApiService.delMedia(WxMedia.of(mediaId));
    }

    public WxMedia.Count getMediaCount() {
        return wxApiService.getMediaCount();
    }

    public WxNews.PageResult batchGetNews(int offset) {
        return wxApiService.batchGetNews(WxNews.PageParam.of(offset));
    }

    public WxMedia.PageResult batchGetMedia(WxMedia.Type type, int offset) {
        return wxApiService.batchGetMedia(WxMedia.PageParam.of(type, offset));
    }
}
