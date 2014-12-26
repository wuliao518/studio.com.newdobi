package com.dobi.ui;

import com.dobi.R;
import com.dobi.common.ConstValue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ThreeHomeActivity extends BaseActivity implements OnClickListener{
	private ImageView news1,news2,news3;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newtest);
		intent=new Intent();
		news1=(ImageView) findViewById(R.id.news1);
		news2=(ImageView) findViewById(R.id.news2);
		news3=(ImageView) findViewById(R.id.news3);
		news1.setOnClickListener(this);
		news2.setOnClickListener(this);
		news3.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.news1:
			intent.setClass(ThreeHomeActivity.this, JiangActivity.class);
			intent.putExtra("type", ConstValue.GOODS_199);
			startActivity(intent);
			break;
		case R.id.news2:
			intent.setClass(ThreeHomeActivity.this, JiangActivity.class);
			intent.putExtra("type", ConstValue.GOODS_299);
			startActivity(intent);
			break;
		case R.id.news3:
			intent.setClass(ThreeHomeActivity.this, JiangActivity.class);
			intent.putExtra("type", ConstValue.GOODS_399);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	public void finishActivity(View view){
		intent.setClass(ThreeHomeActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
}
