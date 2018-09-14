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

package com.mxixm.fastboot.weixin.test.old.media;

import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaResource;
import com.mxixm.mapdb.DB;
import com.mxixm.mapdb.DBMaker;
import com.mxixm.mapdb.HTreeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * FastBootWeixin MapDbWxMediaStore
 *
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
public class WxMediaStore implements InitializingBean {

    private DB db;

    private HTreeMap<String, StoreEntity> tempMediaFileDb;

    private HTreeMap<String, String> tempMediaIdDb;

    private HTreeMap<String, StoreEntity> mediaFileDb;

    private HTreeMap<String, String> mediaIdDb;

    /**
     * 用于保存图片
     */
    private HTreeMap<String, String> urlDb;

    private String defaultFilePath = "weixin/media/file/";

    private String defaultTempFilePath = "weixin/media/file/temp/";

    private String defaultDbPath = "weixin/media/db/store.db";

    /**
     * 根据文件查找tempMediaId
     *
     * @param file
     * @return String
     */
    public String findTempMediaIdByFile(File file) {
        StoreEntity storeEntity = tempMediaFileDb.get(file.getAbsolutePath());
        // 如果保存的最后更新时间再文件的最后更新时间之前，说明文件有更新，返回空
        if (storeEntity != null && storeEntity.lastModifiedTime.getTime() >= file.lastModified()) {
            return storeEntity.mediaId;
        }
        return null;
    }

    /**
     * 根据tempMediaId查找File
     *
     * @param mediaId
     * @return File
     */
    public File findFileByTempMediaId(String mediaId) {
        String filePath = tempMediaIdDb.get(mediaId);
        return findFile(filePath);
    }

    /**
     * 保存tempMedia到File
     *
     * @param mediaId
     * @return File
     */
    public File storeTempMediaToFile(String mediaId, Resource resource) throws IOException {
        WxMediaResource wxMediaResource = (WxMediaResource) resource;
        if (wxMediaResource.isUrlMedia()) {
            return null;
        }
        String fileName = resource.getFilename();
        if (fileName == null) {
            fileName = mediaId;
        }
        File file = new File(StringUtils.applyRelativePath(defaultTempFilePath, fileName));
        if (file.exists()) {
            return file;
        }
        StoreEntity storeEntity = storeFile(file, mediaId, resource);
        tempMediaFileDb.put(file.getAbsolutePath(), storeEntity);
        tempMediaIdDb.put(mediaId, file.getAbsolutePath());
        db.commit();
        return file;
    }

    /**
     * 保存tempMedia到mediaStore
     *
     * @param type
     * @param file
     * @param result
     */
    public WxMedia.TempMediaResult storeFileToTempMedia(WxMedia.Type type, File file, WxMedia.TempMediaResult result) {
        StoreEntity storeEntity = StoreEntity.builder()
                .filePath(file.getAbsolutePath())
                .createTime(result.getCreatedAt())
                .mediaType(type)
                .mediaId(result.getMediaId())
                .lastModifiedTime(new Date(file.lastModified()))
                .build();
        tempMediaFileDb.put(file.getAbsolutePath(), storeEntity);
        if (result.getMediaId() != null) {
            tempMediaIdDb.put(result.getMediaId(), file.getAbsolutePath());
        }
        // 每执行一个写操作，都要commit，否则强制终止程序时会导致打不开数据库文件
        db.commit();
        return result;
    }

    /**
     * 保存tempMedia到mediaStore
     *
     * @param type
     * @param result
     */
    public WxMedia.TempMediaResult storeUrlToTempMedia(WxMedia.Type type, String url, WxMedia.TempMediaResult result) {
        StoreEntity storeEntity = StoreEntity.builder()
                .filePath(url)
                .createTime(result.getCreatedAt())
                .mediaType(type)
                .mediaId(result.getMediaId())
                .lastModifiedTime(result.getCreatedAt())
                .build();
        tempMediaFileDb.put(url, storeEntity);
        if (result.getMediaId() != null) {
            tempMediaIdDb.put(result.getMediaId(), url);
        }
        // 每执行一个写操作，都要commit，否则强制终止程序时会导致打不开数据库文件
        db.commit();
        return result;
    }

    public String findTempMediaIdByUrl(String url) {
        StoreEntity storeEntity = tempMediaFileDb.get(url);
        // 如果保存的最后更新时间再文件的最后更新时间之前，说明文件有更新，返回空
        if (storeEntity != null) {
            return storeEntity.mediaId;
        }
        return null;
    }

