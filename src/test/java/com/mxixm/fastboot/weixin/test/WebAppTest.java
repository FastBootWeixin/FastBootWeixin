/**
 *
 */
package com.mxixm.fastboot.weixin.test;

import com.mxixm.fastboot.weixin.annotation.WxApplication;
import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.annotation.WxButtonMapping;
import com.mxixm.fastboot.weixin.annotation.WxController;
import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaManager;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
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
//@WxApplication(menuAutoCreate = false)
//@WxController
public class WebAppTest {

    @Autowired
    WxMediaManager wxMediaManager;

    @WxButtonMapping(keys = "pages/index/index")
    public void b(WxRequest wxRequest, WxMenu.Button button) {
        System.out.println(button);
    }

    @WxButton(group = WxButton.Group.MIDDLE,
            url = "baidu.com",
            name = "AAA",
            main = true, type = WxButton.Type.MINI_PROGRAM, appId = "12345", pagePath = "/page/index")
    public void test(WxRequest wxRequest) {
        System.out.println(wxRequest);
    }



    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebAppTest.class, args);
    }

    @GetMapping("a")
    public Object a() {
        WxMedia.PageResult result = wxMediaManager.batchGetMedia(WxMedia.Type.IMAGE, 0);
        return result;
    }

}
