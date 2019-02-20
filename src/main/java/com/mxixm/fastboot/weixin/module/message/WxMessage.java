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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.adapter.WxXmlAdapters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

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
public class WxMessage<T extends WxMessageBody> {

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
         * 注意群发消息里，示例中是mpvideo，而文档里是video，到底是啥？
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
        WXCARD(Intent.SEND, Wx.Category.MESSAGE),

        /**
         * 发送小程序消息
         */
        @JsonProperty("miniprogrampage")
        MINI_PROGRAM(Intent.SEND, Wx.Category.MESSAGE),

        /**
         * 发送写入状态
         */
        @JsonProperty("status")
        STATUS(Intent.SEND, Wx.Category.MESSAGE),

        /**
         * 发送模板消息
         */
        @JsonProperty("template")
        TEMPLATE(Intent.SEND, Wx.Category.MESSAGE);

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

    public WxUserMessage toUserMessage() {
        if (this instanceof WxUserMessage) {
            return (WxUserMessage) this;
        } else {
            return WxMessage.builder().msgType(this.messageType).body(this.body).toUser().build();
        }
    }

    public WxGroupMessage toGroupMessage() {
        if (this instanceof WxGroupMessage) {
            return (WxGroupMessage) this;
        } else {
            return WxMessage.builder().msgType(this.messageType).body(this.body).toGroup().build();
        }
    }

    /**
     * 消息的基础字段
     * 消息类型，event
     */
    @XmlJavaTypeAdapter(WxXmlAdapters.MsgTypeAdaptor.class)
    @XmlElement(name = "MsgType", required = true)
    @JsonProperty("msgtype")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Type messageType;

    public Type getMessageType() {
        return messageType;
    }

    protected void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected T body;

    protected void setBody(T wxMessageBody) {
        this.body = wxMessageBody;
    }

    public T getBody() {
        return this.body;
    }

    WxMessage(Type messageType) {
        this.setMessageType(messageType);
    }

