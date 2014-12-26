package com.dobi.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.common.NetUtils;
import com.dobi.ui.HomeActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.weibo.sdk.android.network.ReqParam;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {
	private Button signin_button;
	private Button mRegist, mforgetPwd;
	private EditText usernameEdit, passwordEdit;
	private Dialog dialog;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		ini();
	}

	private void ini() {
		signin_button = (Button) findViewById(R.id.signin_button);
		mRegist = (Button) findViewById(R.id.mRegist);
		mforgetPwd = (Button) findViewById(R.id.mforgetPwd);
		usernameEdit = (EditText) findViewById(R.id.username_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);
		dialog = CommonMethod.showMyDialog(LoginActivity.this);
		signin_button.setOnClickListener(this);
		mRegist.setOnClickListener(this);
		mforgetPwd.setOnClickListener(this);

	}

	public void onClick(View v) {
		intent = new Intent();
		switch (v.getId()) {
		case R.id.signin_button:
			String username = usernameEdit.getText().toString().trim();
			String password = passwordEdit.getText().toString().trim();
			SharedPreferences sp = CommonMethod
					.getPreferences(getApplicationContext());
			final Editor edit = sp.edit();
			dialog.show();
			RequestParams params = new RequestParams();
			params.put("username", username);
			params.put("password", password);
			NetUtils.login(params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data){
					Log.e("jiang", new String(data));
					dialog.dismiss();
					try {
						JSONObject json = new JSONObject(new String(data));
						if (json.getInt("status") == 1) {
							Log.e("jiang", json.getInt("uid") + "");
							edit.putString("uid", json.getInt("uid") + "");
							edit.putString("avatar", json.getString("avatar"));
							edit.putString("username", json.getString("username"));
							edit.commit();
							Toast.makeText(getApplicationContext(),
									json.getString("message"), 0).show();
							finish();
						} else {
							Toast.makeText(getApplicationContext(),
									json.getString("message"), 0).show();
						}
					} catch (JSONException e) {
						Toast.makeText(getApplicationContext(), "登陆出错，稍后再试！", 0)
								.show();
					}
				}
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] data,
						Throwable arg3) {
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "请检查网络稍后再试！", 0)
							.show();
				}
			});
			break;
		case R.id.mRegist:
			intent.setClass(LoginActivity.this, RegistActivity01.class);
			intent.putExtra("type", ConstValue.REGISTER_PASSWORD);
			startActivity(intent);
			break;
		case R.id.mforgetPwd:
			intent.setClass(LoginActivity.this, RegistActivity01.class);
			intent.putExtra("type", ConstValue.UPDATE_PASSWORD);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	public void finishActivity(View view){
		finish();
	}

}
