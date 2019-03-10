package com.mxixm.fastboot.weixin.module.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.util.WxRedirectUtils;
import com.mxixm.fastboot.weixin.util.WxUrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * FastBootWeixin WxMenu
 * 微信一套菜单
 * 经测试，在未启用服务器配置时，人工自定义的菜单可以被接口获取，但是数据缺失，此时菜单事件不会发送到服务器
 * 但可通过接口进行自定义菜单，如果通过接口创建菜单，则自定义菜单配置页面会提示API版本菜单使用中。
 * 该页面显示的菜单版本已失效。当前生效版本请调用API查看。若停用菜单，请点击这里。此时因未启用服务器配置，故不会发送事件到服务器。
 * 此时仍可通过自定义菜单进行保存，保存后会覆盖API自定义的菜单。
 * 在已配置菜单的情况下，启用服务器配置，已自定义的菜单会丢失，服务器菜单会变为空，同时自定义菜单功能会自动关闭。
 *
 * @author Guangshan
 * @date 2018/09/13 23:39
 * @since 0.7.0
 */
public class WxMenu {
    @JsonProperty("button")
    public List<Button> mainButtons = new ArrayList<>();

    public void add(Button button) {
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
        return "com.mxixm.fastboot.weixin.module.menu.WxMenu(mainButtons=" + this.mainButtons + ")";
    }

    public static class Button {

        @JsonProperty("sub_button")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Button> subButtons = new ArrayList<>();

        @JsonIgnore
        private WxButton.Group group;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private WxButton.Type type;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String name;

        @JsonIgnore
        private boolean main;

        @JsonIgnore
        private WxButton.Order order;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String key;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String url;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("media_id")
        private String mediaId;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("appid")
        private String appId;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("pagepath")
        private String pagePath;

        public Button() {
        }

        public List<Button> getSubButtons() {
            return subButtons;
        }

        public WxButton.Type getType() {
            return type;
        }

        public WxButton.Order getOrder() {
            return order;
        }

        public boolean isMain() {
            return main;
        }

        public WxButton.Level getLevel() {
            return isMain() ? WxButton.Level.MAIN : WxButton.Level.SUB;
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

        public String getAppId() {
            return appId;
        }

        public String getPagePath() {
            return pagePath;
        }

        public WxButton.Group getGroup() {
            return group;
        }

        void setGroup(WxButton.Group group) {
            this.group = group;
        }

        void setType(WxButton.Type type) {
            this.type = type;
        }

        void setName(String name) {
            this.name = name;
        }

        void setMain(boolean main) {
            this.main = main;
        }

        void setOrder(WxButton.Order order) {
            this.order = order;
        }

        void setKey(String key) {
            this.key = key;
        }

        void setUrl(String url) {
            this.url = url;
        }

        void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        void setAppId(String appId) {
            this.appId = appId;
        }

        void setPagePath(String pagePath) {
            this.pagePath = pagePath;
        }

        public Button addSubButton(Button item) {
            this.subButtons.add(item);
            return this;
        }

        Button(WxButton.Group group, WxButton.Type type, boolean main, WxButton.Order order, String name,
               String key, String url, String mediaId, String appId, String pagePath) {
            super();
            this.group = group;
            this.type = type;
            this.main = main;
            this.order = order;
            this.name = name;
            this.key = key;
            this.url = url;
            this.mediaId = mediaId;
            this.appId = appId;
            this.pagePath = pagePath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Button)) {
                return false;
            }

            Button that = (Button) o;

            // 子菜单数量不同，直接不相等
            if (this.getSubButtons().size() != that.getSubButtons().size()) {
                return false;
            }

            // 父菜单只比较name和子
            if (this.getSubButtons().size() > 0 && that.getSubButtons().size() > 0) {
                if (this.getSubButtons().equals(that.getSubButtons())) {
                    return getName().equals(that.getName());
                }
                return false;
            }
            // 非父菜单，全部比较，要把每个类型的比较摘出来，不想摘了
            if (getType() != that.getType()) {
                return false;
            }
            if (!getName().equals(that.getName())) {
                return false;
            }
            // VIEW会自动抹掉key，只有两个key都非null的时候才做下一步判断
            // if (getKey() != null && that.getKey() != null && !that.getKey().equals(getKey())) {
            // 上面判断不再适用
            if (!Objects.equals(getKey(), that.getKey())) {
                return false;
            }
            // 同上
            // getUrl() != null && that.getUrl() != null && !getUrl().equals(that.getUrl())
            // 上面判断不再适用
            if (!Objects.equals(getUrl(), that.getUrl())) {
                return false;
            }
            // 小程序类型，做特殊判断
            if (getType() == WxButton.Type.MINI_PROGRAM) {
                if (!Objects.equals(getAppId(), that.getAppId()) || !Objects.equals(getPagePath(), that.getPagePath()) || !Objects.equals(getUrl(), that.getUrl())) {
                    return false;
                }
            }
            return Objects.equals(getMediaId(), that.getMediaId());
        }

