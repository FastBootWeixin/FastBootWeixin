package com.example.myproject.module.media;

import com.example.myproject.module.message.adapters.WxJsonAdapters;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * FastBootWeixin  WxMedia
 * 虽然叫素材，但是要和media区分好
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMedia
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 21:05
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WxMedia {

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
     * 静态工厂方法
     *
     * @param mediaId
     * @return
     */
    public static WxMedia of(String mediaId) {
        return new WxMedia(mediaId);
    }

    /**
     * 标记是result
     */
    public interface Result {

        /**
         * 获取关键信息
         *
         * @return
         */
        String keyInfo();

    }

    /**
     * 上传临时素材的响应
     */
    @Data
    public static class TempMediaResult implements Result {

        @JsonProperty("type")
        private Type type;

        @JsonProperty("media_id")
        private String mediaId;

        @JsonDeserialize(converter = WxJsonAdapters.WxIntDateConverter.class)
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
    public static class MediaResult implements Result {

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

    @Data
    public static class Video {

        @JsonProperty("title")
        private String title;

        @JsonProperty("introduction")
        private String introduction;

        @JsonProperty("down_url")
        private String downUrl;

    }

    /**
     * 新增和获取图文素材的实体，因为转为json提交给微信时的key为articles
     * 从微信获取的key为news_item，故写了setter和getter
     */
    @Data
    public static class News {

        @JsonIgnore
        private List<Article> articles;

        //@JsonProperty("articles")
        @JsonGetter("articles")
        public List<Article> getArticles() {
            return articles;
        }

        @JsonSetter("news_item")
        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }
    }

    /**
     * 更新图文消息的实体，单条new
     */
    @Data
    public static class New {

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
        private Article article;

    }

    /**
     * 图文消息的article
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        @JsonSerialize(converter = WxJsonAdapters.WxBooleanIntConverter.class)
        private boolean showCoverPic;

        /**
         * 是	图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图
         * 片url必须来源"上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。
         */
        @JsonProperty("content")
        private String content;

        /**
         * 否    图文页的URL
         */
        @JsonProperty("url")
        private String url;
        /**
         * 是	图文消息的原文地址，即点击“阅读原文”后的URL
         */
        @JsonProperty("content_source_url")
        private String contentSourceUrl;
    }

    public static class Count {

        @JsonProperty("voice_count")
        private int voice;

        @JsonProperty("video_count")
        private int video;

        @JsonProperty("image_count")
        private int image;

        @JsonProperty("news_count")
        private int news;

    }

}
