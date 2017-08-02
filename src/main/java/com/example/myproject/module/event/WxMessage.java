package com.example.myproject.module.event;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * FastBootWeixin  WxMessage
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMessage
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/2 23:21
 */
@XmlRootElement(name = "xml")
public class WxMessage {

    /**
     * 通用
     * 开发者微信号
     */
    @XmlElement(name = "ToUserName")
    private String toUserName;

    /**
     * 通用
     * 发送方帐号（一个OpenID）
     */
    @XmlElement(name = "FromUserName")
    private String fromUserName;

    /**
     * 通用
     * 消息创建时间 （整型）
     */
    @XmlElement(name = "CreateTime")
    private String createTime;

    /**
     * 通用
     * 消息类型
     */
    @XmlElement(name = "MsgType")
    private String msgType;

    @XmlElement(name = "Event")
    private String event;

    @XmlElement(name = "EventKey")
    private String eventKey;

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
     * 用户消息类型才有
     * 消息id，64位整型
     */
    @XmlElement(name = "MsgId")
    private Long msgId;

}
