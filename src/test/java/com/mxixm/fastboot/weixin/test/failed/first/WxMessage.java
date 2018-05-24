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

package com.mxixm.fastboot.weixin.test.failed.first;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.adapter.WxXmlAdapters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * FastBootWeixin WxMessage
 
 * 所有消息都是通过Msg推送的
 * 坑啊，主动发消息竟然是json格式
 * 真是尴尬，不仅格式不同，结构也不同，坑爹。。。
 * 特别是text消息，json的在text结构下，xml在顶级
 *
 * 注解@JsonUnwrapped @XmlElementWrapper这两个对于XML和JSON完全相反的功能，两个都只提供了一个。。。
 * https://stackoverflow.com/questions/16202583/xmlelementwrapper-for-unwrapped-collections
 * https://github.com/FasterXML/jackson-databind/issues/512
 * FastBootWeixin  WxMessage
 * <p>
 * 加入WxMessageTemplate用于发送消息
 * WxMessageConverter用于转换消息（把文件转换为media_id等）
 * 注解@JsonUnwrapped @XmlElementWrapper这两个对于XML和JSON完全相反的功能，两个都只提供了一个。。。
 * https://stackoverflow.com/questions/16202583/xmlelementwrapper-for-unwrapped-collections
 * https://github.com/FasterXML/jackson-databind/issues/512
 * @author Guangshan
 * @date 2017/8/2 23:21
 * @since 0.1.2
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.NONE)
public class WxMessage {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    public WxMessage() {
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
     * 消息类型，event
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.MsgTypeAdaptor.class)
    @XmlElement(name = "MsgType", required = true)
    @JsonProperty("msgtype")
    protected Type messageType;

    protected WxMessageBody body;

    public WxMessage.Type getMessageType() {
        return this.messageType;
    }

    WxMessage(Type messageType) {
        this.messageType = messageType;
    }

    WxMessage(Type messageType, WxMessageBody body) {
        this.messageType = messageType;
        this.body = body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<T extends Builder> {
        protected Type messageType;
        protected WxMessageBody body;

        Builder() {
        }

        protected T msgType(Type msgType) {
            this.messageType = msgType;
            return (T) this;
        }

        protected T body(WxMessageBody body) {
            this.body = body;
            return (T) this;
        }

        public WxMessage build() {
            return new WxMessage(messageType, body);
        }

        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Builder(messageType=" + this.messageType + ")";
        }
    }

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Text extends WxMessage {

        @XmlElement(name = "Content")
        @XmlJavaTypeAdapter(WxXmlAdapters.TextBodyAdaptor.class)
        @JsonProperty("text")
        protected WxMessageBody.Text body;

        Text(Type messageType, WxMessageBody.Text body) {
            super(messageType);
            this.body = body;
        }

        public Text() {
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.TEXT);
            return builder;
        }

        public WxMessageBody.Text getBody() {
            return this.body;
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
                return new Text(messageType, new WxMessageBody.Text(content));
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Text.Builder(content=" + this.content + ")";
            }
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
        protected WxMessageBody.Image body;

        Image(Type messageType, WxMessageBody.Image body) {
            super(messageType);
            this.body = body;
        }

        public Image() {
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.IMAGE);
            return builder;
        }

        public WxMessageBody.Image getBody() {
            return this.body;
        }

        public static class Builder extends MediaBuilder<Builder> {

            Builder() {
            }

            public Image build() {
                return new Image(messageType, new WxMessageBody.Image(mediaId, mediaPath, mediaUrl));
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
        protected WxMessageBody.Voice body;

        Voice(Type messageType, WxMessageBody.Voice body) {
            super(messageType);
            this.body = body;
        }

        public Voice() {
        }

        public WxMessageBody.Voice getBody() {
            return this.body;
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.VOICE);
            return builder;
        }

        public static class Builder extends MediaBuilder<Builder> {

            Builder() {
            }

            public Voice build() {
                return new Voice(messageType, new WxMessageBody.Voice(mediaId, mediaPath, mediaUrl));
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
        protected WxMessageBody.Video body;

        Video(Type messageType, WxMessageBody.Video body) {
            super(messageType);
            this.body = body;
        }

        public Video() {
        }

        public WxMessageBody.Video getBody() {
            return this.body;
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
        public static class Builder extends MediaBuilder<Builder> {

            protected WxMessageBody.Video body;

            Builder() {
                body = new WxMessageBody.Video();
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
                return new Video(messageType, body);
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
        protected WxMessageBody.Music body;

        Music(Type messageType, WxMessageBody.Music body) {
            super(messageType);
            this.body = body;
        }

        public Music() {
        }

        public WxMessageBody.Music getBody() {
            return this.body;
        }


        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.MUSIC);
            return builder;
        }

        public static class Builder extends MediaBuilder<Builder> {

            protected WxMessageBody.Music body;

            Builder() {
                body = new WxMessageBody.Music();
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
                return new Music(messageType, body);
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
        protected WxMessageBody.News body;

        News(Type messageType, WxMessageBody.News body) {
            super(messageType);
            this.articleCount = body.getArticles().size();
            this.body = body;
        }

        public News() {
        }

        public Integer getArticleCount() {
            return this.articleCount;
        }

        public WxMessageBody.News getBody() {
            return this.body;
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.NEWS);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder> {

            // 是叫items呢还是articles呢
            protected LinkedList<WxMessageBody.News.Item> items;

            protected WxMessageBody.News.Item lastItem;

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
             * @return the result
             */
            public Builder firstItem(String title, String description, String picUrl, String url) {
                this.items.addFirst(new WxMessageBody.News.Item(title, description, picUrl, url));
                return this;
            }

            public Builder firstItem(WxMessageBody.News.Item item) {
                this.items.addFirst(item);
                return this;
            }

            public Builder addItem(String title, String description, String picUrl, String url) {
                this.items.addLast(new WxMessageBody.News.Item(title, description, picUrl, url));
                return this;
            }

            public Builder addItem(WxMessageBody.News.Item item) {
                this.items.addLast(item);
                return this;
            }

            public Builder addItems(Collection<WxMessageBody.News.Item> item) {
                this.items.addAll(item);
                return this;
            }

            public Builder lastItem(WxMessageBody.News.Item item) {
                this.lastItem = item;
                return this;
            }

            // 这里关于最后项目的判断应该能优化一下，今天太累了，明天改2017年8月7日00:18:52
            public News build() {
                // 这里可能不是一个好的代码习惯，可能会造成items变量名混乱。
                List<WxMessageBody.News.Item> items = this.items;
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
                return new News(messageType, new WxMessageBody.News(items));
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
        protected WxMessageBody.MpNews body;

        MpNews(Type messageType, WxMessageBody.MpNews body) {
            super(messageType);
            this.body = body;
        }

        public MpNews() {
        }

        public WxMessageBody.MpNews getBody() {
            return this.body;
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
                return new MpNews(messageType, new WxMessageBody.MpNews(mediaId));
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
        protected WxMessageBody.Card body;

        WxCard(Type messageType, WxMessageBody.Card body) {
            super(messageType);
            this.body = body;
        }

        public WxCard() {
        }

        public WxMessageBody.Card getBody() {
            return this.body;
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
                return new WxCard(messageType, new WxMessageBody.Card(cardId));
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

        Status(Type messageType, boolean isTyping) {
            super(messageType);
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
                return new Status(messageType, isTyping);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.isTyping + ")";
            }
        }
    }

}
