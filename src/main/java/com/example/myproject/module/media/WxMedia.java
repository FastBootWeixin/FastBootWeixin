package com.example.myproject.module.media;

import com.example.myproject.module.message.adapters.WxJsonAdapters;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

/**
 * FastBootWeixin  WxMedia
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxMedia
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 21:05
 */
public class WxMedia {

    public enum Type {

        @JsonProperty("image")
        IMAGE,
        @JsonProperty("voice")
        VOICE,
        @JsonProperty("video")
        VIDEO,
        @JsonProperty("thumb")
        THUMB;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @JsonProperty("type")
    private Type type;

    @JsonProperty("media_id")
    private String mediaId;

    @JsonDeserialize(converter = WxJsonAdapters.WxDateConverter.class)
    @JsonProperty("created_at")
    private Date createdAt;
}
