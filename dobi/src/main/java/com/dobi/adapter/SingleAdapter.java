package com.dobi.adapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Matrix.ScaleToFit;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.logic.ImageManager;
import com.dobi.logic.MyDialog;
import com.dobi.ui.OneImage;
import com.dobi.ui.SingleDrawViewBase;
import com.liu.hz.view.AbsHorizontalListView;
import com.liu.hz.view.AbsHorizontalListView.OnScrollListener;
import com.liu.hz.view.AbsListView;
import com.liu.hz.view.HorizontalListView;

public class SingleAdapter extends BaseAdapter implements OnScrollListener {
	private Context context;
	private List<String> mapList;
	private LayoutInflater mInflater;
	private Point mPoint;
	private OneImage image;
	private HorizontalListView mListView;
	private int type;
	private ImageManager mImageManager;
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

	public SingleAdapter(Context context, List<String> mapList, Point mPoint,
			HorizontalListView mListView, OneImage image, int type) {
		this.context = context;
		this.mapList = mapList;
		mInflater = LayoutInflater.from(context);
		this.mPoint = mPoint;
		this.mListView = mListView;
		this.image = image;
		this.type = type;
		mImageManager = new ImageManager();
		mImageDownLoader = ImageLoader.initLoader(context);
		mListView.setOnScrollListener(this);
	}

