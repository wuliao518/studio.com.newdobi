/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.dobi.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088612687344601";

	//收款支付宝账号
	public static final String DEFAULT_SELLER = "dobi@d-bi.cn";

	//商户私钥，自助生成
	public static final String PRIVATE = 
			"MIICXgIBAAKBgQDlO77rhvgOEE65mZ7FnW6STWX5WUeIzV2n6RC9XLbkkkyZTl+G" +
			"/N+73p1QXK0LafkRZEGt+8JKDp/5JMUh42YSrjjhPygGG+hvBCTYd+Z8LYk6lzNq" +
			"sWO9Qd+MB7hvcVb4ffDeni2xW8H3Q2K1Jx/OCxdQmxHwJqjEUIrGcaJJsQIDAQAB" +
			"AoGBAJnwvG7Y7b4sD2IumuQkrDuNxJxl61cgavO23Wn9WEk5Qj1110sEiI9WmRh9" +
			"lCvlF8EpLb5UIo6vMZkTq1rtpf5CAVaUytxXmhiktX6aMkLkad2vDhDLl52rkEwW" +
			"Yo88/9jALoAB7bkyOTp7MTM5RnidTjDZ2na+KG6IhYR8Q9TRAkEA+fgNtfx2S4kf" +
			"LPbnKllt9icN9XQF1NBrFEOjwd46OWLJOTQCLAk+o7Fz/YRwrsw3Ho6b/NCSz0Zc" +
			"UybaOCHSjQJBAOrDniqlURVrjf5QSGGSrgbqAwZ1vNX3HHufhwBnF1osVENCQsbt" +
			"tkTbHe/wh5Gr+bF71QvSvRY96fKJODW1HLUCQQCkU2BYEaaGk5cODSX8Xhv+pL/1" +
			"axdmRrkN8kVV7kxia0GTPFBtOIqYO9DfiVA2aQOXL/L1qPvKDRwmgbUVD48FAkBv" +
			"qNkayYR2XrCrq/2xQvW43ibQNMG7Nwx6FBc+7smhnwH495QRT9DokRt9GvcwJw7d" +
			"Btv/Ap3i7VHPhMqSaPEdAkEAhsCmy60GYvHGPOobYZXdS3oDGzHOYpLOsgooo1+8" +
			"/98Q7OFaMQBWJGfJ02US7U4ffEYFafRLYriVAE+sAjlRXw==";

	//支付宝公钥
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRA" +
			"FljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQE" +
			"B/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5Ksi" +
			"NG9zpgmLCUYuLkxpLQIDAQAB";
	public static final String ALIPAY_PLUGIN_NAME ="alipay_msp.apk";

}
