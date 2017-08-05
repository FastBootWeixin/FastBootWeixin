/*
 * Copyright 2002-2016 the original author or authors.
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

package com.example.myproject.mvc;

import com.example.myproject.annotation.WxButton;
import com.example.myproject.module.event.WxEvent;
import com.example.myproject.module.message.receive.WxMessage;
import com.example.myproject.mvc.condition.WxButtonTypeCondition;
import com.example.myproject.mvc.condition.WxCategoryCondition;
import com.example.myproject.mvc.condition.WxEventTypeCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMethodMappingNamingStrategy;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * A {@link RequestCondition} that consists of the following other conditions:
 * <ol>
 * <li>{@link PatternsRequestCondition}
 * <li>{@link RequestMethodsRequestCondition}
 * <li>{@link ParamsRequestCondition}
 * <li>{@link HeadersRequestCondition}
 * <li>{@link ConsumesRequestCondition}
 * <li>{@link ProducesRequestCondition}
 * <li>{@code RequestCondition} (optional, custom request condition)
 * </ol>
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class WxMappingInfo implements RequestCondition<WxMappingInfo> {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private final String name;

	private final String eventKey;

	private final WxCategoryCondition wxCategoryCondition;

	private final WxButtonTypeCondition wxButtonTypeCondition;

	private final WxEventTypeCondition wxEventTypeCondition;

	public WxMappingInfo(String name,
						 String eventKey,
						 WxCategoryCondition categories,
						 WxButtonTypeCondition buttonTypes,
						 WxEventTypeCondition eventTypes) {
		this.name = (name != null ? name : "");
		this.eventKey = StringUtils.hasText(eventKey) ? eventKey : null;
		this.wxCategoryCondition = (categories != null ? categories : new WxCategoryCondition());
		this.wxButtonTypeCondition = (buttonTypes != null ? buttonTypes : new WxButtonTypeCondition());
		this.wxEventTypeCondition = (eventTypes != null ? eventTypes : new WxEventTypeCondition());
	}

	/**
	 * Return the name for this mapping, or {@code null}.
	 */
	public String getName() {
		return this.name;
	}

	public String getEventKey() {
		return eventKey;
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

	/**
	 * Combines "this" request mapping info (i.e. the current instance) with another request mapping info instance.
	 * <p>Example: combine type- and method-level request mappings.
	 * @return a new request mapping info instance; never {@code null}
	 */
	@Override
	public WxMappingInfo combine(WxMappingInfo other) {
		String name = combineNames(other);
		WxCategoryCondition categories = this.wxCategoryCondition.combine(other.wxCategoryCondition);
		WxButtonTypeCondition buttonTypes = this.wxButtonTypeCondition.combine(other.wxButtonTypeCondition);
		WxEventTypeCondition eventTypes = this.wxEventTypeCondition.combine(other.wxEventTypeCondition);

		return new WxMappingInfo(name, eventKey, categories, buttonTypes, eventTypes);
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
			String separator = RequestMappingInfoHandlerMethodMappingNamingStrategy.SEPARATOR;
			return this.name + separator + other.name;
		}
		else if (this.name != null) {
			return this.name;
		}
		else {
			return (other.name != null ? other.name : null);
		}
	}

	/**
	 * Checks if all conditions in this request mapping info match the provided request and returns
	 * a potentially new request mapping info with conditions tailored to the current request.
	 * <p>For example the returned instance may contain the subset of URL patterns that match to
	 * the current request, sorted with best matching patterns on top.
	 * @return a new instance in case all conditions match; or {@code null} otherwise
	 */
	@Override
	public WxMappingInfo getMatchingCondition(HttpServletRequest request) {

		WxCategoryCondition categories = (WxCategoryCondition) this.wxCategoryCondition.getMatchingCondition(request);
		WxButtonTypeCondition buttonTypes = (WxButtonTypeCondition) this.wxButtonTypeCondition.getMatchingCondition(request);
		WxEventTypeCondition eventTypes = (WxEventTypeCondition) this.wxEventTypeCondition.getMatchingCondition(request);

		if (categories == null) {
			return null;
		}
		return new WxMappingInfo(this.name, this.eventKey, categories, buttonTypes, eventTypes);
	}

	/**
	 * Compares "this" info (i.e. the current instance) with another info in the context of a request.
	 * <p>Note: It is assumed both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} to ensure they have conditions with
	 * content relevant to current request.
	 */
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
				this.wxCategoryCondition.equals(otherInfo.wxCategoryCondition) &&
				this.wxEventTypeCondition.equals(otherInfo.wxEventTypeCondition) &&
				this.wxButtonTypeCondition.equals(otherInfo.wxButtonTypeCondition));
	}

	@Override
	public int hashCode() {
		return (this.name.hashCode() * 31 +  // primary differentiation
				this.wxCategoryCondition.hashCode() + this.wxEventTypeCondition.hashCode() + this.wxButtonTypeCondition.hashCode());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		builder.append(this.wxCategoryCondition);
		if (!this.wxEventTypeCondition.isEmpty()) {
			builder.append(",events=").append(this.wxEventTypeCondition);
		}
		if (!this.wxButtonTypeCondition.isEmpty()) {
			builder.append(",buttonss=").append(this.wxButtonTypeCondition);
		}
		builder.append('}');
		return builder.toString();
	}


	/**
	 * Create a new {@code WxMappingInfo.Builder} with the given paths.
	 * @param category the paths to use
	 * @since 4.2
	 */
	public static Builder category(WxMessage.Category category) {
		return new DefaultBuilder(category);
	}


	/**
	 * Defines a builder for creating a WxMappingInfo.
	 * @since 4.2
	 */
	public interface Builder {

		Builder buttonTypes(WxButton.Type... buttonTypes);

		Builder messageTypes(WxMessage.Type... messageTypes);

		Builder eventTypes(WxEvent.Type... eventTypes);

		Builder mappingName(String name);

		Builder eventKey(String eventKey);

		Builder options(WxMappingInfo.BuilderConfiguration options);

		WxMappingInfo build();

	}


	private static class DefaultBuilder implements Builder {

		private WxMessage.Category category;

		private WxButton.Type[] buttonTypes;

		private WxMessage.Type[] messageTypes;

		private WxEvent.Type[] eventTypes;

		private String mappingName;

		private String eventKey;

		private BuilderConfiguration options = new BuilderConfiguration();

		public DefaultBuilder(WxMessage.Category category) {
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
			return new WxMappingInfo(mappingName, eventKey,
					new WxCategoryCondition(category),
					new WxButtonTypeCondition(buttonTypes),
					new WxEventTypeCondition(eventTypes));
		}
	}


	/**
	 * Container for configuration options used for request mapping purposes.
	 * Such configuration is required to create WxMappingInfo instances but
	 * is typically used across all WxMappingInfo instances.
	 * @since 4.2
	 * @see Builder#options
	 */
	public static class BuilderConfiguration {

		private UrlPathHelper urlPathHelper;

		private PathMatcher pathMatcher;

		private boolean trailingSlashMatch = true;

		private boolean suffixPatternMatch = true;

		private boolean registeredSuffixPatternMatch = false;

		private ContentNegotiationManager contentNegotiationManager;

		/**
		 * @deprecated as of Spring 4.2.8, in favor of {@link #setUrlPathHelper}
		 */
		@Deprecated
		public void setPathHelper(UrlPathHelper pathHelper) {
			this.urlPathHelper = pathHelper;
		}

		/**
		 * Set a custom UrlPathHelper to use for the PatternsRequestCondition.
		 * <p>By default this is not set.
		 * @since 4.2.8
		 */
		public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
			this.urlPathHelper = urlPathHelper;
		}

		/**
		 * Return a custom UrlPathHelper to use for the PatternsRequestCondition, if any.
		 */
		public UrlPathHelper getUrlPathHelper() {
			return this.urlPathHelper;
		}

		/**
		 * Set a custom PathMatcher to use for the PatternsRequestCondition.
		 * <p>By default this is not set.
		 */
		public void setPathMatcher(PathMatcher pathMatcher) {
			this.pathMatcher = pathMatcher;
		}

		/**
		 * Return a custom PathMatcher to use for the PatternsRequestCondition, if any.
		 */
		public PathMatcher getPathMatcher() {
			return this.pathMatcher;
		}

		/**
		 * Set whether to apply trailing slash matching in PatternsRequestCondition.
		 * <p>By default this is set to 'true'.
		 */
		public void setTrailingSlashMatch(boolean trailingSlashMatch) {
			this.trailingSlashMatch = trailingSlashMatch;
		}

		/**
		 * Return whether to apply trailing slash matching in PatternsRequestCondition.
		 */
		public boolean useTrailingSlashMatch() {
			return this.trailingSlashMatch;
		}

		/**
		 * Set whether to apply suffix pattern matching in PatternsRequestCondition.
		 * <p>By default this is set to 'true'.
		 * @see #setRegisteredSuffixPatternMatch(boolean)
		 */
		public void setSuffixPatternMatch(boolean suffixPatternMatch) {
			this.suffixPatternMatch = suffixPatternMatch;
		}

		/**
		 * Return whether to apply suffix pattern matching in PatternsRequestCondition.
		 */
		public boolean useSuffixPatternMatch() {
			return this.suffixPatternMatch;
		}

		/**
		 * Set whether suffix pattern matching should be restricted to registered
		 * file extensions only. Setting this property also sets
		 * {@code suffixPatternMatch=true} and requires that a
		 * {@link #setContentNegotiationManager} is also configured in order to
		 * obtain the registered file extensions.
		 */
		public void setRegisteredSuffixPatternMatch(boolean registeredSuffixPatternMatch) {
			this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
			this.suffixPatternMatch = (registeredSuffixPatternMatch || this.suffixPatternMatch);
		}

		/**
		 * Return whether suffix pattern matching should be restricted to registered
		 * file extensions only.
		 */
		public boolean useRegisteredSuffixPatternMatch() {
			return this.registeredSuffixPatternMatch;
		}

		/**
		 * Return the file extensions to use for suffix pattern matching. If
		 * {@code registeredSuffixPatternMatch=true}, the extensions are obtained
		 * from the configured {@code contentNegotiationManager}.
		 */
		public List<String> getFileExtensions() {
			if (useRegisteredSuffixPatternMatch() && getContentNegotiationManager() != null) {
				return this.contentNegotiationManager.getAllFileExtensions();
			}
			return null;
		}

		/**
		 * Set the ContentNegotiationManager to use for the ProducesRequestCondition.
		 * <p>By default this is not set.
		 */
		public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
			this.contentNegotiationManager = contentNegotiationManager;
		}

		/**
		 * Return the ContentNegotiationManager to use for the ProducesRequestCondition,
		 * if any.
		 */
		public ContentNegotiationManager getContentNegotiationManager() {
			return this.contentNegotiationManager;
		}
	}

}