    public String findMediaIdByFile(File file) {
        StoreEntity storeEntity = mediaFileDb.get(file.getAbsolutePath());
        // 如果保存的最后更新时间再文件的最后更新时间之前，说明文件有更新，返回空
        if (storeEntity != null && storeEntity.lastModifiedTime.getTime() >= file.lastModified()) {
            return storeEntity.mediaId;
        }
        return null;
    }

    /**
     * 根据mediaId查找File
     *
     * @param mediaId
     * @return File
     */
    public File findFileByMediaId(String mediaId) {
        String filePath = mediaIdDb.get(mediaId);
        return findFile(filePath);
    }

    public WxMedia.MediaResult storeFileToMedia(WxMedia.Type type, File file, WxMedia.MediaResult result) {
        StoreEntity storeEntity = StoreEntity.builder()
                .filePath(file.getAbsolutePath())
                .createTime(new Date())
                .mediaType(type)
                .mediaId(result.getMediaId())
                .lastModifiedTime(new Date(file.lastModified()))
                .mediaUrl(result.getUrl())
                .build();
        mediaFileDb.put(file.getAbsolutePath(), storeEntity);
        if (result.getMediaId() != null) {
            mediaIdDb.put(result.getMediaId(), file.getAbsolutePath());
        }
        if (result.getUrl() != null) {
            // 如果有url，还要额外保存两个东西
            // 1、filePath对应的url。2、mediaId对应的url
            // 至于url与mediaId的映射关系和url与filePath的映射关系，暂时没有发现应用场景
            // 我在想是不是不用保存？其实保存三对儿关系即可？这里先直接保存吧。。。毕竟我想区分image的映射
            urlDb.put(file.getAbsolutePath(), result.getUrl());
            urlDb.put(result.getUrl(), file.getAbsolutePath());
            /* if (result.getMediaId() != null) {
                // 顺着就能找到所有的了
                // update，保持与图片那里一致，只保存两个映射关系
                urlDb.put(result.getUrl(), result.getMediaId());
                urlDb.put(result.getMediaId(), file.getAbsolutePath());
            } */
        }
        // 每执行一个写操作，都要commit，否则强制终止程序时会导致打不开数据库文件
        db.commit();
        return result;
    }

    /**
     * 保存media到File
     *
     * @param mediaId
     * @return File
     */
    public File storeMediaToFile(String mediaId, Resource resource) throws IOException {
        String fileName = resource.getFilename();
        if (fileName == null) {
            fileName = mediaId;
        }
        File file = new File(StringUtils.applyRelativePath(defaultFilePath, fileName));
        if (file.exists()) {
            return file;
        }
        StoreEntity storeEntity = storeFile(file, mediaId, resource);
        mediaFileDb.put(file.getAbsolutePath(), storeEntity);
        mediaIdDb.put(mediaId, file.getAbsolutePath());
        db.commit();
        return file;
    }

    /**
     * 根据file查找url
     * 暂时不考虑图片修改
     *
     * @param file
     * @return String
     */
    public String findUrlByFile(File file) {
        return urlDb.get(file.getAbsolutePath());
    }

    /**
     * 保存图片URL到图片URL
     */
    public void storeUrlToUrl(String imgUrl, WxMedia.ImageResult result) {
        urlDb.put(imgUrl, result.getUrl());
        urlDb.put(result.getUrl(), imgUrl);
        db.commit();
    }

    /**
     * 根据imgUrl查找url
     * 暂时不考虑图片修改
     *
     * @param imgUrl
     * @return String
     */
    public String findUrlByUrl(String imgUrl) {
        return urlDb.get(imgUrl);
    }

    /**
     * 保存文件到url
     *
     * @param file
     * @param result
     */
    public void storeFileToUrl(File file, WxMedia.ImageResult result) {
        urlDb.put(file.getPath(), result.getUrl());
        urlDb.put(result.getUrl(), file.getPath());
        db.commit();
    }

    /**
     * 只能用来删除永久素材
     *
     * @param mediaId
     */
    public void deleteMedia(String mediaId) {
        String filePath = mediaIdDb.remove(mediaId);
        StoreEntity storeEntity = mediaFileDb.remove(filePath);
        if (storeEntity != null && storeEntity.mediaUrl != null) {
            urlDb.remove(filePath);
            urlDb.remove(storeEntity.mediaUrl);
        }
        db.commit();
    }

