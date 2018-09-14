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

package com.mxixm.fastboot.weixin.support;

import com.mxixm.fastboot.weixin.module.media.*;
import com.mxixm.mapdb.DB;
import com.mxixm.mapdb.DBMaker;
import com.mxixm.mapdb.HTreeMap;
import com.mxixm.mapdb.Serializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * FastBootWeixin MapDbWxMediaStore
 * <p>
 * media存储器，提供媒体文件获取，媒体文件保存，转换文件等功能
 * 数据库使用内嵌数据库，经过一天的maven仓库database embedded选型，暂时决定使用MapDB(200k，其实有700K)或者kahaDB(600k)
 * 还有一个PalDB，这些都不小，真不行了我自己实现一个好了。。。暂时先用现成的
 * MapDB最新版依赖真的太多了，不想用了，先用MapDB的老版本吧
 * <p>
 * 重要！这个store类要优化成callback方式，且做的易于扩展，现在我自己都有点看不懂这个东西了
 *
 * @author Guangshan
 * @date 2017/09/21 23:37
 * @since 0.1.2
 */
public class MapDbWxMediaStore implements InitializingBean, WxMediaStore {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private DB db;

    private HTreeMap<String, String> mediaIdDb;

    private HTreeMap<String, StoreEntity> mediaDb;

    private String defaultFilePath = "weixin/media/file/";

    private String defaultTempFilePath = "weixin/media/file/temp/";

    private String defaultDbPath = "weixin/media/db/store.db";

    private Long tempExpired = 3 * 24 * 60 * 60 * 1000L;

    /**
     * 整个查询的封装
     * @param mediaQuery
     * @return MediaEntity
     */
    @Override
    public MediaEntity query(MediaQuery mediaQuery) {
        if (mediaQuery.getMediaId() != null) {
            String key = mediaIdDb.get(mediaQuery.getMediaId());
            mediaQuery.setKey(key);
        }
        MediaEntity result = new MediaEntity();
        StoreEntity storeEntity = mediaDb.get(mediaQuery.getKey());
        if (!expired(mediaQuery, storeEntity)) {
            storeEntity.fillMediaEntity(result);
        }
        return result;
    }

    /**
     * 处理素材的失效
     * @return boolean 素材是否失效
     */
    private boolean expired(MediaQuery mediaQuery, StoreEntity storeEntity) {
        if (storeEntity == null) {
            return true;
        }
        if (mediaQuery.getModifiedTime() != null && storeEntity.modifiedTime < mediaQuery.getModifiedTime()) {
            mediaIdDb.remove(storeEntity.mediaId);
            mediaDb.remove(mediaQuery.getKey());
            return true;
        }
        if (Type.TEMP.equals(storeEntity.storeType)) {
            if (System.currentTimeMillis() - storeEntity.createdTime >= tempExpired) {
                mediaIdDb.remove(storeEntity.mediaId);
                mediaDb.remove(mediaQuery.getKey());
                return true;
            }
        }
        return false;
    }

    /**
     * 保存tempMedia到mediaStore
     *
     * @param mediaEntity
     */
    @Override
    public MediaEntity store(MediaEntity mediaEntity) {
        StoreEntity storeEntity = StoreEntity.builder()
                .resourcePath(mediaEntity.getResourcePath())
                .resourceUrl(mediaEntity.getResourceUrl())
                .createdTime(mediaEntity.getCreatedTime())
                .modifiedTime(mediaEntity.getModifiedTime())
                .mediaType(mediaEntity.getMediaType())
                .mediaId(mediaEntity.getMediaId())
                .mediaUrl(mediaEntity.getMediaUrl())
                .storeType(mediaEntity.getStoreType())
                .build();
        mediaDb.put(mediaEntity.getKey(), storeEntity);
        if (mediaEntity.getMediaId() != null) {
            mediaIdDb.put(mediaEntity.getMediaId(), mediaEntity.getKey());
        }
        // 每执行一个写操作，都要commit，否则强制终止程序时会导致打不开数据库文件
        db.commit();
        return mediaEntity;
    }


    /**
     * 保存Resource到持久化，这个实现中是文件
     *
     * @param mediaEntity
     * @return File
     */
    @Override
    public Resource storeResource(MediaEntity mediaEntity) throws IOException {
        if (!(mediaEntity.getResource() instanceof WxMediaResource)) {
            return null;
        }
        WxMediaResource wxMediaResource = (WxMediaResource) mediaEntity.getResource();
        if (wxMediaResource.isUrlMedia()) {
            return null;
        }
        String fileName = wxMediaResource.getFilename();
        if (fileName == null) {
            fileName = mediaEntity.getMediaId();
        }
        File file = new File(StringUtils.applyRelativePath(Type.TEMP.equals(mediaEntity.getStoreType()) ? defaultTempFilePath : defaultFilePath, fileName));
        if (file.exists()) {
            return new FileSystemResource(file);
        }
        file.createNewFile();
        file.setLastModified(System.currentTimeMillis());
        FileCopyUtils.copy(mediaEntity.getResource().getInputStream(), new FileOutputStream(file));
        mediaEntity.setResourcePath(file.getAbsolutePath());
        store(mediaEntity);
        return new FileSystemResource(file);
    }

