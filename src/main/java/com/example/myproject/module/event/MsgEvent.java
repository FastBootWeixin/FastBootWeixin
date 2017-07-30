package com.example.myproject.module.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 所有消息都是通过Msg推送的
 */
public interface MsgEvent {

    enum Type {
        @JsonProperty("event")EVENT,
        @JsonProperty("text")TEXT,
        @JsonProperty("image")IMAGE,
        @JsonProperty("voice")VOICE,
        @JsonProperty("video")VIDEO,
        @JsonProperty("shortvideo")SHORT_VIDEO,
        @JsonProperty("location")LOCATION,
        @JsonProperty("link")LINK
    }

}
