package com.example.myproject.controller.invoker.contributor;

import com.example.myproject.controller.invoker.annotation.WxApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/10.
 */
public class WxApiParamContributor extends AbstractWxApiRequestContributor<WxApiParam> {

    @Override
    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        Class<?> paramType = parameter.getNestedParameterType();
        if (Map.class.isAssignableFrom(paramType)) {
            return;
        }
        WxApiParam wxApiParam = parameter.getParameterAnnotation(WxApiParam.class);
        String name = (wxApiParam == null || StringUtils.isEmpty(wxApiParam.name()) ? parameter.getParameterName() : wxApiParam.name());
        if (value == null) {
            if (wxApiParam != null) {
                if (!wxApiParam.required() || !wxApiParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
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


}
