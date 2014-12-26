package com.dobi.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.common.NetUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.common.message.Log;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class RegistActivity02 extends BaseActivity implements OnClickListener {

	private Button mBtn_step02;
	private EditText usernameEdit, pwdEdit1, pwdEdit2;
	private SharedPreferences sp;
	private Boolean isSelected = true;
	private ImageButton agree;
	Intent intent;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist_activity02);
		sp = CommonMethod.getPreferences(getApplicationContext());
		type = getIntent().getExtras().getInt("type");
		ini();
	}

	private void ini() {
		mBtn_step02 = (Button) findViewById(R.id.signin_button);
		usernameEdit = (EditText) findViewById(R.id.username_edit);
		pwdEdit1 = (EditText) findViewById(R.id.regit_pwd_01);
		pwdEdit2 = (EditText) findViewById(R.id.regit_pwd_02);
		agree = (ImageButton) findViewById(R.id.agree);
		agree.setOnClickListener(this);
		mBtn_step02.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		intent = new Intent();
		switch (v.getId()) {
		case R.id.signin_button:
			String pwdText1 = pwdEdit1.getText().toString().trim();
			String pwdText2 = pwdEdit2.getText().toString().trim();
			String username = usernameEdit.getText().toString().trim();
			if (!isSelected) {
				Toast.makeText(getApplicationContext(), "请查看协议！", 0).show();
				return;
			}
			if (username == null || pwdText1 == null || pwdText2 == null
					|| username.equals("") || pwdText1.equals("")
					|| pwdText2.equals("")) {
				Toast.makeText(getApplicationContext(), "输入不能为空！", 0).show();
				return;
			}
			if (pwdText1.equals(pwdText2)) {
				final Editor edit = sp.edit();
				edit.putString("password", pwdText1);
				edit.commit();
				if (type == ConstValue.REGISTER_PASSWORD) {// 注册

					// intent.setClass(RegistActivity02.this,
					// RegistActivity03.class);
					// startActivity(intent);
					String mobile = CommonMethod.getStringFromShare(
							getApplicationContext(), "mobile");
					RequestParams params = new RequestParams();
					params.put("username", username);
					params.put("password", pwdText1);
					params.put("mobile", mobile);
					NetUtils.registe(params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] data) {
							try {
								JSONObject json = new JSONObject(new String(
										data));
								if (json.getInt("status") == 1) {// 成功
									edit.putString("uid", json.getInt("uid")
											+ "");
									edit.putString("password", null);
									edit.commit();
									Toast.makeText(getApplicationContext(),
											json.getString("message"), 0)
											.show();
									intent.setClass(RegistActivity02.this,
											HomeActivity.class);
									startActivity(intent);
									RegistActivity02.this.finish();
								} else {
									Toast.makeText(getApplicationContext(),
											json.getString("message"), 0)
											.show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] data, Throwable arg3) {

						}
					});

				} else {// 修改密码
					RequestParams params = new RequestParams();
					params.put("new_pass", sp.getString("password", null));
					params.put("type", "1");
					params.put("mobile", sp.getString("mobile", null));
					NetUtils.updatePassword(params,
							new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(int arg0, Header[] arg1,
										byte[] arg2) {
									Editor edit = sp.edit();
									edit.clear();
									edit.commit();
									intent.setClass(RegistActivity02.this,
											HomeActivity.class);
									startActivity(intent);
									Toast.makeText(getApplicationContext(),
											"密码修改成功！", 0).show();
									finish();
								}

								@Override
								public void onFailure(int arg0, Header[] arg1,
										byte[] arg2, Throwable arg3) {

								}
							});
				}

			} else {
				Toast.makeText(RegistActivity02.this, "两次密码不一致", 0).show();
			}
			break;
		case R.id.agree:
			Log.e("jiang", "靠，设么叫师傅将快速");
			if (isSelected) {
				agree.setBackgroundResource(R.drawable.registration_confirm_btn_normal);
				isSelected = false;
			} else {
				agree.setBackgroundResource(R.drawable.registration_confirm_btn_selected);
				isSelected = true;
			}
			break;
		default:
			break;
		}

	}

	public void finishActivity(View view) {
		finish();
	}

}
