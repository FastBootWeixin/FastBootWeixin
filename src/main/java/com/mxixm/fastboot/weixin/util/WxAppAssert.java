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

package com.mxixm.fastboot.weixin.util;

import com.mxixm.fastboot.weixin.exception.WxAppException;

import java.util.Objects;

/**
 * FastBootWeixin WxAppAssert
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public abstract class WxAppAssert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new WxAppException(message);
        }
    }

    public static void equals(Object left, Object right, String message) {
        if (!Objects.equals(left, right)) {
            throw new WxAppException(message);
        }
    }

}
