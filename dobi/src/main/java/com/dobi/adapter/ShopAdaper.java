package com.dobi.adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.common.NetUtils;
import com.dobi.item.ShopItem;
import com.dobi.ui.JiangActivity;
import com.dobi.ui.TestActivity;
import com.dobi.view.ShopView;

public class ShopAdaper extends BaseAdapter implements OnScrollListener {
	private ArrayList<ShopItem> items;
	private JiangActivity mActivity;
	private LayoutInflater mInflater;
	private ListView mListView;
	//判别类型
	private int type;
	private SharedPreferences sp;
	private Editor edit;
	/**
	 * Image 下载器
	 */
	private ImageLoader mImageDownLoader;
	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 */
	private boolean isFirstEnter = true;
	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;
	private ShopView shopView;
	/**
	 * 一屏中所有item的个数
	 */
	private int mVisibleItemCount;
	public ShopAdaper(ArrayList<ShopItem> items,JiangActivity mActivity,ListView mListView,ShopView shopView,int type) {
		this.items = items;
		this.mActivity=mActivity;
		mInflater=LayoutInflater.from(mActivity);
		this.mListView=mListView;
		this.mListView.setOnScrollListener(this);
		this.type=type;
		this.shopView=shopView;
		mImageDownLoader = ImageLoader.initLoader(mActivity);
		sp=CommonMethod.getPreferences(mActivity);
		edit=sp.edit();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_single, null);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.item_image);
			LayoutParams params = new LayoutParams(
					CommonMethod.getWidth(mActivity) / 4,
					CommonMethod.getWidth(mActivity) / 4);
			viewHolder.image.setLayoutParams(params);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path = NetUtils.IMAGE_PREFIX+items.get(position).getUrl();
		viewHolder.image.setTag(path);
		Bitmap bitmap = mImageDownLoader.showCacheBitmap(
				path.replaceAll("[^\\w]", ""), new Point(80,80), false);
		if(bitmap!=null){
			viewHolder.image.setImageBitmap(bitmap);
			viewHolder.image.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mListView.getParent().getParent().requestDisallowInterceptTouchEvent(true);
						break;
					case MotionEvent.ACTION_MOVE:
						mListView.getParent().getParent().requestDisallowInterceptTouchEvent(true);
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						break;
					}
					
					return false;
				}
			});
			viewHolder.image.setOnClickListener(new ShopOnClickListener(position, bitmap,path));
		}else{
			Bitmap bitmapL = BitmapFactory.decodeResource(
					mActivity.getResources(), R.drawable.default_load);
			viewHolder.image.setImageBitmap(bitmapL);
		}
		return convertView;
	}
	
	private static class ViewHolder {
		public ImageView image;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelTask();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
	}
	private void showImage(int firstVisibleItem, int visibleItemCount) {
		for (int i = firstVisibleItem; i < firstVisibleItem
				+ visibleItemCount; i++) {
			final int position = i;
			ShopItem item = items.get(i);
			final String mImageUrl = NetUtils.IMAGE_PREFIX+item.getUrl();
			final ImageView mImageView = (ImageView) mListView
					.findViewWithTag(mImageUrl);
			if(mImageDownLoader!=null){
				mImageDownLoader.downloadImage(mImageUrl,new onImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap,
							String url) {
						if(mImageView!=null&&bitmap!=null){
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG,
									100, baos);
							InputStream isBm = new ByteArrayInputStream(
									baos.toByteArray());
							Bitmap bitmapL = mImageDownLoader
									.decodeThumbBitmapForInputStream(
											isBm, 120,
											120);
							bitmapL = bitmapL == null ? bitmap
									: bitmapL;
							mImageView.setImageBitmap(bitmapL);
							mImageView.setOnClickListener(new ShopOnClickListener(position, bitmap,url));
						}else{
							if(mImageView!=null){
								mImageView.setOnClickListener(null);
							}
						}
					}

				}, new Point(120,120));
			}
		}
	}
	
	
	/**
	 * 取消下载任务
	 */
	public void cancelTask() {
		mImageDownLoader.cancelTask();
	}
	
	private class ShopOnClickListener implements OnClickListener{
		private Bitmap bitmapL;
		private String path;
		private ShopItem item;
		public ShopOnClickListener(int position, final Bitmap bitmap, String path) {
			this.path = path;
			this.bitmapL = bitmap;
			item=items.get(position);
		}
		@Override
		public void onClick(View v) {
			//mActivity.hideBottomView();
			//mActivity.setClickType(0);
			Bitmap bitmap = null;
			if (path != null) {
				bitmap = mImageDownLoader.getFromFile(path.replaceAll("[^\\w]",
						""));
				Log.i("jiang", "bitmap宽度"+bitmap.getWidth());
			}
			if (bitmap != null){
				this.bitmapL = bitmap;
			}
			switch (type) {
			case 0://换头饰
				//edit.putString("hd_id", item.getId()+"");
				//edit.commit();
				shopView.changeHair(bitmapL,false);
				mActivity.hd_id=item.getId()+"";
				mActivity.setGoodsImage();
				break;
			case 1://换衣服
				//edit.putString("bd_id", item.getId()+"");
				//edit.commit();
				shopView.changeClothes(bitmapL,false);
				mActivity.bd_id=item.getId()+"";
				mActivity.setGoodsImage();
				break;
			default:
				break;
			}
		}
		
	}

}
