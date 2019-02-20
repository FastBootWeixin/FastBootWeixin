package com.mxixm.fastboot.weixin.test.backup.conditions;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;

/**
 * FastBootWeixin AbstractWxRequestCondition
 * 所有微信请求条件的抽象父类
 *
 * @author Guangshan
 * @date 2018-9-16 10:10:05
 * @since 0.7.0
 */
public abstract class AbstractWxRequestCondition<T extends AbstractWxRequestCondition<T>> extends AbstractRequestCondition<T> {

    @Override
    public T getMatchingCondition(HttpServletRequest request) {
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
        return getMatchingCondition(wxRequest);
    }

    /**
     * 转换为微信请求
     * @param wxRequest
     * @return AbstractWxRequestCondition
     */
    public abstract T getMatchingCondition(WxRequest wxRequest);

    @Override
    public int compareTo(T other, HttpServletRequest request) {
        WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
        return compareTo(other, wxRequest);
    }

    /**
     * 转换为微信请求
     * @param other 其他条件
     * @param wxRequest 微信请求
     * @return 比较结果
     */
    public abstract int compareTo(T other, WxRequest wxRequest);

}
