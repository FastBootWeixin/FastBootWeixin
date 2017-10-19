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

package com.mxixm.fastboot.weixin.module.web;

import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.adapters.WxXmlAdapters;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.web.session.WxSession;
import com.mxixm.fastboot.weixin.module.web.session.WxSessionManager;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * FastBootWeixin WxRequest
 *
 * @author Guangshan
 * @date 2017/9/2 22:44
 * @since 0.1.2
 */
public class WxRequest {

    private static Jaxb2RootElementHttpMessageConverter xmlConverter = new Jaxb2RootElementHttpMessageConverter();

    private HttpServletRequest request;

    private Body body;

    private WxSessionManager wxSessionManager;

    public WxRequest(HttpServletRequest request, WxSessionManager wxSessionManager) throws IOException {
        this.request = request;
        this.wxSessionManager = wxSessionManager;
        // ServletWebRequest
        body = (Body) xmlConverter.read(Body.class, new ServletServerHttpRequest(request));
        WxWebUtils.setWxRequestToRequest(request, this);
    }

    public HttpServletRequest getRawRequest() {
        return request;
    }

    public Body getBody() {
        return body;
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    public WxSession getWxSession(boolean create) {
        return this.wxSessionManager.getWxSession(this, create);
    }

    public WxSession getWxSession() {
        return this.getWxSession(true);
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }

    public Object getParameter(String name) {
        Object value = request.getParameter(name);
        if (value == null) {
            value = this.body.getParameter(name);
        }
        return value;
    }

    @XmlRootElement(name = "xml")
    @XmlAccessorType(XmlAccessType.NONE)
    public static class Body {

        private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

        /**
         * 通用
         * 开发者微信号
         */
        @XmlElement(name = "ToUserName", required = true)
        private String toUserName;

        /**
         * 通用
         * 发送方帐号（一个OpenID）
         */
        @XmlElement(name = "FromUserName", required = true)
        private String fromUserName;

        /**
         * 通用
         * 消息创建时间 （整型）
         */
        @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
        @XmlElement(name = "CreateTime", required = true)
        private Date createTime;


        /**
         * 通用
         * 消息类型
         */
        @XmlJavaTypeAdapter(WxXmlAdapters.MsgTypeAdaptor.class)
        @XmlElement(name = "MsgType", required = true)
        private WxMessage.Type messageType;

        /**
         * 缓存消息类别
         */
        private Wx.Category category;

        public Body() {
        }

        /**
         * 事件的类别
         */
        public Wx.Category getCategory() {
            if (category != null) {
                return category;
            }
            if (this.messageType == WxMessage.Type.EVENT) {
                // 有button类型，则是button
                if (this.getButtonType() != null) {
                    category = Wx.Category.BUTTON;
                } else {
                    // 否则是事件
                    category = Wx.Category.EVENT;
                }
            } else {
                // 否则就是消息
                // category = this.messageType.getCategories()[0];
                category = Wx.Category.MESSAGE;
            }
            return category;
        }


        /**
         * event类型有
         * 事件类型
         */
        @XmlJavaTypeAdapter(WxXmlAdapters.EventAdaptor.class)
        @XmlElement(name = "Event")
        private WxEvent.Type eventType;

        /**
         * 按钮类型
         */
        private WxButton.Type buttonType;

        /**
         * button事件的类型
         */
        public WxButton.Type getButtonType() {
            if (this.buttonType != null) {
                return this.buttonType;
            }
            // 只有msgType是event时才是buttonType
            if (this.messageType == WxMessage.Type.EVENT) {
                this.buttonType = Arrays.stream(WxButton.Type.values())
                        .filter(t -> t.name().equals(this.eventType.name()))
                        .findFirst().orElse(null);
            }
            return this.buttonType;
        }

        /**
         * event类型有
         * 事件KEY值，根据event不同而不同
         * CLICK:与自定义菜单接口中KEY值对应
         * VIEW:事件KEY值，设置的跳转URL
         * scancode_push:事件KEY值，由开发者在创建菜单时设定
         * scancode_waitmsg:事件KEY值，由开发者在创建菜单时设定
         * pic_photo_or_album:事件KEY值，由开发者在创建菜单时设定
         * pic_weixin:事件KEY值，由开发者在创建菜单时设定
         * location_select:事件KEY值，由开发者在创建菜单时设定
         * subscribe:事件KEY值，qrscene_为前缀，后面为二维码的参数值
         * unsubscribe:事件KEY值，qrscene_为前缀，后面为二维码的参数值
         * SCAN:事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
         * LOCATION:无
         */
        @XmlElement(name = "EventKey")
        private String eventKey;

        /**
         * event类型为VIEW时才有
         * 指菜单ID，如果是个性化菜单，则可以通过这个字段，知道是哪个规则的菜单被点击了。
         */
        @XmlElement(name = "MenuID")
        private String menuId;

        /**
         * event类型为scancode_push、scancode_waitmsg才有
         * 扫描信息
         */
        @XmlElement(name = "ScanCodeInfo")
        private ScanCodeInfo scanCodeInfo;

        /**
         * event为pic_sysphoto、pic_photo_or_album、pic_weixin才有
         * 发送的图片信息
         */
        @XmlElement(name = "SendPicsInfo")
        private SendPicsInfo sendPicsInfo;

        /**
         * event为location_select才有
         * 发送的位置信息
         */
        @XmlElement(name = "SendLocationInfo")
        private SendLocationInfo sendLocationInfo;

        /**
         * text类型的消息有
         * 文本消息内容
         */
        @XmlElement(name = "Content")
        private String content;

        /**
         * image类型的消息有
         * 图片链接（由系统生成）
         */
        @XmlElement(name = "PicUrl")
        private String picUrl;

        /**
         * image、voice、video类型的消息有
         * 语音消息媒体id，可以调用多媒体文件下载接口拉取数据。
         */
        @XmlElement(name = "MediaId")
        private String mediaId;

        /**
         * voice类型的消息有
         * 语音格式，如amr，speex等
         */
        @XmlElement(name = "Format")
        private String format;

        /**
         * voice类型才有
         * 开启语音识别后，附带的识别结果，UTF8编码
         */
        @XmlElement(name = "Recognition")
        private String recognition;

        /**
         * video、shortvideo类型才有
         * 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
         */
        @XmlElement(name = "ThumbMediaId")
        private String thumbMediaId;

        /**
         * location类型才有
         * 地理位置维度
         */
        @XmlElement(name = "Location_X")
        private Double locationX;

        /**
         * location类型才有
         * 地理位置经度
         */
        @XmlElement(name = "Location_Y")
        private Double locationY;

        /**
         * location类型才有
         * 地图缩放大小
         */
        @XmlElement(name = "Scale")
        private Integer scale;

        /**
         * location类型才有
         * 地理位置信息
         */
        @XmlElement(name = "Label")
        private String label;

        /**
         * link类型才有
         * 消息标题
         */
        @XmlElement(name = "Title")
        private String title;

        /**
         * link类型才有
         * 消息描述
         */
        @XmlElement(name = "Description")
        private String description;

        /**
         * link类型才有
         * 消息链接
         */
        @XmlElement(name = "Url")
        private String url;

        /**
         * 二维码的ticket，可用来换取二维码图片
         */
        @XmlElement(name = "Ticket")
        private String ticket;

        /**
         * 地理位置纬度
         */
        @XmlElement(name = "Latitude")
        private Double latitude;

        /**
         * 地理位置经度
         */
        @XmlElement(name = "Longitude")
        private Double longitude;

        /**
         * 地理位置精度
         */
        @XmlElement(name = "Precision")
        private Double precision;

        /**
         * 有效期 (整形)，指的是时间戳，将于该时间戳认证过期
         */
        @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
        @XmlElement(name = "ExpiredTime")
        private Date expiredTime;

        /**
         * 失败发生时间 (整形)，时间戳
         */
        @XmlJavaTypeAdapter(WxXmlAdapters.CreateTimeAdaptor.class)
        @XmlElement(name = "FailTime")
        private Date failTime;

        /**
         * 认证失败的原因
         */
        @XmlElement(name = "FailReason")
        private String failReason;

        /**
         * 用户消息类型才有
         * 消息id，64位整型
         */
        @XmlElement(name = "MsgId")
        private Long msgId;

        /**
         * 模板消息推送结果
         */
        @XmlElement(name = "Status")
        private String status;

        public String getToUserName() {
            return this.toUserName;
        }

        public String getFromUserName() {
            return this.fromUserName;
        }

        public Date getCreateTime() {
            return this.createTime;
        }

        public WxMessage.Type getMessageType() {
            return this.messageType;
        }

        public WxEvent.Type getEventType() {
            return this.eventType;
        }

        public String getEventKey() {
            return this.eventKey;
        }

        public String getMenuId() {
            return this.menuId;
        }

        public ScanCodeInfo getScanCodeInfo() {
            return this.scanCodeInfo;
        }

        public SendPicsInfo getSendPicsInfo() {
            return this.sendPicsInfo;
        }

        public SendLocationInfo getSendLocationInfo() {
            return this.sendLocationInfo;
        }

        public String getContent() {
            return this.content;
        }

        public String getPicUrl() {
            return this.picUrl;
        }

        public String getMediaId() {
            return this.mediaId;
        }

        public String getFormat() {
            return this.format;
        }

        public String getRecognition() {
            return this.recognition;
        }

        public String getThumbMediaId() {
            return this.thumbMediaId;
        }

        public Double getLocationX() {
            return this.locationX;
        }

        public Double getLocationY() {
            return this.locationY;
        }

        public Integer getScale() {
            return this.scale;
        }

        public String getLabel() {
            return this.label;
        }

        public String getTitle() {
            return this.title;
        }

        public String getDescription() {
            return this.description;
        }

        public String getUrl() {
            return this.url;
        }

        public String getTicket() {
            return this.ticket;
        }

        public Double getLatitude() {
            return this.latitude;
        }

        public Double getLongitude() {
            return this.longitude;
        }

        public Double getPrecision() {
            return this.precision;
        }

        public Date getExpiredTime() {
            return this.expiredTime;
        }

        public Date getFailTime() {
            return this.failTime;
        }

        public String getFailReason() {
            return this.failReason;
        }

        public Long getMsgId() {
            return this.msgId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setToUserName(String toUserName) {
            this.toUserName = toUserName;
        }

        public void setFromUserName(String fromUserName) {
            this.fromUserName = fromUserName;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public void setMessageType(WxMessage.Type messageType) {
            this.messageType = messageType;
        }

        public void setCategory(Wx.Category category) {
            this.category = category;
        }

        public void setEventType(WxEvent.Type eventType) {
            this.eventType = eventType;
        }

        public void setButtonType(WxButton.Type buttonType) {
            this.buttonType = buttonType;
        }

        public void setEventKey(String eventKey) {
            this.eventKey = eventKey;
        }

        public void setMenuId(String menuId) {
            this.menuId = menuId;
        }

        public void setScanCodeInfo(ScanCodeInfo scanCodeInfo) {
            this.scanCodeInfo = scanCodeInfo;
        }

        public void setSendPicsInfo(SendPicsInfo sendPicsInfo) {
            this.sendPicsInfo = sendPicsInfo;
        }

        public void setSendLocationInfo(SendLocationInfo sendLocationInfo) {
            this.sendLocationInfo = sendLocationInfo;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public void setRecognition(String recognition) {
            this.recognition = recognition;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
        }

        public void setLocationX(Double locationX) {
            this.locationX = locationX;
        }

        public void setLocationY(Double locationY) {
            this.locationY = locationY;
        }

        public void setScale(Integer scale) {
            this.scale = scale;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTicket(String ticket) {
            this.ticket = ticket;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public void setPrecision(Double precision) {
            this.precision = precision;
        }

        public void setExpiredTime(Date expiredTime) {
            this.expiredTime = expiredTime;
        }

        public void setFailTime(Date failTime) {
            this.failTime = failTime;
        }

        public void setFailReason(String failReason) {
            this.failReason = failReason;
        }

        public void setMsgId(Long msgId) {
            this.msgId = msgId;
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Body)) return false;
            final Body other = (Body) o;
            if (!other.canEqual((Object) this)) return false;
            final Object this$toUserName = this.getToUserName();
            final Object other$toUserName = other.getToUserName();
            if (this$toUserName == null ? other$toUserName != null : !this$toUserName.equals(other$toUserName))
                return false;
            final Object this$fromUserName = this.getFromUserName();
            final Object other$fromUserName = other.getFromUserName();
            if (this$fromUserName == null ? other$fromUserName != null : !this$fromUserName.equals(other$fromUserName))
                return false;
            final Object this$createTime = this.getCreateTime();
            final Object other$createTime = other.getCreateTime();
            if (this$createTime == null ? other$createTime != null : !this$createTime.equals(other$createTime))
                return false;
            final Object this$messageType = this.getMessageType();
            final Object other$messageType = other.getMessageType();
            if (this$messageType == null ? other$messageType != null : !this$messageType.equals(other$messageType))
                return false;
            final Object this$category = this.getCategory();
            final Object other$category = other.getCategory();
            if (this$category == null ? other$category != null : !this$category.equals(other$category)) return false;
            final Object this$eventType = this.getEventType();
            final Object other$eventType = other.getEventType();
            if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType))
                return false;
            final Object this$buttonType = this.getButtonType();
            final Object other$buttonType = other.getButtonType();
            if (this$buttonType == null ? other$buttonType != null : !this$buttonType.equals(other$buttonType))
                return false;
            final Object this$eventKey = this.getEventKey();
            final Object other$eventKey = other.getEventKey();
            if (this$eventKey == null ? other$eventKey != null : !this$eventKey.equals(other$eventKey)) return false;
            final Object this$menuId = this.getMenuId();
            final Object other$menuId = other.getMenuId();
            if (this$menuId == null ? other$menuId != null : !this$menuId.equals(other$menuId)) return false;
            final Object this$scanCodeInfo = this.getScanCodeInfo();
            final Object other$scanCodeInfo = other.getScanCodeInfo();
            if (this$scanCodeInfo == null ? other$scanCodeInfo != null : !this$scanCodeInfo.equals(other$scanCodeInfo))
                return false;
            final Object this$sendPicsInfo = this.getSendPicsInfo();
            final Object other$sendPicsInfo = other.getSendPicsInfo();
            if (this$sendPicsInfo == null ? other$sendPicsInfo != null : !this$sendPicsInfo.equals(other$sendPicsInfo))
                return false;
            final Object this$sendLocationInfo = this.getSendLocationInfo();
            final Object other$sendLocationInfo = other.getSendLocationInfo();
            if (this$sendLocationInfo == null ? other$sendLocationInfo != null : !this$sendLocationInfo.equals(other$sendLocationInfo))
                return false;
            final Object this$content = this.getContent();
            final Object other$content = other.getContent();
            if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
            final Object this$picUrl = this.getPicUrl();
            final Object other$picUrl = other.getPicUrl();
            if (this$picUrl == null ? other$picUrl != null : !this$picUrl.equals(other$picUrl)) return false;
            final Object this$mediaId = this.getMediaId();
            final Object other$mediaId = other.getMediaId();
            if (this$mediaId == null ? other$mediaId != null : !this$mediaId.equals(other$mediaId)) return false;
            final Object this$format = this.getFormat();
            final Object other$format = other.getFormat();
            if (this$format == null ? other$format != null : !this$format.equals(other$format)) return false;
            final Object this$recognition = this.getRecognition();
            final Object other$recognition = other.getRecognition();
            if (this$recognition == null ? other$recognition != null : !this$recognition.equals(other$recognition))
                return false;
            final Object this$thumbMediaId = this.getThumbMediaId();
            final Object other$thumbMediaId = other.getThumbMediaId();
            if (this$thumbMediaId == null ? other$thumbMediaId != null : !this$thumbMediaId.equals(other$thumbMediaId))
                return false;
            final Object this$locationX = this.getLocationX();
            final Object other$locationX = other.getLocationX();
            if (this$locationX == null ? other$locationX != null : !this$locationX.equals(other$locationX))
                return false;
            final Object this$locationY = this.getLocationY();
            final Object other$locationY = other.getLocationY();
            if (this$locationY == null ? other$locationY != null : !this$locationY.equals(other$locationY))
                return false;
            final Object this$scale = this.getScale();
            final Object other$scale = other.getScale();
            if (this$scale == null ? other$scale != null : !this$scale.equals(other$scale)) return false;
            final Object this$label = this.getLabel();
            final Object other$label = other.getLabel();
            if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
            final Object this$title = this.getTitle();
            final Object other$title = other.getTitle();
            if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
            final Object this$description = this.getDescription();
            final Object other$description = other.getDescription();
            if (this$description == null ? other$description != null : !this$description.equals(other$description))
                return false;
            final Object this$url = this.getUrl();
            final Object other$url = other.getUrl();
            if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
            final Object this$ticket = this.getTicket();
            final Object other$ticket = other.getTicket();
            if (this$ticket == null ? other$ticket != null : !this$ticket.equals(other$ticket)) return false;
            final Object this$latitude = this.getLatitude();
            final Object other$latitude = other.getLatitude();
            if (this$latitude == null ? other$latitude != null : !this$latitude.equals(other$latitude)) return false;
            final Object this$longitude = this.getLongitude();
            final Object other$longitude = other.getLongitude();
            if (this$longitude == null ? other$longitude != null : !this$longitude.equals(other$longitude))
                return false;
            final Object this$precision = this.getPrecision();
            final Object other$precision = other.getPrecision();
            if (this$precision == null ? other$precision != null : !this$precision.equals(other$precision))
                return false;
            final Object this$expiredTime = this.getExpiredTime();
            final Object other$expiredTime = other.getExpiredTime();
            if (this$expiredTime == null ? other$expiredTime != null : !this$expiredTime.equals(other$expiredTime))
                return false;
            final Object this$failTime = this.getFailTime();
            final Object other$failTime = other.getFailTime();
            if (this$failTime == null ? other$failTime != null : !this$failTime.equals(other$failTime)) return false;
            final Object this$failReason = this.getFailReason();
            final Object other$failReason = other.getFailReason();
            if (this$failReason == null ? other$failReason != null : !this$failReason.equals(other$failReason))
                return false;
            final Object this$msgId = this.getMsgId();
            final Object other$msgId = other.getMsgId();
            if (this$msgId == null ? other$msgId != null : !this$msgId.equals(other$msgId)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $toUserName = this.getToUserName();
            result = result * PRIME + ($toUserName == null ? 43 : $toUserName.hashCode());
            final Object $fromUserName = this.getFromUserName();
            result = result * PRIME + ($fromUserName == null ? 43 : $fromUserName.hashCode());
            final Object $createTime = this.getCreateTime();
            result = result * PRIME + ($createTime == null ? 43 : $createTime.hashCode());
            final Object $messageType = this.getMessageType();
            result = result * PRIME + ($messageType == null ? 43 : $messageType.hashCode());
            final Object $category = this.getCategory();
            result = result * PRIME + ($category == null ? 43 : $category.hashCode());
            final Object $eventType = this.getEventType();
            result = result * PRIME + ($eventType == null ? 43 : $eventType.hashCode());
            final Object $buttonType = this.getButtonType();
            result = result * PRIME + ($buttonType == null ? 43 : $buttonType.hashCode());
            final Object $eventKey = this.getEventKey();
            result = result * PRIME + ($eventKey == null ? 43 : $eventKey.hashCode());
            final Object $menuId = this.getMenuId();
            result = result * PRIME + ($menuId == null ? 43 : $menuId.hashCode());
            final Object $scanCodeInfo = this.getScanCodeInfo();
            result = result * PRIME + ($scanCodeInfo == null ? 43 : $scanCodeInfo.hashCode());
            final Object $sendPicsInfo = this.getSendPicsInfo();
            result = result * PRIME + ($sendPicsInfo == null ? 43 : $sendPicsInfo.hashCode());
            final Object $sendLocationInfo = this.getSendLocationInfo();
            result = result * PRIME + ($sendLocationInfo == null ? 43 : $sendLocationInfo.hashCode());
            final Object $content = this.getContent();
            result = result * PRIME + ($content == null ? 43 : $content.hashCode());
            final Object $picUrl = this.getPicUrl();
            result = result * PRIME + ($picUrl == null ? 43 : $picUrl.hashCode());
            final Object $mediaId = this.getMediaId();
            result = result * PRIME + ($mediaId == null ? 43 : $mediaId.hashCode());
            final Object $format = this.getFormat();
            result = result * PRIME + ($format == null ? 43 : $format.hashCode());
            final Object $recognition = this.getRecognition();
            result = result * PRIME + ($recognition == null ? 43 : $recognition.hashCode());
            final Object $thumbMediaId = this.getThumbMediaId();
            result = result * PRIME + ($thumbMediaId == null ? 43 : $thumbMediaId.hashCode());
            final Object $locationX = this.getLocationX();
            result = result * PRIME + ($locationX == null ? 43 : $locationX.hashCode());
            final Object $locationY = this.getLocationY();
            result = result * PRIME + ($locationY == null ? 43 : $locationY.hashCode());
            final Object $scale = this.getScale();
            result = result * PRIME + ($scale == null ? 43 : $scale.hashCode());
            final Object $label = this.getLabel();
            result = result * PRIME + ($label == null ? 43 : $label.hashCode());
            final Object $title = this.getTitle();
            result = result * PRIME + ($title == null ? 43 : $title.hashCode());
            final Object $description = this.getDescription();
            result = result * PRIME + ($description == null ? 43 : $description.hashCode());
            final Object $url = this.getUrl();
            result = result * PRIME + ($url == null ? 43 : $url.hashCode());
            final Object $ticket = this.getTicket();
            result = result * PRIME + ($ticket == null ? 43 : $ticket.hashCode());
            final Object $latitude = this.getLatitude();
            result = result * PRIME + ($latitude == null ? 43 : $latitude.hashCode());
            final Object $longitude = this.getLongitude();
            result = result * PRIME + ($longitude == null ? 43 : $longitude.hashCode());
            final Object $precision = this.getPrecision();
            result = result * PRIME + ($precision == null ? 43 : $precision.hashCode());
            final Object $expiredTime = this.getExpiredTime();
            result = result * PRIME + ($expiredTime == null ? 43 : $expiredTime.hashCode());
            final Object $failTime = this.getFailTime();
            result = result * PRIME + ($failTime == null ? 43 : $failTime.hashCode());
            final Object $failReason = this.getFailReason();
            result = result * PRIME + ($failReason == null ? 43 : $failReason.hashCode());
            final Object $msgId = this.getMsgId();
            result = result * PRIME + ($msgId == null ? 43 : $msgId.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof Body;
        }

        public String toString() {
            return "com.mxixm.fastboot.weixin.module.web.WxRequest.WxMessageBody(toUser=" + this.getToUserName() + ", fromUser=" + this.getFromUserName() + ", createTime=" + this.getCreateTime() + ", messageType=" + this.getMessageType() + ", category=" + this.getCategory() + ", eventType=" + this.getEventType() + ", buttonType=" + this.getButtonType() + ", eventKey=" + this.getEventKey() + ", menuId=" + this.getMenuId() + ", scanCodeInfo=" + this.getScanCodeInfo() + ", sendPicsInfo=" + this.getSendPicsInfo() + ", sendLocationInfo=" + this.getSendLocationInfo() + ", content=" + this.getContent() + ", picUrl=" + this.getPicUrl() + ", mediaId=" + this.getMediaId() + ", format=" + this.getFormat() + ", recognition=" + this.getRecognition() + ", thumbMediaId=" + this.getThumbMediaId() + ", locationX=" + this.getLocationX() + ", locationY=" + this.getLocationY() + ", scale=" + this.getScale() + ", label=" + this.getLabel() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", url=" + this.getUrl() + ", ticket=" + this.getTicket() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ", precision=" + this.getPrecision() + ", expiredTime=" + this.getExpiredTime() + ", failTime=" + this.getFailTime() + ", failReason=" + this.getFailReason() + ", msgId=" + this.getMsgId() + ")";
        }

        /**
         * 扫描信息
         */
        @XmlRootElement(name = "ScanCodeInfo")
        @XmlAccessorType(XmlAccessType.NONE)
        public static class ScanCodeInfo {

            /**
             * 扫描类型，一般是qrcode
             */
            @XmlElement(name = "ScanType")
            private String scanType;

            /**
             * 扫描结果
             */
            @XmlElement(name = "ScanResult")
            private String scanResult;

            public ScanCodeInfo() {
            }

            public String getScanType() {
                return this.scanType;
            }

            public String getScanResult() {
                return this.scanResult;
            }

            public void setScanType(String scanType) {
                this.scanType = scanType;
            }

            public void setScanResult(String scanResult) {
                this.scanResult = scanResult;
            }

            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof ScanCodeInfo)) return false;
                final ScanCodeInfo other = (ScanCodeInfo) o;
                if (!other.canEqual((Object) this)) return false;
                final Object this$scanType = this.getScanType();
                final Object other$scanType = other.getScanType();
                if (this$scanType == null ? other$scanType != null : !this$scanType.equals(other$scanType))
                    return false;
                final Object this$scanResult = this.getScanResult();
                final Object other$scanResult = other.getScanResult();
                if (this$scanResult == null ? other$scanResult != null : !this$scanResult.equals(other$scanResult))
                    return false;
                return true;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $scanType = this.getScanType();
                result = result * PRIME + ($scanType == null ? 43 : $scanType.hashCode());
                final Object $scanResult = this.getScanResult();
                result = result * PRIME + ($scanResult == null ? 43 : $scanResult.hashCode());
                return result;
            }

            protected boolean canEqual(Object other) {
                return other instanceof ScanCodeInfo;
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.web.WxRequest.WxMessageBody.ScanCodeInfo(scanType=" + this.getScanType() + ", scanResult=" + this.getScanResult() + ")";
            }
        }

        /**
         * 发送的图片信息
         */
        @XmlRootElement(name = "SendPicsInfo")
        @XmlAccessorType(XmlAccessType.NONE)
        public static class SendPicsInfo {

            /**
             * 发送的图片数量
             */
            @XmlElement(name = "Count")
            private Integer count;

            /**
             * 图片列表
             */
            @XmlElementWrapper(name = "PicList")
            @XmlElements(@XmlElement(name = "item", type = Item.class))
            private List<Item> picList;

            public SendPicsInfo() {
            }

            public Integer getCount() {
                return this.count;
            }

            public List<Item> getPicList() {
                return this.picList;
            }

            public void setCount(Integer count) {
                this.count = count;
            }

            public void setPicList(List<Item> picList) {
                this.picList = picList;
            }

            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof SendPicsInfo)) return false;
                final SendPicsInfo other = (SendPicsInfo) o;
                if (!other.canEqual((Object) this)) return false;
                final Object this$count = this.getCount();
                final Object other$count = other.getCount();
                if (this$count == null ? other$count != null : !this$count.equals(other$count)) return false;
                final Object this$picList = this.getPicList();
                final Object other$picList = other.getPicList();
                if (this$picList == null ? other$picList != null : !this$picList.equals(other$picList)) return false;
                return true;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $count = this.getCount();
                result = result * PRIME + ($count == null ? 43 : $count.hashCode());
                final Object $picList = this.getPicList();
                result = result * PRIME + ($picList == null ? 43 : $picList.hashCode());
                return result;
            }

