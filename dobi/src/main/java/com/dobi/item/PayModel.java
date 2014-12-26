package com.dobi.item;

import java.util.ArrayList;

public class PayModel {
	private String orderId;
	private String orderNum;
	private String pid;
	private String goodsNum;
	private String totle;
	private String cTime;
	private String postAddr;
	//物流状态
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPostAddr() {
		return postAddr;
	}
	public void setPostAddr(String postAddr) {
		this.postAddr = postAddr;
	}
	public String getcTime() {
		return cTime;
	}
	public void setcTime(String cTime) {
		this.cTime = cTime;
	}
	private ArrayList<CartItem> cartItems;
	public ArrayList<CartItem> getCartItems() {
		return cartItems;
	}
	public void setCartItems(ArrayList<CartItem> cartItems) {
		this.cartItems = cartItems;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}
	public String getTotle() {
		return totle;
	}
	public void setTotle(String totle) {
		this.totle = totle;
	}
	
}
