package com.example.myproject.module.media;

import com.example.myproject.module.message.adapters.WxJsonAdapters;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
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

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

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
    public interface Result extends Serializable {

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
    @NoArgsConstructor
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
    @NoArgsConstructor
    public static class Video {

        @JsonProperty("title")
        private String title;

        @JsonProperty("introduction")
        private String introduction;

        @JsonProperty("down_url")
        private String downUrl;

        Video(String title, String introduction) {
            this.title = title;
            this.introduction = introduction;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String title;
            private String introduction;

            Builder() {
            }

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public Builder introduction(String introduction) {
                this.introduction = introduction;
                return this;
            }

            public Video build() {
                return new Video(title, introduction);
            }

            public String toString() {
                return "com.example.myproject.module.media.WxMedia.Video.Builder(title=" + this.title + ", introduction=" + this.introduction + ")";
            }
        }
    }

    /**
     * 新增和获取图文素材的实体，因为转为json提交给微信时的key为articles
     * 从微信获取的key为news_item，故写了setter和getter
     */
    public static class News {

        @JsonIgnore
        private List<Article> articles;

        News(List<Article> articles) {
            this.articles = articles;
        }

        public static Builder builder() {
            return new Builder();
        }

        //@JsonProperty("articles")
        @JsonGetter("articles")
        public List<Article> getArticles() {
            return articles;
        }

        @JsonSetter("news_item")
        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }

        public static class Builder {

            private LinkedList<Article> articles;

            private Article lastArticle;

            Builder() {
                articles = new LinkedList<>();
            }

            public Builder firstArticle(String title, String thumbMediaId, boolean showCoverPic, String content, String contentSourceUrl, String author, String digest) {
                this.articles.addFirst(new Article(title, thumbMediaId, showCoverPic, content, contentSourceUrl, author, digest));
                return this;
            }

            public Builder firstArticle(Article item) {
                this.articles.addFirst(item);
                return this;
            }

            public Builder addArticle(String title, String thumbMediaId, boolean showCoverPic, String content, String contentSourceUrl, String author, String digest) {
                this.articles.addLast(new Article(title, thumbMediaId, showCoverPic, content, contentSourceUrl, author, digest));
                return this;
            }

            public Builder addArticle(Article item) {
                this.articles.addLast(item);
                return this;
            }

            public Builder addArticles(Collection<Article> item) {
                this.articles.addAll(item);
                return this;
            }

            public Builder lastArticle(Article article) {
                this.lastArticle = article;
                return this;
            }

            // 这里关于最后项目的判断应该能优化一下，今天太累了，明天改2017年8月7日00:18:52
            public News build() {
                // 这里可能不是一个好的代码习惯，可能会造成items变量名混乱。
                List<Article> items = this.articles;
                if (this.articles.size() > 7) {
                    if (this.lastArticle != null) {
                        logger.warn("图文消息至多只能有八条，最后的图文消息将被忽略");
                        items = this.articles.subList(0, 7);
                        items.add(this.lastArticle);
                    } else if (this.articles.size() > 8) {
                        logger.warn("图文消息至多只能有八条，最后的图文消息将被忽略");
                        items = this.articles.subList(0, 8);
                    }
                } else if (this.lastArticle != null) {
                    items.add(this.lastArticle);
                }
                return new News(articles);
            }

            public String toString() {
                return "com.example.myproject.module.media.WxMedia.News.Builder(articles=" + this.articles + ")";
            }
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

        New(String mediaId, Integer index, Article article) {
            this.mediaId = mediaId;
            this.index = index;
            this.article = article;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String mediaId;
            private Integer index;
            private Article article;

            Builder() {
            }

            public Builder mediaId(String mediaId) {
                this.mediaId = mediaId;
                return this;
            }

            public Builder index(Integer index) {
                this.index = index;
                return this;
            }

            public Builder article(Article article) {
                this.article = article;
                return this;
            }

            public New build() {
                return new New(mediaId, index, article);
            }

            public String toString() {
                return "com.example.myproject.module.media.WxMedia.New.Builder(mediaId=" + this.mediaId + ", index=" + this.index + ", article=" + this.article + ")";
            }
        }
    }

    /**
     * 图文消息的article
     */
    @Data
    @NoArgsConstructor
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
         * 是	图文消息的原文地址，即点击“阅读原文”后的URL
         */
        @JsonProperty("content_source_url")
        private String contentSourceUrl;

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
         * 否    图文页的URL
         */
        @JsonProperty("url")
        private String url;

        Article(String title, String thumbMediaId, boolean showCoverPic, String content, String contentSourceUrl, String author, String digest) {
            this.title = title;
            this.thumbMediaId = thumbMediaId;
            this.showCoverPic = showCoverPic;
            this.content = content;
            this.contentSourceUrl = contentSourceUrl;
            this.digest = digest;
            this.author = author;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String title;
            private String thumbMediaId;
            private String author;
            private String digest;
            private boolean showCoverPic;
            private String content;
            private String contentSourceUrl;

            Builder() {
            }

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public Builder thumbMediaId(String thumbMediaId) {
                this.thumbMediaId = thumbMediaId;
                return this;
            }

            public Builder author(String author) {
                this.author = author;
                return this;
            }

            public Builder digest(String digest) {
                this.digest = digest;
                return this;
            }

            public Builder showCoverPic(boolean showCoverPic) {
                this.showCoverPic = showCoverPic;
                return this;
            }

            public Builder content(String content) {
                this.content = content;
                return this;
            }

//            url只是返回结果中的东西
//            public Builder url(String url) {
//                this.url = url;
//                return this;
//            }

            public Builder contentSourceUrl(String contentSourceUrl) {
                this.contentSourceUrl = contentSourceUrl;
                return this;
            }

            public Article build() {
                return new Article(title, thumbMediaId, showCoverPic, content, contentSourceUrl, author, digest);
            }

            public String toString() {
                return "com.example.myproject.module.media.WxMedia.Article.Builder(title=" + this.title + ", thumbMediaId=" + this.thumbMediaId + ", author=" + this.author + ", digest=" + this.digest + ", showCoverPic=" + this.showCoverPic + ", content=" + this.content + ", contentSourceUrl=" + this.contentSourceUrl + ")";
            }
        }
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
