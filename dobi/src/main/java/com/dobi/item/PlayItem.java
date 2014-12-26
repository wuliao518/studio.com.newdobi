package com.dobi.item;

import java.util.List;

public class PlayItem {
	private String name;
	private List<ShowItem> items;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ShowItem> getItems() {
		return items;
	}
	public void setItems(List<ShowItem> items) {
		this.items = items;
	}
}
