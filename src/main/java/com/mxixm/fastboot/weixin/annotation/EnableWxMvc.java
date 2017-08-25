package com.mxixm.fastboot.weixin.annotation;

import com.mxixm.fastboot.weixin.config.invoker.WxInvokerConfiguration;
import com.mxixm.fastboot.weixin.config.media.WxMediaConfiguration;
import com.mxixm.fastboot.weixin.config.message.WxAsyncMessageConfiguration;
import com.mxixm.fastboot.weixin.config.server.WxBuildinMvcConfiguration;
import com.mxixm.fastboot.weixin.config.token.WxTokenConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({WxInvokerConfiguration.class, WxAsyncMessageConfiguration.class, WxBuildinMvcConfiguration.class, WxTokenConfiguration.class, WxMediaConfiguration.class})
public @interface EnableWxMvc {

    /**
     * 是否自动创建菜单
     * @return
     */
    boolean menuAutoCreate() default true;

}
