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

import org.springframework.core.io.Resource;

/**
 * fastboot-weixin  MediaEntity
 * 媒体资源返回实体
 *
 * @author Guangshan
 * @date 2017/10/1 20:40
 * @since 0.2.0
 */
public class MediaEntity extends MediaQuery {

    /**
     * 返回的resource
     */
    private Resource resource;

    public MediaEntity() {
    }

    MediaEntity(String key, String resourcePath, String resourceUrl, Long modifiedTime, String mediaId, String mediaUrl, WxMedia.Type type, Long createdTime, WxMediaStore.Type storeType, Resource resource) {
        super(key, resourcePath, resourceUrl, modifiedTime, mediaId, mediaUrl, type, createdTime, storeType);
        this.resource = resource;
    }

    public static MediaEntityBuilder builder() {
        return new MediaEntityBuilder();
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public static class MediaEntityBuilder extends MediaQueryBuilder {
        private Resource resource;

        MediaEntityBuilder() {
        }

        public MediaEntityBuilder resource(Resource resource) {
            this.resource = resource;
            return this;
        }

        @Override
        public MediaEntity build() {
            return new MediaEntity(key, resourcePath, resourceUrl, modifiedTime, mediaId, mediaUrl, type, createdTime, storeType, resource);
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.MediaEntity.MediaEntityBuilder(resource=" + this.resource + ")";
        }
    }
}
