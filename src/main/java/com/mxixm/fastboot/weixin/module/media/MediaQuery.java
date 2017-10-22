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

/**
 * fastboot-weixin  MediaQuery
 * 媒体资源查询实体
 *
 * @author Guangshan
 * @date 2017/10/1 20:40
 * @since 0.2.0
 */
public class MediaQuery {

    /**
     * 存储的Key
     */
    private String key;

    /**
     * 资源路径
     */
    private String resourcePath;

    /**
     * 资源URL
     */
    private String resourceUrl;

    /**
     * 本地资源修改时间
     */
    private Long modifiedTime;

    /**
     * mediaId
     */
    private String mediaId;

    /**
     * mediaUrl
     */
    private String mediaUrl;

    /**
     * mediaType媒体类型
     */
    private WxMedia.Type mediaType;

    /**
     * 资源创建时间
     */
    private Long createdTime;

    /**
     * 是否是临时资源
     */
    private WxMediaStore.Type storeType;

    MediaQuery() {

    }

    MediaQuery(String key, String resourcePath, String resourceUrl, Long modifiedTime, String mediaId, String mediaUrl, WxMedia.Type mediaType, Long createdTime, WxMediaStore.Type storeType) {
        this.key = key;
        this.resourcePath = resourcePath;
        this.resourceUrl = resourceUrl;
        this.modifiedTime = modifiedTime;
        this.mediaId = mediaId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.createdTime = createdTime;
        this.storeType = storeType;
    }

    public static MediaQueryBuilder builder() {
        return new MediaQueryBuilder();
    }

    public String getKey() {
        if (key == null && (resourcePath != null || resourceUrl != null)) {
            StringBuilder sb = new StringBuilder();
            sb.append(resourcePath != null ? resourcePath : resourceUrl);
            sb.append(":");
            sb.append(storeType);
            key = sb.toString();
        }
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public WxMedia.Type getMediaType() {
        return mediaType;
    }

    public void setMediaType(WxMedia.Type mediaType) {
        this.mediaType = mediaType;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public WxMediaStore.Type getStoreType() {
        return storeType;
    }

    public void setStoreType(WxMediaStore.Type storeType) {
        this.storeType = storeType;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public static class MediaQueryBuilder {
        protected String key;
        protected String resourcePath;
        protected String resourceUrl;
        protected Long modifiedTime;
        protected String mediaId;
        protected String mediaUrl;
        protected WxMedia.Type type;
        protected Long createdTime;
        WxMediaStore.Type storeType;

        MediaQueryBuilder() {
        }

        public MediaQueryBuilder key(String key) {
            this.key = key;
            return this;
        }

        public MediaQueryBuilder resourcePath(String resourcePath) {
            this.resourcePath = resourcePath;
            return this;
        }

        public MediaQueryBuilder resourceUrl(String resourceUrl) {
            this.resourceUrl = resourceUrl;
            return this;
        }

        public MediaQueryBuilder modifiedTime(Long modifiedTime) {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public MediaQueryBuilder mediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }

        public MediaQueryBuilder mediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            return this;
        }

        public MediaQueryBuilder type(WxMedia.Type type) {
            this.type = type;
            return this;
        }

        public MediaQueryBuilder createdTime(Long createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public MediaQueryBuilder storeType(WxMediaStore.Type storeType) {
            this.storeType = storeType;
            return this;
        }

        public MediaQuery build() {
            return new MediaQuery(key, resourcePath, resourceUrl, modifiedTime, mediaId, mediaUrl, type, createdTime, storeType);
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.MediaQuery.MediaQueryBuilder(key=" + this.key + ", resourcePath=" + this.resourcePath + ", resourceUrl=" + this.resourceUrl + ", modifiedTime=" + this.modifiedTime + ", mediaId=" + this.mediaId + ", mediaUrl=" + this.mediaUrl + ", type=" + this.type + ", createdTime=" + this.createdTime + ", isTemp=" + this.storeType + ")";
        }
    }
}
