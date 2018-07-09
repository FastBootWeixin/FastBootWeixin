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

package com.mxixm.fastboot.weixin.module.credential;

/**
 * fastboot-weixin  WxJsTicketPart
 * WxJsTicket组成部分
 *
 * @author Guangshan
 * @date 2018/5/14 22:25
 * @since 0.6.0
 */
public interface WxJsTicketPart {

    int DEFAULT_LENGTH = 16;

    /**
     * 随机字符串
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    String nonce(int length);

    default String nonce() {
        return nonce(DEFAULT_LENGTH);
    }

    /**
     * 时间戳，一般精确到秒，不能超过十位数字
     * @return 时间戳
     */
    long timestamp();

}
