package com.example.myproject.util;

import com.example.myproject.exception.WxAppException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * FastBootWeixin  WxApplicationContextUtils
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxApplicationContextUtils
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/11 21:03
 */
public class WxApplicationContextUtils implements BeanFactoryAware {

    private static ConfigurableBeanFactory configurableBeanFactory;

    private static BeanExpressionContext expressionContext;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            configurableBeanFactory = (ConfigurableBeanFactory) beanFactory;
            expressionContext = new BeanExpressionContext(configurableBeanFactory, null);
        } else {
            // 转成系统异常
            throw new WxAppException("系统初始化异常");
        }
    }

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

}
