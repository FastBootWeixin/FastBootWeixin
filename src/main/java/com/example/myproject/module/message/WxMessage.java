package com.example.myproject.module.message;

import com.example.myproject.module.Wx;
import com.example.myproject.module.message.adapters.WxXmlAdapters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 所有消息都是通过Msg推送的
 * FastBootWeixin  WxMessage
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMessage
 * 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/2 23:21
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
@Getter
public class WxMessage {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());


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
        EVENT(Intent.RECEIVE, Wx.Category.EVENT, Wx.Category.BUTTON),

        /**
         * 文本消息
         */
        TEXT(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 图片消息
         */
        IMAGE(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 语音消息
         */
        VOICE(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 视频消息
         */
        VIDEO(Intent.ALL, Wx.Category.MESSAGE),

        /**
         * 小视频消息
         */
        SHORT_VIDEO(Intent.RECEIVE, Wx.Category.MESSAGE),

        /**
         * 地理位置消息
         */
        LOCATION(Intent.RECEIVE, Wx.Category.MESSAGE),

        /**
         * 链接消息
         */
        LINK(Intent.RECEIVE, Wx.Category.MESSAGE),

        /**
         * 发送音乐消息
         */
        MUSIC(Intent.SEND, Wx.Category.MESSAGE),

        /**
         * 发送图文消息
         */
        NEWS(Intent.SEND, Wx.Category.MESSAGE);

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
    protected String toUserName;

    /**
     * 消息的基础字段
     * 发送方帐号（一个OpenID）
     */
    @XmlElement(name = "FromUserName", required = true)
    protected String fromUserName;

    /**
     * 消息的基础字段
     * 消息创建时间 （整型）
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
    @XmlElement(name = "CreateTime", required = true)
    protected Date createTime;

    /**
     * 消息的基础字段
     * 消息类型，event
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.MsgTypeAdaptor.class)
    @XmlElement(name = "MsgType", required = true)
    protected Type messageType;

    WxMessage(String toUserName, String fromUserName, Date createTime, Type messageType) {
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.createTime = createTime != null ? createTime : new Date();
        this.messageType = messageType;
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
    @NoArgsConstructor
    @Getter
    public static class Text extends WxMessage {

        @XmlElement(name = "Content", required = true)
        protected String content;

        Text(String toUserName, String fromUserName, Date createTime, Type messageType, String content) {
            super(toUserName, fromUserName, createTime, messageType);
            this.content = content;
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.TEXT);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder> {
            private String content;

            Builder() {
            }

            // 父类如何返回？
            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public Text build() {
                return new Text(toUserName, fromUserName, createTime, messageType, content);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Text.Builder(content=" + this.content + ")";
            }
        }
    }

    @XmlRootElement(name = "xml")
    @NoArgsConstructor
    @Getter
    public static class Image extends WxMessage {

        @XmlElementWrapper(name = "Image")
        @XmlElement(name = "MediaId", required = true)
        protected String mediaId;

        Image(String toUserName, String fromUserName, Date createTime, Type messageType, String mediaId) {
            super(toUserName, fromUserName, createTime, messageType);
            this.mediaId = mediaId;
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.IMAGE);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder>{

            protected String mediaId;

            Builder() {
            }

            public Builder mediaId(String mediaId) {
                this.mediaId = mediaId;
                return this;
            }

            public Image build() {
                return new Image(toUserName, fromUserName, createTime, messageType, mediaId);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.mediaId + ")";
            }
        }
    }

    @XmlRootElement(name = "xml")
    @NoArgsConstructor
    @Getter
    public static class Voice extends WxMessage {

        @XmlElementWrapper(name = "Voice")
        @XmlElement(name = "MediaId", required = true)
        protected String mediaId;

        Voice(String toUserName, String fromUserName, Date createTime, Type messageType, String mediaId) {
            super(toUserName, fromUserName, createTime, messageType);
            this.mediaId = mediaId;
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.VOICE);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder>{

            protected String mediaId;

            Builder() {
            }

            public Builder mediaId(String mediaId) {
                this.mediaId = mediaId;
                return this;
            }

            public Voice build() {
                return new Voice(toUserName, fromUserName, createTime, messageType, mediaId);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.mediaId + ")";
            }
        }
    }

    @XmlRootElement(name = "xml")
    @NoArgsConstructor
    @Getter
    public static class Video extends WxMessage {

        @XmlElement(name = "Video")
        protected Body body;

        Video(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        @XmlRootElement(name = "Video")
        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @XmlElement(name = "MediaId", required = true)
            protected String mediaId;

            @XmlElement(name = "Title")
            protected String title;

            @XmlElement(name = "Description")
            protected String description;

        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.VIDEO);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder>{

            protected Body body;

            Builder() {
                body = new Body();
            }

            public Builder body(String mediaId, String title, String description) {
                this.body.mediaId = mediaId;
                this.body.title = title;
                this.body.description = description;
                return this;
            }

            public Builder mediaId(String mediaId) {
                this.body.mediaId = mediaId;
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
    @NoArgsConstructor
    @Getter
    public static class Music extends WxMessage {

        @XmlElement(name = "Video")
        protected Body body;

        Music(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        @XmlRootElement(name = "Video")
        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @XmlElement(name = "ThumbMediaId", required = true)
            protected String thumbMediaId;

            @XmlElement(name = "Title")
            protected String title;

            @XmlElement(name = "Description")
            protected String description;

            @XmlElement(name = "MusicUrl")
            protected String musicUrl;

            @XmlElement(name = "HQMusicUrl")
            protected String hqMusicUrl;

        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.MUSIC);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder>{

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

    @XmlRootElement(name = "xml")
    @NoArgsConstructor
    @Getter
    public static class News extends WxMessage {

        /**
         * 图文消息个数，限制为8条以内
         */
        @XmlElement(name = "ArticleCount", required = true)
        protected Integer articleCount;

        @XmlElementWrapper(name = "Articles", required = true)
        @XmlElements(@XmlElement(name = "item", type = Item.class))
        protected List<Item> articles;

        News(String toUserName, String fromUserName, Date createTime, Type messageType, List<Item> articles) {
            super(toUserName, fromUserName, createTime, messageType);
            this.articleCount = articles.size();
            this.articles = articles;
        }

        /**
         * 突然想省个事，虽然这里确实是用builder更好一点，但是我就是不用
         * 写builder了，但是刚才还有个事情忘记了，不知道是啥了。
         */
        @XmlRootElement(name = "item")
        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Item {

            @XmlElement(name = "Title", required = true)
            protected String title;

            @XmlElement(name = "Description", required = true)
            protected String description;

            @XmlElement(name = "PicUrl", required = true)
            protected String picUrl;

            @XmlElement(name = "Url", required = true)
            protected String url;

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
                    return "com.example.myproject.module.message.WxMessage.News.Item.ItemBuilder(title=" + this.title + ", description=" + this.description + ", picUrl=" + this.picUrl + ", url=" + this.url + ")";
                }
            }
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.NEWS);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder>{

            // 是叫items呢还是articles呢
            protected LinkedList<Item> items;

            Builder() {
                items = new LinkedList<>();
            }

            /**
             * 添加主article，就是最上面那个大图
             * @param title
             * @param description
             * @param picUrl
             * @param url
             * @return
             */
            public Builder mainItem(String title, String description, String picUrl, String url) {
                this.items.addFirst(new Item(title, description, picUrl, url));
                return this;
            }

            public Builder mainItem(Item item) {
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

            public News build() {
                // 这里可能不是一个好的代码习惯，可能会造成items变量名混乱。
                List<Item> items = this.items;
                if (this.items.size() > 8) {
                    logger.warn("图文消息至多只能有八条，最后的图文消息将被忽略");
                    items = this.items.subList(0, 8);
                }
                return new News(toUserName, fromUserName, createTime, messageType, items);
            }

            public String toString() {
                return "com.example.myproject.module.message.WxMessage.Image.Builder(body=" + this.items.toString() + ")";
            }
        }
    }

}
