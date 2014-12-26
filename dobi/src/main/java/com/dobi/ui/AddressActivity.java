package com.dobi.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ParagraphStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.NetUtils;
import com.dobi.item.PostInfo;
import com.facebook.android.AsyncFacebookRunner;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AddressActivity extends BaseActivity implements OnClickListener{
	private ListView mListView;
	private List<PostInfo> postInfos;
	private LayoutInflater mInflater;
	private TextView manager;
	private SharedPreferences sp;
	private int prePosition=-1;
	//是否将第一个地址标记为默认
	private Boolean isDefault=false;
	private AddressAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		mListView=(ListView) findViewById(R.id.list_address);
		manager=(TextView) findViewById(R.id.manager);
		manager.setOnClickListener(this);
		sp=CommonMethod.getPreferences(AddressActivity.this);
		mInflater=LayoutInflater.from(AddressActivity.this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				PostInfo post=(PostInfo) adapter.getItem(position);
				if(getIntent()!=null){
					//判断空，我就不判断了。。。。  
		            Intent intent=new Intent();  
		            intent.putExtra("address", post);  
		            //请求代码可以自己设置，这里设置成20  
		            setResult(20, intent);  
		            //关闭掉这个Activity  
		            finish();
				}
	              
				
//				RequestParams params=new RequestParams();
//				params.put("postId", postInfos.get(position).getPostId());
//				params.put("uid", sp.getString("uid", null));
//				NetUtils.setDefaultAddress(params,new AsyncHttpResponseHandler() {
//					@Override
//					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//						if(prePosition!=position){
//							//adapter.notifyDataSetChanged();
//							if(prePosition!=-1){
//								View preView=(View) mListView.getChildAt(prePosition);
//								TextView preTv=(TextView) preView.findViewById(R.id.item_postAddress);
//								String preAddress=preTv.getText().toString();
//								if(preAddress.startsWith("[默认]")){
//									Toast.makeText(getApplicationContext(), "haha", 0).show();
//									preAddress=preAddress.replace("[默认]", "");
//									preTv.setText(preAddress);
//									preTv.setCompoundDrawables(null, null,null,null);
//								}
//							}
//							prePosition=position;
//							TextView tv=(TextView) view.findViewById(R.id.item_postAddress);
//							String address=tv.getText().toString();
//							tv.setText("[默认]"+address);
//							Drawable drawable =getResources().getDrawable(R.drawable.shipping_address_selected);
//							drawable.setBounds(0, 0, tv.getHeight(), tv.getHeight());
//							tv.setCompoundDrawables(null, null,drawable,null);
//						}
//						
//					}
//					@Override
//					public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
//						
//					}
//				});
			}
		});
		

		
	}
	@Override
	protected void onStart() {
		super.onStart();
		postInfos=new ArrayList<PostInfo>();
		NetUtils.listAddress(sp.getString("uid", null), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				Log.i("jiang", new String(data));
				try {
					JSONObject json=new JSONObject(new String(data));
					JSONArray array=(JSONArray) json.get("postInfo");
					for(int i=0;i<array.length();i++){
						JSONObject object=array.getJSONObject(i);
						if(convertStringToBoolean(object.getString("isDefault"))){
							PostInfo info=new PostInfo();
							info.setPostId(object.getString("postId"));
							info.setPostName(object.getString("postName"));
							info.setPostAddr(object.getString("postAddr"));
							info.setIsDefault(convertStringToBoolean(object.getString("isDefault")));
							postInfos.add(info);
							isDefault=true;
						}
					}
					for(int i=0;i<array.length();i++){
						JSONObject object=array.getJSONObject(i);
						if(!convertStringToBoolean(object.getString("isDefault"))){
							PostInfo info=new PostInfo();
							info.setPostId(object.getString("postId"));
							info.setPostName(object.getString("postName"));
							info.setPostAddr(object.getString("postAddr"));
							info.setIsDefault(convertStringToBoolean(object.getString("isDefault")));
							postInfos.add(info);
						}
					}
					
					adapter=new AddressAdapter(); 
					mListView.setAdapter(adapter);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			private Boolean convertStringToBoolean(String str) {
				if(str.equals("1")){
					return true;
				}else if(str.equals("0")){
					return false;
				}
				return false;
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] data, Throwable arg3) {
				
			}
		});
	}
	private class AddressAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return postInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return postInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			PostInfo info=postInfos.get(position);
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=mInflater.inflate(R.layout.item_address, null);
				viewHolder.postMan=(TextView) convertView.findViewById(R.id.item_postName);
				viewHolder.postAddress=(TextView) convertView.findViewById(R.id.item_postAddress);
				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder) convertView.getTag();
			}
			if(viewHolder!=null){
				viewHolder.postMan.setText(info.getPostName());
				if(isDefault&&position==0){
					viewHolder.postAddress.setText("[默认]"+info.getPostAddr());
					Drawable drawable =getResources().getDrawable(R.drawable.shipping_address_selected);
					viewHolder.postAddress.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
							MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
					drawable.setBounds(0, 0, viewHolder.postAddress.getMeasuredHeight(), viewHolder.postAddress.getMeasuredHeight());
					viewHolder.postAddress.setCompoundDrawables(null, null,drawable,null);
				}else{
					viewHolder.postAddress.setText(info.getPostAddr());
				}
			}
			return convertView;
		}
		
	}
	private static class ViewHolder{
		public TextView postMan,postAddress;
	}
	@Override
	public void onClick(View v) {
		Intent intent=new Intent();
		switch (v.getId()) {
		case R.id.manager:
			intent.setClass(AddressActivity.this, EditAddressActivity.class);
			startActivityForResult(intent,200);
			break;
		default:
			break;
		}
		
	}
	
	public void finishActivity(View view){
		finish();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 200:
			if(data==null){
				return;
			}
			PostInfo info=(PostInfo) data.getExtras().get("address");
			postInfos.add(info);
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
