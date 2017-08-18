package com.example.myproject.controller.invoker.common;

import com.example.myproject.exception.WxApiException;
import com.example.myproject.module.media.WxMediaResource;
import com.example.myproject.mvc.WxRequestResponseUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.*;
import org.springframework.util.ClassUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.*;

/**
 * Implementation of {@link HttpMessageConverter} that can read/write {@link Resource Resources}
 * and supports byte range requests.
 * 覆盖默认的ResourceHttpMessageConverter，因为默认的支持不完善
 *
 * <p>By default, this converter can read all media types. The Java Activation Framework (JAF) -
 * if available - is used to determine the {@code Content-Type} of written resources.
 * If JAF is not available, {@code application/octet-stream} is used.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Kazuki Shimizu
 * @since 3.0.2
 */
public class WxMediaResourceMessageConverter extends ResourceHttpMessageConverter implements ServletContextAware {

	private ServletContext servletContext;

	/*
	本想支持所有类型的，但是想想没有必要，也不好处理，干脆值覆盖父类的功能
	还有也不直接支持File文件，不想写了
	@Override
	protected boolean supports(Class<?> clazz) {
		return WxRequestResponseUtils.isMutlipart(clazz);
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

	protected void addDefaultHeaders(HttpHeaders headers, Resource t, MediaType contentType) throws IOException {
		// 忽略被选择出来的类型，因为如果有选择出来的类型，会影响getDefaultContentType的获取
		super.addDefaultHeaders(headers, t, null);
		// 如果super真的拿不到contentType时，取传入的contentType
		if (headers.getContentType() == null) {
			headers.setContentType(contentType);
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
