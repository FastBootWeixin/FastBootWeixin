package com.example.myproject.controller.invoker.contributor;

import com.example.myproject.controller.invoker.annotation.WxApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.UriComponentsContributor;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

/**
 * FastBootWeixin  AbstractWxApiRequestContributor
 *
 * @author Guangshan
 * @summary FastBootWeixin  AbstractWxApiRequestContributor
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/10 22:15
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

    /**
     * 是否支持这个参数
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(annotationType)) {
            return true;
        } else {
            return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
        }
    }

}
