/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.module.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.message.adapters.WxXmlAdapters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.NONE)
public class WxMessage {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    public WxMessage() {
    }

    public String getToUserName() {
        return this.toUserName;
    }

    public String getFromUserName() {
        return this.fromUserName;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public Type getMessageType() {
        return this.messageType;
    }


    /**
     * 标记是发送的消息还是接收的消息，没有想到一个合适的单词，就先用Intent吧
     */
    public enum Intent {
        SEND, RECEIVE, ALL
    }

    public enum Type {

        /**
         * 收到Button事件和普通事件
         */
        @JsonProperty("event")
        EVENT(Intent.RECEIVE, Wx.Category.EVENT, Wx.Category.BUTTON),

        /**
         * 文本消息
         */
        @JsonProperty("text")
        TEXT(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 图片消息
         */
        @JsonProperty("image")
        IMAGE(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 语音消息
         */
        @JsonProperty("voice")
        VOICE(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 视频消息
         */
        @JsonProperty("video")
        VIDEO(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 小视频消息
         */
        @JsonProperty("short_video")
        SHORT_VIDEO(Intent.RECEIVE, Wx.Category.MESSAGE),

        /**
         * 地理位置消息
         */
        @JsonProperty("location")
        LOCATION(Intent.RECEIVE, Wx.Category.MESSAGE),

        /**
         * 链接消息
         */
        @JsonProperty("link")
        LINK(Intent.RECEIVE, Wx.Category.MESSAGE),

        /**
         * 发送音乐消息
         */
        @JsonProperty("music")
        MUSIC(Intent.SEND, Wx.Category.MESSAGE),

        /**
         * 发送图文消息
         */
        @JsonProperty("news")
        NEWS(Intent.SEND, Wx.Category.MESSAGE),

        /**
         * 发送图文消息（点击跳转到图文消息页面）
         */
        @JsonProperty("mpnews")
        MPNEWS(Intent.SEND, Wx.Category.MESSAGE),

        /**
         * 发送卡券
         */
        @JsonProperty("wxcard")
        WXCARD(Intent.SEND, Wx.Category.MESSAGE);

        private Intent intent;

        private Wx.Category[] categories;

        Type(Intent intent, Wx.Category... categories) {
            this.intent = intent;
            this.categories = categories;
        }

        public Intent getIntent() {
            return intent;
        }

        public Wx.Category[] getCategories() {
            return categories;
        }
    }

    /**
     * 消息的基础字段
     * 开发者微信号
     */
    @XmlElement(name = "ToUserName", required = true)
    @JsonProperty("touser")
    protected String toUserName;

    /**
     * 消息的基础字段
     * 发送方帐号（一个OpenID）
     */
    @XmlElement(name = "FromUserName", required = true)
    @JsonIgnore
    protected String fromUserName;

    /**
     * 消息的基础字段
     * 消息创建时间 （整型）
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
    @XmlElement(name = "CreateTime", required = true)
    @JsonIgnore
    protected Date createTime;

    /**
     * 消息的基础字段
     * 消息类型，event
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.MsgTypeAdaptor.class)
    @XmlElement(name = "MsgType", required = true)
    @JsonProperty("msgtype")
    protected Type messageType;

    WxMessage(String toUserName, String fromUserName, Date createTime, Type messageType) {
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.createTime = createTime != null ? createTime : new Date();
        this.messageType = messageType;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<T extends Builder> {
        protected String toUserName;
        protected String fromUserName;
        protected Date createTime;
        protected Type messageType;

        Builder() {
        }

        public T toUserName(String toUserName) {
            this.toUserName = toUserName;
            return (T) this;
        }

        public T fromUserName(String fromUserName) {
            this.fromUserName = fromUserName;
            return (T) this;
        }

        public T createTime(Date createTime) {
            this.createTime = createTime;
            return (T) this;
        }

        protected T msgType(Type msgType) {
            this.messageType = msgType;
            return (T) this;
        }

        public WxMessage build() {
            return new WxMessage(toUserName, fromUserName, createTime, messageType);
        }

        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Builder(toUserName=" + this.toUserName + ", fromUserName=" + this.fromUserName + ", createTime=" + this.createTime + ", messageType=" + this.messageType + ")";
        }
    }

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Text extends WxMessage {

        @XmlElement(name = "Content")
        @XmlJavaTypeAdapter(Body.TextBodyAdaptor.class)
        @JsonProperty("text")
        protected Body body;

        Text(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public Text() {
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.TEXT);
            return builder;
        }

        public Body getBody() {
            return this.body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body {

            @JsonProperty("content")
            protected String content;

            public Body(String content) {
                this.content = content;
            }

            public Body() {
            }

            public static class TextBodyAdaptor extends XmlAdapter<String, Body> {

                @Override
                public Body unmarshal(String v) throws Exception {
                    return new Body(v);
                }

                @Override
                public String marshal(Body v) throws Exception {
                    return v.content;
                }
            }

        }

        public static class Builder extends WxMessage.Builder<Builder> {

            private String content;

            Builder() {
            }

            // 父类如何返回？
            // 使用泛型搞定了
            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public Text build() {
                return new Text(toUserName, fromUserName, createTime, messageType, new Body(content));
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Text.Builder(content=" + this.content + ")";
            }
        }
    }

    public static class MediaBody {
        @XmlElement(name = "MediaId", required = true)
        @JsonProperty("media_id")
        protected String mediaId;

        @JsonIgnore
        protected String mediaPath;

        @JsonIgnore
        protected String mediaUrl;

        public MediaBody(String mediaId, String mediaPath, String mediaUrl) {
            this.mediaId = mediaId;
            this.mediaPath = mediaPath;
            this.mediaUrl = mediaUrl;
        }

        public MediaBody() {
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
            if (!(o instanceof MediaBody)) return false;
            final MediaBody other = (MediaBody) o;
            if (!other.canEqual((Object) this)) return false;
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
            return other instanceof MediaBody;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.message.WxMessage.MediaBody(mediaId=" + this.getMediaId() + ", mediaPath=" + this.getMediaPath() + ", mediaUrl=" + this.getMediaUrl() + ")";
        }
    }

    public static class MediaBuilder<T extends MediaBuilder> extends Builder<MediaBuilder> {

        protected String mediaId;

        protected String mediaPath;

        protected String mediaUrl;

        MediaBuilder() {
        }

        public T mediaId(String mediaId) {
            this.mediaId = mediaId;
            return (T) this;
        }

        public T mediaPath(String mediaPath) {
            this.mediaPath = mediaPath;
            return (T) this;
        }

        public T mediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            return (T) this;
        }
    }

    @XmlRootElement(name = "xml")
    public static class Image extends WxMessage {

        @XmlElement(name = "Image", required = true)
        @JsonProperty("image")
        protected Body body;

        Image(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public Image() {
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.IMAGE);
            return builder;
        }

        public Body getBody() {
            return this.body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body extends MediaBody {

            public Body(String mediaId, String mediaPath, String mediaUrl) {
                super(mediaId, mediaPath, mediaUrl);
            }

            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof Body)) return false;
                final Body other = (Body) o;
                if (!other.canEqual((Object) this)) return false;
                return true;
            }

            public int hashCode() {
                int result = 1;
                return result;
            }

            protected boolean canEqual(Object other) {
                return other instanceof Body;
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.message.WxMessage.Image.Body()";
            }
        }

        public static class Builder extends WxMessage.MediaBuilder<Builder> {

            Builder() {

            }

            public Image build() {
                return new Image(toUserName, fromUserName, createTime, messageType, new Body(mediaId, mediaPath, mediaUrl));
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.mediaId + ")";
            }
        }
    }

    @XmlRootElement(name = "xml")
    public static class Voice extends WxMessage {

        @XmlElement(name = "Voice", required = true)
        @JsonProperty("voice")
        protected Body body;

        Voice(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public Voice() {
        }

        public Body getBody() {
            return this.body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body extends MediaBody {
            public Body(String mediaId, String mediaPath, String mediaUrl) {
                super(mediaId, mediaPath, mediaUrl);
            }

            public Body() {
            }

            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof Body)) return false;
                final Body other = (Body) o;
                if (!other.canEqual((Object) this)) return false;
                return true;
            }

            public int hashCode() {
                int result = 1;
                return result;
            }

            protected boolean canEqual(Object other) {
                return other instanceof Body;
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.message.WxMessage.Voice.Body()";
            }
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.VOICE);
            return builder;
        }

        public static class Builder extends WxMessage.MediaBuilder<Builder> {

            Builder() {
            }

            public Voice build() {
                return new Voice(toUserName, fromUserName, createTime, messageType, new Body(mediaId, mediaPath, mediaUrl));
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.mediaId + ")";
            }
        }
    }

    @XmlRootElement(name = "xml")
    public static class Video extends WxMessage {

        @XmlElement(name = "Video")
        @JsonProperty("video")
        protected Body body;

        Video(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public Video() {
        }

        public Body getBody() {
            return this.body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body extends MediaBody {

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

            public Body(String thumbMediaId, String title, String description, String thumbMediaPath, String thumbMediaUrl) {
                this.thumbMediaId = thumbMediaId;
                this.title = title;
                this.description = description;
                this.thumbMediaPath = thumbMediaPath;
                this.thumbMediaUrl = thumbMediaUrl;
            }

            public Body() {
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
                if (!(o instanceof Body)) return false;
                final Body other = (Body) o;
                if (!other.canEqual((Object) this)) return false;
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
                return other instanceof Body;
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.message.WxMessage.Video.Body(thumbMediaId=" + this.getThumbMediaId() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", thumbMediaPath=" + this.getThumbMediaPath() + ", thumbMediaUrl=" + this.getThumbMediaUrl() + ")";
            }
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.VIDEO);
            return builder;
        }

        /**
         * 上面的body和下面的body风格不一致。。。算了，什么时候强迫症犯了再改好了
         * 加了几个参数之后发现还是下面这种方式好啊。。。
         */
        public static class Builder extends WxMessage.MediaBuilder<Builder> {

            protected Body body;

            Builder() {
                body = new Body();
            }

            public Builder body(String mediaId, String thumbMediaId, String title, String description) {
                this.body.mediaId = mediaId;
                this.body.thumbMediaId = thumbMediaId;
                this.body.title = title;
                this.body.description = description;
                return this;
            }

            public Builder thumbMediaPath(String thumbMediaPath) {
                this.body.thumbMediaPath = thumbMediaPath;
                return this;
            }

            public Builder thumbMediaUrl(String thumbMediaUrl) {
                this.body.thumbMediaUrl = thumbMediaUrl;
                return this;
            }

            public Builder title(String title) {
                this.body.title = title;
                return this;
            }

            public Builder description(String description) {
                this.body.description = description;
                return this;
            }

            public Video build() {
                return new Video(toUserName, fromUserName, createTime, messageType, body);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(body=" + this.body.toString() + ")";
            }
        }
    }

    @XmlRootElement(name = "xml")
    public static class Music extends WxMessage {

        @XmlElement(name = "Music")
        @JsonProperty("music")
        protected Body body;

        Music(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public Music() {
        }

        public Body getBody() {
            return this.body;
        }

        /**
         * 其实可以再抽象一个thumbMediaBody的。。。我懒
         */
        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body extends MediaBody {

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

            public Body(String thumbMediaId, String title, String description, String musicUrl, String hqMusicUrl) {
                this.thumbMediaId = thumbMediaId;
                this.title = title;
                this.description = description;
                this.musicUrl = musicUrl;
                this.hqMusicUrl = hqMusicUrl;
            }

            public Body() {
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
                if (!(o instanceof Body)) return false;
                final Body other = (Body) o;
                if (!other.canEqual((Object) this)) return false;
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
                return other instanceof Body;
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.message.WxMessage.Music.Body(thumbMediaId=" + this.getThumbMediaId() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", musicUrl=" + this.getMusicUrl() + ", hqMusicUrl=" + this.getHqMusicUrl() + ")";
            }
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.MUSIC);
            return builder;
        }

        public static class Builder extends WxMessage.MediaBuilder<Builder> {

            protected Body body;

            Builder() {
                body = new Body();
            }

            public Builder body(String thumbMediaId, String title, String description, String musicUrl, String hqMusicUrl) {
                this.body.thumbMediaId = thumbMediaId;
                this.body.title = title;
                this.body.description = description;
                this.body.musicUrl = musicUrl;
                this.body.hqMusicUrl = hqMusicUrl;
                return this;
            }

            public Builder thumbMediaId(String thumbMediaId) {
                this.body.thumbMediaId = thumbMediaId;
                return this;
            }

            public Builder thumbMediaPath(String thumbMediaPath) {
                this.body.mediaPath = thumbMediaPath;
                return this;
            }

            public Builder thumbMediaUrl(String thumbMediaUrl) {
                this.body.mediaUrl = thumbMediaUrl;
                return this;
            }

            public Builder title(String title) {
                this.body.title = title;
                return this;
            }

            public Builder description(String description) {
                this.body.description = description;
                return this;
            }

            public Builder musicUrl(String musicUrl) {
                this.body.musicUrl = musicUrl;
                return this;
            }

            public Builder hqMusicUrl(String hqMusicUrl) {
                this.body.hqMusicUrl = hqMusicUrl;
                return this;
            }

            public Music build() {
                return new Music(toUserName, fromUserName, createTime, messageType, body);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(body=" + this.body.toString() + ")";
            }
        }
    }

    /**
     * 图文消息（点击跳转到外链）
     */
    @XmlRootElement(name = "xml")
    public static class News extends WxMessage {

        /**
         * 图文消息个数，限制为8条以内
         */
        @XmlElement(name = "ArticleCount", required = true)
        protected Integer articleCount;

        @XmlElement(name = "Articles", required = true)
        @JsonProperty("news")
        protected Body body;

        News(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.articleCount = body.getArticles().size();
            this.body = body;
        }

        public News() {
        }

        public Integer getArticleCount() {
            return this.articleCount;
        }

        public Body getBody() {
            return this.body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body {

            @XmlElements(@XmlElement(name = "item", type = Item.class))
            @JsonProperty("articles")
            protected List<Item> articles;

            public Body(List<Item> articles) {
                this.articles = articles;
            }

            public Body() {
            }

            public List<Item> getArticles() {
                return this.articles;
            }
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

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.NEWS);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder> {

            // 是叫items呢还是articles呢
            protected LinkedList<Item> items;

            protected Item lastItem;

            Builder() {
                items = new LinkedList<>();
            }

            /**
             * 添加主article，就是最上面那个大图
             *
             * @param title
             * @param description
             * @param picUrl
             * @param url
             * @return dummy
             */
            public Builder firstItem(String title, String description, String picUrl, String url) {
                this.items.addFirst(new Item(title, description, picUrl, url));
                return this;
            }

            public Builder firstItem(Item item) {
                this.items.addFirst(item);
                return this;
            }

            public Builder addItem(String title, String description, String picUrl, String url) {
                this.items.addLast(new Item(title, description, picUrl, url));
                return this;
            }

            public Builder addItem(Item item) {
                this.items.addLast(item);
                return this;
            }

            public Builder addItems(Collection<Item> item) {
                this.items.addAll(item);
                return this;
            }

            public Builder lastItem(Item item) {
                this.lastItem = item;
                return this;
            }

            // 这里关于最后项目的判断应该能优化一下，今天太累了，明天改2017年8月7日00:18:52
            public News build() {
                // 这里可能不是一个好的代码习惯，可能会造成items变量名混乱。
                List<Item> items = this.items;
                if (this.items.size() > 7) {
                    if (this.lastItem != null) {
                        logger.warn("图文消息至多只能有八条，最后的图文消息将被忽略");
                        items = this.items.subList(0, 7);
                        items.add(this.lastItem);
                    } else if (this.items.size() > 8) {
                        logger.warn("图文消息至多只能有八条，最后的图文消息将被忽略");
                        items = this.items.subList(0, 8);
                    }
                } else if (this.lastItem != null) {
                    items.add(this.lastItem);
                }
                return new News(toUserName, fromUserName, createTime, messageType, new Body(items));
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(body=" + this.items.toString() + ")";
            }
        }
    }

    /**
     * 发送图文消息（点击跳转到图文消息页面）
     */
    @XmlRootElement(name = "xml")
    public static class MpNews extends WxMessage {

        @XmlElement(name = "Mpnews", required = true)
        @JsonProperty("mpnews")
        protected Body body;

        MpNews(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public MpNews() {
        }

        public Body getBody() {
            return this.body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body {

            @XmlElement(name = "MediaId", required = true)
            @JsonProperty("media_id")
            protected String mediaId;

            public Body(String mediaId) {
                this.mediaId = mediaId;
            }

            public Body() {
            }
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.MPNEWS);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder> {

            protected String mediaId;

            Builder() {
            }

            public Builder mediaId(String mediaId) {
                this.mediaId = mediaId;
                return this;
            }

            public MpNews build() {
                return new MpNews(toUserName, fromUserName, createTime, messageType, new Body(mediaId));
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.mediaId + ")";
            }
        }
    }

    /**
     * 发送卡券
     */
    @XmlRootElement(name = "xml")
    public static class WxCard extends WxMessage {

        @XmlElement(name = "WxCard", required = true)
        @JsonProperty("wxcard")
        protected Body body;

        WxCard(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public WxCard() {
        }

        public Body getBody() {
            return this.body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        public static class Body {

            @XmlElement(name = "CardId", required = true)
            @JsonProperty("card_id")
            protected String cardId;

            public Body(String cardId) {
                this.cardId = cardId;
            }

            public Body() {
            }
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.WXCARD);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder> {

            protected String cardId;

            Builder() {
            }

            public Builder cardId(String cardId) {
                this.cardId = cardId;
                return this;
            }

            public WxCard build() {
                return new WxCard(toUserName, fromUserName, createTime, messageType, new Body(cardId));
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.cardId + ")";
            }
        }
    }

    public static class Status extends WxMessage {
        @JsonIgnore
        protected Type messageType;

        @JsonProperty("command")
        protected Command command;

        Status(String toUserName, String fromUserName, Date createTime, Type messageType, boolean isTyping) {
            super(toUserName, fromUserName, createTime, messageType);
            this.command = isTyping ? Command.TYPING : Command.CANCEL_TYPING;
        }

        public Status(Type messageType, Command command) {
            this.messageType = messageType;
            this.command = command;
        }

        private enum Command {

            @JsonProperty("Typing")
            TYPING,
            @JsonProperty("CancelTyping")
            CANCEL_TYPING

        }

        public static Builder builder() {
            Builder builder = new Builder();
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder> {

            protected boolean isTyping;

            Builder() {
            }

            public Builder isTyping(boolean isTyping) {
                this.isTyping = isTyping;
                return this;
            }

            public Status build() {
                return new Status(toUserName, fromUserName, createTime, messageType, isTyping);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.isTyping + ")";
            }
        }

    }

}
