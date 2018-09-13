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

import java.lang.annotation.*;

/**
 * FastBootWeixin WxButton
 * 待支持：参数从变量中取
 *
 * @author Guangshan
 * @date 2017/09/21 23:27
 * @since 0.1.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WxMapping
public @interface WxButtonMapping {

    /**
     * 按钮属于哪一组，默认全部
     */
    WxButton.Group[] group() default {};

    /**
     * 匹配name，支持通配符，默认全部
     *
     * @return the result
     */
    String[] name() default {};

    /**
     * 匹配的菜单类型，默认全部
     */
    WxButton.Type[] type() default {};

    /**
     * 匹配的菜单层级，默认全部(也可以按上面那种写法，用数组的方式)
     */
    WxButton.Level[] level() default {};

    /**
     * 匹配菜单需要，默认全部
     */
    WxButton.Order[] order() default {};

    /**
     * 匹配菜单key，支持通配符，默认全部
     */
    String[] key() default {};

    /**
     * 网页 链接，用户点击菜单可打开链接，不超过1024字节。
     * type为miniprogram时，不支持小程序的老版本客户端将打开本url。
     * 同样用通配符判断
     */
    String[] url() default {};

    /**
     * media_id类型和view_limited类型必须
     * 调用新增永久素材接口返回的合法media_id
     * 这里直接匹配判断
     */
    String[] mediaId() default {};

    /**
     * miniprogram类型必须，小程序的appid（仅认证公众号可配置）
     * 直接匹配判断
     */
    String[] appId() default {};

    /**
     * miniprogram类型必须，小程序的页面路径
     * 通配符判断
     */
    String[] pagePath() default {};

}
