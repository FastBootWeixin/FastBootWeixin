package com.mxixm.fastboot.weixin.controller.invoker.contributor;

import com.mxixm.fastboot.weixin.controller.invoker.annotation.WxApiParam;
import com.mxixm.fastboot.weixin.util.WxAppAssert;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Map;

/**
 * FastBootWeixin  WxApiParamContributor
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApiParamContributor
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/10 22:15
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
        WxAppAssert.notNull(name, "请添加编译器的-parameter或者为参数添加注解名称");
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
