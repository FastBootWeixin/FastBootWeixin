package com.example.myproject.controller.invoker.contributor;

import com.example.myproject.controller.invoker.annotation.WxApiPath;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

public class WxApiPathContributor extends AbstractWxApiRequestContributor<WxApiPath>
		implements UriComponentsContributor {

	@Override
	public void contributeMethodArgument(MethodParameter parameter, Object value,
			UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
		if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
			return;
		}
		WxApiPath wx = parameter.getParameterAnnotation(WxApiPath.class);
		String name = (wx != null && !StringUtils.isEmpty(wx.value()) ? wx.value() : parameter.getParameterName());
		value = formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
		uriVariables.put(name, value);
	}

}
