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

package com.mxixm.fastboot.weixin.module.media;

import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * fastboot-weixin  WxMediaStore
 *
 * @author Guangshan
 * @date 2017/10/7 22:43
 * @since 0.2.0.alpha
 */
public interface WxMediaStore {

    enum Type {
        TEMP, MATERIAL, IMAGE
    }

    MediaEntity query(MediaQuery mediaQuery);

    MediaEntity store(MediaEntity mediaEntity);

    Resource storeResource(MediaEntity mediaEntity) throws IOException;

    void delete(String mediaId);
}
