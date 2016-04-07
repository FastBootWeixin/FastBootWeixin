package com.example.myproject.module.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.example.myproject.annotation.WXMenu;
import com.example.myproject.module.WXMenuItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WXMenuManager {

	private Map<String, WXMenuItem> menuNameMap = new HashMap<>();
	
	private Map<String, WXMenuItem> menuIdMap = new HashMap<>();
	
	private Map<String, WXMenuItem> menuMap = new HashMap<>();
	
	private String menuJsonCache;
	
	private WXMenuManager() {}
	
	private static WXMenuManager instance = new WXMenuManager();
	
	public static WXMenuManager getInstance() {
		return instance;
	}
	
	public void add(WXMenuItem button) {
		if (!StringUtils.isEmpty(button.getKey())) {
			menuIdMap.put(button.getKey(), button);
		}
		menuNameMap.put(button.getName(), button);
	}
	
	public void add(WXMenu menu) {
		WXMenuItem button = WXMenuItem.create()
				.setKey(menu.key())
				.setMediaId(menu.mediaId())
				.setName(menu.name())
				.setUrl(menu.url())
				.setSubMenuStrings(menu.subMenu())
				.setType(menu.type()).build();
		menuNameMap.put(button.getName(), button);
	}
	
	//有空了改成lambda表达式，先用老循环
	public String getMenuJson() {
		if (menuJsonCache == null) {
			Set<String> subMenus = new HashSet<>();
			for (WXMenuItem button : menuNameMap.values()) {
				if (button.getSubMenuStrings().length > 0) {
					for (String buttonString : button.getSubMenuStrings()) {
						button.addSubMenu(menuNameMap.get(buttonString));
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
