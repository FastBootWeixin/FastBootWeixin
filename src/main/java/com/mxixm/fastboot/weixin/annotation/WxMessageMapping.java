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
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * FastBootWeixin WxMessageMapping
 * 微信消息请求绑定
 * 暂时不想做pattern匹配
 *
 * @author Guangshan
 * @date 2017/09/21 23:28
 * @since 0.1.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WxMapping(category = Wx.Category.MESSAGE)
public @interface WxMessageMapping {

    /**
     * 名称
     *
     * @return name
     */
    @AliasFor(annotation = WxMapping.class)
    String name() default "";

    /**
     * 请求的消息类型
     *
     * @return type
     */
    WxMessage.Type[] type() default {};

    /**
     * 通配符
     * todo 加入括号pathVaribale，根据非通配符长度计算权重。正则与此相同。
     *
     * @return wildcard
     */
    @AliasFor("contents")
    String[] wildcard() default {};

    /**
     * 通配符
     * todo 加入括号pathVaribale，根据非通配符长度计算权重。正则与此相同。
     *
     * @return contents
     */
    @AliasFor("wildcard")
    String[] contents() default {};
    /**
     * 匹配模式，正则表达式，暂时不支持
     *
     * @return pattern
     */

    // String pattern() default "";

}
