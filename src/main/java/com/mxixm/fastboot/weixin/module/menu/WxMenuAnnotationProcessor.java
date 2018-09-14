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

package com.mxixm.fastboot.weixin.module.menu;

import com.mxixm.fastboot.weixin.annotation.WxButton;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * FastBootWeixin WxMenuAnnotationProcessor
 * 不需要了，用WxMappingHandlerMaping代替了
 *
 * @author Guangshan
 * @date 2017/09/21 23:37
 * @since 0.1.2
 */
@Deprecated
public class WxMenuAnnotationProcessor implements BeanPostProcessor {

    @Autowired
    private WxMenuManager wxMenuManager;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        ReflectionUtils.doWithMethods(targetClass, method -> {
            WxButton wxButton = AnnotationUtils.getAnnotation(method, WxButton.class);
            if (wxButton != null) {
                wxMenuManager.addButton(wxButton);
            }
        });
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
