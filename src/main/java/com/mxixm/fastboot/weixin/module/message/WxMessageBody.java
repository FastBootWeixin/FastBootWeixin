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

package com.mxixm.fastboot.weixin.module.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.io.Resource;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * fastboot-weixin  WxMessageBody
 * 其中@XmlType是干啥的？因为JAXBContext中不能有相同类名，或者说不能有相同的@XmlType的name，默认使用的是类名
 * 所以这里显式指定一下类型名。
 *
 * @author Guangshan
 * @date 2017/9/24 14:08
 * @since 0.1.3
 */
public class WxMessageBody {

    @XmlAccessorType(XmlAccessType.NONE)
    @XmlType(name = "TextBody")
    public static class Text extends WxMessageBody {

        @JsonProperty("content")
        protected String content;

        public Text(String content) {
            this.content = content;
        }

        public Text() {
        }

        public String getContent() {
            return content;
        }
    }


    @XmlType(name = "MediaBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Media extends WxMessageBody {

        @XmlElement(name = "MediaId", required = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("media_id")
        protected String mediaId;

        @JsonIgnore
        protected String mediaPath;

        @JsonIgnore
        protected String mediaUrl;

        @JsonIgnore
        protected Resource mediaResource;

        public Media(String mediaId, String mediaPath, String mediaUrl, Resource mediaResource) {
            this.mediaId = mediaId;
            this.mediaPath = mediaPath;
            this.mediaUrl = mediaUrl;
            this.mediaResource = mediaResource;
        }

        public Media() {
        }

        public String getMediaId() {
            return this.mediaId;
        }

        public String getMediaPath() {
            return this.mediaPath;
        }

        public String getMediaUrl() {
            return this.mediaUrl;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public void setMediaPath(String mediaPath) {
            this.mediaPath = mediaPath;
        }

        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }

        public Resource getMediaResource() {
            return mediaResource;
        }

        public void setMediaResource(Resource mediaResource) {
            this.mediaResource = mediaResource;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Media media = (Media) o;
            return Objects.equals(mediaId, media.mediaId) &&
                    Objects.equals(mediaPath, media.mediaPath) &&
                    Objects.equals(mediaUrl, media.mediaUrl) &&
                    Objects.equals(mediaResource, media.mediaResource);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mediaId, mediaPath, mediaUrl, mediaResource);
        }

        @Override
        public String toString() {
            return "Media{" +
                    "mediaId='" + mediaId + '\'' +
                    ", mediaPath='" + mediaPath + '\'' +
                    ", mediaUrl='" + mediaUrl + '\'' +
                    ", mediaResource=" + mediaResource +
                    '}';
        }
    }