            protected boolean canEqual(Object other) {
                return other instanceof SendPicsInfo;
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.web.WxRequest.WxMessageBody.SendPicsInfo(count=" + this.getCount() + ", picList=" + this.getPicList() + ")";
            }

            @XmlRootElement(name = "item")
            @XmlAccessorType(XmlAccessType.NONE)
            public static class Item {
                /**
                 * 图片的MD5值，开发者若需要，可用于验证接收到图片
                 */
                @XmlElement(name = "PicMd5Sum")
                private String picMd5Sum;

                public Item() {
                }

                public String getPicMd5Sum() {
                    return this.picMd5Sum;
                }

                public void setPicMd5Sum(String picMd5Sum) {
                    this.picMd5Sum = picMd5Sum;
                }

                public boolean equals(Object o) {
                    if (o == this) return true;
                    if (!(o instanceof Item)) return false;
                    final Item other = (Item) o;
                    if (!other.canEqual((Object) this)) return false;
                    final Object this$picMd5Sum = this.getPicMd5Sum();
                    final Object other$picMd5Sum = other.getPicMd5Sum();
                    if (this$picMd5Sum == null ? other$picMd5Sum != null : !this$picMd5Sum.equals(other$picMd5Sum))
                        return false;
                    return true;
                }

                public int hashCode() {
                    final int PRIME = 59;
                    int result = 1;
                    final Object $picMd5Sum = this.getPicMd5Sum();
                    result = result * PRIME + ($picMd5Sum == null ? 43 : $picMd5Sum.hashCode());
                    return result;
                }

