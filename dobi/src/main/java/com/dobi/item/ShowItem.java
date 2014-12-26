package com.dobi.item;

import java.util.List;

public class ShowItem {
	private String normalBack;
	private String selectedBack;
	private List<String> pathList;
	private String typeName;

	public String getNormalBack() {
		return normalBack;
	}

	public void setNormalBack(String normalBack) {
		this.normalBack = normalBack;
	}

	public String getSelectedBack() {
		return selectedBack;
	}

	public void setSelectedBack(String selectedBack) {
		this.selectedBack = selectedBack;
	}

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		return "ShowItem [normalBack=" + normalBack + ", selectedBack="
				+ selectedBack + ", pathList=" + pathList.size() + ", typeName="
				+ typeName + "]";
	}
	

}