	@Override
	public int getCount() {
		if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Face) {
			return mapList.size() + 1;
		}else if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Prop&&(type==0||type==1)) {
			return mapList.size() + 1;
		}
		return mapList.size();
	}

	@Override
	public Object getItem(int position) {
		if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Face) {
			if (position == 0) {
				return "delete";
			} else {
				return mapList.get(position - 1);
			}
		}else if(SingleDrawViewBase.CurrentStage == ConstValue.Stage.Prop&&(type==0||type==1)){
			if (position == 0) {
				return "prop";
			} else {
				return mapList.get(position - 1);
			}
		}
		return mapList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_single, null);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.item_image);
			viewHolder.image.setBackgroundColor(Color.WHITE);
			LayoutParams params = new LayoutParams(
					CommonMethod.getWidth(context) / 5,
					(int) CommonMethod.GetDensity(context) * 60);
			viewHolder.image.setLayoutParams(params);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path = null;
		Bitmap bitmap = null;
		if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Face) {
			if (position == 0) {
				InputStream is = context.getResources().openRawResource(
						R.drawable.cancleselect);
				bitmap = BitmapFactory.decodeStream(is);
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				path = mapList.get(position - 1);
				viewHolder.image.setTag(path);
				viewHolder.image.setScaleType(ScaleType.CENTER_INSIDE);
				switch (type) {
				case 0:
				case 1:
					break;
				case 2:
				case 3:
				case 4:
				case 5:
					bitmap = mImageDownLoader.showCacheBitmap(
							path.replaceAll("[^\\w]", ""), mPoint, false);
					break;
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
					bitmap = mImageDownLoader.showCacheBitmap(
							path.replaceAll("[^\\w]", ""), mPoint, false);
					break;
				default:
					break;
				}
			}

		} else if(SingleDrawViewBase.CurrentStage == ConstValue.Stage.Prop&&(type==0||type==1)) {
			if(position==0){
				InputStream is = context.getResources().openRawResource(
						R.drawable.bubbletext);
				bitmap = BitmapFactory.decodeStream(is);
			}else{
				path = mapList.get(position - 1);
				viewHolder.image.setTag(path);
				bitmap = mImageDownLoader.showCacheBitmap(
						path.replaceAll("[^\\w]", ""), mPoint, false);
			}
			
		}else {
			path = mapList.get(position);
			Log.e("jiang", path);
			viewHolder.image.setTag(path);
			bitmap = mImageDownLoader.showCacheBitmap(
					path.replaceAll("[^\\w]", ""), mPoint, false);
		}
		// 利用NativeImageLoader类加载本地图片
		if (bitmap != null) {
			if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Face) {
				viewHolder.image.setScaleType(ScaleType.CENTER_INSIDE);
				viewHolder.image.setImageBitmap(bitmap);
				OneListener listener = new OneListener(position, bitmap, path);
				viewHolder.image.setOnClickListener(listener);
			}else if(SingleDrawViewBase.CurrentStage == ConstValue.Stage.Prop&&(type==0||type==1)) {
				if(position==0){
					InputStream is = context.getResources().openRawResource(
							R.drawable.bubbletext);
					bitmap = BitmapFactory.decodeStream(is);
					viewHolder.image.setImageBitmap(bitmap);
					OneListener listener = new OneListener(position, bitmap, path);
					viewHolder.image.setOnClickListener(listener);
				}else{
					viewHolder.image.setImageBitmap(bitmap);
					OneListener listener = new OneListener(position, bitmap, path);
					viewHolder.image.setOnClickListener(listener);
				}
				
			}else{
				viewHolder.image.setImageBitmap(bitmap);
				OneListener listener = new OneListener(position, bitmap, path);
				viewHolder.image.setOnClickListener(listener);
			}
		} else {
			Bitmap bitmapL = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.default_load);
			viewHolder.image.setImageBitmap(bitmapL);
			mImageDownLoader.downloadImage(path, new onImageLoaderListener() {
				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					ImageView image = (ImageView) mListView
							.findViewWithTag(url);
					if (image!=null&&bitmap != null) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
						InputStream isBm = new ByteArrayInputStream(baos
								.toByteArray());
						Bitmap bitmapL = mImageDownLoader
								.decodeThumbBitmapForInputStream(isBm,
										mPoint == null ? 0 : mPoint.x,
										mPoint == null ? 0 : mPoint.y);
						bitmapL = bitmapL == null ? bitmap : bitmapL;
						image.setImageBitmap(bitmapL);
						image.setOnClickListener(new OneListener(position,
								bitmapL, url));
					}else{
						if(image!=null){
							image
							.setOnClickListener(null);
						}
					}
				}
			}, mPoint);
		}
		return convertView;
	}

	public static class ViewHolder {
		public ImageView image;
	}

	public class OneListener implements OnClickListener {
		private int position;
		private Bitmap bitmapL;
		private String path;
		public OneListener(int position, final Bitmap bitmap, String path) {
			this.position = position;
			this.path = path;
			this.bitmapL = bitmap;
		}
		@Override
		public void onClick(View v) {
			Bitmap bitmap = null;
			if (path != null) {
				bitmap = mImageDownLoader.getFromFile(path.replaceAll("[^\\w]",
						""));
			}
			if (bitmap != null){
				this.bitmapL = bitmap;
			}
			if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Prop) {
				if(((type==0)||(type==1))&&position==0){
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
								image.addPic(bit);
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
					image.addPic(this.bitmapL);
				}
			} else if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Face) {
				if (0 == position) {
					switch (type) {
					case 0:
					case 1:
					case 2:
						break;
					case 3:
						// 取消眉毛
						image.setEyebrows(null);
						break;
					case 4:
						// 取消胡子
						image.setBeard(null);
						break;
					case 5:
						// 取消腮红
						image.setBlusher(null);
						break;
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
						// 取消腮红
						image.changeHair(null);
						break;
					default:
						break;
					}

				} else {
					switch (type) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						// 设置眉毛
						image.setEyebrows(this.bitmapL);
						break;
					case 4:
						// 设置胡子
						image.setBeard(this.bitmapL);
						break;
					case 5:
						// 设置腮红
						image.setBlusher(this.bitmapL);
						break;
					case 6:
					case 7:
					case 8:
					case 9:
					case 10:
					case 11:
						// 设置发饰
						image.changeHair(this.bitmapL);
						break;
					default:
						break;
					}
				}

			} else if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Scene) {
				if (path != null) {
					bitmapL = mImageDownLoader.getFromFile(path.replaceAll("[^\\w]",
							""));
				}
				image.changeScence(this.bitmapL);
			} else if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Hair) {
				switch (type) {
				case 0:
				case 1:
				case 2:
				case 3:
					image.changeBody(this.bitmapL);
					break;
				default:
					break;
				}
			}

		}
	}

	@Override
	public void onScrollStateChanged(AbsHorizontalListView view, int scrollState) {
		// 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelTask();
		}
	}

	@Override
	public void onScroll(AbsHorizontalListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			showImage(0, mVisibleItemCount);
			isFirstEnter = false;
		}
	}

	/**
	 * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去sd卡或者手机目录查找，在没有就开启线程去下载
	 * 
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount) {
		Bitmap bitmap = null;
		if(visibleItemCount> mapList.size()){
			visibleItemCount=mapList.size();
		}
		if (firstVisibleItem + visibleItemCount > mapList.size()) {
			firstVisibleItem = 0;
		}
		if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Face) {
			if (firstVisibleItem == 0) {

			} else {
				switch (type) {
				case 0:
				case 1:
					break;
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
					for (int i = firstVisibleItem; i < firstVisibleItem
							+ visibleItemCount; i++) {
						final int position = i;
						String mImageUrl = null;
						mImageUrl = mapList.get(i - 1);
						Log.i("jiang", "showImage" + mImageUrl);
						final ImageView mImageView = (ImageView) mListView
								.findViewWithTag(mImageUrl);
						bitmap = mImageDownLoader.downloadImage(mImageUrl,
								new onImageLoaderListener() {
									@Override
									public void onImageLoader(Bitmap bitmap,
											String url) {
										if(mImageView!=null&&bitmap!=null){
											ByteArrayOutputStream baos = new ByteArrayOutputStream();
											bitmap.compress(
													Bitmap.CompressFormat.PNG, 100,
													baos);
											InputStream isBm = new ByteArrayInputStream(
													baos.toByteArray());
											Bitmap bitmapL = mImageDownLoader
													.decodeThumbBitmapForInputStream(
															isBm,
															mPoint == null ? 0
																	: mPoint.x,
															mPoint == null ? 0
																	: mPoint.y);
											bitmapL = bitmapL == null ? bitmap
													: bitmapL;
											if (bitmapL != null) {
												mImageView.setScaleType(ScaleType.CENTER_INSIDE);
												mImageView.setImageBitmap(bitmapL);
												mImageView
														.setOnClickListener(new OneListener(
																position, bitmapL,
																url));
											}
										}else{
											if(mImageView!=null){
												mImageView
												.setOnClickListener(null);
											}
										}
									}

								}, mPoint);
					}
				default:
					break;
				}
			}
			
		} else if (SingleDrawViewBase.CurrentStage == ConstValue.Stage.Prop&&(type==0||type==1)) {//自定义气泡
			if (firstVisibleItem == 0) {

			} else {
				for (int i = firstVisibleItem; i < firstVisibleItem
						+ visibleItemCount; i++) {
					final int position = i;
					String mImageUrl = null;
					mImageUrl = mapList.get(i - 1);
					Log.i("jiang", "showImage" + mImageUrl);
					final ImageView mImageView = (ImageView) mListView
							.findViewWithTag(mImageUrl);
					bitmap = mImageDownLoader.downloadImage(mImageUrl,
							new onImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if(mImageView!=null&&bitmap!=null){
										ByteArrayOutputStream baos = new ByteArrayOutputStream();
										bitmap.compress(
												Bitmap.CompressFormat.PNG, 100,
												baos);
										InputStream isBm = new ByteArrayInputStream(
												baos.toByteArray());
										Bitmap bitmapL = mImageDownLoader
												.decodeThumbBitmapForInputStream(
														isBm,
														mPoint == null ? 0
																: mPoint.x,
														mPoint == null ? 0
																: mPoint.y);
										bitmapL = bitmapL == null ? bitmap
												: bitmapL;
										if (bitmapL != null) {
											mImageView.setImageBitmap(bitmapL);
											mImageView
													.setOnClickListener(new OneListener(
															position, bitmapL,
															url));
										}
									}else{
										if(mImageView!=null){
											mImageView
											.setOnClickListener(null);
										}
									}
								}

							}, mPoint);
				}
				
			}

		}else {
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				final int position = i;
				String mImageUrl = null;
				mImageUrl = mapList.get(i);
				final ImageView mImageView = (ImageView) mListView
						.findViewWithTag(mImageUrl);
				bitmap = mImageDownLoader.downloadImage(mImageUrl,
						new onImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (mImageView!=null&&bitmap != null) {
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									bitmap.compress(Bitmap.CompressFormat.PNG,
											100, baos);
									InputStream isBm = new ByteArrayInputStream(
											baos.toByteArray());
									Bitmap bitmapL = mImageDownLoader
											.decodeThumbBitmapForInputStream(
													isBm, mPoint == null ? 0
															: mPoint.x,
													mPoint == null ? 0
															: mPoint.y);
									bitmapL = bitmapL == null ? bitmap
											: bitmapL;
									mImageView.setImageBitmap(bitmapL);
									mImageView
											.setOnClickListener(new OneListener(
													position, bitmapL, url));
								}else{
									if(mImageView!=null){
										mImageView
										.setOnClickListener(null);
									}
									
								}
							}

						}, mPoint);
			}
		}

	}
	/**
	 * 取消下载任务
	 */
	public void cancelTask() {
		mImageDownLoader.cancelTask();
	}

}
