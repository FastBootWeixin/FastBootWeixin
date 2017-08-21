package com.mxixm.fastbootwx.module.menu;

import com.mxixm.fastbootwx.annotation.WxButton;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 不需要了，用WxMappingHandlerMaping代替了
 */

public class WxMenuAnnotationProcesser implements BeanPostProcessor {

	@Autowired
	private WxMenuManager wxMenuManager;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> targetClass = AopUtils.getTargetClass(bean);
		ReflectionUtils.doWithMethods(targetClass, method -> {
			WxButton wxButton = AnnotationUtils.getAnnotation(method, WxButton.class);
			if (wxButton != null) {
				wxMenuManager.add(wxButton);
			}
		});
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
