package com.dobi.db;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.ui.NoticeInfoActivity;
import com.dobi.ui.PromptActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyDialog extends Dialog implements android.view.View.OnClickListener{

	Context context;
	LinearLayout mLinearLayoutpush;
	TextView mDialogText,mDialogTitle;
	private SharedPreferences sp;	
	private String url;
	private Button mButtonpush;
	private ImageButton mbtn_back;
	Intent intent=new Intent();
	
	public MyDialog(Context context) {
		super(context);
		this.context = context;
	}
	public MyDialog(Context context,int theme){
		super(context,theme);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_push);
		ini();
		sp=CommonMethod.getPreferences(getContext());
		mDialogTitle.setText(sp.getString("custom", "好礼大派送"));
		mDialogText.setText(sp.getString("content", "新来的亲们，不要错过哦！"));
		url = sp.getString("url", "http://www.do-bi.cn");
		
	}

	private void ini() {
		// TODO Auto-generated method stub
		mLinearLayoutpush = (LinearLayout)findViewById(R.id.mLinearLayoutpush);
		mDialogText = (TextView)findViewById(R.id.mDialogText);
		mDialogTitle = (TextView)findViewById(R.id.mDialogTitle);
		mButtonpush = (Button)findViewById(R.id.mButtonpush);
		mbtn_back = (ImageButton)findViewById(R.id.mbtn_back);
		mLinearLayoutpush.setOnClickListener(this);
		mDialogText.setOnClickListener(this);
		mDialogTitle.setOnClickListener(this);
		mButtonpush.setOnClickListener(this);
		mbtn_back.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mLinearLayoutpush:
			intent.setClass(context, PromptActivity.class);
			intent.putExtra("url",url);
			intent.putExtra("title","活动");
			context.startActivity(intent);
			MyDialog.this.dismiss();
			Log.e("jiang", url);
			break;
		case R.id.mButtonpush:
			intent.setClass(context, PromptActivity.class);
			intent.putExtra("url",url);
			intent.putExtra("title","活动");
			context.startActivity(intent);
			MyDialog.this.dismiss();
			break;
		case R.id.mbtn_back:
			MyDialog.this.dismiss();
			break;
		default:
			break;
		}
		
	}

}
