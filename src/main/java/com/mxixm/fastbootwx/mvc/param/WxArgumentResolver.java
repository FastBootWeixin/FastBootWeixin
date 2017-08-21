package com.mxixm.fastbootwx.mvc.param;

import com.mxixm.fastbootwx.annotation.WxButton;
import com.mxixm.fastbootwx.module.WxRequest;
import com.mxixm.fastbootwx.mvc.WxRequestResponseUtils;
import com.mxixm.fastbootwx.mvc.annotation.WxEventMapping;
import com.mxixm.fastbootwx.mvc.annotation.WxMessageMapping;
import com.mxixm.fastbootwx.support.WxUserProvider;
import org.springframework.beans.BeanUtils;
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
 * the parameter value derived from the argument value.
 * <p>
 * <p>If the method parameter type is {@link Map}, the value specified in the
 * annotation is used to resolve the request parameter String value. The value is
 * then converted to a {@link Map} via type conversion assuming a suitable
 * {@link Converter} or {@link PropertyEditor} has been registered.
 * Or if a request parameter value is not specified the
 * {@link RequestParamMapMethodArgumentResolver} is used instead to provide
 * access to all request parameters in the form of a map.
 * <p>
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved request
 * header values that don't yet match the method parameter type.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @see RequestParamMapMethodArgumentResolver
 * implements UriComponentsContributor 这个东西可能在写ApiInvoker调用时有用
 * @since 3.1
 */
public class WxArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    // 是否有更好的方式？有空参看源码
    private WxUserProvider wxUserProvider;

    /**
     * 从谁发的，也不要了，用一个处理
     */
    @Deprecated
    public static final String WX_FROM_USER = "fromUser";

    // 默认同上，另外一个参数名
    public static final String WX_USER = "wxUser";

    /**
     * 发给谁的，不需要了，用ResponseBodyAdvice处理这个
     */
    @Deprecated
    public static final String WX_TO_USER = "toUser";

    public WxArgumentResolver(WxUserProvider wxUserProvider) {
        super();
        this.wxUserProvider = wxUserProvider;
    }

    /**
     * @param beanFactory a bean factory used for resolving  ${...} placeholder
     *                    and #{...} SpEL expressions in default values, or {@code null} if default
     *                    values are not expected to contain expressions
     */
    public WxArgumentResolver(ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
        this.wxUserProvider = beanFactory.getBean(WxUserProvider.class);
    }


    /**
     * Supports the following:
     * <ul>
     * <li>@RequestParam-annotated method arguments.
     * This excludes {@link Map} params where the annotation doesn't
     * specify a value.	See {@link RequestParamMapMethodArgumentResolver}
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
        if (!AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), WxButton.class) &&
                !AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), WxEventMapping.class) &&
                !AnnotatedElementUtils.hasAnnotation(parameter.getMethod(), WxMessageMapping.class)) {
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
        WxRequest wxRequest = WxRequestResponseUtils.getWxRequestFromRequestAttribute(servletRequest);
        // 类型匹配，直接返回
        if (parameter.getParameterType() == WxRequest.class) {
            return wxRequest;
        }
        // 如果可以获取用户则返回用户
        Object user = getUser(parameter, wxRequest);
        if (user != null) {
            return user;
        }
        return wxRequest.getParameterValue(name);
    }

    private Object getUser(MethodParameter parameter, WxRequest wxRequest) {
        // 类型不匹配直接返回
        if (!wxUserProvider.isMatch(parameter.getParameterType())) {
            return null;
        } else if (WX_USER.equals(parameter.getParameterName()) || !BeanUtils.isSimpleProperty(parameter.getParameterType())) {
            // 两个都转换失败时，判断是否是简单属性，如果不是，则尝试转换为用户
            // 因为此时无法得知是要获取to还是from，所以取对于用户来说更需要的from
            return wxUserProvider.getUser(wxRequest.getFromUserName());
        }
        return null;
    }

    /*
        if (WX_TO_USER.equals(parameter.getParameterName())) {
            // 尝试转换toUser
            return wxUserProvider.getToUser(wxRequest.getToUserName());
        } else if (WX_FROM_USER.equals(parameter.getParameterName())) {
            // 尝试转换fromUser
            return wxUserProvider.getFromUser(wxRequest.getFromUserName());
        }
     */

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter, NativeWebRequest request)
            throws Exception {

        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (MultipartResolutionDelegate.isMultipartArgument(parameter)) {
            if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            } else {
                throw new MissingServletRequestPartException(name);
            }
        } else {
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
