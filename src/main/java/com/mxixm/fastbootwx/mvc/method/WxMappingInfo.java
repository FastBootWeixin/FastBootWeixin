package com.mxixm.fastbootwx.mvc.method;

import com.mxixm.fastbootwx.annotation.WxButton;
import com.mxixm.fastbootwx.module.Wx;
import com.mxixm.fastbootwx.module.event.WxEvent;
import com.mxixm.fastbootwx.module.message.WxMessage;
import com.mxixm.fastbootwx.mvc.condition.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.mvc.condition.*;
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

	/**
	 * Return the value for this mapping, or {@code null}.
	 */
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

	/**
	 * Combines "this" request mapping info (i.e. the current instance) with another request mapping info instance.
	 * <p>Example: combine type- and method-level request mappings.
	 * @return a new request mapping info instance; never {@code null}
	 */
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
		}
		else if (this.name != null) {
			return this.name;
		}
		else {
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


	/**
	 * Create a new {@code WxMappingInfo.Builder} with the given paths.
	 * @param category the paths to use
	 * @since 4.2
	 */
	public static Builder category(Wx.Category category) {
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


	/**
	 * Container for configuration options used for request mapping purposes.
	 * Such configuration is required to builder WxMappingInfo instances but
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
