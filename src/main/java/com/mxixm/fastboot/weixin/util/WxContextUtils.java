package com.mxixm.fastboot.weixin.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * FastBootWeixin  WxContextUtils
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxContextUtils
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/11 21:03
 */
public class WxContextUtils implements ApplicationListener<ApplicationReadyEvent>, Ordered {

    private static ConfigurableBeanFactory configurableBeanFactory;

    private static BeanExpressionContext expressionContext;
    /**
     * 解析参数值并执行spel表达式，得到最终结果
     * Spring的@Value也是这样做的
     */
    public static String resolveStringValue(String value) {
        String placeholdersResolved = configurableBeanFactory.resolveEmbeddedValue(value);
        BeanExpressionResolver exprResolver = configurableBeanFactory.getBeanExpressionResolver();
        if (exprResolver == null) {
            return value;
        }
        return exprResolver.evaluate(placeholdersResolved, expressionContext).toString();
    }

    public static <T> T getBean(Class<T> clazz) {
        return configurableBeanFactory.getBean(clazz);
    }

    /**
     * 本来想用Prepared的，但是发现prepared没有地方发布这个事件，可恶
     * @param applicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        configurableBeanFactory = applicationReadyEvent.getApplicationContext().getBeanFactory();
        expressionContext = new BeanExpressionContext(configurableBeanFactory, null);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
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
