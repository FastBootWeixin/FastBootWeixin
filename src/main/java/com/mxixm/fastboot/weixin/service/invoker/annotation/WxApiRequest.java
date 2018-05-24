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

package com.mxixm.fastboot.weixin.service.invoker.annotation;

import com.mxixm.fastboot.weixin.service.invoker.WxInvokerProxyFactoryBean;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

/**
 * FastBootWeixin WxApiRequest
 * 标记一个类为代理调用类
 *
 * @see WxInvokerProxyFactoryBean
 * @author Guangshan
 * @date 2017/09/21 23:31
 * @since 0.1.2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WxApiRequest {

    /**
     * 要调用的主机地址
     *
     * @return the result
     */
    String host() default ValueConstants.DEFAULT_NONE;

    /**
     * 如果以方法名为属性名，通过SPEL表达式获得对应的地址，则prefix需要设置为参数的前缀
     *
     * @return the result
     */
    String prefix() default ValueConstants.DEFAULT_NONE;

    /**
     * 方法上，如果有path，则优先取path，否则按上面的方式拼接
     *
     * @return the result
     */
    String path() default ValueConstants.DEFAULT_NONE;

    /**
     * 调用方法，判断是什么类型的方法
     *
     * @return the result
     */
    Method method() default Method.GET;

    enum Method {

        GET(HttpMethod.GET),
        JSON(HttpMethod.POST),
        XML(HttpMethod.POST),
        FORM(HttpMethod.POST),
        PUT(HttpMethod.POST),
        PATCH(HttpMethod.PATCH),
        DELETE(HttpMethod.DELETE),
        OPTIONS(HttpMethod.OPTIONS),
        TRACE(HttpMethod.TRACE);

        private HttpMethod httpMethod;

        Method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }
    }


}
