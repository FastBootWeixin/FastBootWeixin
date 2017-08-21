package com.mxixm.fastbootwx.annotation;

import com.mxixm.fastbootwx.config.invoker.WxInvokerConfiguration;
import com.mxixm.fastbootwx.config.media.WxMediaConfiguration;
import com.mxixm.fastbootwx.config.message.WxAsyncMessageConfiguration;
import com.mxixm.fastbootwx.config.server.WxBuildinMvcConfiguration;
import com.mxixm.fastbootwx.config.token.WxTokenConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({WxInvokerConfiguration.class, WxAsyncMessageConfiguration.class, WxBuildinMvcConfiguration.class, WxTokenConfiguration.class, WxMediaConfiguration.class})
public @interface EnableWxMvc {
}
