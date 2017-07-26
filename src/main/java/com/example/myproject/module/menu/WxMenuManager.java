package com.example.myproject.module.menu;

import com.example.myproject.annotation.WxButton;
import com.example.myproject.config.ApiInvoker.ApiInvoker;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WxMenuManager implements ApplicationListener<ApplicationReadyEvent> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    @Autowired
    private ApiInvoker apiInvoker;

    private Map<Button.Group, WxButtonItem> mainButtonLookup = new HashMap<>();

    private MultiValueMap<Button.Group, WxButtonItem> groupButtonLookup = new LinkedMultiValueMap<>();

    private Map<String, WxButtonItem> buttonKeyLookup = new HashMap<>();

    private List<WxButtonItem> buttons = new ArrayList<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    private String menuJsonCache;

    private Menu menu;

    public void add(WxButton button) {
        WxButtonItem buttonItem = WxButtonItem.create()
                .setGroup(button.group())
                .setType(button.type())
                .setMain(button.main())
                .setOrder(button.order())
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
        if (menu == null) {
            menu = new Menu();
            mainButtonLookup.entrySet().stream().sorted(Comparator.comparingInt(e2 -> e2.getKey().ordinal()))
                    .forEach(m -> {
                        groupButtonLookup.getOrDefault(m.getKey(), new ArrayList<>()).stream()
                                .sorted(Comparator.comparingInt(w -> w.getOrder().ordinal()))
                                .forEach(b -> m.getValue().addSubButton(b));
                        menu.add(m.getValue());
                    });
            try {
                menuJsonCache = objectMapper.writeValueAsString(menu);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                // TODO: 2017/7/25 加入自己的异常体系
                throw new RuntimeException("todo");
            }
        }
        return menuJsonCache;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String oldMenuJson = apiInvoker.getMenu();
        String newMenuJson = this.getMenuJson();
        try {
            Menus oldMenus = objectMapper.readValue(oldMenuJson, Menus.class);
            if (isMenuChanged(oldMenus)) {
                String result = apiInvoker.createMenu(newMenuJson);
                logger.info("==============================================================");
                logger.info("            执行创建菜单操作       ");
                logger.info("            操作结果：" + result);
                logger.info("            新的菜单json为：" + newMenuJson);
                logger.info("==============================================================");
            } else {
                logger.info("==============================================================");
                logger.info("            菜单未发生变化             ");
                logger.info("            当前菜单json为：" + oldMenuJson);
                logger.info("==============================================================");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean isMenuChanged(Menus menus) {
        return !this.menu.equals(menus.menu);
    }

    private static class Menu {
        @JsonProperty("button")
        public List<WxButtonItem> mainButtons = new ArrayList<>();

        public void add(WxButtonItem button) {
            mainButtons.add(button);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Menu)) return false;
            Menu that = (Menu)o;
            return that.mainButtons.containsAll(this.mainButtons) && this.mainButtons.containsAll(that.mainButtons);
        }
    }

    private static class Menus {

        @JsonProperty("menu")
        public Menu menu;

        @JsonProperty("conditionalmenu")
        public List<Menu> conditionalMenu;
    }

}
