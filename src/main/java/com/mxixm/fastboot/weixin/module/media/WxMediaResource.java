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

import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.service.invoker.common.WxBufferingInputMessageWrapper;
import com.mxixm.fastboot.weixin.util.WxContextUtils;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * FastBootWeixin WxMediaResource
 *
 * @author Guangshan
 * @date 2017/8/12 21:05
 * @since 0.1.2
 */
public class WxMediaResource extends AbstractResource {

    private static final Map<String, String> MEDIA_TYPE_FILE_EXT = new HashMap<>();

    static {
        MEDIA_TYPE_FILE_EXT.put("image/png", "png");
        MEDIA_TYPE_FILE_EXT.put("image/jpeg", "jpg");
        MEDIA_TYPE_FILE_EXT.put("image/gif", "gif");
        MEDIA_TYPE_FILE_EXT.put("image/bmp", "bmp");
        MEDIA_TYPE_FILE_EXT.put("audio/amr", "amr");
        MEDIA_TYPE_FILE_EXT.put("audio/x-wav", "wav");
        MEDIA_TYPE_FILE_EXT.put("audio/mpeg", "mp3");
        MEDIA_TYPE_FILE_EXT.put("audio/x-ms-wma", "wma");
        MEDIA_TYPE_FILE_EXT.put("video/mp4", "mp4");
    }

    private static final String DEFAULT_EXT = "jpg";

    private static final String FILENAME_KEY = "filename=";

    private static final String FILENAME_WITH_CHARSET_KEY = "filename*=";

    private HttpHeaders httpHeaders;

    private byte[] body;

    private String description;

    private MediaType contentType;

    private long contentLength;

    private String filename;

    private String mediaId;

    private URL url;

    private File file;

    private boolean isUrlMedia;

    private boolean isFileResource;

    /**
     * 是否真的需要这么多成员变量？
     *
     * @param httpInputMessage
     * @throws IOException
     */
    public WxMediaResource(HttpInputMessage httpInputMessage) throws IOException {
        this.isFileResource = false;
        if (httpInputMessage instanceof WxBufferingInputMessageWrapper) {
            this.body = ((WxBufferingInputMessageWrapper) httpInputMessage).getRawBody();
        } else {
            this.body = StreamUtils.copyToByteArray(httpInputMessage.getBody());
        }
        this.httpHeaders = httpInputMessage.getHeaders();
        this.contentType = httpHeaders.getContentType();
        this.contentLength = httpHeaders.getContentLength();
        // 判断是否是json
        if (!this.httpHeaders.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            this.isUrlMedia = true;
            if (body[0] == '{') {
                this.url = extractURL(body);
                this.filename = extractFilenameFromURL(url);
            } else if (httpHeaders.containsKey(WxWebUtils.X_WX_REQUEST_URL)) {
                this.url = URI.create(httpHeaders.getFirst(WxWebUtils.X_WX_REQUEST_URL)).toURL();
                this.filename = extractFilenameFromURL(url);
            } else {
                this.filename = UUID.randomUUID().toString() + ".jpg";
            }
        } else {
            this.description = this.httpHeaders.getFirst(HttpHeaders.CONTENT_DISPOSITION);
            this.filename = extractFilename(this.description);
        }
    }

    /**
     * 覆盖FileSystemResource，用于兼容mediaManager中从本地文件获取的资源
     * 是否真的需要这么多成员变量？
     *
     * @param file
     */
    public WxMediaResource(File file) {
        this.isFileResource = true;
        this.file = file;
        this.httpHeaders = new HttpHeaders();
        // 判断是否是json
        this.description = "file [" + this.file.getAbsolutePath() + "]";
        this.filename = file.getName();
        // 这里可以考虑使用文件扩展名获取对应的contentType，但是我不太想管，先不管吧
        // 反正获取contentType的地方做过空判断，如果为空的话会走默认的根据文件扩展名获取
        this.contentLength = file.length();
    }

