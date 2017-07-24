package com.example.myproject.module;

import java.util.HashSet;
import java.util.Set;

import com.example.myproject.module.menu.Button;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WXButton {
	
	@JsonProperty("sub_button")
	private Set<WXButton> subButton;
	
	@JsonIgnore
	private String[] subButtonString;
	
	@JsonInclude(Include.NON_NULL)
	private Button type;
	
	@JsonInclude(Include.NON_NULL)
	private String name;
	
	@JsonInclude(Include.NON_NULL)
	private String key;
	
	@JsonInclude(Include.NON_NULL)
	private String url;
	
	@JsonInclude(Include.NON_NULL)
	@JsonProperty("mediaId")
	private String mediaId;

	public Set<WXButton> getSubButton() {
		return subButton;
	}

	public void setSubButton(Set<WXButton> subButton) {
		this.subButton = subButton;
	}

	public String[] getSubButtonString() {
		return subButtonString;
	}

	public void setSubButtonString(String[] subButtonString) {
		this.subButtonString = subButtonString;
	}

	public Button getType() {
		return type;
	}

	public void setType(Button type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	
	public WXButton() {
		this.subButton = new HashSet<>();
		this.type = Button.click;
		this.name = null;
		this.mediaId = null;
		this.url = null;
		this.key = null;
	}
	
	public Set<WXButton> addSubButton(WXButton button) {
		this.subButton.add(button);
		return this.subButton;
	}
	
	public static void main(String[] args) throws JsonProcessingException {
		System.out.println(new ObjectMapper().writeValueAsString(new WXButton()));
	}
	
}
