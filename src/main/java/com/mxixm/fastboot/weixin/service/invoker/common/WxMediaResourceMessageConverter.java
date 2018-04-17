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

package com.mxixm.fastboot.weixin.service.invoker.common;

import com.mxixm.fastboot.weixin.exception.WxApiException;
import com.mxixm.fastboot.weixin.module.media.WxMediaResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * FastBootWeixin WxMediaResourceMessageConverter
 * 覆盖默认的ResourceHttpMessageConverter，因为默认的支持不完善
 *
 * @author Guangshan
 * @date 2017/08/23 22:31
 * @since 0.1.2
 */
public class WxMediaResourceMessageConverter extends ResourceHttpMessageConverter implements ServletContextAware {

    private ServletContext servletContext;

    /*
    本想支持所有类型的，但是想想没有必要，也不好处理，干脆只覆盖父类的功能
    还有也不直接支持File文件，不想写了
    Spring5对ResourceHttpMessageConverter做了改造，注意兼容
    @Override
    protected boolean supports(Class<?> clazz) {
        return WxWebUtils.isMutlipart(clazz);
    }*/
    @Override
    protected Resource readInternal(Class<? extends Resource> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        WxMediaResource wxMediaResource = new WxMediaResource(inputMessage);
        if (wxMediaResource.isUrlMedia() && !clazz.isAssignableFrom(WxMediaResource.class)) {
            throw new WxApiException("不支持的返回类型，接口返回了url");
        }
        if (InputStreamResource.class == clazz) {
            return new InputStreamResource(wxMediaResource.getInputStream());
        } else if (clazz.isAssignableFrom(WxMediaResource.class)) {
            return wxMediaResource;
        } else if (clazz.isAssignableFrom(ByteArrayResource.class)) {
            return new ByteArrayResource(wxMediaResource.getBody());
        } else if (clazz.isAssignableFrom(FileSystemResource.class)) {
            return new FileSystemResource(wxMediaResource.getFile());
        }
//		else if (clazz.isAssignableFrom(File.class)) {
//			return wxMediaResource.getFile();
//		}
        throw new WxApiException("不支持的返回类型");
    }

    @Override
    protected MediaType getDefaultContentType(Resource resource) {
        MediaType contentType = null;
        if (resource instanceof WxMediaResource) {
            contentType = ((WxMediaResource) resource).getContentType();
        }
        if (contentType == null && servletContext != null && resource.getFilename() != null) {
            String mimeType = servletContext.getMimeType(resource.getFilename());
            if (StringUtils.hasText(mimeType)) {
                contentType = MediaType.parseMediaType(mimeType);
            }
        }
        if (contentType != null) {
            return contentType;
        }
        return super.getDefaultContentType(resource);
    }

    @Override
    protected void addDefaultHeaders(HttpHeaders headers, Resource t, MediaType contentType) throws IOException {
        // 忽略被选择出来的类型，因为如果有选择出来的类型，会影响getDefaultContentType的获取
        super.addDefaultHeaders(headers, t, null);
        // 如果super真的拿不到contentType时，取传入的contentType
        if (headers.getContentType() == null && contentType != null) {
            headers.setContentType(contentType);
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
