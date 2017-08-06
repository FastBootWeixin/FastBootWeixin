package com.example.myproject.module;

import com.example.myproject.annotation.WxButton;
import com.example.myproject.module.event.WxEvent;
import com.example.myproject.module.message.WxMessage;
import com.example.myproject.module.message.adapters.WxXmlAdapters;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * FastBootWeixin  WxRequest
 * 包含了全部的消息内容，最原始的类型
 * 注意默认情况下如果一个值为null，JAXB不会为其生成XML节点，要想控制这个行为，需要使用其他插件如XmlNullPolicy
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxRequest
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 23:38
 */
@Data
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.NONE)
// XmlAccessorType指定属性访问方式，在Class中为指定任何注解时，可以按照这里定义的方式来获取属性。当使用@XmlElement方式指定后，Type最好指定为None
public class WxRequest {

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
     * event为pic_photo_or_album、pic_weixin才有
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
     * 用户消息类型才有
     * 消息id，64位整型
     */
    @XmlElement(name = "MsgId")
    private Long msgId;

    /**
     * 扫描信息
     */
    @Data
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

    }

    /**
     * 发送的图片信息
     */
    @Data
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

        @Data
        @XmlRootElement(name = "item")
        @XmlAccessorType(XmlAccessType.NONE)
        public static class Item {
            /**
             * 图片的MD5值，开发者若需要，可用于验证接收到图片
             */
            @XmlElement(name = "PicMd5Sum")
            private String picMd5Sum;
        }
    }

    /**
     * 发送的位置信息
     */
    @Data
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
    }

    /**
     *
     * @param paramName
     * @return
     */
    public Object getParameterValue(String paramName) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(this.getClass(), paramName);
        if (propertyDescriptor != null) {
            Object value = null;
            try {
                value = propertyDescriptor.getReadMethod().invoke(this.getClass(), new Object[]{});//调用方法获取方法的返回值
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return value;
        }
        return null;
    }

}
