package com.example.myproject.module;

import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.example.myproject.module.menu.Button;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WxMenuItem {

	@JsonProperty("sub_button")
	@JsonInclude(Include.NON_EMPTY)
	private Set<WxMenuItem> subMenus;

	@JsonIgnore
	private Button.Group group;

	@JsonInclude(Include.NON_NULL)
	private Button.Type type;

	@JsonInclude(Include.NON_NULL)
	private String name;

	@JsonIgnore
	private boolean main;

	@JsonInclude(Include.NON_NULL)
	private String key;

	@JsonInclude(Include.NON_NULL)
	private String url;

	@JsonInclude(Include.NON_NULL)
	@JsonProperty("mediaId")
	private String mediaId;

	public Set<WxMenuItem> getsubMenus() {
		return subMenus;
	}

	public Button.Type getType() {
		return type;
	}

	public boolean isMain() {
		return main;
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

	public Button.Group getGroup() {
		return group;
	}

	public WxMenuItem addSubMenu(WxMenuItem item) {
		this.subMenus.add(item);
		return this;
	}

	WxMenuItem(Button.Group group, Button.Type type, boolean main, String name,
               String key, String url, String mediaId) {
		super();
		this.group = group;
		this.type = type;
		this.main = main;
		this.name = name;
		this.key = key;
		this.url = url;
		this.mediaId = mediaId;
	}

	public Set<WxMenuItem> addSubMenus(WxMenuItem button) {
		this.subMenus.add(button);
		return this.subMenus;
	}

	public static WxMenuItem.Builder create() {
        return new Builder();
    }

	public static class Builder {

		private Button.Type type;
		private Button.Group group;
		private boolean main;
		private String name;
		private String key;
		private String url;
		private String mediaId;

		Builder() {
            super();
        }

        public Builder setGroup(Button.Group group) {
			this.group = group;
			return this;
		}

		public Builder setType(Button.Type type) {
			this.type = type;
			return this;
		}

		public Builder setMain(boolean main) {
			this.main = main;
			return this;
		}

		public Builder setName(String name) {
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

		public WxMenuItem build() {
			Assert.isTrue(!StringUtils.isEmpty(name), "菜单名不能为空");
			// 判断子菜单长度
			Assert.isTrue(main && name.getBytes().length <= 16, "一级菜单名过长");
			Assert.isTrue(!main && name.getBytes().length <= 60, "二级菜单名过长");
			Assert.isTrue(key == null || key.getBytes().length <= 128, "key不能过长");
			Assert.notNull(type, "菜单必须有类型");
			Assert.notNull(group, "菜单必须有分组");
			Assert.isTrue(this.type != Button.Type.CLICK || this.key != null,
					"click类型必须有key");
			Assert.isTrue(this.type != Button.Type.VIEW || this.url != null,
					"view类型必须有url");
			Assert.isTrue((this.type != Button.Type.MEDIA_ID && this.type != Button.Type.VIEW_LIMITED) || this.mediaId != null,
					"media_id类型和view_limited类型必须有mediaId");
            return new WxMenuItem(
            		subMenuStrings,
            		type,
            		name,
            		key,
            		url,
            		mediaId);
        }

	}

}
