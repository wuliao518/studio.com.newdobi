package com.dobi.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.common.NetUtils;
import com.dobi.item.NoticeItem;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeInfoActivity extends BaseActivity implements OnClickListener {

	private ListView list;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private List<NoticeItem> noticeItems=new ArrayList<NoticeItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_info);
		list = (ListView) findViewById(R.id.lvCommonListView);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent();
				intent.setClass(NoticeInfoActivity.this, PromptActivity.class);
				intent.putExtra("url",noticeItems.get(position).getUrl());
				intent.putExtra("title","活动");
				startActivity(intent);
			}
		});
		mImageLoader=ImageLoader.initLoader(getApplicationContext());
		mInflater=LayoutInflater.from(getApplicationContext());
		NetUtils.loadNotice("0", "5", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				JSONObject json;
				try {
					json = new JSONObject(new String(data));
					if(json.getInt("status")==1){
						JSONArray array=json.getJSONArray("videoList");
						int length=array.length();
						for(int i=0;i<length;i++){
							JSONObject object=array.getJSONObject(i);
							NoticeItem item=new NoticeItem();
							if(converStringToBoolean(object.getString("top"))){
								item.setTop(true);
								item.setImagePath(object.getString("image"));
								item.setNid(object.getString("nid"));
								item.setUrl(object.getString("url"));
								item.setContent(object.getString("content"));
								noticeItems.add(0, item);
							}else{
								item.setTop(false);
								item.setImagePath(object.getString("image"));
								item.setNid(object.getString("nid"));
								item.setUrl(object.getString("url"));
								item.setContent(object.getString("content"));
								noticeItems.add(item);
							}
						}
						list.setAdapter(new NoticeAdapter());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				
			}
		});
	}

	// // 生成动态数组，加入数据
	// ArrayList<HashMap<String, Object>> listItem = new
	// ArrayList<HashMap<String, Object>>();
	// for (int i = 0; i < 100; i++) {
	// HashMap<String, Object> map = new HashMap<String, Object>();
	// map.put("ItemImage_notice", R.drawable.announcementitem);// 图像资源的ID
	// map.put("ItemTitle_notice", "Level " + i + "：11月11抢3月话费，赶快扩展起来哇！！！");
	// listItem.add(map);
	// }
	// // 生成适配器的Item和动态数组对应的元素
	// SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
	// R.layout.notice_listview_item,// ListItem的XML实现
	// // 动态数组与ImageItem对应的子项
	// new String[] { "ItemImage_notice", "ItemTitle_notice" },
	// // ImageItem的XML文件里面的一个ImageView,两个TextView ID
	// new int[] { R.id.ItemImage_notice, R.id.ItemTitle_notice });
	//
	//
	//
	//
	//
	//
	// // 添加并且显示
	// list.setAdapter(listItemAdapter);
	//
	// // 添加点击
	// list.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(android.widget.AdapterView<?> arg0,
	// View arg1, int arg2, long arg3) {
	//
	// setTitle("点击第" + arg2 + "个项目");
	// }
	// });
	//
	// }
	//
	// private void ini() {
	// mBtnback = (ImageButton) findViewById(R.id.mbtn_back_notice);
	// mBtnback.setOnClickListener(this);
	//
	//
	//
	// }
	//
	//
	//
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	public void finishActivity(View view) {
		finish();
	}
	
	
	
	public boolean converStringToBoolean(String str){
		if(str.equals("1")){
			return true;
		}
		return false;
	}
	
	private class NoticeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return noticeItems.size();
		}

		@Override
		public Object getItem(int position) {
			return noticeItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			NoticeItem item=noticeItems.get(position);
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=mInflater.inflate(R.layout.notice_listview_item, null);
				viewHolder.image=(ImageView) convertView.findViewById(R.id.ItemImage_notice);
				viewHolder.content=(TextView) convertView.findViewById(R.id.ItemTitle_notice);
				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder)convertView.getTag();
			}
			viewHolder.image.setTag(NetUtils.IMAGE_PREFIX+item.getImagePath());
			viewHolder.content.setText(item.getContent());
			Bitmap bitmap=mImageLoader.downloadImage(NetUtils.IMAGE_PREFIX+item.getImagePath(), new onImageLoaderListener() {
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					Log.e("jiang", url);
					ImageView image=(ImageView) list.findViewWithTag(url);
					if(image!=null&bitmap!=null){
						image.setImageBitmap(bitmap);
					}
				}
			}, new Point(0,0));
			if(bitmap!=null){
				viewHolder.image.setImageBitmap(bitmap);
			}else{
				viewHolder.image.setImageResource(R.drawable.notice_default);
			}
			return convertView;
		}
		
	}
	
	private class ViewHolder {
		ImageView image;
		TextView content;
	}
	
	
	

}
