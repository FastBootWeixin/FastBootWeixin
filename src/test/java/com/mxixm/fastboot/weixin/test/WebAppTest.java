/**
 *
 */
package com.mxixm.fastboot.weixin.test;

import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
//@SpringBootApplication
//@RestController
public class WebAppTest {

    @Autowired
    WxMediaManager wxMediaManager;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebAppTest.class, args);
    }

    @GetMapping("a")
    public Object a() {
        WxMedia.PageResult result = wxMediaManager.batchGetMedia(WxMedia.Type.IMAGE, 0);
        return result;
    }

}
