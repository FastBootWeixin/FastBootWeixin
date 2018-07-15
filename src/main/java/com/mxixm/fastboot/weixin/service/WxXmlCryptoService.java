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
/**
 * 针对org.apache.commons.codec.binary.Base64，
 * 需要导入架包commons-codec-1.9（或commons-codec-1.8等其他版本）
 * 官方下载地址：http://commons.apache.org/proper/commons-codec/download_codec.cgi
 */
package com.mxixm.fastboot.weixin.service;

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.exception.WxCryptoException;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.message.WxEncryptMessage;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.CryptUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 提供接收和推送给公众平台消息的加解密接口(UTF8编码的字符串).
 * <ol>
 * <li>第三方回复加密消息给公众平台</li>
 * <li>第三方收到公众平台发送的消息，验证消息的安全性，并对消息进行解密。</li>
 * </ol>
 * 说明：异常java.security.InvalidKeyException:illegal Key Size的解决方案
 * <ol>
 * <li>在官方网站下载JCE无限制权限策略文件（JDK7的下载地址：
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html</li>
 * <li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li>
 * <li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * <li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li>
 * </ol>
 * <p>
 * fastboot-weixin  WxXmlCryptoService
 *
 * @author Guangshan
 * @date 2018-7-11 00:07:34
 * @since 0.6.2
 */
public class WxXmlCryptoService implements InitializingBean {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private static Charset CHARSET = StandardCharsets.UTF_8;

    private final WxProperties wxProperties;

    private byte[] aesKey;

    private String token;

    private String appId;

    private Cipher encryptCipher;

    private Cipher decryptCipher;

