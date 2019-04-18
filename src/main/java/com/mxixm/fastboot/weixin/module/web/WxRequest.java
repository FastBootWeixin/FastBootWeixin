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
import com.mxixm.fastboot.weixin.module.adapter.WxXmlAdapters;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.extend.WxQrCode;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.web.session.WxSession;
import com.mxixm.fastboot.weixin.module.web.session.WxSessionManager;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * FastBootWeixin WxRequest
 *
 * @author Guangshan
 * @date 2017/9/2 22:44
 * @since 0.1.2
 */
public class WxRequest {

    private Body body;

    private final HttpServletRequest request;

    private final WxSessionManager wxSessionManager;

    private final String requestUrl;

    private final String requestUri;

    private final String openId;

    private final String nonce;

    private final String signature;

    /**
     * 加密类型，固定为aes
     */
    private final String encryptType;
    /**
     * 消息签名，用于加解密
     */
    private final String messageSignature;

    private final Long timestamp;

    /**
     * 请求关联的按钮
     */
    private WxMenu.Button button;

    public WxRequest(HttpServletRequest request, WxSessionManager wxSessionManager) throws IOException {
        this.request = request;
        this.wxSessionManager = wxSessionManager;
        this.openId = request.getParameter("openid");
        this.nonce = request.getParameter("nonce");
        this.signature = request.getParameter("signature");
        this.messageSignature = request.getParameter("msg_signature");
        this.timestamp = Long.valueOf(request.getParameter("timestamp"));
        this.encryptType = request.getParameter("encrypt_type");
        requestUrl = request.getRequestURL().toString();
        requestUri = request.getRequestURI();
    }

    public WxMenu.Button getButton() {
        return button;
    }

