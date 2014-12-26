package com.dobi.alipay;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.android.app.sdk.AliPay;
import com.dobi.common.NetUtils;
import com.dobi.exception.ExitAppUtils;
import com.dobi.ui.HomeActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TedoAliPay {
	
	private static final int RQF_PAY = 1;
	private Activity mActivity;
	private String orderId;
	public TedoAliPay(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public void pay(String orderNum,String price,String orderId) {
		String info = getNewOrderInfo(orderNum,price);
		String sign = Rsa.sign(info, Keys.PRIVATE);
		this.orderId=orderId;
		sign = URLEncoder.encode(sign);
		info += "&sign=\"" + sign + "\"&" + getSignType();
		// start the pay.
		final String orderInfo = info;
		new Thread() {
			public void run() {
				AliPay alipay = new AliPay(mActivity, mHandler);
				String result = alipay.pay(orderInfo);
				Message msg = new Message();
				msg.what = RQF_PAY;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			RequestParams params=new RequestParams();
			params.put("orderId", orderId);
			switch (msg.what) {
			case RQF_PAY:
				String src = result.getSrc();
				Log.i("jiang", src);
				Log.e("jiang", (String) msg.obj);
				if(src.equals("9000")){
					params.put("status", 1);
					Toast.makeText(mActivity, "支付成功！", 0).show();
					ExitAppUtils.getInstance().finish();
					//返回到HomeActivity界面
					Intent intent=new Intent();
					intent.setClass(mActivity, HomeActivity.class);
					mActivity.startActivity(intent);
					NetUtils.payReturn(params,new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] data) {
							
						}
						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
							
						}
					});
				}else{
					Toast.makeText(mActivity, result.getResult(),
							Toast.LENGTH_SHORT).show();
					params.put("status", 0);
					NetUtils.payReturn(params,new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
							
						}
						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
							
						}
					});
				}
				break;
			default:
				break;
			}
		};
	};

	@SuppressWarnings("deprecation")
	private String getNewOrderInfo(String orderNum,String price) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(orderNum);
		sb.append("\"&subject=\"");
		sb.append("3d人偶");
		sb.append("\"&body=\"");
		sb.append("这是3d人偶");
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");
		// 网址需要做URL编码
		sb.append(URLEncoder
				.encode(NetUtils.IMAGE_PREFIX+"/api.php/home/payment/notify_url"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		return new String(sb);
	}

	@SuppressLint("SimpleDateFormat")
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);
		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
