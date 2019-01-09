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

package com.mxixm.fastboot.weixin.annotation;

import com.mxixm.fastboot.weixin.module.Wx;

import java.lang.annotation.*;

/**
 * FastBootWeixin WxMapping
 * 标记是微信的Mapping，包括WxButton、WxEventMapping、WxMessageMapping
 *
 * @author Guangshan
 * @date 2017/09/21 23:28
 * @since 0.1.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WxResponseBody
public @interface WxMapping {

    String MATCH_ALL_WILDCARD = "*";

    /**
     * 映射类型，MESSAGE，EVENT，BUTTON，SYSTEM
     * @return
     */
    Wx.Category[] category() default {};

    /**
     * 映射名
     * @return
     */
    String name() default "";

}