    /**
     * 只能用来删除永久素材
     *
     * @param mediaId
     */
    @Override
    public void delete(String mediaId) {
        String key = mediaIdDb.remove(mediaId);
        mediaDb.remove(key);
        db.commit();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File filePath = new File(defaultFilePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        filePath = new File(defaultTempFilePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File dbFile = new File(defaultDbPath);
        if (!dbFile.exists()) {
            dbFile.getParentFile().mkdirs();
            dbFile.createNewFile();
        }
        db = DBMaker.newFileDB(dbFile)
                .cacheDisable()
                .asyncWriteEnable()
                .asyncWriteFlushDelay(100)
                .closeOnJvmShutdown().make();
        mediaDb = db.createHashMap("media")
                .keySerializer(Serializer.STRING)
                .counterEnable()
                .makeOrGet();
        mediaIdDb = db.createHashMap("mediaId")
                .keySerializer(Serializer.STRING)
                .counterEnable()
                .makeOrGet();
    }

    /**
     * 用于存储的实体
     */
    private static class StoreEntity implements Serializable {
        /**
         * 资源路径
         */
        private String resourcePath;

        /**
         * 资源URL
         */
        private String resourceUrl;

        /**
         * 媒体ID
         */
        private String mediaId;

        /**
         * 媒体URL
         */
        private String mediaUrl;

        /**
         * 媒体的创建时间
         */
        private Long createdTime;

        /**
         * 最后一次更新时间
         */
        private Long modifiedTime;

        /**
         * 媒体类型
         */
        private WxMedia.Type mediaType;

        /**
         * 是否是临时资源
         */
        private WxMediaStore.Type storeType;

        StoreEntity(String resourcePath, String resourceUrl, String mediaId, String mediaUrl, Long createdTime, Long modifiedTime, WxMedia.Type mediaType, WxMediaStore.Type storeType) {
            this.resourcePath = resourcePath;
            this.resourceUrl = resourceUrl;
            this.mediaId = mediaId;
            this.mediaUrl = mediaUrl;
            this.createdTime = createdTime;
            this.modifiedTime = modifiedTime;
            this.mediaType = mediaType;
            this.storeType = storeType;
        }

        void fillMediaEntity(MediaEntity mediaEntity) {
            mediaEntity.setResourcePath(resourcePath);
            mediaEntity.setResourceUrl(resourceUrl);
            mediaEntity.setMediaId(mediaId);
            mediaEntity.setMediaUrl(mediaUrl);
            mediaEntity.setCreatedTime(createdTime);
            mediaEntity.setModifiedTime(modifiedTime);
            mediaEntity.setMediaType(mediaType);
            mediaEntity.setStoreType(storeType);
            if (resourcePath != null) {
                mediaEntity.setResource(new FileSystemResource(resourcePath));
            } else if (resourceUrl != null) {
                try {
                    mediaEntity.setResource(new UrlResource(URI.create(resourceUrl)));
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String resourcePath;
            private String resourceUrl;
            private String mediaId;
            private String mediaUrl;
            private Long createdTime;
            private Long modifiedTime;
            private WxMedia.Type mediaType;
            private WxMediaStore.Type storeType;

            Builder() {
            }

            public Builder resourcePath(String resourcePath) {
                this.resourcePath = resourcePath;
                return this;
            }

            public Builder resourceUrl(String resourceUrl) {
                this.resourceUrl = resourceUrl;
                return this;
            }

            public Builder mediaId(String mediaId) {
                this.mediaId = mediaId;
                return this;
            }

            public Builder mediaUrl(String mediaUrl) {
                this.mediaUrl = mediaUrl;
                return this;
            }

            public Builder createdTime(Long createdTime) {
                this.createdTime = createdTime;
                return this;
            }

            public Builder modifiedTime(Long modifiedTime) {
                this.modifiedTime = modifiedTime;
                return this;
            }

            public Builder mediaType(WxMedia.Type mediaType) {
                this.mediaType = mediaType;
                return this;
            }

            public Builder storeType(WxMediaStore.Type storeType) {
                this.storeType = storeType;
                return this;
            }

            public StoreEntity build() {
                return new StoreEntity(resourcePath, resourceUrl, mediaId, mediaUrl, createdTime, modifiedTime, mediaType, storeType);
            }

            @Override
            public String toString() {
                return "com.mxixm.fastboot.weixin.support.MapDbWxMediaStore.StoreEntity.Builder(resourcePath=" + this.resourcePath + ", mediaId=" + this.mediaId + ", mediaUrl=" + this.mediaUrl + ", createdTime=" + this.createdTime + ", modifiedTime=" + this.modifiedTime + ", mediaType=" + this.mediaType + ")";
            }
        }
    }

}
