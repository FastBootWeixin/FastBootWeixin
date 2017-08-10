package com.example.myproject.controller.invoker;

import com.example.myproject.controller.invoker.annotation.WxParam;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/10.
 */
public class WxParamContributor implements UriComponentsContributor {

    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    /**
     * 是否支持这个参数
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(WxParam.class)) {
            if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
                String paramName = parameter.getParameterAnnotation(WxParam.class).name();
                return StringUtils.hasText(paramName);
            } else {
                return true;
            }
        } else {
            return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
        }
    }

    @Override
    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        Class<?> paramType = parameter.getNestedParameterType();
        if (Map.class.isAssignableFrom(paramType)) {
            return;
        }

        WxParam wxParam = parameter.getParameterAnnotation(WxParam.class);
        String name = (wxParam == null || StringUtils.isEmpty(wxParam.name()) ? parameter.getParameterName() : wxParam.name());

        if (value == null) {
            if (wxParam != null) {
                if (!wxParam.required() || !wxParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    return;
                }
            }
            builder.queryParam(name);
        }
        else if (value instanceof Collection) {
            for (Object element : (Collection<?>) value) {
                element = formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element);
                builder.queryParam(name, element);
            }
        }
        else {
            builder.queryParam(name, formatUriValue(conversionService, new TypeDescriptor(parameter), value));
        }
    }

    /**
     * 吧参数格式化成字符串用于拼接url
     * @param cs
     * @param sourceType
     * @param value
     * @return
     */
    protected String formatUriValue(ConversionService cs, TypeDescriptor sourceType, Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof String) {
            return (String) value;
        }
        else if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        else {
            return value.toString();
        }
    }

}
