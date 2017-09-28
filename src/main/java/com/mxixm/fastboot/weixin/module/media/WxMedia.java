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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mxixm.fastboot.weixin.module.adapters.WxJsonAdapters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * FastBootWeixin WxMedia
 * 虽然叫素材，但是要和media区分好
 *
 * @author Guangshan
 * @date 2017/8/12 21:05
 * @since 0.1.2
 */
public class WxMedia {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    public WxMedia(String mediaId) {
        this.mediaId = mediaId;
    }

    public WxMedia() {
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof WxMedia)) return false;
        final WxMedia other = (WxMedia) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$mediaId = this.getMediaId();
        final Object other$mediaId = other.getMediaId();
        if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mediaId = this.getMediaId();
        result = result * PRIME + ($mediaId == null ? 43 : $mediaId.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof WxMedia;
    }

    public String toString() {
        return "com.mxixm.fastboot.weixin.module.media.WxMedia(mediaId=" + this.getMediaId() + ")";
    }

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
     * @return dummy
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
         * @return dummy
         */
        String keyInfo();

    }

    /**
     * 上传临时素材的响应
     */
    public static class TempMediaResult implements Result {

        @JsonProperty("type")
        private Type type;

        @JsonProperty("media_id")
        private String mediaId;

        @JsonDeserialize(converter = WxJsonAdapters.WxIntDateConverter.class)
        @JsonProperty("created_at")
        private Date createdAt;

        public TempMediaResult() {
        }

        @Override
        public String keyInfo() {
            return this.mediaId;
        }

        public Type getType() {
            return this.type;
        }

        public String getMediaId() {
            return this.mediaId;
        }

        public Date getCreatedAt() {
            return this.createdAt;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof TempMediaResult)) return false;
            final TempMediaResult other = (TempMediaResult) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$type = this.getType();
            final Object other$type = other.getType();
            if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) return false;
            final Object this$createdAt = this.getCreatedAt();
            final Object other$createdAt = other.getCreatedAt();
            if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt))
                return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $type = this.getType();
            result = result * PRIME + ($type == null ? 43 : $type.hashCode());
            final Object $mediaId = this.getMediaId();
            result = result * PRIME + ($mediaId == null ? 43 : $mediaId.hashCode());
            final Object $createdAt = this.getCreatedAt();
            result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof TempMediaResult;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.TempMediaResult(type=" + this.getType() + ", mediaId=" + this.getMediaId() + ", createdAt=" + this.getCreatedAt() + ")";
        }
    }

    /**
     * 上传永久素材的接口
     */
    public static class MediaResult implements Result {

        @JsonProperty("url")
        private String url;

        @JsonProperty("media_id")
        private String mediaId;

        public MediaResult() {
        }

        @Override
        public String keyInfo() {
            if (url != null) {
                return url;
            } else {
                return mediaId;
            }
        }

        public String getUrl() {
            return this.url;
        }

        public String getMediaId() {
            return this.mediaId;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof MediaResult)) return false;
            final MediaResult other = (MediaResult) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            final Object $mediaId = this.getMediaId();
            result = result * PRIME + ($mediaId == null ? 43 : $mediaId.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof MediaResult;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.MediaResult(url=" + this.getUrl() + ", mediaId=" + this.getMediaId() + ")";
        }
    }

    /**
     * 上传图片的结果
     */
    public static class ImageResult implements Result {

        @JsonProperty("url")
        private String url;

        public ImageResult() {
        }

        @Override
        public String keyInfo() {
            return this.url;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ImageResult)) return false;
            final ImageResult other = (ImageResult) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof ImageResult;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.ImageResult(url=" + this.getUrl() + ")";
        }
    }

    /**
     * 上传图文消息的结果
     */
    public static class NewsResult implements Result {

        @JsonProperty("media_id")
        private String mediaId;

        public NewsResult() {
        }

        @Override
        public String keyInfo() {
            return this.mediaId;
        }

        public String getMediaId() {
            return this.mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof NewsResult)) return false;
            final NewsResult other = (NewsResult) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $mediaId = this.getMediaId();
            result = result * PRIME + ($mediaId == null ? 43 : $mediaId.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof NewsResult;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.NewsResult(mediaId=" + this.getMediaId() + ")";
        }
    }

    public static class Video implements Serializable {

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

        public Video() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getTitle() {
            return this.title;
        }

        public String getIntroduction() {
            return this.introduction;
        }

        public String getDownUrl() {
            return this.downUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public void setDownUrl(String downUrl) {
            this.downUrl = downUrl;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Video)) return false;
            final Video other = (Video) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$title = this.getTitle();
            final Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
            final Object this$introduction = this.getIntroduction();
            final Object other$introduction = other.getIntroduction();
            if (this$introduction == null ? other$introduction != null : !this$introduction.equals(other$introduction))
                return false;
            final Object this$downUrl = this.getDownUrl();
            final Object other$downUrl = other.getDownUrl();
            if (this$downUrl == null ? other$downUrl != null : !this$downUrl.equals(other$downUrl)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $title = this.getTitle();
            result = result * PRIME + ($title == null ? 43 : $title.hashCode());
            final Object $introduction = this.getIntroduction();
            result = result * PRIME + ($introduction == null ? 43 : $introduction.hashCode());
            final Object $downUrl = this.getDownUrl();
            result = result * PRIME + ($downUrl == null ? 43 : $downUrl.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Video;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.Video(title=" + this.getTitle() + ", introduction=" + this.getIntroduction() + ", downUrl=" + this.getDownUrl() + ")";
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
                return new News(items);
            }

            public String toString() {
                return "com.example.myproject.module.media.WxMedia.News.Builder(articles=" + this.articles + ")";
            }
        }
    }

    /**
     * 更新图文消息的实体，单条new
     */
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

        public String getMediaId() {
            return this.mediaId;
        }

        public Integer getIndex() {
            return this.index;
        }

        public Article getArticle() {
            return this.article;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public void setArticle(Article article) {
            this.article = article;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof New)) return false;
            final New other = (New) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) return false;
            final Object this$index = this.getIndex();
            final Object other$index = other.getIndex();
            if (this$index == null ? other$index != null : !this$index.equals(other$index)) return false;
            final Object this$article = this.getArticle();
            final Object other$article = other.getArticle();
            if (this$article == null ? other$article != null : !this$article.equals(other$article)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $mediaId = this.getMediaId();
            result = result * PRIME + ($mediaId == null ? 43 : $mediaId.hashCode());
            final Object $index = this.getIndex();
            result = result * PRIME + ($index == null ? 43 : $index.hashCode());
            final Object $article = this.getArticle();
            result = result * PRIME + ($article == null ? 43 : $article.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof New;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.New(mediaId=" + this.getMediaId() + ", index=" + this.getIndex() + ", article=" + this.getArticle() + ")";
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

        public Article() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getTitle() {
            return this.title;
        }

        public String getThumbMediaId() {
            return this.thumbMediaId;
        }

        public boolean isShowCoverPic() {
            return this.showCoverPic;
        }

        public String getContent() {
            return this.content;
        }

        public String getContentSourceUrl() {
            return this.contentSourceUrl;
        }

        public String getAuthor() {
            return this.author;
        }

        public String getDigest() {
            return this.digest;
        }

        public String getUrl() {
            return this.url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
        }

        public void setShowCoverPic(boolean showCoverPic) {
            this.showCoverPic = showCoverPic;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setContentSourceUrl(String contentSourceUrl) {
            this.contentSourceUrl = contentSourceUrl;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Article)) return false;
            final Article other = (Article) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$title = this.getTitle();
            final Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
            final Object this$thumbMediaId = this.getThumbMediaId();
            final Object other$thumbMediaId = other.getThumbMediaId();
            if (this$thumbMediaId == null ? other$thumbMediaId != null : !this$thumbMediaId.equals(other$thumbMediaId))
                return false;
            if (this.isShowCoverPic() != other.isShowCoverPic()) return false;
            final Object this$content = this.getContent();
            final Object other$content = other.getContent();
            if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
            final Object this$contentSourceUrl = this.getContentSourceUrl();
            final Object other$contentSourceUrl = other.getContentSourceUrl();
            if (this$contentSourceUrl == null ? other$contentSourceUrl != null : !this$contentSourceUrl.equals(other$contentSourceUrl))
                return false;
            final Object this$author = this.getAuthor();
            final Object other$author = other.getAuthor();
            if (this$author == null ? other$author != null : !this$author.equals(other$author)) return false;
            final Object this$digest = this.getDigest();
            final Object other$digest = other.getDigest();
            if (this$digest == null ? other$digest != null : !this$digest.equals(other$digest)) return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $title = this.getTitle();
            result = result * PRIME + ($title == null ? 43 : $title.hashCode());
            final Object $thumbMediaId = this.getThumbMediaId();
            result = result * PRIME + ($thumbMediaId == null ? 43 : $thumbMediaId.hashCode());
            result = result * PRIME + (this.isShowCoverPic() ? 79 : 97);
            final Object $content = this.getContent();
            result = result * PRIME + ($content == null ? 43 : $content.hashCode());
            final Object $contentSourceUrl = this.getContentSourceUrl();
            result = result * PRIME + ($contentSourceUrl == null ? 43 : $contentSourceUrl.hashCode());
            final Object $author = this.getAuthor();
            result = result * PRIME + ($author == null ? 43 : $author.hashCode());
            final Object $digest = this.getDigest();
            result = result * PRIME + ($digest == null ? 43 : $digest.hashCode());
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Article;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.Article(title=" + this.getTitle() + ", thumbMediaId=" + this.getThumbMediaId() + ", showCoverPic=" + this.isShowCoverPic() + ", content=" + this.getContent() + ", contentSourceUrl=" + this.getContentSourceUrl() + ", author=" + this.getAuthor() + ", digest=" + this.getDigest() + ", url=" + this.getUrl() + ")";
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
