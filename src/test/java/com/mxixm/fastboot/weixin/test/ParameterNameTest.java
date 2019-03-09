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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.message.WxGroupMessage;
import com.mxixm.fastboot.weixin.util.WxUrlUtils;

public class ParameterNameTest {

    /**
     * 换任何类库都不支持接口的参数名获取，因为class文件中根本就没有这个信息
     * 其实根本原因是因为类中，参数其实是作为localVariable出现的，而localVariable都有一个名字，所以才可以获取参数名
     * 而接口根本就不需要调用，当然没有localVariable。
     *
     * @param args
     * @throws NoSuchMethodException
     */
    public static void main(String[] args) throws NoSuchMethodException, JsonProcessingException {
//        Paranamer paranamer = new CachingParanamer();
//        Method method = WxApiService.class.getMethod("storeFileToTempMedia", File.class);
//        String[] s = paranamer.lookupParameterNames(method);
//        System.out.println(s);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(WxGroupMessage.mpNewsBuilder().mediaId("aa").toGroup("gs", "bb").build()));
        Wx.Environment.instance().setCallbackUrl("http://mxixm.com");
        System.out.println(WxUrlUtils.absoluteUrl("a"));
        System.out.println(WxUrlUtils.absoluteUrl("a.b"));
        System.out.println(WxUrlUtils.absoluteUrl("/a"));
        System.out.println(WxUrlUtils.absoluteUrl("/a.b"));
        System.out.println(WxUrlUtils.absoluteUrl("baidu.com"));
        System.out.println(WxUrlUtils.absoluteUrl("//baidu.com"));
        System.out.println(WxUrlUtils.absoluteUrl("https://baidu.com"));
        System.out.println(WxUrlUtils.absoluteUrl("baidu/h.c"));
    }

}
