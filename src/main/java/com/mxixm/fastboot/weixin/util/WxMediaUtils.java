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

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

/**
 * fastboot-weixin  WxMediaUtils
 *
 * @author Guangshan
 * @date 2017/10/7 14:58
 * @since 0.2.0
 */
public class WxMediaUtils {

    public static String resourcePath(Resource resource) {
        URI uri = null;
        try {
            uri = resource.getURI();
        } catch (IOException e) {
            return resource.getFilename();
        }
        String path = uri.getPath();
        if (File.pathSeparator == "/") {
            return path;
        } else {
            return path.substring(1);
        }
    }

    public static Date resourceModifiedTime(Resource resource) {
        try {
            File file = resource.getFile();
            if (file != null) {
                return new Date(file.lastModified());
            }
        } catch (IOException e) {
            // ignore it;
        }
        return null;
    }

}
