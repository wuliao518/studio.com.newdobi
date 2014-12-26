package com.dobi.ui;

import com.dobi.R;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class SingleActivity extends BaseActivity implements OnClickListener{
	private GridView mGridView;
	private HorizontalScrollView mScrollView;
	private TextView[] tvs=new TextView[10];
	private Button btnHZ,btnPropMore;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single);
		initView();	
	}
	//初始化组件
	private void initView() {
		for(int i=0;i<10;i++){
			tvs[i]=new TextView(getApplicationContext());
		}
		mScrollView=(HorizontalScrollView) findViewById(R.id.scrollImageView);
		mGridView=(GridView) findViewById(R.id.faceGridView);
		mGridView.setNumColumns(10);
		mGridView.setAdapter(new MyAdapter());
		
		/*button-----start-----*/
		btnHZ=(Button) findViewById(R.id.btnHZ);
		btnPropMore=(Button) findViewById(R.id.btnPropMore);
		btnHZ.setOnClickListener(this);
		btnPropMore.setOnClickListener(this);
		/*button-----end-----*/
		
		mScrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						float X = event.getX();
						int scrollX = view.getScrollX();
						int width = view.getWidth();
				}
				return false;
			}
		});
	}
	private class MyAdapter extends BaseAdapter{
		//private 
		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public Object getItem(int position) {
			return tvs[position];
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return tvs[position];
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnHZ:
			
			break;
		case R.id.btnPropMore:
			
			break;

		default:
			break;
		}
	}
	
}