        @Override
        public int hashCode() {
            int result = getSubButtons() != null ? getSubButtons().hashCode() : 0;
            result = 31 * result + getGroup().hashCode();
            result = 31 * result + getType().hashCode();
            result = 31 * result + getName().hashCode();
            result = 31 * result + (isMain() ? 1 : 0);
            result = 31 * result + (getOrder() != null ? getOrder().hashCode() : 0);
            result = 31 * result + (getKey() != null ? getKey().hashCode() : 0);
            result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
            result = 31 * result + (getMediaId() != null ? getMediaId().hashCode() : 0);
            return result;
        }

        public static Builder builder() {
            return new Builder();
        }

        @Override
        public String toString() {
            return "com.mxixm.fastboot.weixin.module.menu.WxMenu.Button(subButtons=" + this.getSubButtons() + ", group=" + this.getGroup() + ", type=" + this.getType() + ", name=" + this.getName() + ", main=" + this.isMain() + ", order=" + this.getOrder() + ", key=" + this.getKey() + ", url=" + this.getUrl() + ", mediaId=" + this.getMediaId() + ")";
        }

        public static class Builder {

            private WxButton.Type type;
            private WxButton.Group group;
            private boolean main;
            private WxButton.Order order;
            private String name;
            private String key;
            private String url;
            private String mediaId;
            private String appId;
            private String pagePath;

            Builder() {
                super();
            }

            public Builder setGroup(WxButton.Group group) {
                this.group = group;
                return this;
            }

            public Builder setType(WxButton.Type type) {
                this.type = type;
                return this;
            }

            public Builder setMain(boolean main) {
                this.main = main;
                return this;
            }

            public Builder setOrder(WxButton.Order order) {
                this.order = order;
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

            public Builder setAppId(String appId) {
                this.appId = StringUtils.isEmpty(appId) ? null : appId;
                return this;
            }

            public Builder setPagePath(String pagePath) {
                this.pagePath = StringUtils.isEmpty(pagePath) ? null : pagePath;
                return this;
            }

            public Builder setUrl(String url) {
                // 如果是callbackUrl，则重定向，否则不重定向
                // WxUrlUtils.absoluteUrl()
                if (!StringUtils.isEmpty(url)) {
                    url = WxUrlUtils.absoluteUrl(url);
                    // 因为菜单可以在未关注时打开，所以菜单的链接也要用snsapi_userinfo方式
                    this.url = WxUrlUtils.isCallbackUrl(url) ? WxRedirectUtils.redirect(url) : url;
                }
                return this;
            }

            public Builder setMediaId(String mediaId) {
                this.mediaId = StringUtils.isEmpty(mediaId) ? null : mediaId;
                return this;
            }

            public Button build() {
                Assert.isTrue(!StringUtils.isEmpty(name), "菜单名不能为空");
                Assert.notNull(type, "菜单必须有类型");
                Assert.notNull(group, "菜单必须有分组");
                if (this.main) {
                    // main菜单没有order
                    this.order = null;
                    // 判断一级菜单长度小于等于16
                    Assert.isTrue(name.getBytes(StandardCharsets.UTF_8).length <= 16, "一级菜单名过长，不能超过16字节");
                } else {
                    // 判断二级菜单长度小于等于60
                    Assert.isTrue(main || name.getBytes().length <= 60, "二级菜单名过长，不能超过60字节");
                }
                switch (this.type) {
                    case VIEW: {
                        Assert.isTrue(!StringUtils.isEmpty(url), "view类型必须有url");
                        // view类型key、mediaId、appId、pagePath是无效的
                        return new Button(group, type, main, order, name, null, url, null, null, null);
                    }
                    case MEDIA_ID:
                    case VIEW_LIMITED: {
                        Assert.isTrue(!StringUtils.isEmpty(mediaId), "media_id类型和view_limited类型必须有mediaId");
                        // media_id类型和view_limited类型key、url、appId、pagePath是无效的
                        return new Button(group, type, main, order, name, null, null, mediaId, null, null);
                    }
                    case MINI_PROGRAM: {
                        Assert.isTrue(!StringUtils.isEmpty(appId) && !StringUtils.isEmpty(pagePath) && !StringUtils.isEmpty(this.url),
                                "miniprogram类型必须有appid、pagepath和url");
                        // miniprogram中，appid, pagepath, url是有效的。
                        return new Button(group, type, main, order, name, null, url, null, appId, pagePath);
                    }
                    default: {
                        // 当类型为view的时候，key是url，忽略assert
                        Assert.isTrue(!StringUtils.isEmpty(key), "click等类型必须有key");
                        Assert.isTrue(key.getBytes().length <= 128, "key过长，不能超过128字节");
                        // 其他类型只有key是有效的
                        return new Button(group, type, main, order, name, key, null, null, null, null);
                    }
                }
            }
        }
    }
}