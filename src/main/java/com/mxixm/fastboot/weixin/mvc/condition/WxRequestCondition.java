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

package com.mxixm.fastboot.weixin.mvc.condition;

import com.mxixm.fastboot.weixin.module.Wx;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.util.WxWebUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

/**
 * FastBootWeixin WxRequestCondition
 * 静态工厂方法，理论上应该放到AbstractWxEnumCondition中的，或者这里换成实例工厂方法，暂时先不修改
 *
 * @author Guangshan
 * @date 2018-9-17 10:03:14
 * @since 0.7.0
 */
public interface WxRequestCondition<T> extends RequestCondition<T> {

	enum Type {
		CATEGORY(null, "categories"),
		BUTTON_TYPE(Wx.Category.BUTTON, "buttonTypes"),
		BUTTON_KEY(Wx.Category.BUTTON, "buttonKeys"),
		BUTTON_NAME(Wx.Category.BUTTON, "buttonNames"),
		BUTTON_URL(Wx.Category.BUTTON, "buttonUrls"),
		BUTTON_MEDIA_ID(Wx.Category.BUTTON, "buttonMediaIds"),
		BUTTON_APP_ID(Wx.Category.BUTTON, "buttonAppIds"),
		BUTTON_PAGE_PATH(Wx.Category.BUTTON, "buttonPagePaths"),
		BUTTON_ORDER(Wx.Category.BUTTON, "buttonOrders"),
		BUTTON_GROUP(Wx.Category.BUTTON, "buttonGroups"),
		BUTTON_LEVEL(Wx.Category.BUTTON, "buttonLevels"),
		MESSAGE_TYPE(Wx.Category.MESSAGE, "messageTypes"),
		MESSAGE_CONTENT(Wx.Category.MESSAGE, "messageContents"),
		EVENT_TYPE(Wx.Category.EVENT, "eventTypes"),
		EVENT_SCENE(Wx.Category.EVENT, "eventScenes"),
		EVENT_KEY(Wx.Category.EVENT, "eventKeys"),
		COMPOSITES(null, "composites"),
		OTHER(null, "others");

		private Wx.Category category;

		private String text;

		Type(Wx.Category category, String text) {
			this.category = category;
			this.text = text;
		}

		public Wx.Category getCategory() {
			return category;
		}

		@Override
		public String toString() {
			return this.text;
		}
	}

	/**
	 * 获取匹配条件
	 * @param request
	 * @return WxRequestCondition
	 */
	@Override
	default T getMatchingCondition(HttpServletRequest request) {
		WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
		if (wxRequest == null) {
			return null;
		}
		return getMatchingCondition(wxRequest);
	}

	/**
	 * 转换为微信请求
	 * @param wxRequest
	 * @return WxRequestCondition
	 */
	T getMatchingCondition(WxRequest wxRequest);

	/**
	 * 两者比较
	 * @param other
	 * @param request
	 * @return 比较结果
	 */
	@Override
	default int compareTo(T other, HttpServletRequest request) {
		WxRequest wxRequest = WxWebUtils.getWxRequestFromRequest(request);
		return compareTo(other, wxRequest);
	}

	/**
	 * 转换为微信请求
	 * @param other 其他条件
	 * @param wxRequest 微信请求
	 * @return 比较结果
	 */
	int compareTo(T other, WxRequest wxRequest);

	/**
	 * 获取条件类型
	 * @return
	 */
	WxRequestCondition.Type getType();

	/**
	 * 获取条件名，用于toString()
	 * @return
	 */
	default String getName() {
		return this.getType() != null ? this.getType().name() : "unknown";
	}

}
