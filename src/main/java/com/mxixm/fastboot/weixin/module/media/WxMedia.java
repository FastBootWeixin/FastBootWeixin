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
import com.mxixm.fastboot.weixin.module.adapter.WxJsonAdapters;
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

    public WxMedia() {
    }

    public WxMedia(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WxMedia)) {
            return false;
        }
        final WxMedia other = (WxMedia) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$mediaId = this.getMediaId();
        final Object other$mediaId = other.getMediaId();
        if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) {
            return false;
        }
        return true;
    }

    @Override
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

    @Override
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
        THUMB,
        @JsonProperty("news")
        NEWS
    }

    @JsonProperty("media_id")
    private String mediaId;

    /**
     * 静态工厂方法
     *
     * @param mediaId
     * @return the result
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
         * @return the result
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

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof TempMediaResult)) {
                return false;
            }
            final TempMediaResult other = (TempMediaResult) o;
            if (!other.canEqual((Object) this)) {
                return false;
            }
            final Object this$type = this.getType();
            final Object other$type = other.getType();
            if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
                return false;
            }
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) {
                return false;
            }
            final Object this$createdAt = this.getCreatedAt();
            final Object other$createdAt = other.getCreatedAt();
            if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) {
                return false;
            }
            return true;
        }

        @Override
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

        @Override
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

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MediaResult)) {
                return false;
            }
            final MediaResult other = (MediaResult) o;
            if (!other.canEqual((Object) this)) {
                return false;
            }
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
                return false;
            }
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) {
                return false;
            }
            return true;
        }

        @Override
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

        @Override
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

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ImageResult)) {
                return false;
            }
            final ImageResult other = (ImageResult) o;
            if (!other.canEqual((Object) this)) {
                return false;
            }
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
                return false;
            }
            return true;
        }

        @Override
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

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.media.WxMedia.ImageResult(url=" + this.getUrl() + ")";
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

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Video)) {
                return false;
            }
            final Video other = (Video) o;
            if (!other.canEqual((Object) this)) {
                return false;
            }
            final Object this$title = this.getTitle();
            final Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
                return false;
            }
            final Object this$introduction = this.getIntroduction();
            final Object other$introduction = other.getIntroduction();
            if (this$introduction == null ? other$introduction != null : !this$introduction.equals(other$introduction)) {
                return false;
            }
            final Object this$downUrl = this.getDownUrl();
            final Object other$downUrl = other.getDownUrl();
            if (this$downUrl == null ? other$downUrl != null : !this$downUrl.equals(other$downUrl)) {
                return false;
            }
            return true;
        }

        @Override
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

        @Override
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

            @Override
            public String toString() {
                return "com.example.myproject.module.media.WxMedia.Video.Builder(title=" + this.title + ", introduction=" + this.introduction + ")";
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

        public int getVoice() {
            return voice;
        }

        public int getVideo() {
            return video;
        }

        public int getImage() {
            return image;
        }

        public int getNews() {
            return news;
        }
    }

    public static class PageParam {

        @JsonProperty("type")
        private Type type;

        @JsonProperty("offset")
        private int offset;

        @JsonProperty("count")
        private int count;

        public static PageParam of(Type type, int offset, int count) {
            PageParam pageParam = new PageParam();
            pageParam.type = type;
            pageParam.offset = offset;
            pageParam.count = Math.min(20, count);
            return pageParam;
        }

        public static PageParam of(Type type, int offset) {
            return of(type, offset, 20);
        }
    }

    public static class PageResult {

        @JsonProperty("total_count")
        private int totalCount;

        @JsonProperty("item_count")
        private int itemCount;

        @JsonProperty("item")
        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getItemCount() {
            return itemCount;
        }

        public static class Item {

            @JsonProperty("media_id")
            private String mediaId;

            @JsonProperty("name")
            private String name;

            @JsonDeserialize(converter = WxJsonAdapters.WxIntDateConverter.class)
            @JsonProperty("update_time")
            private Date updateTime;

            @JsonProperty("url")
            private String url;

            public String getMediaId() {
                return mediaId;
            }

            public String getName() {
                return name;
            }

            public Date getUpdateTime() {
                return updateTime;
            }

            public String getUrl() {
                return url;
            }
        }

    }


}
