package com.example.myproject.support;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FastBootWeixin  WxUser
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxUser
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/5 22:29
 */
@Data
@AllArgsConstructor
public class WxUser {

    private String fromUserName;

    private String toUserName;

}
