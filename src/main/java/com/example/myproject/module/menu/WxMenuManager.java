package com.example.myproject.module.menu;

import java.lang.invoke.MethodHandles;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.example.myproject.annotation.WxButton;
import com.example.myproject.module.WxButtonItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WxMenuManager {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private Map<Button.Group, WxButtonItem> mainButtonLookup = new HashMap<>();

    private MultiValueMap<Button.Group, WxButtonItem> groupButtonLookup = new LinkedMultiValueMap<>();

    private Map<String, WxButtonItem> buttonKeyLookup = new HashMap<>();

    private List<WxButtonItem> buttons = new ArrayList<>();

    private String menuJsonCache;

    private static WxMenuManager instance = new WxMenuManager();

    private WxMenuManager() {
    }

    public static WxMenuManager getInstance() {
        return instance;
    }

    public void add(WxButton button) {
        WxButtonItem buttonItem = WxButtonItem.create()
                .setGroup(button.group())
                .setType(button.type())
                .setMain(button.main())
                .setKey(button.key())
                .setMediaId(button.mediaId())
                .setName(button.name())
                .setUrl(button.url()).build();
        if (button.main()) {
            Assert.isNull(mainButtonLookup.get(button.group()), String.format("已经存在该分组的主菜单，分组是%s", button.group()));
            mainButtonLookup.put(button.group(), buttonItem);
        } else {
            groupButtonLookup.add(button.group(), buttonItem);
        }
        if (!StringUtils.isEmpty(button.key())) {
            buttonKeyLookup.put(button.key(), buttonItem);
        }
        buttons.add(buttonItem);
    }

    //有空了改成lambda表达式，先用老循环
    public String getMenuJson() {
        if (menuJsonCache == null) {
            for (WxButtonItem button : buttons) {
                if (!button.isMain()) {
                    WxButtonItem parent = mainButtonLookup.get(button.getGroup());
                    if (parent == null) {
                        logger.warn(parent.toString() + "没有对应的一级菜单");
                    } else {
                        parent.addSubButton(button);
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("button", mainButtonLookup.values());
            try {
                menuJsonCache = new ObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                // TODO: 2017/7/25 加入自己的异常体系
                throw new RuntimeException("todo");
            }
        }
        return menuJsonCache;
    }

}
