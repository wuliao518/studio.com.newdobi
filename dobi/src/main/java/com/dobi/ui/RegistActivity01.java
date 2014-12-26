package com.dobi.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.common.NetUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegistActivity01 extends BaseActivity implements OnClickListener {
	private Button mBtn_step, token;
	private EditText phone, mcode;
	private int timeCount = 60;
	private Timer timer;
	private Dialog dialog;
	private int codeId=0;
	private String codeMessage;
	private String number;
	private int type;
	private TextView title;
	private SharedPreferences sp;
	Intent intent;
	@SuppressLint("HandlerLeak")
	private Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (timeCount >= 0) {
				token.setText(timeCount + "");
				timeCount--;
				token.setEnabled(false);
			} else {
				timeCount = 60;
				timer.cancel();
				token.setEnabled(true);
				token.setText("从新获取");
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist_activity01);
		type=getIntent().getExtras().getInt("type");
		sp=CommonMethod.getPreferences(RegistActivity01.this);
		ini();
	}

	private void ini() {
		mBtn_step = (Button) findViewById(R.id.mBtn_step);
		title=(TextView) findViewById(R.id.showText);
		//mBtn_step.setEnabled(false);
		token = (Button) findViewById(R.id.regit_token);
		phone = (EditText) findViewById(R.id.username_edit01);
		mcode = (EditText) findViewById(R.id.mCodeEdit);
		initTitle();
		dialog=CommonMethod.showMyDialog(RegistActivity01.this);
		mBtn_step.setOnClickListener(this);
		token.setOnClickListener(this);
	}

	private void initTitle() {
		if(type==ConstValue.REGISTER_PASSWORD){
			title.setText("注册");
		}else if(type==ConstValue.UPDATE_PASSWORD){
			title.setText("忘记密码");
		}
	}

	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		intent = new Intent();
		switch (v.getId()) {
		case R.id.mBtn_step:
//			intent.setClass(RegistActivity01.this, RegistActivity02.class);
//			startActivity(intent);
			number = phone.getText().toString();
			String strcode = mcode.getText().toString().trim();
			if(TextUtils.isEmpty(strcode)){
				Toast.makeText(RegistActivity01.this, "验证码不能为空！", 1).show();
			}else{
				Log.e("jiang", number+"..."+strcode+"..."+sp.getString("codeId", null));
				register(strcode);
			}
			break;
		case R.id.regit_token:
			number = phone.getText().toString();
			if (number.length() == 11) {
				dialog.show();
				Editor edit=sp.edit();
				edit.putString("mobile", number);
				edit.commit();
				if(type==ConstValue.REGISTER_PASSWORD){
					smsRegister();
				}else{
					update();
				}
			} else {
				Toast.makeText(RegistActivity01.this, "请输入正确的手机号！", 0).show();
			}
		default:
			break;
		}

	}

	private void smsRegister() {
		RequestParams params = new RequestParams();
		params.add("mobile", number);
		NetUtils.smsRegiste(params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				dialog.dismiss();
				try {
					JSONObject json = new JSONObject(new String(data));
					if(json.getInt("status")==1){
						Log.i("jiang", new String(data));
						codeMessage=json.getString("message");
						codeId=json.getInt("codeId");
						Editor edit=sp.edit();
						edit.putString("codeId", codeId+"");
						edit.commit();
						setTokenDisenable();
					}else{
						Toast.makeText(getApplicationContext(), json.getString("message"), 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] data,
					Throwable arg3) {
				dialog.dismiss();
			}
		});
	}

	private void update() {
		RequestParams params=new RequestParams();
		params.put("mobile", number);
		NetUtils.validateCode(params,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				dialog.dismiss();
				try {
					JSONObject json = new JSONObject(new String(data));
					if(json.getInt("status")==1){
						Log.i("jiang", new String(data));
						codeMessage=json.getString("message");
						codeId=json.getInt("codeId");
						Editor edit=sp.edit();
						edit.putString("codeId", codeId+"");
						edit.commit();
						setTokenDisenable();
					}else{
						Toast.makeText(getApplicationContext(), json.getString("message"), 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				dialog.dismiss();
			}
		});
	}

	private void register(String strcode) {
		RequestParams params=new RequestParams();
		params.put("mobile", number);
		params.put("codeId",sp.getString("codeId", null));
		params.put("code", strcode);
		NetUtils.validateSms(params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				try {
					JSONObject json=new JSONObject(new String(data));
					Log.e("jiang", new String(data));
					if(json.getInt("status")==1){
						intent.setClass(RegistActivity01.this, RegistActivity02.class);
						intent.putExtra("type", type);
						startActivity(intent);
					}else{
						Toast.makeText(getApplicationContext(), json.getString("message"), 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] data, Throwable arg3) {
				
			}
		});
	}

	public void setTokenDisenable() {
		mBtn_step.setEnabled(true);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handle.sendEmptyMessage(0);
			}
		}, 0, 1000);

	}
	public void finishActivity(View view){
		finish();
	}

}
