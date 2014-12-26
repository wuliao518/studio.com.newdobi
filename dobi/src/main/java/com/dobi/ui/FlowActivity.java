package com.dobi.ui;

import com.dobi.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FlowActivity extends Activity {
	private String[] statusStrs=new String[]{"正在获取订单照片","3D建模中","3D打印中","已发货"};
	private TextView tvStatus;
	private ImageView ivStatus;
	private int flowType=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flow);
		tvStatus=(TextView) findViewById(R.id.tv_flow_status);
		ivStatus=(ImageView) findViewById(R.id.iv_flow_status);
		flowType=getIntent().getExtras().getInt("flowType",0);
		loadData();
	}
	private void loadData() {
		if(flowType==0||flowType==1){//图片确认
			tvStatus.setText(statusStrs[0]);
			ivStatus.setImageResource(R.drawable.flow_one);
		}else if(flowType==2){//3d建模中
			tvStatus.setText(statusStrs[1]);
			ivStatus.setImageResource(R.drawable.flow_two);
		}else if(flowType==3){//3d打印中
			tvStatus.setText(statusStrs[2]);
			ivStatus.setImageResource(R.drawable.flow_three);
		}else if(flowType==4){//已发货
			tvStatus.setText(statusStrs[3]);
			ivStatus.setImageResource(R.drawable.flow_four);
		}else{
			tvStatus.setText("物流信息未知");
		}
	}
	public void finishActivity(View v) {
		finish();
	}
	
}