    public WxXmlCryptoService(WxProperties wxProperties) {
        this.wxProperties = wxProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.token = wxProperties.getToken();
        this.appId = wxProperties.getAppId();
        if (!wxProperties.isEncrypt() || StringUtils.isEmpty(wxProperties.getEncodingAesKey())) {
            return;
        }
        this.aesKey = Base64.getDecoder().decode(wxProperties.getEncodingAesKey() + "=");
        try {
            SecretKeySpec keySpec = new SecretKeySpec(this.aesKey, "AES");

            encryptCipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec ive = new IvParameterSpec(aesKey, 0, 16);
            encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, ive);

            // 设置解密模式为AES的CBC模式
            decryptCipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec ivd = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, ivd);
        } catch (InvalidKeyException e) {
            throw new WxCryptoException(WxCryptoException.Code.ILLEGAL_AES_KEY, e);
        }
    }

    /**
     * 将公众平台回复用户的消息加密打包.
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成xml格式</li>
     * </ol>
     *
     * @param wxRequest 微信请求
     * @param body      需要加密的数据
     * @return 包含加密内容的加密消息实体
     * @throws WxCryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public WxEncryptMessage encrypt(WxRequest wxRequest, String body) throws WxCryptoException {
        // 加密
        String encrypted = encrypt(body);
        String rawString = Stream.of(token, encrypted, wxRequest.getTimestamp().toString(),
                wxRequest.getNonce()).sorted().collect(Collectors.joining());
        String signature = CryptUtils.encryptSHA1(rawString);
        WxEncryptMessage wxEncryptMessage = new WxEncryptMessage(
                encrypted, signature, wxRequest.getTimestamp(), wxRequest.getNonce());
        return wxEncryptMessage;
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取xml中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param wxRequest 微信请求
     * @param body      密文，对应POST请求的数据
     * @return 解密后的原文
     * @throws WxCryptoException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decrypt(WxRequest wxRequest, String body)
            throws WxCryptoException {
        // 密钥，公众账号的app secret
        String rawString = Stream.of(token, wxRequest.getTimestamp().toString(), wxRequest.getNonce(), body).sorted().collect(Collectors.joining());
        // 验证安全签名
        String signature = CryptUtils.encryptSHA1(rawString);
        // 和URL中的签名比较是否相等
        if (!signature.equals(wxRequest.getMessageSignature())) {
            throw new WxCryptoException(WxCryptoException.Code.VALIDATE_SIGNATURE_ERROR);
        }
        // 解密
        return decrypt(body);
    }


    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     * @throws WxCryptoException aes加密失败
     */
    private String encrypt(String text) throws WxCryptoException {
        byte[] randomBytes = getRandomString().getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] appidBytes = appId.getBytes(CHARSET);
        int length = randomBytes.length +
                textBytes.length + networkBytesOrder.length + appidBytes.length;
        byte[] padBytes = PKCS7Padding.pad(length);
        Object[] objects = Stream.of(randomBytes, networkBytesOrder, textBytes, appidBytes, padBytes)
                .flatMap(a -> Arrays.stream(ObjectUtils.toObjectArray(a)))
                .collect(Collectors.toList())
                .toArray();
        // 获得最终的字节流, 未加密
        byte[] unencrypted = toPrimitive(objects);
        try {
            // 加密
            byte[] encrypted = encryptCipher.doFinal(unencrypted);
            // 使用BASE64对加密后的字符串进行编码
            String base64Encrypted = Base64.getEncoder().encodeToString(encrypted);
            return base64Encrypted;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WxCryptoException(WxCryptoException.Code.ENCRYPT_AES_ERROR);
        }
    }

    /**
     * 对密文进行解密.
     *
     * @param text 需要解密的密文
     * @return 解密得到的明文
     * @throws WxCryptoException aes解密失败
     */
    private String decrypt(String text) throws WxCryptoException {
        byte[] original;
        try {
            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.getDecoder().decode(text);
            // 解密
            original = decryptCipher.doFinal(encrypted);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WxCryptoException(WxCryptoException.Code.DECRYPT_AES_ERROR);
        }

        String xmlContent, fromAppid;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Padding.unpad(original);
            // 分离16位随机字符串,网络字节序和AppId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int xmlLength = recoverNetworkBytesOrder(networkOrder);
            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            fromAppid = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new WxCryptoException(WxCryptoException.Code.ILLEGAL_BUFFER);
        }
        // appid不相同的情况
        if (!fromAppid.equals(appId)) {
            throw new WxCryptoException(WxCryptoException.Code.VALIDATE_APPID_ERROR);
        }
        return xmlContent;
    }

    /**
     * 生成4个字节的网络字节序
     *
     * @return 字节数组
     */
    private byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    /**
     * 还原4个字节的网络字节序
     *
     * @return 数字
     */
    private int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    private static final int DEFAULT_LENGTH = 16;

    /**
     * 随机生成16位字符串
     *
     * @return 随机串
     */
    private String getRandomString() {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int number = random.nextInt(Wx.DICTIONARY.length());
            sb.append(Wx.DICTIONARY.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 数组转换，不考虑特殊情况
     *
     * @param array
     * @return
     */
    private byte[] toPrimitive(Object[] array) {
        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            Byte b = (Byte) array[i];
            result[i] = b;
        }
        return result;
    }

    static class PKCS7Padding {

        static int BLOCK_SIZE = 32;

        /**
         * 获得对明文进行补位填充的字节.
         *
         * @param count 需要进行填充补位操作的明文字节个数
         * @return 补齐用的字节数组
         */
        static byte[] pad(int count) {
            // 计算需要填充的位数
            int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
            if (amountToPad == 0) {
                amountToPad = BLOCK_SIZE;
            }
            // 获得补位所用的字符
            char padChr = chr(amountToPad);
            String tmp = new String();
            for (int index = 0; index < amountToPad; index++) {
                tmp += padChr;
            }
            return tmp.getBytes(CHARSET);
        }

        /**
         * 删除解密后明文的补位字符
         *
         * @param decrypted 解密后的明文
         * @return 删除补位字符后的明文
         */
        static byte[] unpad(byte[] decrypted) {
            int pad = (int) decrypted[decrypted.length - 1];
            if (pad < 1 || pad > 32) {
                pad = 0;
            }
            return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
        }

        /**
         * 将数字转化成ASCII码对应的字符，用于对明文进行补码
         *
         * @param a 需要转化的数字
         * @return 转化得到的字符
         */
        static char chr(int a) {
            byte target = (byte) (a & 0xFF);
            return (char) target;
        }

    }

}