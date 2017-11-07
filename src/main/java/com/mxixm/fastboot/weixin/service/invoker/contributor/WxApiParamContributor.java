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

package com.mxixm.fastboot.weixin.service.invoker.contributor;

import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiParam;
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
 * FastBootWeixin WxApiParamContributor
 *
 * @author Guangshan
 * @date 2017/08/10 22:15
 * @since 0.1.2
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
        } else if (value instanceof Collection) {
            for (Object element : (Collection<?>) value) {
                element = formatUriValue(conversionService, TypeDescriptor.nested(parameter, 1), element);
                builder.queryParam(name, element);
            }
        } else {
            builder.queryParam(name, formatUriValue(conversionService, new TypeDescriptor(parameter), value));
        }
    }


}
