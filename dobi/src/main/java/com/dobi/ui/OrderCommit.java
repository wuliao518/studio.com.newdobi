package com.dobi.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dobi.R;
import com.dobi.alipay.TedoAliPay;
import com.dobi.item.OrderInfo;

public class OrderCommit extends BaseActivity {
	private OrderInfo order;
	private TextView orderNum, orderTime, orderAddress, orderCount, orderType,
			orderPrice;
	private Button button;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ordercommit);
		order = (OrderInfo) getIntent().getExtras().get("order");
		intent=new Intent();
		initView();
		loadData();
	}

	private void loadData() {
		orderNum.setText(order.getBid());
		orderTime.setText(order.getcTime());
		orderAddress.setText(order.getPostAddr());
		orderCount.setText(order.getGoodsNum());
		orderType.setText("支付宝");
		orderPrice.setText(order.getTotal());
	}

	@SuppressLint("CutPasteId")
	private void initView() {
		orderNum = (TextView) findViewById(R.id.order_num);
		orderTime = (TextView) findViewById(R.id.order_time);
		orderAddress = (TextView) findViewById(R.id.order_address);
		orderCount = (TextView) findViewById(R.id.order_count);
		orderType = (TextView) findViewById(R.id.order_type);
		orderPrice = (TextView) findViewById(R.id.order_price);
		button = (Button) findViewById(R.id.orderPay);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TedoAliPay pay=new TedoAliPay(OrderCommit.this);
				pay.pay(order.getBid(),order.getTotal(),order.getOrderId());
			}
		});
	}
	
	public void finishActivity(View view){
		finish();
	}
}
