/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.annotation;

import com.mxixm.fastboot.weixin.module.event.WxEvent;

import java.lang.annotation.*;

/**
 * 微信请求绑定
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WxAsyncMessage
@WxMapping
public @interface WxEventMapping {

    /**
     * 请求事件的类型
     *
     * @return dummy
     */
    WxEvent.Type type();

    /**
     * 名称
     *
     * @return dummy
     */
    String name() default "";

}