    public File findFileByUrl(String url) {
        String filePath = urlDb.get(url);
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    private File findFile(String filePath) {
        if (filePath == null) {
            return null;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private StoreEntity storeFile(File file, String mediaId, Resource resource) throws IOException {
        file.createNewFile();
        file.setLastModified(0l);
        FileCopyUtils.copy(resource.getInputStream(), new FileOutputStream(file));
        StoreEntity storeEntity = StoreEntity.builder()
                .filePath(file.getAbsolutePath())
                .createTime(new Date())
                .mediaType(null) //有必要的话可以尝试解析文件名来获取mediaType，暂时不想做
                .mediaId(mediaId)
                .lastModifiedTime(new Date(0l))
                .build();
        return storeEntity;
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
                .transactionDisable()
                .cacheDisable()
                .asyncWriteEnable()
                .checksumEnable()
                .closeOnJvmShutdown().make();
        tempMediaFileDb = db.createHashMap("tempMediaFile").expireAfterWrite(3, TimeUnit.DAYS).makeOrGet();
        tempMediaIdDb = db.createHashMap("tempMediaId").expireAfterWrite(3, TimeUnit.DAYS).makeOrGet();
        mediaFileDb = db.createHashMap("mediaFile").makeOrGet();
        mediaIdDb = db.createHashMap("mediaId").makeOrGet();
        urlDb = db.createHashMap("imageUrl").makeOrGet();
    }

    /**
     * 用于存储的实体
     */
    private static class StoreEntity implements Serializable {
        /**
         * 文件路径
         */
        private String filePath;

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
        private Date createTime;

        /**
         * 最后一次更新时间
         */
        private Date lastModifiedTime;

        /**
         * 媒体类型
         */
        private WxMedia.Type mediaType;

        StoreEntity(String filePath, String mediaId, String mediaUrl, Date createTime, Date lastModifiedTime, WxMedia.Type mediaType) {
            this.filePath = filePath;
            this.mediaId = mediaId;
            this.mediaUrl = mediaUrl;
            this.createTime = createTime;
            this.lastModifiedTime = lastModifiedTime;
            this.mediaType = mediaType;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String filePath;
            private String mediaId;
            private String mediaUrl;
            private Date createTime;
            private Date lastModifiedTime;
            private WxMedia.Type mediaType;

            Builder() {
            }

            public Builder filePath(String filePath) {
                this.filePath = filePath;
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

            public Builder createTime(Date createTime) {
                this.createTime = createTime;
                return this;
            }

            public Builder lastModifiedTime(Date lastModifiedTime) {
                this.lastModifiedTime = lastModifiedTime;
                return this;
            }

            public Builder mediaType(WxMedia.Type mediaType) {
                this.mediaType = mediaType;
                return this;
            }

            public WxMediaStore.StoreEntity build() {
                return new WxMediaStore.StoreEntity(filePath, mediaId, mediaUrl, createTime, lastModifiedTime, mediaType);
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.support.MapDbWxMediaStore.StoreEntity.Builder(filePath=" + this.filePath + ", mediaId=" + this.mediaId + ", mediaUrl=" + this.mediaUrl + ", createTime=" + this.createTime + ", lastModifiedTime=" + this.lastModifiedTime + ", mediaType=" + this.mediaType + ")";
            }
        }
    }

    /*
    public static void main1(String[] args) throws IOException {
        File file = new File("~/weixin/media/db/store.db");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        DB db = DBMaker.newFileDB(file)
                .transactionDisable().asyncWriteFlushDelay(100).closeOnJvmShutdown().make();
//        db.catPut("a", "b");
        String b = db.catGet("a");
        // 2.0支持对更新创建和获取操作加入过期时间
        HTreeMap map = db.getHashMap("tempMedia");
        System.out.println(b);

        WxMedia.TempMediaResult result = new WxMedia.TempMediaResult();
        result.setCreatedTime(new Date());
        result.setType(WxMedia.Type.IMAGE);
        result.setMediaId("asfsfsafsfdsf");

        StoreEntity storeEntity = StoreEntity.builder()
                .resourcePath(file.getAbsolutePath())
                .createdTime(new Date())
                .mediaType(WxMedia.Type.VIDEO)
                .mediaId("adsfsfsffs")
                .modifiedTime(new Date(file.lastModified()))
                .build();
//        map.put(file.getPath(), storeEntity);
        Object o = map.get(file.getAbsolutePath());
        System.out.println(o);
    } */

}
