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

package com.mxixm.fastboot.weixin.exception;

/**
 * fastboot-weixin  WxCryptoException
 *
 * @author Guangshan
 * @date 2018/7/8 14:38
 * @since 0.7.0
 */
public class WxCryptoException extends WxAppException {
    private Code code;

    public WxCryptoException(Code code) {
        super(code.message);
        this.code = code;
    }

    public WxCryptoException(Code code, Exception cause) {
        super(code.message, cause);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }

    public enum Code {
        OK("成功"),
        VALIDATE_SIGNATURE_ERROR("签名验证错误"),
        PARSE_XML_ERROR("xml解析失败"),
        COMPUTE_SIGNATURE_ERROR("sha加密生成签名失败"),
        ILLEGAL_AES_KEY("SymmetricKey非法，需要在JDK官网下载JCE无限制权限策略文件，参考readme"),
        VALIDATE_APPID_ERROR("appid校验失败"),
        ENCRYPT_AES_ERROR("aes加密失败"),
        DECRYPT_AES_ERROR("aes解密失败"),
        ILLEGAL_BUFFER("解密后得到的buffer非法"),
        NO_SUCH_ALGORITHM("算法不存在");

        String message;

        Code(String message) {
            this.message = message;
        }

    }
}
