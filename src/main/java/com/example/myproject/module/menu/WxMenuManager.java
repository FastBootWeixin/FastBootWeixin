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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WxMenuManager implements ApplicationListener<ApplicationReadyEvent> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    @Autowired
    private ApiInvoker apiInvoker;

    private Map<Button.Group, WxButtonItem> mainButtonLookup = new HashMap<>();

    private MultiValueMap<Button.Group, WxButtonItem> groupButtonLookup = new LinkedMultiValueMap<>();

    private Map<String, WxButtonItem> buttonKeyLookup = new HashMap<>();

    private List<WxButtonItem> buttons = new ArrayList<>();

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
            mainButtonLookup.entrySet().stream().sorted((e1, e2) -> e1.getKey().ordinal() - e2.getKey().ordinal())
                    .forEach(m -> {
                        groupButtonLookup.getOrDefault(m.getKey(), new ArrayList<>()).stream()
                                .sorted((w1, w2) -> w1.getOrder().ordinal() - w2.getOrder().ordinal())
                                .forEach(b -> m.getValue().addSubButton(b));
                        menu.add(m.getValue());
                    });
            try {
                menuJsonCache = new ObjectMapper().writeValueAsString(menu);
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
        String result = apiInvoker.createMenu(this.getMenuJson());
        logger.info(result);
    }

    private static class Menu {
        @JsonProperty("button")
        public List<WxButtonItem> mainButtons = new ArrayList<>();

        public void add(WxButtonItem button) {
            mainButtons.add(button);
        }
    }

}