    WxMessage(Type messageType, T body) {
        this.setMessageType(messageType);
        this.setBody(body);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<B extends Builder, M extends WxMessageBody> {

        protected Type messageType;
        protected M body;

        Builder() {
        }

        protected B msgType(Type msgType) {
            this.messageType = msgType;
            return (B) this;
        }

        protected B body(M body) {
            this.body = body;
            return (B) this;
        }

        /**
         * 调用默认build返回WxUserMessage
         * @return
         */
        public WxUserMessage build() {
            return toUser().build();
        }

        public WxUserMessage.UserMessageBuilder toUser() {
            return new WxUserMessage.UserMessageBuilder(this);
        }

        public WxGroupMessage.GroupMessageBuilder toGroup() {
            return new WxGroupMessage.GroupMessageBuilder(this);
        }

        public WxUserMessage.UserMessageBuilder toUser(String user) {
            return new WxUserMessage.UserMessageBuilder(this).toUser(user);
        }

        public WxGroupMessage.GroupMessageBuilder toGroup(int tagId) {
            return new WxGroupMessage.GroupMessageBuilder(this).toTag(tagId);
        }

        public WxGroupMessage.GroupMessageBuilder toGroup(Collection<String> userList) {
            return new WxGroupMessage.GroupMessageBuilder(this).toUsers(userList);
        }

        public WxGroupMessage.GroupMessageBuilder toGroup(String... users) {
            return new WxGroupMessage.GroupMessageBuilder(this).toUsers(users);
        }

        public WxGroupMessage.GroupMessageBuilder preview(String user) {
            return new WxGroupMessage.GroupMessageBuilder(this).preview(user);
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Builder(messageType=" + this.messageType + ")";
        }
    }

    public static class TextBuilder extends WxMessage.Builder<TextBuilder, WxMessageBody.Text> {

        TextBuilder() {
            super();
            msgType(Type.TEXT);
            body(new WxMessageBody.Text());
        }

        // 父类如何返回？
        // 使用泛型搞定了
        public TextBuilder content(String content) {
            this.body.content = content;
            return this;
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Text.Builder(body=" + this.body + ")";
        }
    }

    public static class Text {

        public static TextBuilder builder() {
            return textBuilder();
        }

    }

    public static TextBuilder textBuilder() {
        return new TextBuilder();
    }

    public static TextBuilder text() {
        return new TextBuilder();
    }

    public static class MediaBuilder<B extends MediaBuilder, M extends WxMessageBody.Media>
            extends Builder<B, M> {

        protected String mediaId;

        protected String mediaPath;

        protected String mediaUrl;

        protected Resource mediaResource;

        MediaBuilder() {
        }

        public B mediaId(String mediaId) {
            this.body.mediaId = mediaId;
            return (B) this;
        }

        public B mediaPath(String mediaPath) {
            this.body.mediaPath = mediaPath;
            return (B) this;
        }

        public B mediaUrl(String mediaUrl) {
            this.body.mediaUrl = mediaUrl;
            return (B) this;
        }

        public B mediaResource(Resource mediaResource) {
            this.body.mediaResource = mediaResource;
            return (B) this;
        }

    }

    public static class ImageBuilder extends WxMessage.MediaBuilder<ImageBuilder, WxMessageBody.Image> {

        ImageBuilder() {
            super();
            this.msgType(Type.IMAGE);
            this.body(new WxMessageBody.Image());
        }

    }

    public static class Image {

        public static ImageBuilder builder() {
            return imageBuilder();
        }

    }

    public static ImageBuilder imageBuilder() {
        return new ImageBuilder();
    }

    public static ImageBuilder image() {
        return new ImageBuilder();
    }

    public static class VoiceBuilder extends WxMessage.MediaBuilder<VoiceBuilder, WxMessageBody.Voice> {

        VoiceBuilder() {
            super();
            this.msgType(Type.VOICE);
            this.body(new WxMessageBody.Voice());
        }

    }

    public static class Voice {

        public static VoiceBuilder builder() {
            return voiceBuilder();
        }

    }

    public static VoiceBuilder voice() {
        return new VoiceBuilder();
    }

    public static VoiceBuilder voiceBuilder() {
        return new VoiceBuilder();
    }


    /**
     * 上面的body和下面的body风格不一致。。。算了，什么时候强迫症犯了再改好了
     * 加了几个参数之后发现还是下面这种方式好啊。。。
     */
    public static class VideoBuilder extends WxMessage.MediaBuilder<VideoBuilder, WxMessageBody.Video> {

        VideoBuilder() {
            super();
            this.msgType(Type.VIDEO);
            this.body(new WxMessageBody.Video());
        }

        public VideoBuilder body(String mediaId, String thumbMediaId, String title, String description) {
            this.body.mediaId = mediaId;
            this.body.thumbMediaId = thumbMediaId;
            this.body.title = title;
            this.body.description = description;
            return this;
        }

        public VideoBuilder thumbMediaPath(String thumbMediaPath) {
            this.body.thumbMediaPath = thumbMediaPath;
            return this;
        }

        public VideoBuilder thumbMediaUrl(String thumbMediaUrl) {
            this.body.thumbMediaUrl = thumbMediaUrl;
            return this;
        }

        public VideoBuilder thumbMediaResource(Resource thumbMediaResource) {
            this.body.thumbMediaResource = thumbMediaResource;
            return this;
        }

        public VideoBuilder title(String title) {
            this.body.title = title;
            return this;
        }

        public VideoBuilder description(String description) {
            this.body.description = description;
            return this;
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Image.Builder(body=" + this.body.toString() + ")";
        }
    }


    public static class Video {

        public static VideoBuilder builder() {
            return videoBuilder();
        }

    }

    public static VideoBuilder video() {
        return new VideoBuilder();
    }

    public static VideoBuilder videoBuilder() {
        return new VideoBuilder();
    }

    public static class MiniProgramBuilder extends WxMessage.Builder<MiniProgramBuilder, WxMessageBody.MiniProgram> {

        MiniProgramBuilder() {
            super();
            this.msgType(Type.MINI_PROGRAM);
            this.body(new WxMessageBody.MiniProgram());
        }

        public MiniProgramBuilder body(String title, String appId, String pagePath, String thumbMediaId) {
            this.body.title = title;
            this.body.appId = appId;
            this.body.pagePath = pagePath;
            this.body.thumbMediaId = thumbMediaId;
            return this;
        }

        public MiniProgramBuilder thumbMediaPath(String thumbMediaPath) {
            this.body.thumbMediaPath = thumbMediaPath;
            return this;
        }

        public MiniProgramBuilder thumbMediaUrl(String thumbMediaUrl) {
            this.body.thumbMediaUrl = thumbMediaUrl;
            return this;
        }

        public MiniProgramBuilder title(String title) {
            this.body.title = title;
            return this;
        }

        public MiniProgramBuilder appId(String appId) {
            this.body.appId = appId;
            return this;
        }

        public MiniProgramBuilder pagePath(String pagePath) {
            this.body.pagePath = pagePath;
            return this;
        }

        public MiniProgramBuilder thumbMediaId(String thumbMediaId) {
            this.body.thumbMediaId = thumbMediaId;
            return this;
        }

        @Override
        public String toString() {
            return "MiniProgramBuilder{" +
                    "messageType=" + messageType +
                    ", body=" + body +
                    '}';
        }
    }


    public static class MiniProgram {

        public static MiniProgramBuilder builder() {
            return miniProgramBuilder();
        }

    }

    public static MiniProgramBuilder miniProgram() {
        return new MiniProgramBuilder();
    }

    public static MiniProgramBuilder miniProgramBuilder() {
        return new MiniProgramBuilder();
    }

    public static class MusicBuilder extends WxMessage.MediaBuilder<MusicBuilder, WxMessageBody.Music> {

        MusicBuilder() {
            super();
            this.msgType(Type.MUSIC);
            this.body(new WxMessageBody.Music());
        }

        public MusicBuilder body(String thumbMediaId, String title, String description, String musicUrl, String hqMusicUrl) {
            this.body.mediaId = thumbMediaId;
            this.body.title = title;
            this.body.description = description;
            this.body.musicUrl = musicUrl;
            this.body.hqMusicUrl = hqMusicUrl;
            return this;
        }

        public MusicBuilder thumbMediaId(String thumbMediaId) {
            this.body.mediaId = thumbMediaId;
            return this;
        }

        public MusicBuilder thumbMediaPath(String thumbMediaPath) {
            this.body.mediaPath = thumbMediaPath;
            return this;
        }

        public MusicBuilder thumbMediaUrl(String thumbMediaUrl) {
            this.body.mediaUrl = thumbMediaUrl;
            return this;
        }

        public MusicBuilder thumbMediaResource(Resource thumbMediaResource) {
            this.body.mediaResource = thumbMediaResource;
            return this;
        }

        public MusicBuilder title(String title) {
            this.body.title = title;
            return this;
        }

        public MusicBuilder description(String description) {
            this.body.description = description;
            return this;
        }

        public MusicBuilder musicUrl(String musicUrl) {
            this.body.musicUrl = musicUrl;
            return this;
        }

        public MusicBuilder hqMusicUrl(String hqMusicUrl) {
            this.body.hqMusicUrl = hqMusicUrl;
            return this;
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Image.Builder(body=" + this.body.toString() + ")";
        }
    }

    public static MusicBuilder music() {
        return new MusicBuilder();
    }

    public static MusicBuilder musicBuilder() {
        return new MusicBuilder();
    }

    public static class Music {

        public static MusicBuilder builder() {
            return musicBuilder();
        }

    }


    public static class NewsBuilder extends WxMessage.Builder<NewsBuilder, WxMessageBody.News> {

        // 是叫items呢还是articles呢
        protected LinkedList<WxMessageBody.News.Item> items;

        protected WxMessageBody.News.Item lastItem;

        NewsBuilder() {
            super();
            this.msgType(Type.NEWS);
            this.body(new WxMessageBody.News());
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
        public NewsBuilder firstItem(String title, String description, String picUrl, String url) {
            this.items.addFirst(new WxMessageBody.News.Item(title, description, picUrl, url));
            return prepare();
        }

        public NewsBuilder firstItem(WxMessageBody.News.Item item) {
            this.items.addFirst(item);
            return prepare();
        }

        public NewsBuilder addItem(String title, String description, String picUrl, String url) {
            this.items.addLast(new WxMessageBody.News.Item(title, description, picUrl, url));
            return prepare();
        }

        public NewsBuilder addItem(WxMessageBody.News.Item item) {
            this.items.addLast(item);
            return prepare();
        }

        public NewsBuilder addItems(Collection<WxMessageBody.News.Item> item) {
            this.items.addAll(item);
            return prepare();
        }

        public NewsBuilder lastItem(WxMessageBody.News.Item item) {
            this.lastItem = item;
            return prepare();
        }

        // 这里关于最后项目的判断应该能优化一下，今天太累了，明天改2017年8月7日00:18:52
        // 由于消息重构，这里变了，可能性能有问题，但是实在没办法。2017年9月28日10:47:24
        public NewsBuilder prepare() {
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
            this.body.articles = items;
            return this;
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Image.Builder(body=" + this.items.toString() + ")";
        }
    }


    public static NewsBuilder news() {
        return new NewsBuilder();
    }

    public static NewsBuilder newsBuilder() {
        return new NewsBuilder();
    }

    /**
     * 图文消息（点击跳转到外链）
     */
    public static class News {
        public static NewsBuilder builder() {
            return newsBuilder();
        }
    }

    public static WxTemplateMessage.TemplateMessageBuilder templateBuilder() {
        return new WxTemplateMessage.TemplateMessageBuilder();
    }

    public static WxTemplateMessage.TemplateMessageBuilder template() {
        return new WxTemplateMessage.TemplateMessageBuilder();
    }

    /**
     * 模板消息
     */
    public static class Template {
        public static WxTemplateMessage.TemplateMessageBuilder builder() {
            return templateBuilder();
        }
    }


    public static class MpNewsBuilder extends WxMessage.Builder<MpNewsBuilder, WxMessageBody.MpNews> {

        MpNewsBuilder() {
            super();
            this.msgType(Type.MPNEWS);
            this.body(new WxMessageBody.MpNews());
        }

        public MpNewsBuilder mediaId(String mediaId) {
            this.body.mediaId = mediaId;
            return this;
        }

        public MpNewsBuilder sendIgnoreReprint(boolean sendIgnoreReprint) {
            this.body.sendIgnoreReprint = sendIgnoreReprint;
            return this;
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.body + ")";
        }
    }

    public static MpNewsBuilder mpNewsBuilder() {
        return new MpNewsBuilder();
    }

    public static MpNewsBuilder mpNews() {
        return new MpNewsBuilder();
    }

    /**
     * 发送图文消息（点击跳转到图文消息页面）
     */
    public static class MpNews {
        public static MpNewsBuilder builder() {
            return mpNewsBuilder();
        }
    }

    public static class WxCardBuilder extends WxMessage.Builder<WxCardBuilder, WxMessageBody.WxCard> {

        WxCardBuilder() {
            super();
            this.msgType(Type.WXCARD);
            this.body(new WxMessageBody.WxCard());
        }

        public WxCardBuilder cardId(String cardId) {
            this.body.cardId = cardId;
            return this;
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.body + ")";
        }
    }

    public static WxCardBuilder wxCard() {
        return new WxCardBuilder();
    }

    public static WxCardBuilder wxCardBuilder() {
        return new WxCardBuilder();
    }

    /**
     * 发送卡券
     */
    public static class WxCard {

        public static WxCardBuilder builder() {
            return wxCardBuilder();
        }
    }

    public static class StatusBuilder extends WxMessage.Builder<StatusBuilder, WxMessageBody.Status> {

        StatusBuilder() {
            super();
            this.msgType(Type.STATUS);
            this.body(new WxMessageBody.Status());
        }

        public Builder isTyping(boolean isTyping) {
            this.body.isTyping = isTyping;
            return this;
        }

        @Override
        public String toString() {
            return "com.example.myproject.module.message.WxMessage.Image.Builder(mediaId=" + this.body + ")";
        }
    }

    public static StatusBuilder statusBuilder() {
        return new StatusBuilder();
    }

    public static StatusBuilder status() {
        return new StatusBuilder();
    }

    public static class Status {

        public static StatusBuilder builder() {
            return statusBuilder();
        }

    }

}
