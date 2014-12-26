package com.dobi.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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

public class UpdateActivity extends BaseActivity implements OnClickListener{
	private EditText old_edit,new_edit,commit_edit;
	private Button signin_button;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_pwd);
		old_edit=(EditText) findViewById(R.id.old_edit);
		new_edit=(EditText) findViewById(R.id.new_edit);
		commit_edit=(EditText) findViewById(R.id.commit_edit);
		signin_button=(Button) findViewById(R.id.signin_button);
		signin_button.setOnClickListener(this);
		sp=CommonMethod.getPreferences(getApplicationContext());
		
	}
	public void finishActivity(View v){
		finish();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signin_button:
			String oldPwd=old_edit.getText().toString().trim();
			String newPwd=new_edit.getText().toString().trim();
			String commitPwd=commit_edit.getText().toString().trim();
			if(newPwd.equals(commitPwd)){
				RequestParams params=new RequestParams();
				params.put("uid", sp.getString("uid", null));
				params.put("old_pass", oldPwd);
				params.put("new_pass", newPwd);
				params.put("type", "0");
				NetUtils.updatePassword(params,new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						try {
							JSONObject json=new JSONObject(new String(data));
							if(json.getInt("status")==1){
								Toast.makeText(getApplicationContext(), "修改成功", 0).show();
								Editor edit=sp.edit();
								edit.clear();
								edit.commit();
								Intent intent=new Intent();
								intent.setClass(UpdateActivity.this, HomeActivity.class);
								UpdateActivity.this.startActivity(intent);
							}else{
								Toast.makeText(getApplicationContext(), json.getString("message"), 0).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						Toast.makeText(getApplicationContext(), "未知错误！请重试。", 0).show();
					}
					
				});
			}else{
				
			}
			
			break;
		default:
			break;
		}
	}
}
