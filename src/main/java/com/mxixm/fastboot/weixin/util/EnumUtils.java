package com.mxixm.fastboot.weixin.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EnumUtils {

    private static final Map<Class<?>, Map<String, ?>> SHARED_ENUM_MAP = new ConcurrentHashMap<>();

    private static final Method ENUM_MAP_METHOD = ReflectionUtils.findMethod(Class.class, "enumConstantDirectory");
    static {
        ENUM_MAP_METHOD.setAccessible(true);
    }
    public static <E extends Enum<E>> E valueOf(Class<E> enumClass, String value) {
        Map<String, ?> enumMap = SHARED_ENUM_MAP.get(enumClass);
        if (enumMap == null) {
            enumMap = initEnumMap(enumClass);
            SHARED_ENUM_MAP.put(enumClass, enumMap);
        }
        return (E) enumMap.get(value);
    }

    private static <E extends Enum<E>> Map<String, E> initEnumMap(Class<E> enumClass) {
        return (Map<String, E>) ReflectionUtils.invokeMethod(ENUM_MAP_METHOD, enumClass);
    }

}
