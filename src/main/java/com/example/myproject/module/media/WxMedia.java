package com.example.myproject.module.media;

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

        IMAGE, VOICE, VIDEO, THUMB;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

}
