/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.module.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.exception.WxApiResultException;
import com.mxixm.fastboot.weixin.service.WxApiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * FastBootWeixin WxMenuManager
 *
 * @author Guangshan
 * @date 2017/09/21 23:39
 * @since 0.1.2
 */
public class WxMenuManager implements EnvironmentAware, ApplicationListener<ApplicationReadyEvent> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private WxApiService wxApiService;

    private Map<WxButton.Group, WxButtonItem> mainButtonLookup = new HashMap<>();

    private MultiValueMap<WxButton.Group, WxButtonItem> groupButtonLookup = new LinkedMultiValueMap<>();

    private List<WxButtonItem> buttons = new ArrayList<>();

    private WxButtonEventKeyStrategy wxButtonEventKeyStrategy;

    private WxMenu wxMenu;

    private boolean autoCreate;

    private Environment environment;

    public WxMenuManager(WxApiService wxApiService, WxButtonEventKeyStrategy wxButtonEventKeyStrategy, boolean autoCreate) {
        this.wxApiService = wxApiService;
        this.wxButtonEventKeyStrategy = wxButtonEventKeyStrategy;
        this.autoCreate = autoCreate;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public WxButtonItem add(WxButton wxButton) {
        WxButtonItem buttonItem = WxButtonItem.builder()
                .setGroup(wxButton.group())
                .setType(wxButton.type())
                .setMain(wxButton.main())
                .setOrder(wxButton.order())
                .setKey(wxButtonEventKeyStrategy.getEventKey(wxButton))
                .setMediaId(this.environment.resolvePlaceholders(wxButton.mediaId()))
                .setName(this.environment.resolvePlaceholders(wxButton.name()))
                .setAppId(this.environment.resolvePlaceholders(wxButton.appId()))
                .setPagePath(this.environment.resolvePlaceholders(wxButton.pagePath()))
                .setUrl(this.environment.resolvePlaceholders(wxButton.url()))
                .build();
        if (buttonItem.isMain()) {
            Assert.isNull(mainButtonLookup.get(buttonItem.getGroup()), String.format("已经存在该分组的主菜单，分组是%s", buttonItem.getGroup()));
            mainButtonLookup.put(buttonItem.getGroup(), buttonItem);
        } else {
            // 可以校验不要超过五个，或者忽略最后的
            groupButtonLookup.add(buttonItem.getGroup(), buttonItem);
        }
        buttons.add(buttonItem);
        return buttonItem;
    }

    public WxMenu getMenu() {
        if (wxMenu == null) {
            wxMenu = new WxMenu();
            mainButtonLookup.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                    .forEach(m -> {
                        groupButtonLookup.getOrDefault(m.getKey(), new ArrayList<>()).stream()
                                .sorted(Comparator.comparingInt(w -> w.getOrder().ordinal()))
                                .forEach(b -> m.getValue().addSubButton(b));
                        wxMenu.add(m.getValue());
                    });
        }
        return wxMenu;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (!autoCreate) {
            return;
        }
        WxMenus oldWxMenu = null;
        try {
            oldWxMenu = wxApiService.getMenu();
        } catch (WxApiResultException e) {
            // 如果不是菜单不存在，则继续抛出，否则执行创建菜单操作
            if (e.getResultCode() != WxApiResultException.WxApiResultCode.NOT_FOUND_MENU_DATA) {
                throw e;
            }
        }
        WxMenu newWxMenu = this.getMenu();
        // WxMenus oldWxMenus = objectMapper.readValue(oldMenuJson, WxMenus.class);
        if (oldWxMenu == null || isMenuChanged(oldWxMenu)) {
            String result = wxApiService.createMenu(newWxMenu);
            logger.info("==============================================================");
            logger.info("            执行创建菜单操作       ");
            logger.info("            操作结果：" + result);
            logger.info("            新的菜单json为：" + newWxMenu);
            logger.info("==============================================================");
        } else {
            logger.info("==============================================================");
            logger.info("            菜单未发生变化             ");
            logger.info("            当前菜单json为：" + oldWxMenu);
            logger.info("==============================================================");
        }
    }

    private boolean isMenuChanged(WxMenus wxMenus) {
        return !this.wxMenu.equals(wxMenus.wxMenu);
    }

    public static class WxMenu {
        @JsonProperty("button")
        public List<WxButtonItem> mainButtons = new ArrayList<>();

        public void add(WxButtonItem button) {
            mainButtons.add(button);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof WxMenu)) {
                return false;
            }
            final WxMenu other = (WxMenu) o;
            if (!other.canEqual(this)) {
                return false;
            }
            final Object this$mainButtons = this.mainButtons;
            final Object other$mainButtons = other.mainButtons;
            if (this$mainButtons == null ? other$mainButtons != null : !this$mainButtons.equals(other$mainButtons)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $mainButtons = this.mainButtons;
            result = result * PRIME + ($mainButtons == null ? 43 : $mainButtons.hashCode());
            return result;
        }

        protected boolean canEqual(Object other) {
            return other instanceof WxMenu;
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.menu.WxMenuManager.WxMenu(mainButtons=" + this.mainButtons + ")";
        }
    }

    public static class WxMenus {

        @JsonProperty("menu")
        public WxMenu wxMenu;

        @JsonProperty("conditionalmenu")
        public List<WxMenu> conditionalWxMenu;

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.menu.WxMenuManager.WxMenus(wxMenu=" + this.wxMenu + ", conditionalWxMenu=" + this.conditionalWxMenu + ")";
        }
    }
}
