package com.dobi.item;

import java.io.Serializable;

public class PostInfo implements Serializable{
	private String postId;
	private String postName;
	private String postAddr;
	private Boolean isDefault;
	private String province;
	private String city;
	private String zone;
	private String addr;
	private String postNum;
	private String pMobile;
	private String pTel;
	
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPostNum() {
		return postNum;
	}
	public void setPostNum(String postNum) {
		this.postNum = postNum;
	}
	public String getpMobile() {
		return pMobile;
	}
	public void setpMobile(String pMobile) {
		this.pMobile = pMobile;
	}
	public String getpTel() {
		return pTel;
	}
	public void setpTel(String pTel) {
		this.pTel = pTel;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getPostName() {
		return postName;
	}
	public void setPostName(String postName) {
		this.postName = postName;
	}
	public String getPostAddr() {
		return postAddr;
	}
	public void setPostAddr(String postAddr) {
		this.postAddr = postAddr;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

}