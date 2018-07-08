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

import com.mxixm.fastboot.weixin.module.Wx;

import java.util.Random;

/**
 * fastboot-weixin  DefaultWxJsTicketPart
 *
 * @author Guangshan
 * @date 2018/5/14 22:32
 * @since 0.6.0
 */
public class DefaultWxJsTicketPart implements WxJsTicketPart {
    
    /**
     * 考虑随机化是否有问题
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    @Override
    public String nonce(int length) {
        //随机类初始化
        Random random = new Random();
        // StringBuffer类生成，为了拼接字符串
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(Wx.DICTIONARY.length());
            sb.append(Wx.DICTIONARY.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 这里的实现是使用System.currentTimeMillis()做的，但是据说这个方法性能有问题
     * 因为这个方法会进入内核获取当前的时间，故性能不佳。有高性能要求的朋友们，请自行使用其他方式
     * @return
     */
    @Override
    public long timestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