    public void setButton(WxMenu.Button button) {
        this.button = button;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public HttpServletRequest getRawRequest() {
        return request;
    }

    public Body getBody() {
        return body;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getRequestUrl() {
        return this.requestUrl;
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

    public String getOpenId() {
        return openId;
    }

    public String getNonce() {
        return nonce;
    }

    public String getSignature() {
        return signature;
    }

    public String getEncryptType() {
        return encryptType;
    }

    public String getMessageSignature() {
        return messageSignature;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean isEncrypted() {
        return !StringUtils.isEmpty(this.encryptType);
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
         * 可选通用
         * 加密消息体
         */
        @XmlElement(name = "Encrypt", required = false)
        private String encrypt;

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
            if (this.messageType != WxMessage.Type.EVENT) {
                category = Wx.Category.MESSAGE;
                return category;
            }
            // 默认使用eventType中的Category即可，理论上逻辑与下面相同
            if (this.eventType != null) {
                category = this.eventType.getCategory();
            } else {
                // 不明类型，使用EVENT
                category = Wx.Category.EVENT;
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
            if (this.buttonType != null || this.getCategory() != Wx.Category.BUTTON) {
                return this.buttonType;
            }
            // 只有msgType是event时才是buttonType
            this.buttonType = WxButton.Type.ofEventType(this.eventType);
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
         * 二维码场景值
         */
        private String scene;

        /**
         * 获取扫描二维码的场景值
         * @return
         */
        public String getQrScene() {
            return this.getScene();
        }

        /**
         * 获取扫描二维码的场景值
         * @return
         */
        public String getScene() {
            if (scene == null && eventKey != null) {
                if (WxEvent.Type.SCAN == eventType) {
                    scene = eventKey;
                } else if (eventKey.startsWith(WxQrCode.QR_SCENE_SUFFIX)) {
                    // 明确是否只有扫关注码时才有scene，如果是则可再加入WxEvent.Type.Subscribe == eventType的判断
                    scene = eventKey.substring(WxQrCode.QR_SCENE_SUFFIX.length());
                }
            }
            return scene;
        }

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
         * 用户群发消息类型才有
         * 消息id，64位整型
         */
        @XmlElement(name = "MsgID")
        private Long msgId;

        /**
         * 模板消息或者群发消息推送结果
         * 	群发的结构，为“send success”或“send fail”或“err(num)”。
         * 	但send success时，也有可能因用户拒收公众号的消息、系统错误等原因造成少量用户接收失败。
         * 	err(num)是审核失败的具体原因，可能的情况如下：
         * 	err(10001), //涉嫌广告
         * 	err(20001), //涉嫌政治
         * 	err(20004), //涉嫌社会
         * 	err(20002), //涉嫌色情
         * 	err(20006), //涉嫌违法犯罪
         * 	err(20008), //涉嫌欺诈
         * 	err(20013), //涉嫌版权
         * 	err(22000), //涉嫌互推(互相宣传)
         * 	err(21000), //涉嫌其他
         * 	err(30001) // 原创校验出现系统错误且用户选择了被判为转载就不群发
         * 	err(30002) // 原创校验被判定为不能群发
         * 	err(30003) // 原创校验被判定为转载文且用户选择了被判为转载就不群发
         * 	更合理的做法是这里结构化一下做成枚举，但是暂时偷懒，不想思考放在哪个包下面，故暂不进行结构化。等有人用的时候考虑结构化
         */
        @XmlElement(name = "Status")
        private String status;

        /**
         * 以下为群发消息相关内容，暂时只在request中提供支持，没有对应的子消息类型
         * tag_id下粉丝数；或者openid_list中的粉丝数
         */
        @XmlElement(name = "TotalCount")
        private Integer totalCount;

        /**
         * 过滤（过滤是指特定地区、性别的过滤、用户设置拒收的过滤，用户接收已超4条的过滤）后，
         * 准备发送的粉丝数，原则上，FilterCount = SentCount + ErrorCount
         */
        @XmlElement(name = "FilterCount")
        private Integer filterCount;

        /**
         * 发送成功的粉丝数
         */
        @XmlElement(name = "SentCount")
        private Integer sendCount;

        /**
         * 发送失败的粉丝数
         */
        @XmlElement(name = "ErrorCount")
        private Integer errorCount;

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

        public String getEncrypt() {
            return encrypt;
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

        public void setScene(String scene) {
            this.scene = scene;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getFilterCount() {
            return filterCount;
        }

        public void setFilterCount(Integer filterCount) {
            this.filterCount = filterCount;
        }

        public Integer getSendCount() {
            return sendCount;
        }

        public void setSendCount(Integer sendCount) {
            this.sendCount = sendCount;
        }

        public Integer getErrorCount() {
            return errorCount;
        }

        public void setErrorCount(Integer errorCount) {
            this.errorCount = errorCount;
        }

        public void setEncrypt(String encrypt) {
            this.encrypt = encrypt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Body)) {
                return false;
            }
            Body body = (Body) o;
            return Objects.equals(getToUserName(), body.getToUserName()) &&
                    Objects.equals(getEncrypt(), body.getEncrypt()) &&
                    Objects.equals(getFromUserName(), body.getFromUserName()) &&
                    Objects.equals(getCreateTime(), body.getCreateTime()) &&
                    getMessageType() == body.getMessageType() &&
                    getCategory() == body.getCategory() &&
                    getEventType() == body.getEventType() &&
                    getButtonType() == body.getButtonType() &&
                    Objects.equals(getEventKey(), body.getEventKey()) &&
                    Objects.equals(getScene(), body.getScene()) &&
                    Objects.equals(getMenuId(), body.getMenuId()) &&
                    Objects.equals(getScanCodeInfo(), body.getScanCodeInfo()) &&
                    Objects.equals(getSendPicsInfo(), body.getSendPicsInfo()) &&
                    Objects.equals(getSendLocationInfo(), body.getSendLocationInfo()) &&
                    Objects.equals(getContent(), body.getContent()) &&
                    Objects.equals(getPicUrl(), body.getPicUrl()) &&
                    Objects.equals(getMediaId(), body.getMediaId()) &&
                    Objects.equals(getFormat(), body.getFormat()) &&
                    Objects.equals(getRecognition(), body.getRecognition()) &&
                    Objects.equals(getThumbMediaId(), body.getThumbMediaId()) &&
                    Objects.equals(getLocationX(), body.getLocationX()) &&
                    Objects.equals(getLocationY(), body.getLocationY()) &&
                    Objects.equals(getScale(), body.getScale()) &&
                    Objects.equals(getLabel(), body.getLabel()) &&
                    Objects.equals(getTitle(), body.getTitle()) &&
                    Objects.equals(getDescription(), body.getDescription()) &&
                    Objects.equals(getUrl(), body.getUrl()) &&
                    Objects.equals(getTicket(), body.getTicket()) &&
                    Objects.equals(getLatitude(), body.getLatitude()) &&
                    Objects.equals(getLongitude(), body.getLongitude()) &&
                    Objects.equals(getPrecision(), body.getPrecision()) &&
                    Objects.equals(getExpiredTime(), body.getExpiredTime()) &&
                    Objects.equals(getFailTime(), body.getFailTime()) &&
                    Objects.equals(getFailReason(), body.getFailReason()) &&
                    Objects.equals(getMsgId(), body.getMsgId()) &&
                    Objects.equals(getStatus(), body.getStatus()) &&
                    Objects.equals(getTotalCount(), body.getTotalCount()) &&
                    Objects.equals(getFilterCount(), body.getFilterCount()) &&
                    Objects.equals(getSendCount(), body.getSendCount()) &&
                    Objects.equals(getErrorCount(), body.getErrorCount());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getToUserName(), getEncrypt(), getFromUserName(), getCreateTime(), getMessageType(), getCategory(), getEventType(), getButtonType(), getEventKey(), getScene(), getMenuId(), getScanCodeInfo(), getSendPicsInfo(), getSendLocationInfo(), getContent(), getPicUrl(), getMediaId(), getFormat(), getRecognition(), getThumbMediaId(), getLocationX(), getLocationY(), getScale(), getLabel(), getTitle(), getDescription(), getUrl(), getTicket(), getLatitude(), getLongitude(), getPrecision(), getExpiredTime(), getFailTime(), getFailReason(), getMsgId(), getStatus(), getTotalCount(), getFilterCount(), getSendCount(), getErrorCount());
        }

        @Override
        public String toString() {
            return "WxRequest.Body{" +
                    "toUserName='" + toUserName + '\'' +
                    ", encrypt='" + encrypt + '\'' +
                    ", fromUserName='" + fromUserName + '\'' +
                    ", createTime=" + createTime +
                    ", messageType=" + messageType +
                    ", category=" + category +
                    ", eventType=" + eventType +
                    ", buttonType=" + buttonType +
                    ", eventKey='" + eventKey + '\'' +
                    ", scene='" + scene + '\'' +
                    ", menuId='" + menuId + '\'' +
                    ", scanCodeInfo=" + scanCodeInfo +
                    ", sendPicsInfo=" + sendPicsInfo +
                    ", sendLocationInfo=" + sendLocationInfo +
                    ", content='" + content + '\'' +
                    ", picUrl='" + picUrl + '\'' +
                    ", mediaId='" + mediaId + '\'' +
                    ", format='" + format + '\'' +
                    ", recognition='" + recognition + '\'' +
                    ", thumbMediaId='" + thumbMediaId + '\'' +
                    ", locationX=" + locationX +
                    ", locationY=" + locationY +
                    ", scale=" + scale +
                    ", label='" + label + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", url='" + url + '\'' +
                    ", ticket='" + ticket + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", precision=" + precision +
                    ", expiredTime=" + expiredTime +
                    ", failTime=" + failTime +
                    ", failReason='" + failReason + '\'' +
                    ", msgId=" + msgId +
                    ", status='" + status + '\'' +
                    ", totalCount=" + totalCount +
                    ", filterCount=" + filterCount +
                    ", sendCount=" + sendCount +
                    ", errorCount=" + errorCount +
                    '}';
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

            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof ScanCodeInfo)) {
                    return false;
                }
                final ScanCodeInfo other = (ScanCodeInfo) o;
                if (!other.canEqual((Object) this)) {
                    return false;
                }
                final Object this$scanType = this.getScanType();
                final Object other$scanType = other.getScanType();
                if (this$scanType == null ? other$scanType != null : !this$scanType.equals(other$scanType)) {
                    return false;
                }
                final Object this$scanResult = this.getScanResult();
                final Object other$scanResult = other.getScanResult();
                if (this$scanResult == null ? other$scanResult != null : !this$scanResult.equals(other$scanResult)) {
                    return false;
                }
                return true;
            }

            @Override
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

            @Override
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

            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof SendPicsInfo)) {
                    return false;
                }
                final SendPicsInfo other = (SendPicsInfo) o;
                if (!other.canEqual((Object) this)) {
                    return false;
                }
                final Object this$count = this.getCount();
                final Object other$count = other.getCount();
                if (this$count == null ? other$count != null : !this$count.equals(other$count)) {
                    return false;
                }
                final Object this$picList = this.getPicList();
                final Object other$picList = other.getPicList();
                if (this$picList == null ? other$picList != null : !this$picList.equals(other$picList)) {
                    return false;
                }
                return true;
            }