                protected boolean canEqual(Object other) {
                    return other instanceof Item;
                }

                public String toString() {
                    return "com.mxixm.fastboot.weixin.module.web.WxRequest.WxMessageBody.SendPicsInfo.Item(picMd5Sum=" + this.getPicMd5Sum() + ")";
                }
            }
        }

        /**
         * 发送的位置信息
         */
        @XmlRootElement(name = "SendLocationInfo")
        @XmlAccessorType(XmlAccessType.NONE)
        public static class SendLocationInfo {

            /**
             * X坐标信息
             */
            @XmlElement(name = "Location_X")
            private Double locationX;

            /**
             * Y坐标信息
             */
            @XmlElement(name = "Location_Y")
            private Double locationY;

            /**
             * 精度，可理解为精度或者比例尺、越精细的话 scale越高
             */
            @XmlElement(name = "Scale")
            private Integer scale;

            /**
             * 地理位置的字符串信息
             */
            @XmlElement(name = "Label")
            private String label;

            /**
             * 朋友圈POI的名字，可能为空
             * POI（Point of Interest）
             */
            @XmlElement(name = "Poiname")
            private String poiname;

            public SendLocationInfo() {
            }

            public Double getLocationX() {
                return this.locationX;
            }

            public Double getLocationY() {
                return this.locationY;
            }

            public Integer getScale() {
                return this.scale;
            }

            public String getLabel() {
                return this.label;
            }

            public String getPoiname() {
                return this.poiname;
            }

            public void setLocationX(Double locationX) {
                this.locationX = locationX;
            }

            public void setLocationY(Double locationY) {
                this.locationY = locationY;
            }

            public void setScale(Integer scale) {
                this.scale = scale;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public void setPoiname(String poiname) {
                this.poiname = poiname;
            }

            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof SendLocationInfo)) return false;
                final SendLocationInfo other = (SendLocationInfo) o;
                if (!other.canEqual((Object) this)) return false;
                final Object this$locationX = this.getLocationX();
                final Object other$locationX = other.getLocationX();
                if (this$locationX == null ? other$locationX != null : !this$locationX.equals(other$locationX))
                    return false;
                final Object this$locationY = this.getLocationY();
                final Object other$locationY = other.getLocationY();
                if (this$locationY == null ? other$locationY != null : !this$locationY.equals(other$locationY))
                    return false;
                final Object this$scale = this.getScale();
                final Object other$scale = other.getScale();
                if (this$scale == null ? other$scale != null : !this$scale.equals(other$scale)) return false;
                final Object this$label = this.getLabel();
                final Object other$label = other.getLabel();
                if (this$label == null ? other$label != null : !this$label.equals(other$label)) return false;
                final Object this$poiname = this.getPoiname();
                final Object other$poiname = other.getPoiname();
                if (this$poiname == null ? other$poiname != null : !this$poiname.equals(other$poiname)) return false;
                return true;
            }

            public int hashCode() {
                final int PRIME = 59;
                int result = 1;
                final Object $locationX = this.getLocationX();
                result = result * PRIME + ($locationX == null ? 43 : $locationX.hashCode());
                final Object $locationY = this.getLocationY();
                result = result * PRIME + ($locationY == null ? 43 : $locationY.hashCode());
                final Object $scale = this.getScale();
                result = result * PRIME + ($scale == null ? 43 : $scale.hashCode());
                final Object $label = this.getLabel();
                result = result * PRIME + ($label == null ? 43 : $label.hashCode());
                final Object $poiname = this.getPoiname();
                result = result * PRIME + ($poiname == null ? 43 : $poiname.hashCode());
                return result;
            }

            protected boolean canEqual(Object other) {
                return other instanceof SendLocationInfo;
            }

            public String toString() {
                return "com.mxixm.fastboot.weixin.module.web.WxRequest.WxMessageBody.SendLocationInfo(locationX=" + this.getLocationX() + ", locationY=" + this.getLocationY() + ", scale=" + this.getScale() + ", label=" + this.getLabel() + ", poiname=" + this.getPoiname() + ")";
            }
        }

        /**
         * @param name
         * @return dummy
         */
        public Object getParameter(String name) {
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(this.getClass(), name);
            if (propertyDescriptor != null) {
                Object value = null;
                try {
                    value = propertyDescriptor.getReadMethod().invoke(this, new Object[]{});//调用方法获取方法的返回值
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                return value;
            }
            return null;
        }

    }

}
