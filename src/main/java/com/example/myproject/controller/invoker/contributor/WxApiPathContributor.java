package com.example.myproject.controller.invoker.contributor;

import com.example.myproject.controller.invoker.annotation.WxApiPath;
import com.example.myproject.util.WxAppAssert;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.util.UriComponentsBuilder;

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
		WxAppAssert.notNull(name, "请添加编译器的-parameter或者为参数添加注解名称");
		value = formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
		uriVariables.put(name, value);
	}

}