    private URL extractURL(byte[] body) {
        String json = new String(body, StandardCharsets.UTF_8);
        int start = json.indexOf(":\"") + 2;
        int end = json.indexOf("\"", start);
        try {
            return new URL(json.substring(start, end));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * copy from StandardMultipartHttpServletRequest
     *
     * @param disposition
     * @return the result
     */
    private String extractFilename(String disposition) {
        String fileName = extractFilename(disposition, FILENAME_KEY);
        if (fileName == null) {
            fileName = extractFilenameWithCharset(disposition);
        }
        return fileName;
    }


    private String extractFilename(String contentDisposition, String key) {
        if (contentDisposition == null) {
            return null;
        }
        int startIndex = contentDisposition.indexOf(key);
        if (startIndex == -1) {
            return null;
        }
        String filename = contentDisposition.substring(startIndex + key.length());
        if (filename.startsWith("\"")) {
            int endIndex = filename.indexOf("\"", 1);
            if (endIndex != -1) {
                return filename.substring(1, endIndex);
            }
        } else {
            int endIndex = filename.indexOf(";");
            if (endIndex != -1) {
                return filename.substring(0, endIndex);
            }
        }
        return filename;
    }

    private String extractFilenameWithCharset(String contentDisposition) {
        String filename = extractFilename(contentDisposition, FILENAME_WITH_CHARSET_KEY);
        if (filename == null) {
            return null;
        }
        int index = filename.indexOf("'");
        if (index != -1) {
            Charset charset = null;
            try {
                charset = Charset.forName(filename.substring(0, index));
            } catch (IllegalArgumentException ex) {
                // ignore
            }
            filename = filename.substring(index + 1);
            // Skip language information..
            index = filename.indexOf("'");
            if (index != -1) {
                filename = filename.substring(index + 1);
            }
            if (charset != null) {
                filename = new String(filename.getBytes(US_ASCII), charset);
            }
        }
        return filename;
    }

    private String extractFilenameFromURL(URL url) {
        String uri = url.toString();
        int index = uri.lastIndexOf('/') + 1;
        String name = uri.substring(index);
        if (name.contains("?")) {
            name = name.substring(0, name.indexOf("?"));
        }
        String ext = MEDIA_TYPE_FILE_EXT.getOrDefault(this.contentType.toString(), DEFAULT_EXT);
        return name.contains(".") ? name : name + "." + ext;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (isFileResource) {
            return new FileInputStream(this.file);
        }
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public URL getURL() throws IOException {
        if (this.isFileResource) {
            return this.file.toURI().toURL();
        }
        return this.url;
    }

    /**
     * 我的stream用的并不好，所以有空要梳理和观察一下当前的Stream是否会出现内存泄漏
     * 加锁防止多次写入
     * findBugs确实提示了这个问题，先不管吧。。
     */
    @Override
    public File getFile() throws IOException {
        if (isFileResource) {
            return this.file;
        }
        return this.getFile(Wx.Environment.instance().getDefaultMediaPath());
    }

    public synchronized File getFile(String path) throws IOException {
        if (this.file == null) {
            // 拿到临时文件路径
            String pathToUse = StringUtils.applyRelativePath(path, this.filename);
            this.file = new File(pathToUse);
            if (!this.file.exists()) {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            }
            FileCopyUtils.copy(this.getBody(), file);
        }
        return this.file;
    }

    /**
     * This implementation reads the entire InputStream to calculate the
     * content length. Subclasses will almost always be able to provide
     * a more optimal version of this, e.g. checking a File length.
     *
     * @see #getInputStream()
     */
    @Override
    public long contentLength() throws IOException {
        return this.contentLength;
    }

    @Override
    public Resource createRelative(String mediaId) throws IOException {
        if (isFileResource) {
            String pathToUse = StringUtils.applyRelativePath(StringUtils.cleanPath(file.getPath()), mediaId);
            return new WxMediaResource(new File(pathToUse));
        }
        return WxContextUtils.getBean(WxApiService.class).getTempMedia(mediaId);
    }

    /**
     * This implementation always returns {@code null},
     * assuming that this resource type does not have a filename.
     */
    @Override
    public String getFilename() {
        return this.filename;
    }

    /**
     * body只在
     *
     * @return the result
     */
    public byte[] getBody() {
        if (this.body == null && this.file != null) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                this.body = StreamUtils.copyToByteArray(is);
            } catch (Exception e) {
                throw new WxAppException(e);
            }
        }
        return body;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public boolean isUrlMedia() {
        return isUrlMedia;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setURL(URI uri) {
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            // ignore it
        }
        this.mediaId = UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("media_id");
    }

}
