package com.example.myproject.module;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FastBootWeixin 微信常量类
 *
 * @author Guangshan
 * @summary FastBootWeixin  Wx
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/5 21:34
 */
public class Wx {

    /**
     * 个人定义的类目
     */
    public enum Category {
        /**
         * 收到用户消息
         */
        MESSAGE,
        /**
         * 包括按钮事件和用户事件(如关注)
         * 后续可考虑分离按钮事件和用户时间
         */
        EVENT,
        /**
         * 用户按钮事件
         */
        BUTTON,
        /**
         * 系统事件
         */
        SYSTEM
    }


    public static class Environment {

        private static Environment instance = new Environment();

        public static Environment getInstance() {
            return instance;
        }

        private Environment() {
        }

        private String wxUserName;

        public String getWxUserName() {
            return wxUserName;
        }

        public void setWxUserName(String wxUserName) {
            this.wxUserName = wxUserName;
        }
    }

}
