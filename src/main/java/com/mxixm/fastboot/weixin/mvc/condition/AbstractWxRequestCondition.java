package com.mxixm.fastboot.weixin.mvc.condition;

import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * FastBootWeixin AbstractWxRequestCondition
 * 所有微信请求条件的抽象父类
 *
 * @author Guangshan
 * @date 2018-9-16 10:10:05
 * @since 0.7.0
 */
public abstract class AbstractWxRequestCondition<T extends AbstractWxRequestCondition<T>> extends AbstractRequestCondition<T> implements WxRequestCondition<T> {

    protected final WxRequestCondition.Type type;

    protected AbstractWxRequestCondition(WxRequestCondition.Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    /**
     * 实例方法不能再调用父类构造之前调用，故用static
     * @param content
     * @return Collection
     */
    protected static <U> Collection<U> convertContent(U... content) {
        if (content == null || (content.length == 1 && content[0] == null)) {
            return Collections.unmodifiableCollection(Collections.emptyList());
        }
        return Collections.unmodifiableCollection(Arrays.asList(content));
    }

}
