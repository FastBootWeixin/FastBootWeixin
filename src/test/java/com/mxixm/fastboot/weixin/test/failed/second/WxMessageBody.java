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

package com.mxixm.fastboot.weixin.test.failed.second;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.util.List;

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
        @JsonProperty("media_id")
        protected String mediaId;

        @JsonIgnore
        protected String mediaPath;

        @JsonIgnore
        protected String mediaUrl;

        public Media(String mediaId, String mediaPath, String mediaUrl) {
            this.mediaId = mediaId;
            this.mediaPath = mediaPath;
            this.mediaUrl = mediaUrl;
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

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Media)) return false;
            final Media other = (Media) o;
            if (!other.canEqual(this)) return false;
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) return false;
            final Object this$mediaPath = this.getMediaPath();
            final Object other$mediaPath = other.getMediaPath();
            if (this$mediaPath == null ? other$mediaPath != null : !this$mediaPath.equals(other$mediaPath))
                return false;
            final Object this$mediaUrl = this.getMediaUrl();
            final Object other$mediaUrl = other.getMediaUrl();
            if (this$mediaUrl == null ? other$mediaUrl != null : !this$mediaUrl.equals(other$mediaUrl)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $mediaId = this.getMediaId();
            result = result * PRIME + ($mediaId == null ? 43 : $mediaId.hashCode());
            final Object $mediaPath = this.getMediaPath();
            result = result * PRIME + ($mediaPath == null ? 43 : $mediaPath.hashCode());
            final Object $mediaUrl = this.getMediaUrl();
            result = result * PRIME + ($mediaUrl == null ? 43 : $mediaUrl.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Media;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.message.WxMessage.Media(mediaId=" + this.getMediaId() + ", mediaPath=" + this.getMediaPath() + ", mediaUrl=" + this.getMediaUrl() + ")";
        }
    }

    @XmlType(name = "ImageBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Image extends Media {

        public Image() {}

        public Image(String mediaId, String mediaPath, String mediaUrl) {
            super(mediaId, mediaPath, mediaUrl);
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Image)) return false;
            final Image other = (Image) o;
            if (!other.canEqual(this)) return false;
            return true;
        }

        public int hashCode() {
            int result = 1;
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Image;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.message.WxMessage.Image.WxMessageBody()";
        }
    }

    @XmlType(name = "VoiceBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Voice extends Media {
        public Voice(String mediaId, String mediaPath, String mediaUrl) {
            super(mediaId, mediaPath, mediaUrl);
        }

        public Voice() {
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Voice)) return false;
            final Voice other = (Voice) o;
            if (!other.canEqual(this)) return false;
            return true;
        }

        public int hashCode() {
            int result = 1;
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Voice;
        }

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

        public Video(String thumbMediaId, String title, String description, String thumbMediaPath, String thumbMediaUrl) {
            this.thumbMediaId = thumbMediaId;
            this.title = title;
            this.description = description;
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

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Video)) return false;
            final Video other = (Video) o;
            if (!other.canEqual(this)) return false;
            final Object this$thumbMediaId = this.getThumbMediaId();
            final Object other$thumbMediaId = other.getThumbMediaId();
            if (this$thumbMediaId == null ? other$thumbMediaId != null : !this$thumbMediaId.equals(other$thumbMediaId))
                return false;
            final Object this$title = this.getTitle();
            final Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
            final Object this$description = this.getDescription();
            final Object other$description = other.getDescription();
            if (this$description == null ? other$description != null : !this$description.equals(other$description))
                return false;
            final Object this$thumbMediaPath = this.getThumbMediaPath();
            final Object other$thumbMediaPath = other.getThumbMediaPath();
            if (this$thumbMediaPath == null ? other$thumbMediaPath != null : !this$thumbMediaPath.equals(other$thumbMediaPath))
                return false;
            final Object this$thumbMediaUrl = this.getThumbMediaUrl();
            final Object other$thumbMediaUrl = other.getThumbMediaUrl();
            if (this$thumbMediaUrl == null ? other$thumbMediaUrl != null : !this$thumbMediaUrl.equals(other$thumbMediaUrl))
                return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $thumbMediaId = this.getThumbMediaId();
            result = result * PRIME + ($thumbMediaId == null ? 43 : $thumbMediaId.hashCode());
            final Object $title = this.getTitle();
            result = result * PRIME + ($title == null ? 43 : $title.hashCode());
            final Object $description = this.getDescription();
            result = result * PRIME + ($description == null ? 43 : $description.hashCode());
            final Object $thumbMediaPath = this.getThumbMediaPath();
            result = result * PRIME + ($thumbMediaPath == null ? 43 : $thumbMediaPath.hashCode());
            final Object $thumbMediaUrl = this.getThumbMediaUrl();
            result = result * PRIME + ($thumbMediaUrl == null ? 43 : $thumbMediaUrl.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Video;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.message.WxMessage.Video.WxMessageBody(thumbMediaId=" + this.getThumbMediaId() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", thumbMediaPath=" + this.getThumbMediaPath() + ", thumbMediaUrl=" + this.getThumbMediaUrl() + ")";
        }
    }

    /**
     * 其实可以再抽象一个thumbMediaBody的。。。我懒
     */
    @XmlType(name = "MusicBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Music extends Media {

        @XmlElement(name = "ThumbMediaId", required = true)
        @JsonProperty("thumb_media_id")
        protected String thumbMediaId;

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
            this.thumbMediaId = thumbMediaId;
            this.title = title;
            this.description = description;
            this.musicUrl = musicUrl;
            this.hqMusicUrl = hqMusicUrl;
        }

        public Music() {
        }

        /**
         * 懒省事，做个简单的替换
         *
         * @param thumbMediaId
         */
        public void setMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
        }

        public String getMediaId() {
            return this.thumbMediaId;
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

        public String getMusicUrl() {
            return this.musicUrl;
        }

        public String getHqMusicUrl() {
            return this.hqMusicUrl;
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

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public void setHqMusicUrl(String hqMusicUrl) {
            this.hqMusicUrl = hqMusicUrl;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Music)) return false;
            final Music other = (Music) o;
            if (!other.canEqual(this)) return false;
            final Object this$thumbMediaId = this.getThumbMediaId();
            final Object other$thumbMediaId = other.getThumbMediaId();
            if (this$thumbMediaId == null ? other$thumbMediaId != null : !this$thumbMediaId.equals(other$thumbMediaId))
                return false;
            final Object this$title = this.getTitle();
            final Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
            final Object this$description = this.getDescription();
            final Object other$description = other.getDescription();
            if (this$description == null ? other$description != null : !this$description.equals(other$description))
                return false;
            final Object this$musicUrl = this.getMusicUrl();
            final Object other$musicUrl = other.getMusicUrl();
            if (this$musicUrl == null ? other$musicUrl != null : !this$musicUrl.equals(other$musicUrl))
                return false;
            final Object this$hqMusicUrl = this.getHqMusicUrl();
            final Object other$hqMusicUrl = other.getHqMusicUrl();
            if (this$hqMusicUrl == null ? other$hqMusicUrl != null : !this$hqMusicUrl.equals(other$hqMusicUrl))
                return false;
            return true;
        }

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

        public MpNews(String mediaId) {
            this.mediaId = mediaId;
        }

        public MpNews() {
        }
    }

    @XmlType(name = "CardBody")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Card extends WxMessageBody {

        /**
         * 群发时只能发送已审核通过的卡券
         */
        @XmlElement(name = "CardId", required = true)
        @JsonProperty("card_id")
        protected String cardId;

        public Card(String cardId) {
            this.cardId = cardId;
        }

        public Card() {
        }
    }

}
