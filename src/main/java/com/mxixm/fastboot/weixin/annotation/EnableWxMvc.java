package com.mxixm.fastboot.weixin.annotation;

import com.mxixm.fastboot.weixin.config.WxProperties;
import com.mxixm.fastboot.weixin.config.invoker.WxInvokerConfiguration;
import com.mxixm.fastboot.weixin.config.media.WxMediaConfiguration;
import com.mxixm.fastboot.weixin.config.message.WxAsyncMessageConfiguration;
import com.mxixm.fastboot.weixin.config.server.WxBuildinMvcConfiguration;
import com.mxixm.fastboot.weixin.config.token.WxTokenConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.*;

/**
 * PropertySource注解不能放在ConfigurationProperties注解上，否则这个注解只对当前类有效，不会写入环境中
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@PropertySource("classpath:/wx.properties")
@EnableConfigurationProperties(WxProperties.class)
@Import({WxInvokerConfiguration.class, WxAsyncMessageConfiguration.class, WxBuildinMvcConfiguration.class, WxTokenConfiguration.class, WxMediaConfiguration.class})
public @interface EnableWxMvc {

    /**
     * 是否自动创建菜单
     * @return
     */
    boolean menuAutoCreate() default true;

}
