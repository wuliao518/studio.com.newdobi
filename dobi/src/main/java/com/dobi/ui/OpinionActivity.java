package com.dobi.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.NetUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class OpinionActivity extends BaseActivity {
	private Button button;
	private EditText mEditText;
	private SharedPreferences sp;
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);
		button=(Button) findViewById(R.id.commitOption);
		mEditText=(EditText) findViewById(R.id.editSms);
		dialog=CommonMethod.showMyDialog(OpinionActivity.this);
		sp=CommonMethod.getPreferences(getApplicationContext());
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
				RequestParams params=new RequestParams();
				params.put("content", mEditText.getText().toString().trim());
				params.put("uid", sp.getString("uid", null));
				NetUtils.commitOption(params,new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						Log.i("jiang", new String(data));
						try {
							dialog.dismiss();
							JSONObject json=new JSONObject(new String(data));
							if(json.getInt("status")==1){
								Toast.makeText(getApplicationContext(), "感谢您的反馈！", 0).show();
								finish();
							}else{
								Toast.makeText(getApplicationContext(), "信息反馈失败，请重试！", 0).show();
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
		});
	}
	public void finishActivity(View view){
		finish();
	}
	
}
