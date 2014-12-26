package com.dobi.ui;

import com.dobi.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class ServerActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
	}
	public void finishActivity(View view){
		finish();
	}
}
