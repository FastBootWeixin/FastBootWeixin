package com.example.myproject.module.media;

import com.example.myproject.module.message.adapters.WxJsonAdapters;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * FastBootWeixin  WxMaterial
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMaterial
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 21:05
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WxMaterial {

    public enum Type {

        @JsonProperty("image")
        IMAGE,
        @JsonProperty("voice")
        VOICE,
        @JsonProperty("video")
        VIDEO,
        @JsonProperty("thumb")
        THUMB;
    }

    @JsonProperty("media_id")
    private String mediaId;

    /**
     * 标记是result
     */
    public interface Result {

        /**
         * 获取关键信息
         * @return
         */
        String keyInfo();

    }

    /**
     * 上传临时素材的响应
     */
    @Data
    public static class MediaResult implements Result {

        @JsonProperty("type")
        private Type type;

        @JsonProperty("media_id")
        private String mediaId;

        @JsonDeserialize(converter = WxJsonAdapters.WxDateConverter.class)
        @JsonProperty("created_at")
        private Date createdAt;

        @Override
        public String keyInfo() {
            return this.mediaId;
        }
    }

    /**
     * 上传永久素材的接口
     */
    @Data
    public static class MaterialResult implements Result {

        @JsonProperty("url")
        private String url;

        @JsonProperty("media_id")
        private String mediaId;

        @Override
        public String keyInfo() {
            if (url != null) {
                return url;
            } else {
                return mediaId;
            }
        }
    }

    /**
     * 上传图片的结果
     */
    @Data
    public static class ImageResult implements Result {

        @JsonProperty("url")
        private String url;

        @Override
        public String keyInfo() {
            return this.url;
        }
    }

    /**
     * 上传图文消息的结果
     */
    @Data
    public static class NewsResult implements Result {

        @JsonProperty("media_id")
        private String mediaId;

        @Override
        public String keyInfo() {
            return this.mediaId;
        }

    }

    public static class Video {

        @JsonProperty("title")
        private String title;

        @JsonProperty("introduction")
        private String introduction;

        @JsonProperty("down_url")
        private String downUrl;

    }

    /**
     * 新增图文素材的实体
     */
    public static class News {

        @JsonProperty("articles")
        private List<Article> articles;

        public static class Article {
            /**
             * 是	标题
             */
            @JsonProperty("title")
            private String title;

            /**
             * 是	图文消息的封面图片素材id（必须是永久mediaID）
             */
            @JsonProperty("thumb_media_id")
            private String thumbMediaId;

            /**
             * 否	作者
             */
            @JsonProperty("author")
            private String author;

            /**
             * 否	图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前64个字。
             */
            @JsonProperty("digest")
            private String digest;

            /**
             * 是	是否显示封面，0为false，即不显示，1为true，即显示
             */
            @JsonProperty("show_cover_pic")
            private boolean showCoverPic;

            /**
             * 是	图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图
             * 片url必须来源"上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。
             */
            @JsonProperty("content")
            private String content;

            /**
             * 是	图文消息的原文地址，即点击“阅读原文”后的URL
             */
            @JsonProperty("content_source_url")
            private String contentSourceUrl;
        }
    }

    public static class NewsForUpdate {

        /**
         * 要修改的图文消息的id
         */
        @JsonProperty("media_id")
        private String mediaId;

        /**
         * 要修改的索引
         */
        @JsonProperty("index")
        private Integer index;

        /**
         * 修改成什么
         */
        @JsonProperty("articles")
        private News.Article article;

    }


}
