package com.example.myproject.module.media;

import lombok.Builder;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * media存储器，提供媒体文件获取，媒体文件保存，转换文件等功能
 * 数据库使用内嵌数据库，经过一天的maven仓库database embedded选型，暂时决定使用MapDB(200k，其实有700K)或者kahaDB(600k)
 * 还有一个PalDB，这些都不小，真不行了我自己实现一个好了。。。暂时先用现成的
 * MapDB最新版依赖真的太多了，不想用了，先用MapDB的老版本吧
 */
public class WxMediaStore implements InitializingBean {

    private DB db;

    private HTreeMap<String, StoreEntity> tempMediaFileDb;

    private HTreeMap<String, String> tempMediaIdDb;

    public WxMedia.TempMediaResult getTempMedia(String filePath) {
        return (WxMedia.TempMediaResult) tempMediaFileDb.get(filePath).result;
    }

    public void storeTempMedia(WxMedia.Type type, File file, WxMedia.TempMediaResult result) {
        StoreEntity storeEntity = StoreEntity.builder()
                .filePath(file.getPath())
                .createTime(result.getCreatedAt())
                .mediaType(type)
                .mediaId(result.getMediaId())
                .lastModifiedTime(new Date(file.lastModified()))
                .result(result)
                .build();
        tempMediaFileDb.put(file.getPath(), storeEntity);
        if (result.getMediaId() != null) {
            tempMediaIdDb.put(result.getMediaId(), file.getPath());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File file = new File("~/weixin/media/db/store.db");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        db = DBMaker.newFileDB(file)
                .transactionDisable().asyncWriteFlushDelay(100).make();
        if (db.exists("tempMediaFile")) {
            tempMediaFileDb = db.getHashMap("tempMediaFile");
            tempMediaIdDb = db.getHashMap("tempMediaId");
        } else {
            tempMediaFileDb = db.createHashMap("tempMediaFile").expireAfterWrite(3, TimeUnit.DAYS).make();
            tempMediaIdDb = db.createHashMap("tempMediaId").expireAfterWrite(3, TimeUnit.DAYS).make();
        }
    }

    public static void main(String[] args) throws IOException {
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
        HTreeMap map = db.createHashMap("tempMedia").expireAfterWrite(3, TimeUnit.DAYS).make();
        System.out.println(b);
    }

    /**
     * 用于存储的实体
     */
    @Builder
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

        /**
         * 原始结果
         */
        private WxMedia.Result result;
    }

}
