package com.example.myproject.module.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * FastBootWeixin  AccessToken
 *
 * @author Guangshan
 * @summary FastBootWeixin  AccessToken
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:45
 */
@Data
public class AccessToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private int expiresIn;

}
