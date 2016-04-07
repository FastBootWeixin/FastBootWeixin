package com.example.myproject.module;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.example.myproject.module.menu.MenuType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WXMenuItem {
	
	@JsonProperty("sub_button")
	private Set<WXMenuItem> subMenus;
	
	@JsonIgnore
	private String[] subMenuStrings;
	
	@JsonInclude(Include.NON_NULL)
	private MenuType type;
	
	@JsonInclude(Include.NON_NULL)
	private String name;
	
	@JsonInclude(Include.NON_NULL)
	private String key;
	
	@JsonInclude(Include.NON_NULL)
	private String url;
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("mediaId")
	private String mediaId;

	public Set<WXMenuItem> getsubMenus() {
		return subMenus;
	}

	public String[] getSubMenuStrings() {
		return subMenuStrings;
	}

	public MenuType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getUrl() {
		return url;
	}

	public String getMediaId() {
		return mediaId;
	}

	public WXMenuItem addSubMenu(WXMenuItem item) {
		this.subMenus.add(item);
		return this;
	}
	
	WXMenuItem() {
		this.subMenus = new HashSet<>();
		this.type = MenuType.click;
		this.name = null;
		this.mediaId = null;
		this.url = null;
		this.key = null;
	}
	
	WXMenuItem(String[] subMenuStrings, MenuType type, String name,
			String key, String url, String mediaId) {
		this.subMenuStrings = subMenuStrings;
		this.type = type;
		this.name = name;
		this.key = key;
		this.url = url;
		this.mediaId = mediaId;
	}
	
	public Set<WXMenuItem> addSubMenus(WXMenuItem button) {
		this.subMenus.add(button);
		return this.subMenus;
	}
	
	public static WXMenuItem.Builder create() {
        return new Builder();
    }
	
	public static class Builder {
		
		private String[] subMenuStrings;
		private MenuType type;
		private String name;
		private String key;
		private String url;
		private String mediaId;
		
		Builder() {
            super();
            this.subMenuStrings = null;
            this.type = MenuType.click;
            this.name = null;
            this.key = null;
            this.url = null;
            this.mediaId = null;
        }
		
		public Builder setSubMenuStrings(String[] subMenuStrings) {
			if (subMenuStrings != null && subMenuStrings.length > 0) {
				this.subMenuStrings = subMenuStrings;
			}
			return this;
		}

		public Builder setType(MenuType type) {
			Assert.notNull(type, "菜单必须有类型");
			this.type = type;
			return this;
		}

		public Builder setName(String name) {
			Assert.isTrue(!StringUtils.isEmpty(name), "菜单名不能为空");
			//没有判断父菜单过长
			Assert.isTrue(name.length() <= 40, "菜单名过长");
			this.name = name;
			return this;
		}

		public Builder setKey(String key) {
			this.key = StringUtils.isEmpty(key) ? null : key;
			return this;
		}

		public Builder setUrl(String url) {
			this.url = StringUtils.isEmpty(url) ? null : url;
			return this;
		}

		public Builder setMediaId(String mediaId) {
			this.mediaId = StringUtils.isEmpty(mediaId) ? null : mediaId;
			return this;
		}

		public WXMenuItem build() {
			Assert.isTrue(this.type == MenuType.click && this.key == null, 
					"click类型必须有key");
			Assert.isTrue(this.type == MenuType.view && this.url == null, 
					"view类型必须有url");
			Assert.isTrue((this.type == MenuType.media_id || this.type == MenuType.view_limited) && this.mediaId == null, 
					"media_id类型和view_limited类型必须有mediaId");
            return new WXMenuItem(
            		subMenuStrings,
            		type,
            		name,
            		key,
            		url,
            		mediaId);
        }
		
	}
	
}
