package com.example.myproject.mvc.param;

import com.example.myproject.annotation.WxButton;
import com.example.myproject.module.message.receive.RawWxMessage;
import com.example.myproject.mvc.WxMappingUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditor;
import java.util.Map;

/**
 * Resolves method arguments annotated with @{@link RequestParam}, arguments of
 * type {@link MultipartFile} in conjunction with Spring's {@link MultipartResolver}
 * abstraction, and arguments of type {@code javax.servlet.http.Part} in conjunction
 * with Servlet 3.0 multipart requests. This resolver can also be created in default
 * resolution mode in which simple types (int, long, etc.) not annotated with
 * {@link RequestParam @RequestParam} are also treated as request parameters with
 * the parameter name derived from the argument name.
 *
 * <p>If the method parameter type is {@link Map}, the name specified in the
 * annotation is used to resolve the request parameter String value. The value is
 * then converted to a {@link Map} via type conversion assuming a suitable
 * {@link Converter} or {@link PropertyEditor} has been registered.
 * Or if a request parameter name is not specified the
 * {@link RequestParamMapMethodArgumentResolver} is used instead to provide
 * access to all request parameters in the form of a map.
 *
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved request
 * header values that don't yet match the method parameter type.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 3.1
 * @see RequestParamMapMethodArgumentResolver
 * implements UriComponentsContributor 这个东西可能在写ApiInvoker调用时有用
 */
public class WxButtonArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

	public WxButtonArgumentResolver() {
		super();
	}

	/**
	 * @param beanFactory a bean factory used for resolving  ${...} placeholder
	 * and #{...} SpEL expressions in default values, or {@code null} if default
	 * values are not expected to contain expressions
	 */
	public WxButtonArgumentResolver(ConfigurableBeanFactory beanFactory) {
		super(beanFactory);
	}


	/**
	 * Supports the following:
	 * <ul>
	 * <li>@RequestParam-annotated method arguments.
	 * This excludes {@link Map} params where the annotation doesn't
	 * specify a name.	See {@link RequestParamMapMethodArgumentResolver}
	 * instead for such params.
	 * <li>Arguments of type {@link MultipartFile}
	 * unless annotated with @{@link RequestPart}.
	 * <li>Arguments of type {@code javax.servlet.http.Part}
	 * unless annotated with @{@link RequestPart}.
	 * <li>In default resolution mode, simple type arguments
	 * even if not with @{@link RequestParam}.
	 * </ul>
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// 只有method上有注解WxButton时才支持解析
		if (!AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), WxButton.class)) {
			return false;
		}
		return true;
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		return new RequestParamNamedValueInfo();
	}

	@Override
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
		RawWxMessage rawWxMessage = WxMappingUtils.getRawWxMessageFromRequest(servletRequest);
		// 类型匹配，直接返回
		if (parameter.getParameterType() == RawWxMessage.class) {
			return rawWxMessage;
		}
		return rawWxMessage.getParameterValue(name);
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request)
			throws Exception {

		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
		if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
			if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
				throw new MultipartException("Current request is not a multipart request");
			}
			else {
				throw new MissingServletRequestPartException(name);
			}
		}
		else {
			throw new MissingServletRequestParameterException(name,
					parameter.getNestedParameterType().getSimpleName());
		}
	}

	private static class RequestParamNamedValueInfo extends NamedValueInfo {

		public RequestParamNamedValueInfo() {
			super("", false, ValueConstants.DEFAULT_NONE);
		}

		public RequestParamNamedValueInfo(RequestParam annotation) {
			super(annotation.name(), annotation.required(), annotation.defaultValue());
		}
	}

}
