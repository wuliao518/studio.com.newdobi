package com.dobi.ui;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.NetUtils;
import com.dobi.item.PayModel;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NoPayActivity extends BaseActivity implements OnScrollListener{
	private ListView mListView;
	private int start=0;
	private final static int PAGER_NUMBER=5;
	private int currentCount=0;
	private LayoutInflater mInflater;
	private int firstItem=0;
	private int visibleItemCount=0;
	private NopayAdapter adapter;
	private View footer;
	private SharedPreferences sp;
	private TextView title;
	private ArrayList<PayModel> payList=new ArrayList<PayModel>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nonpayment);
		mListView=(ListView)findViewById(R.id.list_nopay);
		title=(TextView) findViewById(R.id.showText);
		title.setText("未支付");
		mInflater=LayoutInflater.from(NoPayActivity.this);
		mListView.setOnScrollListener(this);
		adapter=new NopayAdapter();
		sp=CommonMethod.getPreferences(getApplicationContext());
		loadData();
	}
	private void loadData() {
		RequestParams params=new RequestParams();
		params.put("start", start+currentCount*PAGER_NUMBER);
		params.put("num",PAGER_NUMBER);
		params.put("uid",sp.getString("uid", null));
		params.put("type", 0);
		NetUtils.getOrderList(params,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				Log.i("jiang", new String(data));
				try {
					JSONObject json=new JSONObject(new String(data));
					if(json.getInt("status")==1){
						JSONArray orderArray=json.getJSONArray("orderList");
						for(int i=0;i<orderArray.length();i++){
							JSONObject object=orderArray.getJSONObject(i);
							PayModel model=new PayModel();
							model.setOrderId(object.getString("orderId"));
							model.setOrderNum(object.getString("order_num"));
							model.setPid(object.getString("pid"));
							payList.add(model);
						}
						footer=mInflater.inflate(R.layout.item_footer, null);
						mListView.addFooterView(footer);
						footer.setVisibility(View.GONE);
						mListView.setAdapter(adapter);
						currentCount++;
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				
			}
		});
	}
	
	private class NopayAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return payList.size();
		}

		@Override
		public Object getItem(int position) {
			return payList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=mInflater.inflate(R.layout.item_nonpayment, null);
				viewHolder.goodsPrice=(TextView) convertView.findViewById(R.id.goodsPrice);
			}else{
				
			}
			return convertView;
		}
		
	}
	private static class ViewHolder{
		public TextView goodsPrice,goodsNum,totlePrice,goodsSize;
		public ImageButton commit,delete;
		public ImageView goodShow;
		
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			if((firstItem+visibleItemCount)>=payList.size()){
				footer.setVisibility(View.VISIBLE);
				RequestParams params=new RequestParams();
				params.put("start", start+currentCount*PAGER_NUMBER);
				params.put("num",PAGER_NUMBER);
				params.put("uid",sp.getString("uid", null));
				params.put("type", 0);
				NetUtils.getOrderList(params,new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						Log.i("jiang", new String(data));
						try {
							JSONObject json=new JSONObject(new String(data));
							if(json.getInt("status")==1){
								JSONArray orderArray=json.getJSONArray("orderList");
								for(int i=0;i<orderArray.length();i++){
									JSONObject object=orderArray.getJSONObject(i);
									PayModel model=new PayModel();
									model.setOrderId(object.getString("orderId"));
									model.setOrderNum(object.getString("order_num"));
									model.setPid(object.getString("pid"));
									payList.add(model);
								}
								footer.setVisibility(View.GONE);
								adapter.notifyDataSetChanged();
								currentCount++;
							}else{
								footer.setVisibility(View.GONE);
								Toast.makeText(getApplicationContext(), "加载完毕！", 0).show();
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
						
					}
				});
			}
		} 
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstItem=firstVisibleItem;
		this.visibleItemCount=visibleItemCount;
	}
	
	public void finishActivity(View view){
		finish();
	}
	
	
	
	
}
