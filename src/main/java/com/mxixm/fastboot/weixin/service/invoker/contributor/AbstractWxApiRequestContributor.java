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

import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiBody;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiForm;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.web.method.support.UriComponentsContributor;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * FastBootWeixin AbstractWxApiRequestContributor
 *
 * @author Guangshan
 * @date 2017/08/10 22:15
 * @since 0.1.2
 */
public abstract class AbstractWxApiRequestContributor<T extends Annotation> implements UriComponentsContributor {

    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    private final Class<T> annotationType;

    protected AbstractWxApiRequestContributor() {
        Type type = this.getClass().getGenericSuperclass();
        annotationType = (Class) ((ParameterizedType) type).getRawType();
    }

    /**
     * 把参数格式化成字符串用于拼接url
     *
     * @param cs
     * @param sourceType
     * @param value
     * @return the result
     */
    protected String formatUriValue(ConversionService cs, TypeDescriptor sourceType, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Enum) {
            // 枚举通过toString取值，conversionService默认从name取值
            return value.toString();
        } else if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        } else {
            return value.toString();
        }
    }

    /**
     * 是否支持这个参数
     *
     * @param parameter
     * @return the result
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 有这两个注解，就不支持
        if (parameter.hasParameterAnnotation(WxApiBody.class) || parameter.hasParameterAnnotation(WxApiForm.class)) {
            return false;
        }
        if (parameter.hasParameterAnnotation(annotationType)) {
            return true;
        } else {
            return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
        }
    }

}
