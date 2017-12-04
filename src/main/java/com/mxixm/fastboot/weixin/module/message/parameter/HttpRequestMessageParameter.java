package com.mxixm.fastboot.weixin.module.message.parameter;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * FastBootWeixin  HttpRequestMessageParameter
 *
 * @author Guangshan
 * @date 2017/12/3 23:35
 * @since 0.3.4
 */
public class HttpRequestMessageParameter implements WxMessageParameter {

    private String requestUrl;

    private String toUser;

    private String fromUser;

    private Date createTime;

    public HttpRequestMessageParameter(HttpServletRequest request) {
        if (request != null && request.getScheme() != null) {
            this.requestUrl = request.getRequestURL().toString();
        }
        this.createTime = new Date();
    }

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @Override
    public String getRequestUrl() {
        return requestUrl;
    }

    @Override
    public String getToUser() {
        return toUser;
    }

    @Override
    public String getFromUser() {
        return fromUser;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

}
