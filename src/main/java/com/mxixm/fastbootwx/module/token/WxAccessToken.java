package com.mxixm.fastbootwx.module.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * FastBootWeixin  WxAccessToken
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxAccessToken
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:45
 */
@Data
public class WxAccessToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private int expiresIn;

}
