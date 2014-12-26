package com.dobi.view;

import java.util.List;

public interface ImageInterface {
	//默认状态
	public void setNormal(String normal);
	//选中状态
	public void setSelected(String selected);
	//设置listview数据
	public void setList(List list);
	//清除兄弟按钮的状态
}
