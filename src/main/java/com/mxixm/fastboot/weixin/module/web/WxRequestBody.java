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

import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FastBootWeixin WxRequestBody
 * 整理成请求体，是WxRequest.Body的转换
 *
 * @author Guangshan
 * @date 2017/9/2 23:41
 * @since 0.1.2
 */
public class WxRequestBody {

    /**
     * 类型声明写泛型上
     *
     * @param clazz
     * @param <T>
     * @return the result
     */
    public static <T extends WxRequestBody> T of(Class<T> clazz, WxRequest.Body body) {
        WxRequestBody wxRequestBody = BeanUtils.instantiateClass(clazz);
        return (T) wxRequestBody.of(body);
    }

    /**
     * 通用
     * 开发者微信号
     */
    protected String toUserName;

    /**
     * 通用
     * 发送方帐号（一个OpenID）
     */
    protected String fromUserName;

    /**
     * 通用
     * 消息创建时间 （整型）
     */
    protected Date createTime;

    /**
     * 通用
     * 消息类型
     */
    private WxMessage.Type messageType;

    /**
     * 消息转换
     */
    public WxRequestBody of(WxRequest.Body body) {
        this.toUserName = body.getToUserName();
        this.fromUserName = body.getFromUserName();
        this.createTime = body.getCreateTime();
        this.messageType = body.getMessageType();
        return this;
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

    public WxMessage.Type getMessageType() {
        return this.messageType;
    }

    /**
     * button事件的父类
     */
    public static class Button extends WxRequestBody {
        /**
         * event类型有
         * 事件类型
         */
        protected WxEvent.Type eventType;

        /**
         * 事件Key
         * event类型有
         */
        protected String eventKey;

        /**
         * 消息转换
         */
        @Override
        public Button of(WxRequest.Body body) {
            super.of(body);
            this.eventType = body.getEventType();
            this.eventKey = body.getEventKey();
            return this;
        }

        public WxEvent.Type getEventType() {
            return this.eventType;
        }

        public String getEventKey() {
            return this.eventKey;
        }
    }

    /**
     * click按钮的点击事件，其实就是event
     */
    public static class Click extends Button {
        /**
         * 消息转换
         */
        @Override
        public Click of(WxRequest.Body body) {
            super.of(body);
            return this;
        }
    }

    /**
     * View的点击事件
     */
    public static class View extends Button {
        /**
         * event类型为VIEW时才有
         * 指菜单ID，如果是个性化菜单，则可以通过这个字段，知道是哪个规则的菜单被点击了。
         */
        private String menuId;

        @Override
        public View of(WxRequest.Body body) {
            super.of(body);
            this.menuId = body.getMenuId();
            return this;
        }

        public String getMenuId() {
            return this.menuId;
        }
    }

    /**
     * event类型为scancode_push、scancode_waitmsg才有
     */
    public static class ScanCode extends Button {

        /**
         * 扫描类型，一般是qrcode
         */
        private String scanType;

        /**
         * 扫描结果
         */
        private String scanResult;

        @Override
        public ScanCode of(WxRequest.Body body) {
            super.of(body);
            WxRequest.Body.ScanCodeInfo scanCodeInfo = body.getScanCodeInfo();
            if (scanCodeInfo != null) {
                this.scanType = scanCodeInfo.getScanType();
                this.scanResult = scanCodeInfo.getScanResult();
            }
            return this;
        }

        public String getScanType() {
            return this.scanType;
        }

        public String getScanResult() {
            return this.scanResult;
        }
    }

    /**
     * event为pic_sysphoto、pic_photo_or_album、pic_weixin才有
     */
    public static class SendPicture extends Button {

        /**
         * 发送的图片数量
         */
        private Integer count;

        /**
         * 图片MD5列表
         */
        private List<String> picMd5SumList;

        @Override
        public SendPicture of(WxRequest.Body body) {
            super.of(body);
            WxRequest.Body.SendPicsInfo sendPicsInfo = body.getSendPicsInfo();
            if (sendPicsInfo != null) {
                this.count = sendPicsInfo.getCount();
                if (this.count > 0) {
                    this.picMd5SumList = sendPicsInfo.getPicList().stream().map(WxRequest.Body.SendPicsInfo.Item::getPicMd5Sum).collect(Collectors.toList());
                }
            }
            return this;
        }

        public Integer getCount() {
            return this.count;
        }

        public List<String> getPicMd5SumList() {
            return this.picMd5SumList;
        }
    }

    /**
     * event为location_select时才有
     */
    public static class SelectLocation extends Button {

        /**
         * X坐标信息
         */
        private Double locationX;

        /**
         * Y坐标信息
         */
        private Double locationY;

        /**
         * 精度，可理解为精度或者比例尺、越精细的话 scale越高
         */
        private Integer scale;

        /**
         * 地理位置的字符串信息
         */
        private String label;

        /**
         * 朋友圈POI的名字，可能为空
         * POI（Point of Interest）
         */
        private String poiname;

        @Override
        public SelectLocation of(WxRequest.Body body) {
            super.of(body);
            WxRequest.Body.SendLocationInfo sendLocationInfo = body.getSendLocationInfo();
            if (sendLocationInfo != null) {
                this.label = sendLocationInfo.getLabel();
                this.locationX = sendLocationInfo.getLocationX();
                this.locationY = sendLocationInfo.getLocationY();
                this.scale = sendLocationInfo.getScale();
                this.poiname = sendLocationInfo.getPoiname();
            }
            return this;
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
    }

    /**
     * 所有消息类型的父类
     */
    public static class Message extends WxRequestBody {
        /**
         * 通用
         * 消息类型
         */
        protected WxMessage.Type messageType;

        protected Long msgId;

        @Override
        public Message of(WxRequest.Body body) {
            super.of(body);
            this.messageType = body.getMessageType();
            this.msgId = body.getMsgId();
            return this;
        }

        @Override
        public WxMessage.Type getMessageType() {
            return this.messageType;
        }

        public Long getMsgId() {
            return this.msgId;
        }
    }

    /**
     * 文本消息
     */
    public static class Text extends Message {
        /**
         * 消息
         */
        private String content;

        @Override
        public Text of(WxRequest.Body body) {
            super.of(body);
            this.content = body.getContent();
            return this;
        }

        public String getContent() {
            return this.content;
        }
    }

    /**
     * 媒体消息的父类
     */
    public static class MediaMessage extends Message {
        /**
         * 媒体ID
         */
        protected String mediaId;

        @Override
        public MediaMessage of(WxRequest.Body body) {
            super.of(body);
            this.mediaId = body.getMediaId();
            return this;
        }

        public String getMediaId() {
            return this.mediaId;
        }
    }

    /**
     * 图片媒体消息
     */
    public static class Image extends MediaMessage {
        /**
         * image类型的消息有
         * 图片链接（由系统生成）
         */
        private String picUrl;

        @Override
        public Image of(WxRequest.Body body) {
            super.of(body);
            this.picUrl = body.getPicUrl();
            return this;
        }

        public String getPicUrl() {
            return this.picUrl;
        }
    }

    /**
     * 声音媒体消息
     */
    public static class Voice extends MediaMessage {
        /**
         * voice类型的消息有
         * 语音格式，如amr，speex等
         */
        private String format;

        /**
         * voice类型才有
         * 开启语音识别后，附带的识别结果，UTF8编码
         */
        private String recognition;

        @Override
        public Voice of(WxRequest.Body body) {
            super.of(body);
            this.format = body.getFormat();
            this.recognition = body.getRecognition();
            return this;
        }

        public String getFormat() {
            return this.format;
        }

        public String getRecognition() {
            return this.recognition;
        }
    }

    /**
     * 视频消息
     */
    public static class Video extends MediaMessage {
        /**
         * video、shortvideo类型才有
         * 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
         */
        private String thumbMediaId;

        @Override
        public Video of(WxRequest.Body body) {
            super.of(body);
            this.thumbMediaId = body.getThumbMediaId();
            return this;
        }

        public String getThumbMediaId() {
            return this.thumbMediaId;
        }
    }

    /**
     * 地理位置消息
     */
    public static class Location extends Message {
        /**
         * location类型才有
         * 地理位置维度
         */
        private Double locationX;

        /**
         * location类型才有
         * 地理位置经度
         */
        private Double locationY;

        /**
         * location类型才有
         * 地图缩放大小
         */
        private Integer scale;

        /**
         * location类型才有
         * 地理位置信息
         */
        private String label;

        @Override
        public Location of(WxRequest.Body body) {
            super.of(body);
            this.label = body.getLabel();
            this.locationX = body.getLocationX();
            this.locationY = body.getLocationY();
            this.scale = body.getScale();
            return this;
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
    }

    /**
     * 链接消息
     */
    public static class Link extends Message {
        /**
         * link类型才有
         * 消息标题
         */
        private String title;

        /**
         * link类型才有
         * 消息描述
         */
        private String description;

        /**
         * link类型才有
         * 消息链接
         */
        private String url;

        @Override
        public Link of(WxRequest.Body body) {
            super.of(body);
            this.title = body.getTitle();
            this.description = body.getDescription();
            this.url = body.getUrl();
            return this;
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
    }

    /**
     * Event事件的父类
     */
    public static class Event extends WxRequestBody {
        /**
         * event类型有
         * 事件类型
         */
        protected WxEvent.Type eventType;

        @Override
        public Event of(WxRequest.Body body) {
            super.of(body);
            this.eventType = body.getEventType();
            return this;
        }

        public WxEvent.Type getEventType() {
            return this.eventType;
        }
    }

    /**
     * 关注事件，包括扫描关注
     */
    public static class Subscribe extends Event {

        /**
         * 事件KEY值，qrscene_为前缀，后面为二维码的参数值
         */
        protected String eventKey;

        /**
         * 二维码的ticket，可用来换取二维码图片
         */
        private String ticket;

        /**
        */
        private String scene;

        @Override
        public Subscribe of(WxRequest.Body body) {
            super.of(body);
            this.eventKey = body.getEventKey();
            this.scene = body.getScene();
            this.ticket = body.getTicket();
            return this;
        }

        public String getScene() {
            return scene;
        }

        public String getEventKey() {
            return this.eventKey;
        }

        public String getTicket() {
            return this.ticket;
        }
    }

    /**
     * 取关事件
     */
    public static class Unsubscribe extends Event {
        /**
         * 消息转换
         */
        @Override
        public Unsubscribe of(WxRequest.Body body) {
            super.of(body);
            return this;
        }
    }

    /**
     * 模板消息相关事件
     */
    public static class Template extends Event {

        /**
         * 模板消息结果：是否要枚举化？
         */
        private String status;

        private Long msgId;

        /**
         * 消息转换
         */
        @Override
        public Template of(WxRequest.Body body) {
            super.of(body);
            this.status = body.getStatus();
            this.msgId = body.getMsgId();
            return this;
        }

        public String getStatus() {
            return status;
        }

        public Long getMsgId() {
            return msgId;
        }
    }

    /**
     * 扫码事件
     */
    public static class Scan extends Event {

        /**
         * 事件KEY值，对于Scan事件，eventKey就是scene
         */
        protected String eventKey;

        /**
         * 二维码的ticket，可用来换取二维码图片
         */
        private String ticket;

        /**
         * 二维码场景
         */
        private String scene;

        @Override
        public Scan of(WxRequest.Body body) {
            super.of(body);
            this.eventKey = body.getEventKey();
            this.ticket = body.getTicket();
            this.scene = body.getScene();
            return this;
        }

        public String getEventKey() {
            return this.eventKey;
        }

        public String getTicket() {
            return this.ticket;
        }

        public String getScene() {
            return this.scene;
        }
    }

    /**
     * 上报地理位置
     */
    public static class LocationReport extends Event {

        /**
         * 地理位置纬度
         */
        private Double latitude;

        /**
         * 地理位置经度
         */
        private Double longitude;

        /**
         * 地理位置精度
         */
        private Double precision;

        @Override
        public LocationReport of(WxRequest.Body body) {
            super.of(body);
            this.latitude = body.getLatitude();
            this.longitude = body.getLongitude();
            this.precision = body.getPrecision();
            return this;
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
    }

}
