package com.mxixm.fastboot.weixin.test;

import com.mxixm.fastboot.weixin.annotation.*;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.web.WxRequestBody;
import com.mxixm.fastboot.weixin.module.web.session.WxSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;

/**
 * 微信菜单接口
 * 
 */
//@WxController
//@WxApplication(menuAutoCreate = false)
public class WxButtonMappingTest {
	private final Log log = LogFactory.getLog(this.getClass());

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WxButtonMappingTest.class, args);
    }

    @WxButtonMapping(names = "*消息")
    public void left(WxMenu.Button button) {
        System.out.println(button);
    }

}
