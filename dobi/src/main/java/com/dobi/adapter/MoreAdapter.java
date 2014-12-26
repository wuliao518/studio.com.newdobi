package com.dobi.adapter;

import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.db.DBManager;
import com.dobi.logic.ImageManager;
import com.dobi.logic.MyDialog;
import com.dobi.ui.MoreActivity;
import com.dobi.view.MoreImageView;

public class MoreAdapter extends BaseAdapter implements OnScrollListener {
	private Context context;
	private List<String> mapList;
	private LayoutInflater mInflater;
	private Point mPoint;
	private MoreImageView image;
	private ListView mListView;
	private int type;
	private ImageManager mImageManager;
	private DBManager manager;
	private Handler handler;
	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 */
	private boolean isFirstEnter = true;
	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;

	/**
	 * 一屏中所有item的个数
	 */
	private int mVisibleItemCount;
	/**
	 * Image 下载器
	 */
	private ImageLoader mImageDownLoader;

	public MoreAdapter(Context context, List<String> mapList, Point mPoint,
			ListView mListView, MoreImageView image, int type,String path,Handler handler) {
		this.context = context;
		this.mapList = mapList;
		mInflater = LayoutInflater.from(context);
		this.mPoint = mPoint;
		this.mListView = mListView;
		this.image = image;
		this.handler=handler;
		this.type = type;
		manager=new DBManager(context);
		mImageManager = new ImageManager();
		mImageDownLoader = ImageLoader.initLoader(context);
		this.mListView.setOnScrollListener(this);
	}

