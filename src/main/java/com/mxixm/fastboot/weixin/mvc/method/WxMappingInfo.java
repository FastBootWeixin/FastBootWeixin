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
import com.mxixm.fastboot.weixin.mvc.condition.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * FastBootWeixin WxMappingInfo
 *
 * @author Guangshan
 * @date 2017/8/12 22:51
 * @since 0.1.2
 */
public final class WxMappingInfo implements RequestCondition<WxMappingInfo> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final String name;

    private final String eventKey;

    private final Wx.Category category;

    // 暂时没用
    private final WxCategoryCondition wxCategoryCondition;

    private final WxButtonTypeCondition wxButtonTypeCondition;

    private final WxEventTypeCondition wxEventTypeCondition;

    private final WxMessageTypeCondition wxMessageTypeCondition;

    private final WxMessageWildcardCondition wxMessageWildcardCondition;

    public WxMappingInfo(String name,
                         Wx.Category category,
                         String eventKey,
                         WxCategoryCondition categories,
                         WxButtonTypeCondition buttonTypes,
                         WxEventTypeCondition eventTypes,
                         WxMessageTypeCondition messageTypes,
                         WxMessageWildcardCondition wildcards) {
        this.name = (name != null ? name : "");
        this.category = category;
        this.eventKey = StringUtils.hasText(eventKey) ? eventKey : null;
        this.wxCategoryCondition = (categories != null ? categories : new WxCategoryCondition());
        this.wxButtonTypeCondition = (buttonTypes != null ? buttonTypes : new WxButtonTypeCondition());
        this.wxEventTypeCondition = (eventTypes != null ? eventTypes : new WxEventTypeCondition());
        this.wxMessageTypeCondition = (messageTypes != null ? messageTypes : new WxMessageTypeCondition());
        this.wxMessageWildcardCondition = (wildcards != null ? wildcards : new WxMessageWildcardCondition());
    }

    public String getName() {
        return this.name;
    }

    public String getEventKey() {
        return eventKey;
    }

    public Wx.Category getCategory() {
        return category;
    }

    public WxCategoryCondition getWxCategoryCondition() {
        return wxCategoryCondition;
    }

    public WxButtonTypeCondition getWxButtonTypeCondition() {
        return wxButtonTypeCondition;
    }

    public WxEventTypeCondition getWxEventTypeCondition() {
        return wxEventTypeCondition;
    }

    public WxMessageTypeCondition getWxMessageTypeCondition() {
        return wxMessageTypeCondition;
    }

    public WxMessageWildcardCondition getWxMessageWildcardCondition() {
        return wxMessageWildcardCondition;
    }

    @Override
    public WxMappingInfo combine(WxMappingInfo other) {
        String name = combineNames(other);
        String eventKey = combineEventKeys(other);
        // category不能合并
        WxCategoryCondition categories = this.wxCategoryCondition.combine(other.wxCategoryCondition);
        WxButtonTypeCondition buttonTypes = this.wxButtonTypeCondition.combine(other.wxButtonTypeCondition);
        WxEventTypeCondition eventTypes = this.wxEventTypeCondition.combine(other.wxEventTypeCondition);
        WxMessageTypeCondition messageTypes = this.wxMessageTypeCondition.combine(other.wxMessageTypeCondition);
        WxMessageWildcardCondition wildcards = this.wxMessageWildcardCondition.combine(other.wxMessageWildcardCondition);
        return new WxMappingInfo(name, category, eventKey, categories, buttonTypes, eventTypes, messageTypes, wildcards);
    }

    private String combineEventKeys(WxMappingInfo other) {
        if (!StringUtils.isEmpty(this.eventKey) && !StringUtils.isEmpty(other.eventKey)) {
            logger.warn("两个合并时都包括eventKey，强制忽略other的eventKey");
            return this.eventKey;
        } else {
            return StringUtils.isEmpty(this.eventKey) ? other.eventKey : this.eventKey;
        }
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
    public WxMappingInfo getMatchingCondition(HttpServletRequest request) {

        WxCategoryCondition categories = (WxCategoryCondition) this.wxCategoryCondition.getMatchingCondition(request);
        WxButtonTypeCondition buttonTypes = (WxButtonTypeCondition) this.wxButtonTypeCondition.getMatchingCondition(request);
        WxEventTypeCondition eventTypes = (WxEventTypeCondition) this.wxEventTypeCondition.getMatchingCondition(request);
        WxMessageTypeCondition messageTypes = (WxMessageTypeCondition) this.wxMessageTypeCondition.getMatchingCondition(request);
        WxMessageWildcardCondition wildcards = this.wxMessageWildcardCondition.getMatchingCondition(request);
        if (categories == null) {
            return null;
        }
        return new WxMappingInfo(this.name, this.category, this.eventKey, categories, buttonTypes, eventTypes, messageTypes, wildcards);
    }

    @Override
    public int compareTo(WxMappingInfo other, HttpServletRequest request) {
        int result;
        result = this.wxCategoryCondition.compareTo(other.getWxCategoryCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.wxButtonTypeCondition.compareTo(other.getWxButtonTypeCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.wxEventTypeCondition.compareTo(other.getWxEventTypeCondition(), request);
        if (result != 0) {
            return result;
        }
        result = this.wxMessageTypeCondition.compareTo(other.getWxMessageTypeCondition(), request);
        if (result != 0) {
            return result;
        }
        return 0;
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
                this.category == otherInfo.category &&
                this.eventKey == otherInfo.eventKey &&
                this.wxCategoryCondition.equals(otherInfo.wxCategoryCondition) &&
                this.wxEventTypeCondition.equals(otherInfo.wxEventTypeCondition) &&
                this.wxButtonTypeCondition.equals(otherInfo.wxButtonTypeCondition) &&
                this.wxMessageTypeCondition.equals(otherInfo.wxMessageTypeCondition) &&
                this.wxMessageWildcardCondition.equals(otherInfo.wxMessageWildcardCondition));
    }

    @Override
    public int hashCode() {
        return (this.name.hashCode() * 31 +  // primary differentiation
                this.category.hashCode() +
                (StringUtils.isEmpty(this.eventKey) ? "" : this.eventKey).hashCode() +
                this.wxCategoryCondition.hashCode() +
                this.wxEventTypeCondition.hashCode() +
                this.wxButtonTypeCondition.hashCode() +
                this.wxMessageTypeCondition.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        builder.append(this.name);
        builder.append(",category=").append(this.category);
        if (StringUtils.hasText(this.eventKey)) {
            builder.append(",eventKey=").append(this.eventKey);
        }
        if (!this.wxEventTypeCondition.isEmpty()) {
            builder.append(",events=").append(this.wxEventTypeCondition);
        }
        if (!this.wxButtonTypeCondition.isEmpty()) {
            builder.append(",buttons=").append(this.wxButtonTypeCondition);
        }
        if (!this.wxMessageTypeCondition.isEmpty()) {
            builder.append(",buttons=").append(this.wxMessageTypeCondition);
        }
        builder.append('}');
        return builder.toString();
    }


    public static Builder category(Wx.Category category) {
        return new DefaultBuilder(category);
    }


    public interface Builder {

        Builder buttonTypes(WxButton.Type... buttonTypes);

        Builder messageTypes(WxMessage.Type... messageTypes);

        Builder eventTypes(WxEvent.Type... eventTypes);

        Builder mappingName(String name);

        Builder eventKey(String eventKey);

        Builder wildcards(String... wildcards);

        Builder options(WxMappingInfo.BuilderConfiguration options);

        WxMappingInfo build();

    }


    private static class DefaultBuilder implements Builder {

        private Wx.Category category;

        private WxButton.Type[] buttonTypes;

        private WxMessage.Type[] messageTypes;

        private WxEvent.Type[] eventTypes;

        private String[] wildcards;

        private String mappingName;

        private String eventKey;

        private BuilderConfiguration options = new BuilderConfiguration();

        public DefaultBuilder(Wx.Category category) {
            this.category = category;
        }

        @Override
        public DefaultBuilder buttonTypes(WxButton.Type... buttonTypes) {
            this.buttonTypes = buttonTypes;
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
        public DefaultBuilder wildcards(String... wildcards) {
            this.wildcards = wildcards;
            return this;
        }

        @Override
        public DefaultBuilder mappingName(String name) {
            this.mappingName = name;
            return this;
        }

        @Override
        public DefaultBuilder eventKey(String eventKey) {
            this.eventKey = eventKey;
            return this;
        }

        @Override
        public Builder options(BuilderConfiguration options) {
            this.options = options;
            return this;
        }

        @Override
        public WxMappingInfo build() {
            return new WxMappingInfo(mappingName, category, eventKey,
                    new WxCategoryCondition(category),
                    new WxButtonTypeCondition(buttonTypes),
                    new WxEventTypeCondition(eventTypes),
                    new WxMessageTypeCondition(messageTypes),
                    new WxMessageWildcardCondition(wildcards));
        }
    }


    public static class BuilderConfiguration {

        private UrlPathHelper urlPathHelper;

        private PathMatcher pathMatcher;

        private boolean trailingSlashMatch = true;

        private boolean suffixPatternMatch = true;

        private boolean registeredSuffixPatternMatch = false;

        private ContentNegotiationManager contentNegotiationManager;

        @Deprecated
        public void setPathHelper(UrlPathHelper pathHelper) {
            this.urlPathHelper = pathHelper;
        }

        public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
            this.urlPathHelper = urlPathHelper;
        }

        public UrlPathHelper getUrlPathHelper() {
            return this.urlPathHelper;
        }

        public void setPathMatcher(PathMatcher pathMatcher) {
            this.pathMatcher = pathMatcher;
        }

        public PathMatcher getPathMatcher() {
            return this.pathMatcher;
        }

        public void setTrailingSlashMatch(boolean trailingSlashMatch) {
            this.trailingSlashMatch = trailingSlashMatch;
        }

        public boolean useTrailingSlashMatch() {
            return this.trailingSlashMatch;
        }

        public void setSuffixPatternMatch(boolean suffixPatternMatch) {
            this.suffixPatternMatch = suffixPatternMatch;
        }

        public boolean useSuffixPatternMatch() {
            return this.suffixPatternMatch;
        }

        public void setRegisteredSuffixPatternMatch(boolean registeredSuffixPatternMatch) {
            this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
            this.suffixPatternMatch = (registeredSuffixPatternMatch || this.suffixPatternMatch);
        }

        public boolean useRegisteredSuffixPatternMatch() {
            return this.registeredSuffixPatternMatch;
        }

        public List<String> getFileExtensions() {
            if (useRegisteredSuffixPatternMatch() && getContentNegotiationManager() != null) {
                return this.contentNegotiationManager.getAllFileExtensions();
            }
            return null;
        }

        public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
            this.contentNegotiationManager = contentNegotiationManager;
        }

        public ContentNegotiationManager getContentNegotiationManager() {
            return this.contentNegotiationManager;
        }
    }

}
