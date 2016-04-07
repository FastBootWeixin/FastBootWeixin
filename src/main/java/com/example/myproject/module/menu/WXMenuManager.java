package com.example.myproject.module.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.example.myproject.annotation.WXMenu;
import com.example.myproject.module.WXButton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WXMenuManager {

	private Map<String, WXButton> menuNameMap = new HashMap<>();
	
	private Map<String, WXButton> menuIdMap = new HashMap<>();
	
	private Map<String, WXButton> menuMap = new HashMap<>();
	
	private String menuJsonCache;
	
	private WXMenuManager() {}
	
	private static WXMenuManager instance = new WXMenuManager();
	
	public static WXMenuManager getInstance() {
		return instance;
	}
	
	public void add(WXButton button) {
		if (!StringUtils.isEmpty(button.getKey())) {
			menuIdMap.put(button.getKey(), button);
		}
		menuNameMap.put(button.getName(), button);
	}
	
	public void add(WXMenu menu) {
		WXButton button = new WXButton();
		button.setKey(StringUtils.isEmpty(menu.key()) ? null : menu.key());
		button.setMediaId(StringUtils.isEmpty(menu.mediaId()) ? null : menu.mediaId());
		button.setName(StringUtils.isEmpty(menu.name()) ? null : menu.name());
		button.setUrl(StringUtils.isEmpty(menu.url()) ? null : menu.url());
		button.setSubButtonString(menu.subMenu());
		button.setType(menu.type());
		menuNameMap.put(button.getName(), button);
	}
	
	//有空了改成lambda表达式，先用老循环
	public String getMenuJson() {
		if (menuJsonCache == null) {
			Set<String> subMenus = new HashSet<>();
			for (WXButton button : menuNameMap.values()) {
				if (button.getSubButtonString().length > 0) {
					for (String buttonString : button.getSubButtonString()) {
						button.addSubButton(menuNameMap.get(buttonString));
						subMenus.add(buttonString);
						//之前被误插入子菜单的移除掉
						menuMap.remove(buttonString);
					}
				}
				//防止被加到子菜单中的又被加一次
				if (!subMenus.contains(button.getName())) {
					menuMap.put(button.getName(), button);
				}
			}
			Map<String, Object> map = new HashMap<>();
			map.put("button", menuMap.values());
			try {
				menuJsonCache = new ObjectMapper().writeValueAsString(map);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return menuJsonCache;
	}
	
}
