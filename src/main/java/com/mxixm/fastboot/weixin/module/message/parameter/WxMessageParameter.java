package com.mxixm.fastboot.weixin.module.message.parameter;

import java.util.Date;

/**
 * FastBootWeixin  WxMessageParameter
 * 微信消息参数接口，用于包装WxRequest和HttpServletRequest
 * 主要是为了兼容手动发送消息和返回消息时的自动发送
 *
 * @author Guangshan
 * @date 2017/12/3 23:23
 * @since 0.3.4
 */
public interface WxMessageParameter {

    /**
     * 获取请求的地址，可以为空
     * @return requestUrl
     */
    String getRequestUrl();

    /**
     * 发送给谁
     * @return 发送对象的openId
     */
    String getToUser();

    /**
     * 从哪里发来的
     * @return 发送人的openId
     */
    String getFromUser();

    /**
     * 获取创建时间
     * @return 消息创建时间
     */
    Date getCreateTime();

    /**
     * 设置requestUrl
     * @param requestUrl
     */
    void setRequestUrl(String requestUrl);

    /**
     * 设置toUser
     * @param toUser
     */
    void setToUser(String toUser);

    /**
     * 设置fromUser
     * @param fromUser
     */
    void setFromUser(String fromUser);

    /**
     * 设置创建时间
     * @param createTime
     */
    void setCreateTime(Date createTime);


}
