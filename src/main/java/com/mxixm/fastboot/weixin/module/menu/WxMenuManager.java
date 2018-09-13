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

import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.exception.WxApiResultException;
import com.mxixm.fastboot.weixin.exception.WxAppException;
import com.mxixm.fastboot.weixin.service.WxApiService;
import com.mxixm.fastboot.weixin.util.WxMenuUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.*;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * FastBootWeixin WxMenuManager
 *
 * @author Guangshan
 * @date 2017/09/21 23:39
 * @since 0.1.2
 */
public class WxMenuManager implements EmbeddedValueResolverAware, ApplicationListener<ApplicationReadyEvent> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private WxApiService wxApiService;

    private Map<ButtonKey, WxMenu.Button> keyButtonLookup = new HashMap<>();

    private Map<WxButton.Group, WxMenu.Button> mainButtonLookup = new HashMap<>();

    private MultiValueMap<WxButton.Group, WxMenu.Button> groupButtonLookup = new LinkedMultiValueMap<>();

    private WxButtonEventKeyStrategy wxButtonEventKeyStrategy;

    private WxMenu wxMenu;

    private boolean autoCreate;

    private StringValueResolver stringValueResolver;

    public WxMenuManager(WxApiService wxApiService, WxButtonEventKeyStrategy wxButtonEventKeyStrategy, boolean autoCreate) {
        this.wxApiService = wxApiService;
        this.wxButtonEventKeyStrategy = wxButtonEventKeyStrategy;
        this.autoCreate = autoCreate;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
    }

    public WxMenu.Button add(WxButton wxButton) {
        WxMenu.Button buttonItem = WxMenu.Button.builder()
                .setGroup(wxButton.group())
                .setType(wxButton.type())
                .setMain(wxButton.main())
                .setOrder(wxButton.order())
                .setKey(wxButtonEventKeyStrategy.getEventKey(wxButton))
                .setMediaId(this.stringValueResolver.resolveStringValue(wxButton.mediaId()))
                .setName(this.stringValueResolver.resolveStringValue(wxButton.name()))
                .setAppId(this.stringValueResolver.resolveStringValue(wxButton.appId()))
                .setPagePath(this.stringValueResolver.resolveStringValue(wxButton.pagePath()))
                .setUrl(this.stringValueResolver.resolveStringValue(wxButton.url()))
                .build();
        if (buttonItem.isMain()) {
            Assert.isNull(mainButtonLookup.get(buttonItem.getGroup()), String.format("已经存在该分组的主菜单，分组是%s", buttonItem.getGroup()));
            mainButtonLookup.put(buttonItem.getGroup(), buttonItem);
        } else {
            // 可以校验不要超过五个，或者忽略最后的
            groupButtonLookup.add(buttonItem.getGroup(), buttonItem);
        }
        return buttonItem;
    }

    public WxMenu getMenu() {
        if (this.wxMenu == null) {
            this.wxMenu = initMenu();
        }
        return this.wxMenu;
    }

    private WxMenu initMenu() {
        WxMenu wxMenu = new WxMenu();
        mainButtonLookup.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getKey().ordinal()))
                .forEach(m -> {
                    groupButtonLookup.getOrDefault(m.getKey(), new ArrayList<>()).stream()
                            .sorted(Comparator.comparingInt(w -> w.getOrder().ordinal()))
                            .forEach(b -> m.getValue().addSubButton(b));
                    wxMenu.add(m.getValue());
                });
        return wxMenu;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        WxMenus remoteWxMenus = null;
        try {
            remoteWxMenus = wxApiService.getMenu();
        } catch (WxApiResultException e) {
            // 如果不是菜单不存在，则继续抛出，否则执行创建菜单操作
            if (e.getResultCode() != WxApiResultException.Code.NOT_FOUND_MENU_DATA &&
                    e.getResultCode() != WxApiResultException.Code.API_UNAUTHED) {
                throw e;
            }
        }
        if (!autoCreate) {
            if (remoteWxMenus != null) {
                // 处理远程菜单
                this.wxMenu = processRemoteMenu(remoteWxMenus.wxMenu);
                mappingMenu(this.wxMenu);
            }
            return;
        }
        WxMenu localWxMenu = this.getMenu();
        // WxMenus oldWxMenus = objectMapper.readValue(oldMenuJson, WxMenus.class);
        if (CollectionUtils.isEmpty(localWxMenu.mainButtons)) {
            logger.info("未扫描到有效菜单，请检查项目配置，可能是@WxController类没有被扫描到或者没有声明为@WxController",
                    new WxAppException("未检测到有效菜单，不执行创建菜单动作"));
        }
        if (remoteWxMenus == null || isMenuChanged(remoteWxMenus.wxMenu)) {
            String result = wxApiService.createMenu(localWxMenu);
            logger.info("==============================================================");
            logger.info("            执行创建菜单操作       ");
            logger.info("            操作结果：" + result);
            logger.info("            新的菜单json为：" + localWxMenu);
            logger.info("==============================================================");
        } else {
            logger.info("==============================================================");
            logger.info("            菜单未发生变化             ");
            logger.info("            当前菜单json为：" + remoteWxMenus);
            logger.info("==============================================================");
        }
        mappingMenu(this.wxMenu);
    }

    /**
     * 映射远程菜单，注意远程的主菜单是按照left, middle, right定义的
     * 如果远程1个主菜单，则占用left，两个则left，middle，三个正常。这是考虑编码习惯而不是考虑日常习惯的
     *
     * @param wxMenu
     */
    private WxMenu processRemoteMenu(WxMenu wxMenu) {
        // 开始用atomicInteger处理，但是atomic用在多线程中，这里有点过度使用
        int[] index = new int[2];
        wxMenu.mainButtons.forEach(button -> {
            // 有子菜单
            if (!CollectionUtils.isEmpty(button.getSubButtons())) {
                button.getSubButtons().forEach(subButton -> {
                    subButton.setMain(false);
                    subButton.setGroup(WxButton.Group.values()[index[0]]);
                    subButton.setOrder(WxButton.Order.values()[index[1]++]);
                });
            }
            index[1] = 0;
            button.setMain(true);
            button.setGroup(WxButton.Group.values()[index[0]++]);
        });
        return wxMenu;
    }

    /**
     * 映射菜单
     *
     * @param wxMenu
     */
    private void mappingMenu(WxMenu wxMenu) {
        wxMenu.mainButtons.forEach(button -> {
            // 没有子菜单
            if (CollectionUtils.isEmpty(button.getSubButtons())) {
                this.keyButtonLookup.put(ButtonKey.of(button), button);
            } else {
                // 有子菜单
                button.getSubButtons().forEach(subButton -> this.keyButtonLookup.put(ButtonKey.of(subButton), subButton));
            }
        });
    }

    private boolean isMenuChanged(WxMenu remoteWxMenu) {
        return !this.wxMenu.equals(remoteWxMenu);
    }

    /**
     * 防止类型不同，但是key相同的情况
     */
    private static final class ButtonKey {

        private final String key;

        private final WxButton.Type type;

        public ButtonKey(String key, WxButton.Type type) {
            this.key = key;
            this.type = type;
        }

        @Override
        public boolean equals(Object other) {
            return (this == other || (other instanceof ButtonKey &&
                    this.type == ((ButtonKey) other).type) && this.key.equals(((ButtonKey) other).key));
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public String toString() {
            return type + ":" + this.key.toString();
        }

        public static ButtonKey of(WxMenu.Button button) {
            // 只有这里因为微信的机制，要获取不同情况的key，其他都使用原始情况
            return new ButtonKey(WxMenuUtils.getKey(button), button.getType());
        }

    }

}
