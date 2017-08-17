package com.example.myproject.module.media;

import com.example.myproject.controller.invoker.WxApiInvokeSpi;
import com.example.myproject.controller.invoker.common.WxBufferingInputMessageWrapper;
import com.example.myproject.module.Wx;
import com.example.myproject.util.WxApplicationContextUtils;
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

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * FastBootWeixin  WxMediaResource
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMediaResource
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 21:05
 */
public class WxMediaResource extends AbstractResource {

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

    /**
     * 是否真的需要这么多成员变量？
     * @param httpInputMessage
     * @throws IOException
     */
    public WxMediaResource(HttpInputMessage httpInputMessage) throws IOException {
        if (httpInputMessage instanceof WxBufferingInputMessageWrapper) {
            this.body = ((WxBufferingInputMessageWrapper) httpInputMessage).getRawBody();
        } else {
            this.body = StreamUtils.copyToByteArray(httpInputMessage.getBody());
        }
        this.httpHeaders = httpInputMessage.getHeaders();
        // 判断是否是json
        if (!this.httpHeaders.containsKey(HttpHeaders.CONTENT_DISPOSITION) || body[0] == '{') {
            this.isUrlMedia = true;
            this.url = extractURL(body);
        }
        this.httpHeaders = httpInputMessage.getHeaders();
        this.description = this.httpHeaders.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        this.filename = extractFilename(this.description);
        this.contentType = httpHeaders.getContentType();
        this.contentLength = httpHeaders.getContentLength();
    }

    /**
     * 如果返回的是素材地址
     * @param messageBody
     */
    public WxMediaResource(String messageBody) {

    }

    public URL extractURL(byte[] body) {
        String json = new String(body);
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
     * @param disposition
     * @return
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
        }
        else {
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
            }
            catch (IllegalArgumentException ex) {
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

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public URL getURL() throws IOException {
        return this.url;
    }

    /**
     * 我的stream用的并不好，所以有空要梳理和观察一下当前的Stream是否会出现内存泄漏
     * 加锁防止多次写入
     */
    @Override
    public synchronized File getFile() throws IOException {
        if (this.file != null) {
            // 拿到临时文件令
            String pathToUse = StringUtils.applyRelativePath(Wx.Environment.getInstance().getDefaultMediaPath(), this.filename);
            this.file = new File(pathToUse);
            FileCopyUtils.copy(this.getBody(), file);
        }
        return this.file;
    }

    /**
     * This implementation reads the entire InputStream to calculate the
     * content length. Subclasses will almost always be able to provide
     * a more optimal version of this, e.g. checking a File length.
     * @see #getInputStream()
     */
    @Override
    public long contentLength() throws IOException {
        return this.contentLength;
    }

    @Override
    public Resource createRelative(String mediaId) throws IOException {
        return WxApplicationContextUtils.getBean(WxApiInvokeSpi.class).getTempMedia(mediaId);
    }

    /**
     * This implementation always returns {@code null},
     * assuming that this resource type does not have a filename.
     */
    @Override
    public String getFilename() {
        return this.filename;
    }

    public byte[] getBody() {
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
