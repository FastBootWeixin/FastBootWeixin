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

package com.mxixm.fastboot.weixin.test;

import com.mxixm.fastboot.weixin.exception.WxCryptoException;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import org.springframework.core.ResolvableType;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringBootTest {

    public static void main1(String[] args) throws NoSuchMethodException {
        Method method = WxApp.class.getMethod("cardMessage", String.class);
        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        ResolvableType arrayType = ResolvableType.forArrayComponent(ResolvableType.forClass(WxMessage.class));
        ResolvableType iterableType = ResolvableType.forClassWithGenerics(Iterable.class, WxMessage.class);

        System.out.println(resolvableType);
    }

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void main(String[] args) {
        System.out.println(Arrays.toString(encrypt1("1234")));
        System.out.println(Arrays.toString(encrypt2("1234")));
    }

    private static Object[] encrypt1(String text) throws WxCryptoException {
        byte[] randomBytes = "1234567890098765".getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] appidBytes = "wx19c85a9e7632a5eb".getBytes(CHARSET);
        int length = randomBytes.length +
                textBytes.length + networkBytesOrder.length + appidBytes.length;
        byte[] padBytes = PKCS7Padding.pad(length);
        Object[] objects = Stream.of(randomBytes, textBytes, networkBytesOrder, appidBytes, padBytes)
                .flatMap(a -> Arrays.stream(ObjectUtils.toObjectArray(a)))
                .collect(Collectors.toList())
                .toArray();
        // 获得最终的字节流, 未加密
//        byte[] unencrypted = toPrimitive(objects);
//        try {
//             加密
//            byte[] encrypted = encryptCipher.doFinal(unencrypted);
//             使用BASE64对加密后的字符串进行编码
//            String base64Encrypted = Base64.getEncoder().encodeToString(encrypted);
//            return base64Encrypted;
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            throw new WxCryptoException(WxCryptoException.Code.ENCRYPT_AES_ERROR);
//        }
        return objects;
    }

    private static byte[] encrypt2(String text) throws WxCryptoException {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = "1234567890098765".getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] appidBytes = "wx19c85a9e7632a5eb".getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + appid
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(appidBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();
        return unencrypted;
    }
    static class ByteGroup {

        ArrayList<Byte> byteContainer = new ArrayList<Byte>();

        public byte[] toBytes() {
            byte[] bytes = new byte[byteContainer.size()];
            for (int i = 0; i < byteContainer.size(); i++) {
                bytes[i] = byteContainer.get(i);
            }
            return bytes;
        }

        public ByteGroup addBytes(byte[] bytes) {
            for (byte b : bytes) {
                byteContainer.add(b);
            }
            return this;
        }
        public int size() {
            return byteContainer.size();
        }

    }
    static class PKCS7Encoder {
        static Charset CHARSET = Charset.forName("utf-8");

        static int BLOCK_SIZE = 32;

        /**
         * 获得对明文进行补位填充的字节.
         *
         * @param count 需要进行填充补位操作的明文字节个数
         * @return 补齐用的字节数组
         */
        static byte[] encode(int count) {
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
        static byte[] decode(byte[] decrypted) {
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

    private static byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
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
