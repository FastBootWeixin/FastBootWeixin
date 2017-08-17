package com.example.myproject.module.media;

import org.springframework.beans.factory.InitializingBean;

/**
 * media存储器，提供媒体文件获取，媒体文件保存，转换文件等功能
 * 数据库使用内嵌数据库，经过一天的maven仓库database embedded选型，暂时决定使用MapDB(200k)或者kahaDB(100k)
 */
public class WxMediaStore implements InitializingBean {



    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
