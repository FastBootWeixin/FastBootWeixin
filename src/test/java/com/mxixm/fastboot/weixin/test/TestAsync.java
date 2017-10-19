package com.mxixm.fastboot.weixin.test;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TestAsync {

    @Async
    public void async(String a) {
        System.out.printf(a);
    }

}
