/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * fastboot-weixin  WxEncryptMessage
 *
 * @author Guangshan
 * @date 2018/7/10 23:46
 * @since 0.7.2
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.NONE)
public class WxEncryptMessage {

    /**
     * 加密消息体
     */
    @XmlElement(name = "Encrypt", required = true)
    private String encrypt;

    @XmlElement(name = "MsgSignature", required = true)
    private String messageSignature;

    @XmlElement(name = "TimeStamp", required = true)
    private Long timestamp;

    @XmlElement(name = "Nonce", required = true)
    private String nonce;

    public WxEncryptMessage(String encrypt, String messageSignature, Long timestamp, String nonce) {
        this.encrypt = encrypt;
        this.messageSignature = messageSignature;
        this.timestamp = timestamp;
        this.nonce = nonce;
    }

    public WxEncryptMessage() {

    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    public String getMessageSignature() {
        return messageSignature;
    }

    public void setMessageSignature(String messageSignature) {
        this.messageSignature = messageSignature;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
