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

package com.mxixm.fastboot.weixin.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.Ordered;
import org.springframework.util.StringValueResolver;

/**
 * FastBootWeixin WxContextUtils
 *
 * @author Guangshan
 * @date 2017/8/11 21:03
 * @since 0.1.2
 */
public class WxContextUtils implements BeanFactoryAware, EmbeddedValueResolverAware, Ordered {

    private static BeanFactory beanFactory;

    private static StringValueResolver stringValueResolver;

    /**
     * 解析参数值并执行spel表达式，得到最终结果
     * Spring的@Value也是这样做的
     */
    public static String resolveStringValue(String value) {
        return stringValueResolver.resolveStringValue(value);
    }

    public static <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    /**
     * 本来想用Prepared的，但是发现prepared没有地方发布这个事件，可恶
     * 这个事件时机太靠后，configurableBeanFactory可能很早就要使用了
     *
     */
//    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//        beanFactory = applicationReadyEvent.getApplicationContext().getBeanFactory();
//        expressionContext = new BeanExpressionContext(beanFactory, null);
//    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        WxContextUtils.stringValueResolver = stringValueResolver;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        WxContextUtils.beanFactory = beanFactory;
    }

    /**
     * 在实例方法中设置static的值会被findBugs找出来。。。但是我就是要这样用
     * 这里可以再看看包里的其他工具，还有很多好用的
     * 这里是错误用法，因为这个是用来自定义初始化配置的，而不是设置configurableBeanFactory的
     * @param
     * @throws BeansException
     */
    /*@Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        configurableBeanFactory = applicationContext.getBeanFactory();
        expressionContext = new BeanExpressionContext(configurableBeanFactory, null);
    }*/

}