    @XmlType(name = "ImageBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Image extends Media {

        public Image() {
        }

        public Image(String mediaId, String mediaPath, String mediaUrl, Resource mediaResource) {
            super(mediaId, mediaPath, mediaUrl, mediaResource);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Image)) {
                return false;
            }
            final Image other = (Image) o;
            if (!other.canEqual(this)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Image;
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.message.WxMessage.Image.WxMessageBody()";
        }
    }

    @XmlType(name = "VoiceBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Voice extends Media {
        public Voice(String mediaId, String mediaPath, String mediaUrl, Resource mediaResource) {
            super(mediaId, mediaPath, mediaUrl, mediaResource);
        }

        public Voice() {
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Voice)) {
                return false;
            }
            final Voice other = (Voice) o;
            if (!other.canEqual(this)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 1;
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Voice;
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.message.WxMessage.Voice.WxMessageBody()";
        }
    }

    @XmlType(name = "VideoBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Video extends Media {

        @XmlElement(name = "ThumbMediaId")
        @JsonProperty("thumb_media_id")
        protected String thumbMediaId;

        @XmlElement(name = "Title")
        @JsonProperty("title")
        protected String title;

        @XmlElement(name = "Description")
        @JsonProperty("description")
        protected String description;

        @JsonIgnore
        protected String thumbMediaPath;

        @JsonIgnore
        protected String thumbMediaUrl;

        @JsonIgnore
        protected Resource thumbMediaResource;

        public Video(String thumbMediaId, String title, String description, Resource thumbMediaResource, String thumbMediaPath, String thumbMediaUrl) {
            this.thumbMediaId = thumbMediaId;
            this.title = title;
            this.description = description;
            this.thumbMediaResource = thumbMediaResource;
            this.thumbMediaPath = thumbMediaPath;
            this.thumbMediaUrl = thumbMediaUrl;
        }

        public Video() {
        }

        public String getThumbMediaId() {
            return this.thumbMediaId;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }

        public String getThumbMediaPath() {
            return this.thumbMediaPath;
        }

        public String getThumbMediaUrl() {
            return this.thumbMediaUrl;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setThumbMediaPath(String thumbMediaPath) {
            this.thumbMediaPath = thumbMediaPath;
        }

        public void setThumbMediaUrl(String thumbMediaUrl) {
            this.thumbMediaUrl = thumbMediaUrl;
        }

        public Resource getThumbMediaResource() {
            return thumbMediaResource;
        }

        public void setThumbMediaResource(Resource thumbMediaResource) {
            this.thumbMediaResource = thumbMediaResource;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Video)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            Video video = (Video) o;
            return Objects.equals(thumbMediaId, video.thumbMediaId) &&
                    Objects.equals(title, video.title) &&
                    Objects.equals(description, video.description) &&
                    Objects.equals(thumbMediaPath, video.thumbMediaPath) &&
                    Objects.equals(thumbMediaUrl, video.thumbMediaUrl) &&
                    Objects.equals(thumbMediaResource, video.thumbMediaResource);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), thumbMediaId, title, description, thumbMediaPath, thumbMediaUrl, thumbMediaResource);
        }

        @Override
        public String toString() {
            return "Video{" +
                    "mediaId='" + mediaId + '\'' +
                    ", mediaPath='" + mediaPath + '\'' +
                    ", mediaUrl='" + mediaUrl + '\'' +
                    ", mediaResource=" + mediaResource +
                    ", thumbMediaId='" + thumbMediaId + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", thumbMediaPath='" + thumbMediaPath + '\'' +
                    ", thumbMediaUrl='" + thumbMediaUrl + '\'' +
                    ", thumbMediaResource=" + thumbMediaResource +
                    '}';
        }
    }

    /**
     * 小程序类型，这个只有用户消息有，群发消息没有
     * 有空把其他equals、hashcode、toString重构一下
     */
    @XmlType(name = "MiniProgram")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class MiniProgram extends WxMessageBody {

        @XmlElement(name = "ThumbMediaId")
        @JsonProperty("thumb_media_id")
        protected String thumbMediaId;

        @XmlElement(name = "Title")
        @JsonProperty("title")
        protected String title;

        @XmlElement(name = "Appid")
        @JsonProperty("appid")
        protected String appId;

        @XmlElement(name = "Pagepath")
        @JsonProperty("pagepath")
        protected String pagePath;

        @JsonIgnore
        protected String thumbMediaPath;

        @JsonIgnore
        protected String thumbMediaUrl;

        public MiniProgram() {
        }

        public String getThumbMediaId() {
            return thumbMediaId;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getPagePath() {
            return pagePath;
        }

        public void setPagePath(String pagePath) {
            this.pagePath = pagePath;
        }

        public String getThumbMediaPath() {
            return thumbMediaPath;
        }

        public void setThumbMediaPath(String thumbMediaPath) {
            this.thumbMediaPath = thumbMediaPath;
        }

        public String getThumbMediaUrl() {
            return thumbMediaUrl;
        }

        public void setThumbMediaUrl(String thumbMediaUrl) {
            this.thumbMediaUrl = thumbMediaUrl;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MiniProgram that = (MiniProgram) o;
            return Objects.equals(thumbMediaId, that.thumbMediaId) &&
                    Objects.equals(title, that.title) &&
                    Objects.equals(appId, that.appId) &&
                    Objects.equals(pagePath, that.pagePath) &&
                    Objects.equals(thumbMediaPath, that.thumbMediaPath) &&
                    Objects.equals(thumbMediaUrl, that.thumbMediaUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(thumbMediaId, title, appId, pagePath, thumbMediaPath, thumbMediaUrl);
        }

        @Override
        public String toString() {
            return "MiniProgram{" +
                    "thumbMediaId='" + thumbMediaId + '\'' +
                    ", title='" + title + '\'' +
                    ", appId='" + appId + '\'' +
                    ", pagePath='" + pagePath + '\'' +
                    ", thumbMediaPath='" + thumbMediaPath + '\'' +
                    ", thumbMediaUrl='" + thumbMediaUrl + '\'' +
                    '}';
        }
    }

    /**
     * 其实可以再抽象一个thumbMediaBody的。。。我懒
     */
    @XmlType(name = "MusicBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Music extends Media {

        /**
         * 替换父类中的mediaId，因为音乐类型的消息没有mediaId，只有thumbMediaId
         * 这里用mediaId表示thumbMediaId，以便做一些统一的处理
         */
        @XmlElement(name = "ThumbMediaId", required = true)
        @JsonProperty("thumb_media_id")
        protected String mediaId;

        @XmlElement(name = "Title")
        @JsonProperty("title")
        protected String title;

        @XmlElement(name = "Description")
        @JsonProperty("description")
        protected String description;

        @XmlElement(name = "MusicUrl")
        @JsonProperty("musicurl")
        protected String musicUrl;

        @XmlElement(name = "HQMusicUrl")
        @JsonProperty("hqmusicurl")
        protected String hqMusicUrl;

        public Music(String thumbMediaId, String title, String description, String musicUrl, String hqMusicUrl) {
            this.mediaId = thumbMediaId;
            this.title = title;
            this.description = description;
            this.musicUrl = musicUrl;
            this.hqMusicUrl = hqMusicUrl;
        }

        public Music() {
        }

        /**
         * 懒省事，做个简单的替换。音乐类型的消息没有mediaId，只有一个thumbMediaId，这里做一个简单的替换
         * 使用Media中的mediaId、mediaPath、mediaUrl作为音乐类型的thumb对应的属性。
         *
         * @param thumbMediaId
         */
        @Override
        public void setMediaId(String thumbMediaId) {
            this.mediaId = thumbMediaId;
        }

        @Override
        public String getMediaId() {
            return this.mediaId;
        }

        @JsonIgnore
        public String getThumbMediaId() {
            return this.mediaId;
        }

        @JsonIgnore
        public String getThumbMediaUrl() {
            return this.mediaUrl;
        }

        @JsonIgnore
        public String getThumbMediaPath() {
            return this.mediaPath;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }

        public String getMusicUrl() {
            return this.musicUrl;
        }

        public String getHqMusicUrl() {
            return this.hqMusicUrl;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.mediaId = thumbMediaId;
        }

        public void setThumbMediaPath(String thumbMediaPath) {
            this.mediaPath = thumbMediaPath;
        }

        public void setThumbMediaUrl(String thumbMediaUrl) {
            this.mediaUrl = thumbMediaUrl;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public void setHqMusicUrl(String hqMusicUrl) {
            this.hqMusicUrl = hqMusicUrl;
        }

        @JsonIgnore
        public Resource getThumbMediaResource() {
            return this.mediaResource;
        }

        public void setThumbMediaResource(Resource thumbMediaResource) {
            this.mediaResource = thumbMediaResource;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Music)) {
                return false;
            }
            final Music other = (Music) o;
            if (!other.canEqual(this)) {
                return false;
            }
            final Object this$thumbMediaId = this.getThumbMediaId();
            final Object other$thumbMediaId = other.getThumbMediaId();
            if (this$thumbMediaId == null ? other$thumbMediaId != null : !this$thumbMediaId.equals(other$thumbMediaId)) {
                return false;
            }
            final Object this$title = this.getTitle();
            final Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
                return false;
            }
            final Object this$description = this.getDescription();
            final Object other$description = other.getDescription();
            if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
                return false;
            }
            final Object this$musicUrl = this.getMusicUrl();
            final Object other$musicUrl = other.getMusicUrl();
            if (this$musicUrl == null ? other$musicUrl != null : !this$musicUrl.equals(other$musicUrl)) {
                return false;
            }
            final Object this$hqMusicUrl = this.getHqMusicUrl();
            final Object other$hqMusicUrl = other.getHqMusicUrl();
            if (this$hqMusicUrl == null ? other$hqMusicUrl != null : !this$hqMusicUrl.equals(other$hqMusicUrl)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $thumbMediaId = this.getThumbMediaId();
            result = result * PRIME + ($thumbMediaId == null ? 43 : $thumbMediaId.hashCode());
            final Object $title = this.getTitle();
            result = result * PRIME + ($title == null ? 43 : $title.hashCode());
            final Object $description = this.getDescription();
            result = result * PRIME + ($description == null ? 43 : $description.hashCode());
            final Object $musicUrl = this.getMusicUrl();
            result = result * PRIME + ($musicUrl == null ? 43 : $musicUrl.hashCode());
            final Object $hqMusicUrl = this.getHqMusicUrl();
            result = result * PRIME + ($hqMusicUrl == null ? 43 : $hqMusicUrl.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Music;
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.message.WxMessage.Music.WxMessageBody(thumbMediaId=" + this.getThumbMediaId() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", musicUrl=" + this.getMusicUrl() + ", hqMusicUrl=" + this.getHqMusicUrl() + ")";
        }
    }


    @XmlType(name = "NewsBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class News extends WxMessageBody {

        @XmlElements(@XmlElement(name = "item", type = Item.class))
        @JsonProperty("articles")
        protected List<Item> articles;

        public News(List<Item> articles) {
            this.articles = articles;
        }

        public News() {
        }

        public List<Item> getArticles() {
            return this.articles;
        }

        public static Item.Builder itemBuilder() {
            return Item.builder();
        }

        /**
         * 突然想省个事，虽然这里确实是用builder更好一点，但是我就是不用
         * 写builder了，但是刚才还有个事情忘记了，不知道是啥了。
         */
        @XmlAccessorType(XmlAccessType.NONE)
        public static class Item {

            @XmlElement(name = "Title", required = true)
            @JsonProperty("title")
            protected String title;

            @XmlElement(name = "Description", required = true)
            @JsonProperty("description")
            protected String description;

            @XmlElement(name = "PicUrl", required = true)
            @JsonProperty("picurl")
            protected String picUrl;

            @XmlElement(name = "Url", required = true)
            @JsonProperty("url")
            protected String url;

            public Item(String title, String description, String picUrl, String url) {
                this.title = title;
                this.description = description;
                this.picUrl = picUrl;
                this.url = url;
            }

            public Item() {
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getUrl() {
                return url;
            }

            public static Builder builder() {
                return new Builder();
            }

            public static class Builder {
                private String title;
                private String description;
                private String picUrl;
                private String url;

                Builder() {
                }

                public Builder title(String title) {
                    this.title = title;
                    return this;
                }

                public Builder description(String description) {
                    this.description = description;
                    return this;
                }

                public Builder picUrl(String picUrl) {
                    this.picUrl = picUrl;
                    return this;
                }

                public Builder url(String url) {
                    this.url = url;
                    return this;
                }

                public Item build() {
                    return new Item(title, description, picUrl, url);
                }

                @Override
                public String toString() {
                    return "com.example.myproject.module.message.WxMessage.News.Item.ItemBuilder(title=" + this.title + ", description=" + this.description + ", picUrl=" + this.picUrl + ", host=" + this.url + ")";
                }
            }
        }

    }


    @XmlType(name = "MpNewsBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class MpNews extends WxMessageBody {

        @XmlElement(name = "MediaId", required = true)
        @JsonProperty("media_id")
        protected String mediaId;

        /**
         * 图文消息被判定为转载时，是否继续群发。1为继续群发（转载），0为停止群发。该参数默认为0。
         * 这里用来传值，不体现在最终的xml或者json中
         */
        @JsonIgnore
        @XmlTransient
        protected boolean sendIgnoreReprint;

        public MpNews(String mediaId) {
            this.mediaId = mediaId;
        }

        public MpNews() {
        }
    }

    @XmlType(name = "CardBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class WxCard extends WxMessageBody {

        /**
         * 群发时只能发送已审核通过的卡券
         */
        @XmlElement(name = "CardId", required = true)
        @JsonProperty("card_id")
        protected String cardId;

        public WxCard(String cardId) {
            this.cardId = cardId;
        }

        public WxCard() {
        }
    }

    /**
     * 发送状态消息的封装
     */
    public static class Status extends WxMessageBody {
        @JsonIgnore
        protected boolean isTyping;

        public enum Command {

            @JsonProperty("Typing")
            TYPING,
            @JsonProperty("CancelTyping")
            CANCEL_TYPING

        }

    }

    /**
     * 模板消息体
     */
    public static class Template extends WxMessageBody implements Map<String, Template.TemplateData> {

        private Map<String, TemplateData> templateDateMap = new HashMap<>();

        @Override
        public int size() {
            return templateDateMap.size();
        }

        @Override
        public TemplateData get(Object key) {
            return templateDateMap.get(key);
        }

        @Override
        public Set<Map.Entry<String, TemplateData>> entrySet() {
            return templateDateMap.entrySet();
        }

        @Override
        public void putAll(Map<? extends String, ? extends TemplateData> m) {
            templateDateMap.putAll(m);
        }

        @Override
        public boolean containsKey(Object key) {
            return templateDateMap.containsKey(key);
        }

        @Override
        public TemplateData put(String key, TemplateData value) {
            return templateDateMap.put(key, value);
        }

        @Override
        public void clear() {
            templateDateMap.clear();
        }

        @Override
        public TemplateData remove(Object key) {
            return templateDateMap.remove(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return templateDateMap.containsValue(value);
        }

        @Override
        public Set<String> keySet() {
            return templateDateMap.keySet();
        }

        @Override
        public boolean isEmpty() {
            return templateDateMap.isEmpty();
        }

        @Override
        public Collection<TemplateData> values() {
            return templateDateMap.values();
        }

        @Override
        public boolean equals(Object o) {
            return templateDateMap.equals(o);
        }

        @Override
        public int hashCode() {
            return templateDateMap.hashCode();
        }

        public static class TemplateData {

            /**
             * 值
             */
            @JsonProperty("value")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            protected String value;

            /**
             * 模板内容字体颜色，不填默认为黑色
             */
            @JsonProperty("color")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            protected String color;

            public TemplateData(String value, String color) {
                this.value = value;
                this.color = color;
            }

            public TemplateData(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }
        }
    }


}
