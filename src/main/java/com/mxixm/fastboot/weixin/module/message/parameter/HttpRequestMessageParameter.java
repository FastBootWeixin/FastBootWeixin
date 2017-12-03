package com.mxixm.fastboot.weixin.module.message.parameter;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import org.springframework.web.bind.annotation.RequestAttribute;
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

    public HttpRequestMessageParameter(RequestAttributes requestAttributes) {
        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request.getScheme() != null) {
                this.requestUrl = request.getRequestURL().toString();
            }
        }
        this.createTime = new Date();
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
