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

import org.springframework.beans.BeanInstantiationException;
import org.springframework.util.Assert;

/**
 * fastboot-weixin  WxBeanUtils
 * Spring5把BeanUtils.instantiate弃用了，故用这个代替。
 * 还有一种代替方法，使用BeanUtils.instantiateClass代替
 * 暂时使用上面方案代替
 *
 * @author Guangshan
 * @date 2018-4-11 14:43:46
 * @since 0.5.0
 */
public class WxBeanUtils {

    /**
     * Convenience method to instantiate a class using its no-arg constructor.
     * deprecated as of Spring 5.0, following the deprecation of
     * @param clazz class to instantiate
     * @return the new instance
     * @throws BeanInstantiationException if the bean cannot be instantiated
     * {@link Class#newInstance()} in JDK 9
     * @see Class#newInstance()
     */
    public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return clazz.newInstance();
        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
        }
    }

}