            @Override
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

            @Override
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

                @Override
                public boolean equals(Object o) {
                    if (o == this) {
                        return true;
                    }
                    if (!(o instanceof Item)) {
                        return false;
                    }
                    final Item other = (Item) o;
                    if (!other.canEqual((Object) this)) {
                        return false;
                    }
                    final Object this$picMd5Sum = this.getPicMd5Sum();
                    final Object other$picMd5Sum = other.getPicMd5Sum();
                    if (this$picMd5Sum == null ? other$picMd5Sum != null : !this$picMd5Sum.equals(other$picMd5Sum)) {
                        return false;
                    }
                    return true;
                }

                @Override
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

                @Override
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

            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof SendLocationInfo)) {
                    return false;
                }
                final SendLocationInfo other = (SendLocationInfo) o;
                if (!other.canEqual((Object) this)) {
                    return false;
                }
                final Object this$locationX = this.getLocationX();
                final Object other$locationX = other.getLocationX();
                if (this$locationX == null ? other$locationX != null : !this$locationX.equals(other$locationX)) {
                    return false;
                }
                final Object this$locationY = this.getLocationY();
                final Object other$locationY = other.getLocationY();
                if (this$locationY == null ? other$locationY != null : !this$locationY.equals(other$locationY)) {
                    return false;
                }
                final Object this$scale = this.getScale();
                final Object other$scale = other.getScale();
                if (this$scale == null ? other$scale != null : !this$scale.equals(other$scale)) {
                    return false;
                }
                final Object this$label = this.getLabel();
                final Object other$label = other.getLabel();
                if (this$label == null ? other$label != null : !this$label.equals(other$label)) {
                    return false;
                }
                final Object this$poiname = this.getPoiname();
                final Object other$poiname = other.getPoiname();
                if (this$poiname == null ? other$poiname != null : !this$poiname.equals(other$poiname)) {
                    return false;
                }
                return true;
            }

            @Override
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

            @Override
            public String toString() {
                return "com.mxixm.fastboot.weixin.module.web.WxRequest.WxMessageBody.SendLocationInfo(locationX=" + this.getLocationX() + ", locationY=" + this.getLocationY() + ", scale=" + this.getScale() + ", label=" + this.getLabel() + ", poiname=" + this.getPoiname() + ")";
            }
        }

        /**
         * @param name
         * @return the result
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
