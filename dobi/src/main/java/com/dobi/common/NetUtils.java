package com.dobi.common;

import java.io.File;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetUtils {
	public static final String URL_PREFIX="http://api.do-bi.cn/api.php/home/";
	public static final String IMAGE_PREFIX="http://api.do-bi.cn";
	public static AsyncHttpClient client= new AsyncHttpClient();;
	static{
		client.setTimeout(10000);
	}
	/**
	 * 短信发送
	 * @param params
	 * @param asy
	 */
	public static void smsRegiste(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("login/smsSign", params, asy);
	}
	/**
	 * 验证短信
	 * @param params
	 * @param asy
	 */
	public static void validateSms(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("login/validateMobileReg", params, asy);
	}
	/**
	 * 注册
	 * @param params
	 * @param asy
	 */
	public static void registe(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("login/sign", params, asy);
	}
	/**
	 * 登陆
	 * @param params
	 * @param asy
	 */
	public static void login(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("login/login", params, asy);
	}
	/**
	 * 购物主界面
	 * @param asy
	 */
	public static void mainFrame(int type,AsyncHttpResponseHandler asy){
		doGet("home/mainFrame?type="+type,asy);
	}
	/**
	 * 选定商品详情
	 * @param uId
	 * @param hdId
	 * @param bdId
	 * @param asy
	 */
	public static void selectGoods(String uId,String hdId,String bdId,AsyncHttpResponseHandler asy){
		String url="goods/selectGoodsInfo?uid="+uId+"&hd_id="
				+hdId+"&bd_id="+bdId;
		doGet(url, asy);
	}
	/**
	 * 确认购买商品接口
	 * @param uId
	 * @param hdId
	 * @param bdId
	 * @param number
	 * @param size
	 */
	public static void commitGoods(RequestParams params,AsyncHttpResponseHandler asy){
		//String url="goods/confirmBuyGoods/uid/20/hd_id/1/bd_id/2/num/2/size_type/1?uid="+uId+"&hd_id="
		//		+hdId+"&bd_id="+bdId+"&num="+num+"&size_type="+size;
		//doGet(url, asy);
		doPost("goods/confirmBuyGoods",params,asy);
	}
	/**
	 * 加入购物车
	 * @param uId
	 * @param hdId
	 * @param bdId
	 * @param num
	 * @param size
	 * @param asy
	 */
	public static void addCard(RequestParams params,AsyncHttpResponseHandler asy){
		//String url="trolley/addGoods/uid/20/hd_id/1/bd_id/2/num/2/size_type/1?uid="+uId+"&hd_id="
		//		+hdId+"&bd_id="+bdId+"&num="+num+"&size_type="+size;
		//doGet(url, asy);
		doPost("trolley/addGoods", params,asy);
	}
	/**
	 * 购物车列表
	 * @param uId
	 * @param asy
	 */
	public static void listCard(String uId,AsyncHttpResponseHandler asy){
		String url="trolley/listGoods?uid="+uId;
		doGet(url, asy);
	}
	
	
	
	/**
	 * 基础post
	 * @param url
	 * @param params
	 * @param asy
	 */
	private static void doPost(String url,RequestParams params,AsyncHttpResponseHandler asy){
		client.post(URL_PREFIX+url, params, asy);
	}
	/**
	 * 基础get
	 * @param url
	 * @param asy
	 */
	private static void doGet(String url,AsyncHttpResponseHandler asy){
		client.get(URL_PREFIX+url, asy);
	}
	/**
	 * 
	 * @param uId 用户id
	 * @param type 第几张，从1开始
	 * @param name 图片名称，自定义
	 * @param file 上传图片
	 * @param asy
	 * @throws Exception
	 */
	public static void doUpload(String uId,int type,String name,File file,AsyncHttpResponseHandler asy) throws Exception{
		RequestParams params=new RequestParams();
		params.put("file",file);
		params.put("uid", uId);
		params.put("type", type);
		params.put("name", name);
		client.post(URL_PREFIX+"image/uploadImage/", params, asy);
	}
	public static void uploadUserAvatar(String uId,String name,File file,AsyncHttpResponseHandler asy) throws Exception{
		RequestParams params=new RequestParams();
		params.put("profile_picture",file);
		params.put("uid", uId);
		params.put("name", name);
		client.post(URL_PREFIX+"User/modUserAvatar/", params, asy);
	}
	/**
	 * 获取视屏
	 */
	public static void getVideoList(int start,int num,AsyncHttpResponseHandler asy) {
		String url="video/videoList?start="+start+"&num="+num;
		doGet(url, asy);
	}
	/**
	 * 获取主页所有数据
	 * @param asy
	 */
	public static void getAllInfo(AsyncHttpResponseHandler asy){
		String url="image/getDisguiseImage/type/0";
		doGet(url, asy);
	}
	
	/**
	 * 收货地址增加
	 * @param params
	 * @param asy
	 */
	public static void addressAdd(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("post/OperationPost", params, asy);
	}
	/**
	 * RSA加密
	 * @param params
	 * @param asy
	 */
	public static void RSASign(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("payment/privateKaySign", params, asy);
	}
	/**
	 * 删除购物车
	 * @param params
	 * @param asy
	 */
	public static void deleteCart(RequestParams params,AsyncHttpResponseHandler asy) {
		doPost("trolley/deleteTrolleyGood",params,asy);
	}
	/**
	 * 订单确认
	 * @param params
	 * @param asy
	 */
	public static void goodsOrder(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("goods/goodsOrder",params,asy);
	}
	/**
	 * 购物车内确认
	 * @param params
	 * @param asy
	 */
	public static void cartCommit(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("trolley/confirmGoods", params, asy);
	}
	/**
	 * 改变商品数量
	 * @param params
	 * @param asy
	 */
	public static void changeGoodsNum(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("goods/changeGoodsNum", params, asy);
	}
	/**
	 * 获取商品图片
	 * @param uid
	 * @param hd_id
	 * @param bd_id
	 * @param asy
	 */
	public static void getImageUrl(String uid,String hd_id,String bd_id,AsyncHttpResponseHandler asy){
		String url="goods/selectGoodsInfo?uid="+uid+"&hd_id="+hd_id+"&bd_id="+bd_id;
		doGet(url, asy);
	}
	/**
	 * 支付返回
	 * @param params
	 * @param asy
	 */
	public static void payReturn(RequestParams params,AsyncHttpResponseHandler asy){
		doPost("payment/payReturn", params, asy);
	}
	/**
	 * 设置默认地址
	 * @param params
	 * @param asyncHttpResponseHandler
	 */
	public static void setDefaultAddress(RequestParams params,
			AsyncHttpResponseHandler asy) {
		doPost("post/setDefaultPost", params, asy);
	}
	public static void listAddress(String uid,AsyncHttpResponseHandler asy){
		String url="post/postInfoList?uid="+uid;
		doGet(url,asy);
	}
	public static void getUserInfo(String uid,
			AsyncHttpResponseHandler asy) {
		String url="user/userInfo?uid="+uid;
		doGet(url,asy);
	}
	public static void commitOption(RequestParams params,
			AsyncHttpResponseHandler asy) {
		doPost("home/feedBack", params, asy);
	}
	public static void getOrderList(RequestParams params,
			AsyncHttpResponseHandler asy) {
		doPost("order/getUserOrder", params, asy);
	}
	public static void updatePassword(RequestParams params,
			AsyncHttpResponseHandler asy) {
		doPost("user/changePass", params, asy);
	}
	public static void validateCode(RequestParams params,
			AsyncHttpResponseHandler asy) {
		doPost("login/mobileChangePass", params, asy);
	}
	
	/**
	 * 购物车列表
	 * @param uId
	 * @param asy
	 */
	public static void loadNotice(String start,String num,AsyncHttpResponseHandler asy){
		String url="news/newsList?start="+start+"&num="+num;
		doGet(url, asy);
	}
	
	/**
	 * 地址详细列表
	 * @param uId
	 * @param asy
	 */
	public static void getDescAddress(String uid,String postId,AsyncHttpResponseHandler asy){
		String url="post/postInfo?uid="+uid+"&postId="+postId;
		doGet(url, asy);
	}
	public static void updateAvaor(RequestParams params,
			AsyncHttpResponseHandler asy) {
		doPost("user/modUserAvatar", params, asy);
		
	}
	public static void deleteOrder(RequestParams params,
			AsyncHttpResponseHandler asy) {
		doPost("order/delOrderInfo", params, asy);
	}
	//删除地址
	public static void deleteAddress(RequestParams params,
			AsyncHttpResponseHandler asy){
		doPost("post/delPostInfo", params, asy);
	}
}
