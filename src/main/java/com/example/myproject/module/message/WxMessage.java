package com.example.myproject.module.message;

import com.example.myproject.module.Wx;
import com.example.myproject.module.message.adapters.WxXmlAdapters;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

/**
 * 所有消息都是通过Msg推送的
 * 坑啊，主动发消息竟然是json格式
 * 真是尴尬，不仅格式不同，结构也不同，坑爹。。。
 * 特别是text消息，json的在text结构下，xml在顶级
 * @JsonUnwrapped @XmlElementWrapper这两个对于XML和JSON完全相反的功能，两个都只提供了一个。。。
 * https://stackoverflow.com/questions/16202583/xmlelementwrapper-for-unwrapped-collections
 * https://github.com/FasterXML/jackson-databind/issues/512
 * FastBootWeixin  WxMessage
 *
 * 加入WxMessageTemplate用于发送消息
 * WxMessageConverter用于转换消息（把文件转换为media_id等）
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
    @NoArgsConstructor
    @Getter
    public static class Text extends WxMessage {

        @XmlElement(name = "Content")
        @XmlJavaTypeAdapter(Body.TextBodyAdaptor.class)
        @JsonProperty("text")
        protected Body body;

        Text(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.TEXT);
            return builder;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @JsonProperty("content")
            protected String content;

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

    @XmlRootElement(name = "xml")
    @NoArgsConstructor
    @Getter
    public static class Image extends WxMessage {

        @XmlElement(name = "Image", required = true)
        @JsonProperty("image")
        protected Body body;

        Image(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.IMAGE);
            return builder;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @XmlElement(name = "MediaId", required = true)
            @JsonProperty("media_id")
            protected String mediaId;

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
                return new Image(toUserName, fromUserName, createTime, messageType, new Body(mediaId));
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

        @XmlElement(name = "Voice", required = true)
        @JsonProperty("voice")
        protected Body body;

        Voice(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @XmlElement(name = "MediaId", required = true)
            @JsonProperty("media_id")
            protected String mediaId;

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
                return new Voice(toUserName, fromUserName, createTime, messageType, new Body(mediaId));
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
        @JsonProperty("video")
        protected Body body;

        Video(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @XmlElement(name = "MediaId", required = true)
            @JsonProperty("media_id")
            protected String mediaId;

            @XmlElement(name = "ThumbMediaId")
            @JsonProperty("thumb_media_id")
            protected String thumbMediaId;

            @XmlElement(name = "Title")
            @JsonProperty("title")
            protected String title;

            @XmlElement(name = "Description")
            @JsonProperty("description")
            protected String description;

        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.VIDEO);
            return builder;
        }

        /**
         * 上面的body和下面的body风格不一致。。。算了，什么时候强迫症犯了再改好了
         */
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

        @XmlElement(name = "Music")
        @JsonProperty("music")
        protected Body body;

        Music(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

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

    /**
     * 图文消息（点击跳转到外链）
     */
    @XmlRootElement(name = "xml")
    @NoArgsConstructor
    @Getter
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

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        public static class Body {

            @XmlElements(@XmlElement(name = "item", type = Item.class))
            @JsonProperty("articles")
            protected List<Item> articles;

        }

        /**
         * 突然想省个事，虽然这里确实是用builder更好一点，但是我就是不用
         * 写builder了，但是刚才还有个事情忘记了，不知道是啥了。
         */
        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
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
            @JsonProperty("host")
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
                    return "com.example.myproject.module.message.WxMessage.News.Item.ItemBuilder(title=" + this.title + ", description=" + this.description + ", picUrl=" + this.picUrl + ", host=" + this.url + ")";
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

            protected Item lastItem;

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
    @NoArgsConstructor
    @Getter
    public static class MpNews extends WxMessage {

        @XmlElement(name = "Mpnews", required = true)
        @JsonProperty("mpnews")
        protected Body body;

        MpNews(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @XmlElement(name = "MediaId", required = true)
            @JsonProperty("media_id")
            protected String mediaId;

        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.MPNEWS);
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
    @NoArgsConstructor
    @Getter
    public static class WxCard extends WxMessage {

        @XmlElement(name = "WxCard", required = true)
        @JsonProperty("wxcard")
        protected Body body;

        WxCard(String toUserName, String fromUserName, Date createTime, Type messageType, Body body) {
            super(toUserName, fromUserName, createTime, messageType);
            this.body = body;
        }

        @XmlAccessorType(XmlAccessType.NONE)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Body {

            @XmlElement(name = "CardId", required = true)
            @JsonProperty("card_id")
            protected String cardId;

        }

        public static Builder builder() {
            Builder builder = new Builder();
            builder.msgType(Type.WXCARD);
            return builder;
        }

        public static class Builder extends WxMessage.Builder<Builder>{

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

}
