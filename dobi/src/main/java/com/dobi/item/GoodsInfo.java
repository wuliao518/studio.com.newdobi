package com.dobi.item;

import java.io.Serializable;
import java.util.ArrayList;

public class GoodsInfo implements Serializable{
	private ArrayList<PostInfo> postAddrList;
	private ArrayList<CartItem> goodsList;
	private ArrayList<String> userImage;
	public ArrayList<PostInfo> getPostAddrList() {
		return postAddrList;
	}
	public void setPostAddrList(ArrayList<PostInfo> postAddrList) {
		this.postAddrList = postAddrList;
	}
	public ArrayList<CartItem> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(ArrayList<CartItem> goodsList) {
		this.goodsList = goodsList;
	}
	public ArrayList<String> getUserImage() {
		return userImage;
	}
	public void setUserImage(ArrayList<String> userImage) {
		this.userImage = userImage;
	}

}
