package com.example.myproject.module.menu;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.example.myproject.annotation.WxButton;

@Component
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