	@Override
	public int getCount() {
		switch (MoreActivity.current) {
			case MoreActivity.PROP:
				return mapList.size()+1;
			case MoreActivity.SCENE:
				return mapList.size();
			default:
				return mapList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return mapList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String itemPath=null;
		String prefix=null;
		int count=0;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_single, null);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.item_image);
			viewHolder.image.setBackgroundColor(Color.WHITE);
			LayoutParams params = new LayoutParams(
					CommonMethod.getHeight(context) / 5,
					(int) CommonMethod.GetDensity(context) * 60);
			viewHolder.image.setLayoutParams(params);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Bitmap bitmap=null;
		if(MoreActivity.current==MoreActivity.PROP){
			if(position==0){
				InputStream is = context.getResources().openRawResource(
						R.drawable.bubbletext);
				bitmap = BitmapFactory.decodeStream(is);
			}else{
				itemPath=mapList.get(position-1);
				prefix=itemPath.substring(0, itemPath.lastIndexOf("/"));
				viewHolder.image.setTag(itemPath);
				bitmap = mImageDownLoader.showCacheBitmap((ConstValue.urlPrefix+itemPath).replaceAll("[^\\w]", ""), mPoint, false);
			}
			
		}else{
			itemPath=mapList.get(position);
			prefix=itemPath.substring(0, itemPath.lastIndexOf("/"));
			count=manager.getInt(prefix);
			viewHolder.image.setTag(itemPath);
			bitmap = mImageDownLoader.showCacheBitmap((ConstValue.urlPrefix+itemPath).replaceAll("[^\\w]", ""), mPoint, false);
		}
		if (bitmap != null) {
			viewHolder.image.setImageBitmap(bitmap);
			viewHolder.image.setOnClickListener(new MoreClickListener(itemPath,
					bitmap,position,count));
		} else {
			Bitmap bitmapL = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.default_load);
			viewHolder.image.setImageBitmap(bitmapL);
			mImageDownLoader.downMoreloadImage(itemPath, new onImageLoaderListener() {
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					ImageView image = (ImageView) mListView
							.findViewWithTag(url);
					int countJ=manager.getInt(url.substring(0, url.lastIndexOf("/")));
					if (image != null && bitmap != null) {
						image.setImageBitmap(bitmap);
						image.setOnClickListener(new MoreClickListener(url,
								bitmap,position,countJ));
					}else{
						if(image!=null){
							image
							.setOnClickListener(null);
						}
					}
				}

			}, mPoint,count);
		}

		return convertView;
	}

	public static class ViewHolder {
		public ImageView image;
	}

	/**
	 * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去sd卡或者手机目录查找，在没有就开启线程去下载
	 * 
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount) {
		if(visibleItemCount> mapList.size()){
			visibleItemCount=mapList.size();
		}
		if (firstVisibleItem + visibleItemCount > mapList.size()) {
			firstVisibleItem = 0;
		}
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			final int position = i;
			String mImageUrl = null;
			if(MoreActivity.current==MoreActivity.PROP){
				if(i!=0){
					mImageUrl = mapList.get(i-1);
					final int count=manager.getInt(mImageUrl.substring(0, mImageUrl.lastIndexOf("/")));
					final ImageView mImageView = (ImageView) mListView
							.findViewWithTag(mImageUrl);
					mImageDownLoader.downMoreloadImage(mImageUrl,
							new onImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap, String url) {
									int countJ=manager.getInt(url.substring(0, url.lastIndexOf("/")));
									//final int countL=manager.getInt(url.substring(0, url.lastIndexOf("/")));
									if (mImageView != null && bitmap != null) {
										mImageView.setImageBitmap(bitmap);
										mImageView.setOnClickListener(new MoreClickListener(
														url, bitmap,position,countJ));
									}else{
										if(image!=null){
											image
											.setOnClickListener(null);
										}
									}
								}

							}, mPoint,count);
				}
				
			}else{
				mImageUrl = mapList.get(i);
				final int count=manager.getInt(mImageUrl.substring(0, mImageUrl.lastIndexOf("/")));
				final ImageView mImageView = (ImageView) mListView
						.findViewWithTag(mImageUrl);
				mImageDownLoader.downMoreloadImage(mImageUrl,
						new onImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								int countJ=manager.getInt(url.substring(0, url.lastIndexOf("/")));
								//final int countL=manager.getInt(url.substring(0, url.lastIndexOf("/")));
								if (mImageView != null && bitmap != null) {
									mImageView.setImageBitmap(bitmap);
									mImageView.setOnClickListener(new MoreClickListener(
													url, bitmap,position,countJ));
								}else{
									if(image!=null){
										image
										.setOnClickListener(null);
									}
								}
							}

						}, mPoint,count);
			}

			
		}
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTask() {
		mImageDownLoader.cancelTask();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
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
			showImage(0, mVisibleItemCount);
			isFirstEnter = false;
		}
	}

	private class MoreClickListener implements OnClickListener {
		private String path;
		private Bitmap bitmapL;
		private int count;
		private int position;
		public MoreClickListener(String path, Bitmap bitmap,int position,int count) {
			this.path = path;
			this.bitmapL = bitmap;
			this.position=position;
			this.count=count;
		}

		@Override
		public void onClick(View v) {
			Bitmap bitmap = null;
			if (path != null) {
				bitmap = mImageDownLoader.getFromFile((ConstValue.urlPrefix+path).replaceAll("[^\\w]",
						""));
			}
			if (bitmap != null)
				this.bitmapL = bitmap;
			switch (MoreActivity.current) {
			case MoreActivity.PROP:
				if(position==0){
					LayoutInflater inflater = LayoutInflater
							.from(context);
					View view = inflater.inflate(R.layout.dialog_bubble,
							null);
					final MyDialog myDialog = new MyDialog(context, view,
							R.style.Self_Dialog);
					final EditText edit = (EditText) view
							.findViewById(R.id.dialog_edittext);
					ImageButton btnCommit = (ImageButton) view
							.findViewById(R.id.dialog_button);
					 
					btnCommit.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								String word = edit.getText().toString();
								Bitmap bit = null;
								
								bit = mImageManager.setTextToBitmap(bitmapL,
										word);
								image.addProp(bit);
								myDialog.dismiss();
								// 从文件路径中获取bitmap对象
							}
						});
					
					/******************************************************************/
				//	context
					WindowManager m = ((Activity)context).getWindowManager();
					Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
					WindowManager.LayoutParams p = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
					// p.height = (int) (d.getHeight() * 0.2); //高度设置为屏幕的0.6
					p.height = LayoutParams.WRAP_CONTENT;
					p.width = (int) (d.getWidth() * 1); // 宽度设置为屏幕的0.95
					myDialog.getWindow().setAttributes(p);
					myDialog.getWindow().setGravity(Gravity.BOTTOM);
					myDialog.setContentView(view);
					myDialog.setCancelable(true);
					myDialog.show();
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							// 调用软键盘
							((InputMethodManager) context
									.getSystemService(context.INPUT_METHOD_SERVICE))
									.toggleSoftInput(
											0,
											InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}, 100);
				}else{
					image.addProp(bitmapL);
				}
				break;
			case MoreActivity.SCENE:
				
				Dialog dialog=CommonMethod.showMyDialog((Activity) context);
				dialog.show();
				image.setScene((Activity) context,image,bitmapL,path,count);
				dialog.dismiss();
				break;
			default:
				break;
			}

		}

	}

}
