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

package com.mxixm.fastboot.weixin.mvc.method;

import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.mvc.condition.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Objects;

/**
 * FastBootWeixin WxMappingInfo
 * 此类有大量重复代码，还有可优化余地
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public class WxMappingInfo implements WxRequestCondition<WxMappingInfo> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final String name;

    private final WxEnumRequestCondition categories;

    private final WxEnumRequestCondition buttonTypes;

    private final WxWildcardRequestCondition buttonKeys;

    private final WxWildcardRequestCondition buttonNames;

    private final WxWildcardRequestCondition buttonUrls;

    private final WxWildcardRequestCondition buttonMediaIds;

    private final WxWildcardRequestCondition buttonAppIds;

    private final WxWildcardRequestCondition buttonPagePaths;

    private final WxEnumRequestCondition buttonGroups;

    private final WxEnumRequestCondition buttonOrders;

    private final WxEnumRequestCondition buttonLevels;

    private final WxEnumRequestCondition messageTypes;

    private final WxWildcardRequestCondition messageContents;

    private final WxEnumRequestCondition eventTypes;

    private final WxWildcardRequestCondition eventScenes;

    private final WxWildcardRequestCondition eventKeys;

    private final AbstractWxRequestCondition[] conditions;

    public WxMappingInfo(String name,
                         WxEnumRequestCondition categories,
                         WxEnumRequestCondition buttonTypes,
                         WxWildcardRequestCondition buttonKeys,
                         WxWildcardRequestCondition buttonNames,
                         WxWildcardRequestCondition buttonUrls,
                         WxWildcardRequestCondition buttonMediaIds,
                         WxWildcardRequestCondition buttonAppIds,
                         WxWildcardRequestCondition buttonPagePaths,
                         WxEnumRequestCondition buttonGroups,
                         WxEnumRequestCondition buttonOrders,
                         WxEnumRequestCondition buttonLevels,
                         WxEnumRequestCondition messageTypes,
                         WxWildcardRequestCondition messageContents,
                         WxEnumRequestCondition eventTypes,
                         WxWildcardRequestCondition eventScenes,
                         WxWildcardRequestCondition eventKeys) {
        this.name = (name != null ? name : "");
        this.categories = categories != null ? categories : WxRequestConditionFactory.createWxCategoriesCondition();
        this.buttonTypes = buttonTypes != null ? buttonTypes : WxRequestConditionFactory.createWxButtonTypesCondition();
        this.buttonKeys = buttonKeys != null ? buttonKeys : WxRequestConditionFactory.createWxButtonKeysCondition();
        this.buttonNames = buttonNames != null ? buttonNames : WxRequestConditionFactory.createWxButtonNamesCondition();
        this.buttonUrls = buttonUrls != null ? buttonUrls : WxRequestConditionFactory.createWxButtonUrlsCondition();
        this.buttonMediaIds = buttonMediaIds != null ? buttonMediaIds : WxRequestConditionFactory.createWxButtonMediaIdsCondition();
        this.buttonAppIds = buttonAppIds != null ? buttonAppIds : WxRequestConditionFactory.createWxButtonAppIdsCondition();
        this.buttonPagePaths = buttonPagePaths != null ? buttonPagePaths : WxRequestConditionFactory.createWxButtonPagePathsCondition();
        this.buttonGroups = buttonGroups != null ? buttonGroups : WxRequestConditionFactory.createWxButtonGroupsCondition();
        this.buttonOrders = buttonOrders != null ? buttonOrders : WxRequestConditionFactory.createWxButtonOrdersCondition();
        this.buttonLevels = buttonLevels != null ? buttonLevels : WxRequestConditionFactory.createWxButtonLevelsCondition();
        this.messageTypes = messageTypes != null ? messageTypes : WxRequestConditionFactory.createWxMessageTypesCondition();
        this.messageContents = messageContents != null ? messageContents : WxRequestConditionFactory.createWxMessageContentsCondition();
        this.eventTypes = eventTypes != null ? eventTypes : WxRequestConditionFactory.createWxEventTypesCondition();
        this.eventScenes = eventScenes != null ? eventScenes : WxRequestConditionFactory.createWxEventScenesCondition();
        this.eventKeys = eventKeys != null ? eventScenes : WxRequestConditionFactory.createWxEventKeysCondition();
        this.conditions = new AbstractWxRequestCondition[]{categories, buttonTypes, buttonKeys, buttonNames, buttonUrls, buttonMediaIds, buttonAppIds, buttonPagePaths,
                buttonGroups, buttonOrders, buttonLevels, messageTypes, messageContents, eventTypes, eventScenes, eventKeys};
    }

    public WxEnumRequestCondition getCategories() {
        return categories;
    }

    public WxEnumRequestCondition getButtonTypes() {
        return buttonTypes;
    }

    public WxWildcardRequestCondition getButtonKeys() {
        return buttonKeys;
    }

    public WxWildcardRequestCondition getButtonNames() {
        return buttonNames;
    }

    public WxWildcardRequestCondition getButtonUrls() {
        return buttonUrls;
    }

    public WxWildcardRequestCondition getButtonMediaIds() {
        return buttonMediaIds;
    }

    public WxWildcardRequestCondition getButtonAppIds() {
        return buttonAppIds;
    }

    public WxWildcardRequestCondition getButtonPagePaths() {
        return buttonPagePaths;
    }

    public WxEnumRequestCondition getButtonOrders() {
        return buttonOrders;
    }

    public WxEnumRequestCondition getButtonGroups() {
        return buttonGroups;
    }

    public WxEnumRequestCondition getMessageTypes() {
        return messageTypes;
    }

    public WxWildcardRequestCondition getMessageContents() {
        return messageContents;
    }

    public WxEnumRequestCondition getEventTypes() {
        return eventTypes;
    }

    public WxWildcardRequestCondition getEventScenes() {
        return eventScenes;
    }

    public WxEnumRequestCondition getButtonLevels() {
        return buttonLevels;
    }

    public WxWildcardRequestCondition getEventKeys() {
        return eventKeys;
    }

    @Override
    public WxMappingInfo combine(WxMappingInfo other) {
        String name = combineNames(other);
        WxEnumRequestCondition categories = this.categories.combine(other.categories);
        WxEnumRequestCondition buttonTypes = this.buttonTypes.combine(other.buttonTypes);
        WxWildcardRequestCondition buttonKeys = this.buttonKeys.combine(other.buttonKeys);
        WxWildcardRequestCondition buttonNames = this.buttonNames.combine(other.buttonNames);
        WxWildcardRequestCondition buttonUrls = this.buttonUrls.combine(other.buttonUrls);
        WxWildcardRequestCondition buttonMediaIds = this.buttonMediaIds.combine(other.buttonMediaIds);
        WxWildcardRequestCondition buttonAppIds = this.buttonAppIds.combine(other.buttonAppIds);
        WxWildcardRequestCondition buttonPagePaths = this.buttonPagePaths.combine(other.buttonPagePaths);
        WxEnumRequestCondition buttonGroups = this.buttonGroups.combine(other.buttonGroups);
        WxEnumRequestCondition buttonOrders = this.buttonOrders.combine(other.buttonOrders);
        WxEnumRequestCondition buttonLevels = this.buttonLevels.combine(other.buttonLevels);
        WxEnumRequestCondition messageTypes = this.messageTypes.combine(other.messageTypes);
        WxWildcardRequestCondition messageContents = this.messageContents.combine(other.messageContents);
        WxEnumRequestCondition eventTypes = this.eventTypes.combine(other.eventTypes);
        WxWildcardRequestCondition eventScenes = this.eventScenes.combine(other.eventScenes);
        WxWildcardRequestCondition eventKeys = this.eventKeys.combine(other.eventKeys);
        return new WxMappingInfo(name, categories, buttonTypes, buttonKeys, buttonNames, buttonUrls, buttonMediaIds, buttonAppIds, buttonPagePaths,
                buttonGroups, buttonOrders, buttonLevels, messageTypes, messageContents, eventTypes, eventScenes, eventKeys);
    }

    private String combineNames(WxMappingInfo other) {
        if (this.name != null && other.name != null) {
            String separator = WxMappingHandlerMethodNamingStrategy.SEPARATOR;
            return this.name + separator + other.name;
        } else if (this.name != null) {
            return this.name;
        } else {
            return (other.name != null ? other.name : null);
        }
    }

    @Override
    public WxMappingInfo getMatchingCondition(WxRequest wxRequest) {
        WxEnumRequestCondition categories = this.categories.getMatchingCondition(wxRequest);
        WxEnumRequestCondition buttonTypes = this.buttonTypes.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition buttonKeys = this.buttonKeys.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition buttonNames = this.buttonNames.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition buttonUrls = this.buttonUrls.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition buttonMediaIds = this.buttonMediaIds.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition buttonAppIds = this.buttonAppIds.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition buttonPagePaths = this.buttonPagePaths.getMatchingCondition(wxRequest);
        WxEnumRequestCondition buttonGroups = this.buttonGroups.getMatchingCondition(wxRequest);
        WxEnumRequestCondition buttonOrders = this.buttonOrders.getMatchingCondition(wxRequest);
        WxEnumRequestCondition buttonLevels = this.buttonLevels.getMatchingCondition(wxRequest);
        WxEnumRequestCondition messageTypes = this.messageTypes.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition messageContents = this.messageContents.getMatchingCondition(wxRequest);
        WxEnumRequestCondition eventTypes = this.eventTypes.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition eventScenes = this.eventScenes.getMatchingCondition(wxRequest);
        WxWildcardRequestCondition eventKeys = this.eventKeys.getMatchingCondition(wxRequest);
        if (isAnyNull(categories, buttonTypes, buttonKeys, buttonNames, buttonUrls, buttonMediaIds, buttonAppIds, buttonPagePaths,
                buttonGroups, buttonOrders, messageTypes, messageContents, eventTypes, eventScenes, eventKeys)) {
            return null;
        }
        return new WxMappingInfo(name, categories, buttonTypes, buttonKeys, buttonNames, buttonUrls, buttonMediaIds, buttonAppIds, buttonPagePaths,
                buttonGroups, buttonOrders, buttonLevels, messageTypes, messageContents, eventTypes, eventScenes, eventKeys);
    }

    private boolean isAnyNull(AbstractWxRequestCondition... conditions) {
        for (AbstractWxRequestCondition condition : conditions) {
            if (Objects.isNull(condition)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(WxMappingInfo other, WxRequest wxRequest) {
        for (int i = 0; i < conditions.length; i++) {
            int result = conditions[i].compareTo(other.conditions[i], wxRequest);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public Type getType() {
        return Type.COMPOSITES;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WxMappingInfo)) {
            return false;
        }
        WxMappingInfo otherInfo = (WxMappingInfo) other;
        return (this.name.equals(otherInfo.name) &&
                Arrays.equals(this.conditions, otherInfo.conditions));
    }

    @Override
    public int hashCode() {
        return (this.name.hashCode() * 31 +  // primary differentiation
                Arrays.hashCode(this.conditions));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        builder.append(StringUtils.isEmpty(this.name) ? "-" : this.name);
        for (AbstractWxRequestCondition condition : conditions) {
            if (!condition.isEmpty()) {
                builder.append(",").append(condition.getName()).append("=").append(condition);
            }
        }
        builder.append('}');
        return builder.toString();
    }

    public static Builder create(Wx.Category... categories) {
        return new DefaultBuilder(categories);
    }

    public interface Builder {

        Builder mappingName(String mappingName);

        Builder buttonTypes(WxButton.Type... buttonTypes);

        Builder buttonGroups(WxButton.Group... buttonGroups);

        Builder buttonOrders(WxButton.Order... buttonOrders);

        Builder buttonLevels(WxButton.Level... buttonLevels);

        Builder messageTypes(WxMessage.Type... messageTypes);

        Builder eventTypes(WxEvent.Type... eventTypes);

        Builder messageContents(String... messageContents);

        Builder buttonNames(String... buttonNames);

        Builder eventScenes(String... eventScenes);

        Builder eventKeys(String... eventKeys);

        Builder buttonUrls(String... buttonUrls);

        Builder buttonAppIds(String... buttonAppIds);

        Builder buttonPagePaths(String... buttonPagePaths);

        Builder buttonMediaIds(String... buttonMediaIds);

        Builder buttonKeys(String... buttonKeys);

        WxMappingInfo build();

    }

    private static class DefaultBuilder implements Builder {

        private String mappingName;

        private Wx.Category[] categories;

        private WxButton.Type[] buttonTypes;

        private WxButton.Group[] buttonGroups;

        private WxButton.Order[] buttonOrders;

        private WxButton.Level[] buttonLevels;

        private WxMessage.Type[] messageTypes;

        private WxEvent.Type[] eventTypes;

        private String[] messageContents;

        private String[] buttonNames;

        private String[] eventScenes;

        private String[] eventKeys;

        private String[] buttonUrls;

        private String[] buttonAppIds;

        private String[] buttonPagePaths;

        private String[] buttonMediaIds;

        private String[] buttonKeys;

        DefaultBuilder(Wx.Category[] categories) {
            this.categories = categories;
        }

        @Override
        public DefaultBuilder mappingName(String mappingName) {
            this.mappingName = mappingName;
            return this;
        }

        @Override
        public DefaultBuilder buttonTypes(WxButton.Type... buttonTypes) {
            this.buttonTypes = buttonTypes;
            return this;
        }

        @Override
        public DefaultBuilder buttonGroups(WxButton.Group... buttonGroups) {
            this.buttonGroups = buttonGroups;
            return this;
        }

        @Override
        public DefaultBuilder buttonOrders(WxButton.Order... buttonOrders) {
            this.buttonOrders = buttonOrders;
            return this;
        }

        @Override
        public DefaultBuilder messageTypes(WxMessage.Type... messageTypes) {
            this.messageTypes = messageTypes;
            return this;
        }

        @Override
        public DefaultBuilder eventTypes(WxEvent.Type... eventTypes) {
            this.eventTypes = eventTypes;
            return this;
        }

        @Override
        public DefaultBuilder buttonLevels(WxButton.Level... buttonLevels) {
            this.buttonLevels = buttonLevels;
            return this;
        }

        @Override
        public DefaultBuilder messageContents(String... messageContents) {
            this.messageContents = messageContents;
            return this;
        }

        @Override
        public DefaultBuilder buttonNames(String... buttonNames) {
            this.buttonNames = buttonNames;
            return this;
        }

        @Override
        public DefaultBuilder eventScenes(String... eventScenes) {
            this.eventScenes = eventScenes;
            return this;
        }

        @Override
        public DefaultBuilder eventKeys(String... eventKeys) {
            this.eventKeys = eventKeys;
            return this;
        }

        @Override
        public DefaultBuilder buttonUrls(String... buttonUrls) {
            this.buttonUrls = buttonUrls;
            return this;
        }

        @Override
        public DefaultBuilder buttonAppIds(String... buttonAppIds) {
            this.buttonAppIds = buttonAppIds;
            return this;
        }

        @Override
        public DefaultBuilder buttonPagePaths(String... buttonPagePaths) {
            this.buttonPagePaths = buttonPagePaths;
            return this;
        }

        @Override
        public DefaultBuilder buttonMediaIds(String... buttonMediaIds) {
            this.buttonMediaIds = buttonMediaIds;
            return this;
        }

        @Override
        public DefaultBuilder buttonKeys(String... buttonKeys) {
            this.buttonKeys = buttonKeys;
            return this;
        }

        @Override
        public WxMappingInfo build() {
            return new WxMappingInfo(mappingName,
                    WxRequestConditionFactory.createWxCategoriesCondition(categories),
                    WxRequestConditionFactory.createWxButtonTypesCondition(buttonTypes),
                    WxRequestConditionFactory.createWxButtonKeysCondition(buttonKeys),
                    WxRequestConditionFactory.createWxButtonNamesCondition(buttonNames),
                    WxRequestConditionFactory.createWxButtonUrlsCondition(buttonUrls),
                    WxRequestConditionFactory.createWxButtonMediaIdsCondition(buttonMediaIds),
                    WxRequestConditionFactory.createWxButtonAppIdsCondition(buttonAppIds),
                    WxRequestConditionFactory.createWxButtonPagePathsCondition(buttonPagePaths),
                    WxRequestConditionFactory.createWxButtonGroupsCondition(buttonGroups),
                    WxRequestConditionFactory.createWxButtonOrdersCondition(buttonOrders),
                    WxRequestConditionFactory.createWxButtonLevelsCondition(buttonLevels),
                    WxRequestConditionFactory.createWxMessageTypesCondition(messageTypes),
                    WxRequestConditionFactory.createWxMessageContentsCondition(messageContents),
                    WxRequestConditionFactory.createWxEventTypesCondition(eventTypes),
                    WxRequestConditionFactory.createWxEventScenesCondition(eventScenes),
                    WxRequestConditionFactory.createWxEventKeysCondition(eventKeys));
        }

        @Override
        public String toString() {
            return "WxMappingInfo.DefaultBuilder(mappingName=" + this.mappingName + ", categories=" + java.util.Arrays.deepToString(this.categories) + ", buttonTypes=" + java.util.Arrays.deepToString(this.buttonTypes) + ", buttonGroups=" + java.util.Arrays.deepToString(this.buttonGroups) + ", buttonOrders=" + java.util.Arrays.deepToString(this.buttonOrders) + ", messageTypes=" + java.util.Arrays.deepToString(this.messageTypes) + ", eventTypes=" + java.util.Arrays.deepToString(this.eventTypes) + ", messageContents=" + java.util.Arrays.deepToString(this.messageContents) + ", buttonNames=" + java.util.Arrays.deepToString(this.buttonNames) + ", eventScenes=" + java.util.Arrays.deepToString(this.eventScenes) + ", buttonUrls=" + java.util.Arrays.deepToString(this.buttonUrls) + ", buttonAppIds=" + java.util.Arrays.deepToString(this.buttonAppIds) + ", buttonPagePaths=" + java.util.Arrays.deepToString(this.buttonPagePaths) + ", buttonMediaIds=" + java.util.Arrays.deepToString(this.buttonMediaIds) + ", buttonKeys=" + java.util.Arrays.deepToString(this.buttonKeys) + ")";
        }

    }

}
