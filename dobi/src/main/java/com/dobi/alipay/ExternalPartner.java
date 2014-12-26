package com.dobi.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alipay.android.app.sdk.AliPay;
import com.dobi.common.NetUtils;

public class ExternalPartner extends Activity implements OnItemClickListener,
		OnClickListener {
	public static final String TAG = "alipay-sdk";

	private static final int RQF_PAY = 1;

	private static final int RQF_LOGIN = 2;

	private EditText mUserId;
	private Button mLogon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.external_partner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST, 1, "快速登录");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			
			break;
		}
		return false;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		try {
			Log.i("ExternalPartner", "onItemClick");
			String info = getNewOrderInfo(position);
			
			String sign = Rsa.sign(info, Keys.PRIVATE);
			Log.e("jiang", "sign = " + sign);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("ExternalPartner", "start pay");
			// start the pay.
			

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(ExternalPartner.this, mHandler);
					
					//设置为沙箱模式，不设置默认为线上环境
					//alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getNewOrderInfo(int position) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(getOutTradeNo());
		sb.append("\"&subject=\"");
		sb.append(sProducts[position].subject);
		sb.append("\"&body=\"");
		sb.append(sProducts[position].body);
		sb.append("\"&total_fee=\"");
		sb.append(sProducts[position].price.replace("一口价:", ""));
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(NetUtils.IMAGE_PREFIX+"/api.php/home/payment/notify_url"));
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

	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);
		Log.d(TAG, "outTradeNo: " + key);
		return key;
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	
	private void doLogin() {
		final String orderInfo = getUserInfo();
		new Thread() {
			public void run() {
				String result = new AliPay(ExternalPartner.this, mHandler)
						.pay(orderInfo);

				Log.i(TAG, "result = " + result);
				Message msg = new Message();
				msg.what = RQF_LOGIN;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	private String getUserInfo() {
		String userId = mUserId.getText().toString();
		return trustLogin(Keys.DEFAULT_PARTNER, userId);

	}

	private String trustLogin(String partnerId, String appUserId) {
		StringBuilder sb = new StringBuilder();
		sb.append("app_name=\"mc\"&biz_type=\"trust_login\"&partner=\"");
		sb.append(partnerId);
		Log.d("TAG", "UserID = " + appUserId);
		if (!TextUtils.isEmpty(appUserId)) {
			appUserId = appUserId.replace("\"", "");
			sb.append("\"&app_id=\"");
			sb.append(appUserId);
		}
		sb.append("\"");

		String info = sb.toString();

		// 请求信息签名
		String sign = Rsa.sign(info, Keys.PRIVATE);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		info += "&sign=\"" + sign + "\"&" + getSignType();

		return info;
	}


	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);

			switch (msg.what) {
			case RQF_PAY:
			case RQF_LOGIN: {
				Toast.makeText(ExternalPartner.this, result.getResult(),
						Toast.LENGTH_SHORT).show();
			}
				break;
			default:
				break;
			}
		};
	};

	public static class Product {
		public String subject;
		public String body;
		public String price;
	}

	public static Product[] sProducts;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}