package com.mxixm.fastboot.weixin.module.message.parameter;

import com.mxixm.fastboot.weixin.module.web.WxRequest;

import java.util.Date;

/**
 * FastBootWeixin  WxRequestMessageParameter
 *
 * @author Guangshan
 * @date 2017/12/3 23:31
 * @since 0.3.4
 */
public class WxRequestMessageParameter implements WxMessageParameter {

    private String requestUrl;

    private String toUser;

    private String fromUser;

    private Date createTime;

    public WxRequestMessageParameter(WxRequest wxRequest) {
        this.requestUrl = wxRequest.getRequestUrl();
        this.toUser = wxRequest.getBody().getFromUserName();
        this.fromUser = wxRequest.getBody().getToUserName();
        this.createTime = new Date();
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

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @Override
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
