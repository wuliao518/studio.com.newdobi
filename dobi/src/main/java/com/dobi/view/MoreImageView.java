package com.dobi.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.common.ImageLoader;
import com.dobi.common.ConstValue.Stage;
import com.dobi.item.MoreFaceItem;
import com.dobi.logic.ImageManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MoreImageView extends BaseMoreImageView {
	private static int cj_width, cj_height;
	private ImageManager mImageManager;
	// 脸部集合
	protected static List<MoreFaceItem> moreFaceItems;
	private LinearLayout linear;// 照相机弹出框
	/**
	 * Image 下载器
	 */
	private ImageLoader mImageDownLoader;
	/**
	 * 除道具以外的图片数量
	 */
	public static Stage CurrentStage = null;
	/**
	 * 可显示图片数量
	 */
	protected int imgCount = 0;
	public static int count = 0;
	private Bitmap bitmapBj;

	public MoreImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		cj_width = CommonMethod.getWidth(context);
		cj_height = CommonMethod.getHeight(context);
		mImageManager = new ImageManager();
		baseImgCount = 6;
		// 定义场景需要显示图片的数量
		imgCount = baseImgCount + PROP_COUNT + 1;
		// 定义场景需要显示图片的数量
		mBmps = new Bmp[imgCount];
		mImageDownLoader = ImageLoader.initLoader(context);
	}

	public void initView(Activity activity) {
		bitmapBj = BitmapFactory.decodeResource(getResources(),
				R.drawable.scene0);
		int bjWidth = bitmapBj.getWidth();
		int bjHeight = bitmapBj.getHeight();
		sceneBitmap = Bitmap.createScaledBitmap(bitmapBj,
				cj_height * bitmapBj.getWidth() / bitmapBj.getHeight(),
				cj_height, false);
		creatMoreFaceItemList(1);
		// 将头部相关图片放入图层
		for (MoreFaceItem mMoreFaceItem : moreFaceItems) {
			int index = mMoreFaceItem.getIndex();
			Bitmap faceBackBitmap = BitmapFactory.decodeResource(
					getResources(), R.drawable.scene1);
			faceBackBitmap = Bitmap.createScaledBitmap(faceBackBitmap,
					faceBackBitmap.getWidth() * cj_height * bitmapBj.getWidth()
							/ bitmapBj.getHeight() / bjWidth,
					faceBackBitmap.getHeight() * cj_height / bjHeight, false);
			mBmps[index * 2 + 1] = new Bmp(faceBackBitmap, -1,
					(float) mMoreFaceItem.getLocation()[0],
					(float) mMoreFaceItem.getLocation()[1], false, false, false);
		}
		updateFaces();
		invalidate();
	}

	/**
	 * 更新脸部图片
	 * 
	 * ; @param isChangeScene 是否属于更换场景
	 */
	@SuppressLint("NewApi")
	public void updateFaces() {
		if (moreFaceItems != null) {
			if (moreFaceItems.size() > 0) {
				for (int i = 0; i < moreFaceItems.size(); i++) {
					if (mBmps[i * 2] == null) {
						// 默认使用已经拍过的照片
						Bitmap faceBitmap = BitmapFactory
								.decodeFile(Environment
										.getExternalStorageDirectory()
										+ ConstValue.ROOT_PATH
										+ ConstValue.FACE_PATH
										+ "clicp"
										+ i
										+ "png");
						if (faceBitmap != null) {
							// 正比控制显示比例
							float expSize = 18f;
							faceBitmap = Bitmap
									.createScaledBitmap(
											faceBitmap,
											(int) (cj_width * expSize / 100),
											(int) (cj_width * expSize / 100
													* faceBitmap.getHeight() / faceBitmap
													.getWidth()), true);
							int face_x = moreFaceItems.get(i).getLocation()[0];
							int face_y = moreFaceItems.get(i).getLocation()[1]
									+ cj_height * 2 / 100;
							mBmps[i * 2] = new Bmp(faceBitmap, i * 2, face_x,
									face_y, true, true, false);
						} else {
							mBmps[i * 2] = null;
						}
					}
				}

			}
		}
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			super.onDraw(canvas);
			if (sceneBitmap != null&&!sceneBitmap.isRecycled()) {
				canvas.drawBitmap(sceneBitmap, new Matrix(), null);
			}else if(sceneBitmap != null&&sceneBitmap.isRecycled()){
				sceneBitmap=null;
			}
			int length=mBmps.length;
			for (int i = 0; i < length; i++) {
				if (mBmps[i] != null&&!mBmps[i].getPic().isRecycled()) {
					canvas.drawBitmap(mBmps[i].getPic(), mBmps[i].matrix, mBmps[i].paint);
				}else if(mBmps[i] != null&&mBmps[i].getPic().isRecycled()){
					mBmps[i]=null;
				}
			}
			if (deleteBmp != null&&!deleteBmp.getPic().isRecycled()) {
				canvas.drawBitmap(deleteBmp.getPic(), deleteBmp.matrix, null);
			}else if(deleteBmp != null&&deleteBmp.getPic().isRecycled()){
				deleteBmp=null;
			}
		} catch (Exception e) {
			Log.e("jiang", "use recycle");
		}
	}
	public void recycleBitmap(){
		//释放背景
		if(sceneBitmap!=null&&!sceneBitmap.isRecycled()){
			sceneBitmap.recycle();
			sceneBitmap=null;
		}
		int length=mBmps.length;
		for (int i = 0; i < length; i++) {
			if (mBmps[i] != null&&!mBmps[i].getPic().isRecycled()) {
				mBmps[i].recycleMap();
			}
		}
		if (deleteBmp != null&&!deleteBmp.getPic().isRecycled()) {
			deleteBmp.recycleMap();
		}
	}

	/**
	 * 脸部类集合
	 * 
	 * @return
	 */
	public List<MoreFaceItem> getMoreFaceItems() {
		return moreFaceItems;
	}

	/**
	 * 根据背景图片创建头部列表
	 * 
	 * @param count
	 *            头像数量
	 */
	private void creatMoreFaceItemList(int count) {
		List<MoreFaceItem> listMoreFaceItem = new ArrayList<MoreFaceItem>();
		List<int[]> list = mImageManager.getRed(sceneBitmap, count);
		for (int i = 0; i < list.size(); i++) {
			MoreFaceItem mMoreFaceItem = new MoreFaceItem();
			mMoreFaceItem.setIndex(i);
			mMoreFaceItem.setLocation(list.get(i));
			mMoreFaceItem.setHangest(true);
			listMoreFaceItem.add(mMoreFaceItem);
		}
		moreFaceItems = listMoreFaceItem;
	}

	public void addProp(Bitmap bitmap) {
		if (bitmap != null) {
			for (int i = baseImgCount; i < baseImgCount + PROP_COUNT; i++) {
				if (mBmps[i] == null) {
					int x = (new Random()).nextInt(cj_width * 2 / 4 - 1)
							+ cj_width / 4 + 1;
					int y = (new Random()).nextInt(cj_height * 2 / 4 - 1) + cj_height / 4+ 1;
					mBmps[i] = new Bmp(bitmap, i, x, y, true, false, false);
					break;
				}
			}
			this.invalidate();
		}
	}

	@SuppressLint("NewApi")
	public void setScene(final Activity activity, final MoreImageView image,
			Bitmap bitmap, String path, int count) {
		clearBK();
		for (int i = 0; i < 6; i++) {
			if (mBmps != null) {
				mBmps[i] = null;
			}
		}
		linear = (LinearLayout) activity.findViewById(R.id.cameraWidget);
		String prefix = path.substring(0, path.lastIndexOf("/"));
		if (bitmap != null) {
			sceneBitmap.recycle();
			sceneBitmap = null;
			sceneBitmap = Bitmap.createScaledBitmap(bitmap, cj_height * bitmapBj.getWidth() / bitmapBj.getHeight(),
					cj_height, false);
			creatMoreFaceItemList(count);
			for (MoreFaceItem mMoreFaceItem : moreFaceItems) {
				int index = mMoreFaceItem.getIndex();
				String itemPath = prefix + "/" + (index + 1) + ".png";
				Bitmap faceBackBitmap = mImageDownLoader
						.showCacheBitmap((ConstValue.urlPrefix + itemPath)
								.replaceAll("[^\\w]", ""));
				faceBackBitmap = Bitmap.createScaledBitmap(
						faceBackBitmap,
						faceBackBitmap.getWidth() * cj_height
								* bitmap.getWidth() / bitmap.getHeight()
								/ bitmap.getWidth(), faceBackBitmap.getHeight()
								* cj_height / bitmap.getHeight(), false);

				mBmps[index * 2 + 1] = new Bmp(faceBackBitmap, -1,
						(float) mMoreFaceItem.getLocation()[0],
						(float) mMoreFaceItem.getLocation()[1], false, false,
						false);

			}
			updateFaces();
			flashMask(activity);
			this.invalidate();

		}
	}

	private void clearBK() {
		for(int i=0;i<5;i++){
			if(mBmps[i]!=null){
				mBmps[i].getPic().recycle();
				mBmps[i]=null;
			}
		}
		
	}

	public void flashFace(Activity activity, int index) {
		Bitmap faceBitmap = BitmapFactory.decodeFile(Environment
				.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH
				+ ConstValue.FACE_PATH + "clicp" + index + "png");
		if (faceBitmap != null) {
			// 正比控制显示比例
			float expSize = 18f;
			faceBitmap = Bitmap.createScaledBitmap(faceBitmap, (int) (cj_width
					* expSize / 100), (int) (cj_width * expSize / 100
					* faceBitmap.getHeight() / faceBitmap.getWidth()), true);
			if (mBmps[index * 2] != null) {
				mBmps[index * 2].setPic(faceBitmap);
			} else {
				int[] location = getMoreFaceItems().get(index).getLocation();
				mBmps[index * 2] = new Bmp(faceBitmap, index * 2, location[0],
						location[1], true, false, false);
			}
			flashMask(activity);
			invalidate();
		}

	}

	private void flashMask(final Activity activity) {
		FrameLayout view = (FrameLayout) activity
				.findViewById(R.id.drawViewFrameLayout);
		linear = (LinearLayout) activity.findViewById(R.id.cameraWidget);
		int num = view.getChildCount();
		if (num >= 2) {
			view.removeViews(1, num - 1);
		}
		for (final MoreFaceItem mMoreFaceItem : moreFaceItems) {
			int[] c = mMoreFaceItem.getLocation();
			final int[] d = c;
			ImageButton head = new ImageButton(activity);
			head.setBackgroundResource(R.drawable.button_face);
			if (mBmps[mMoreFaceItem.getIndex() * 2] == null) {
				head.setBackgroundResource(R.drawable.shoot);
			}

			final int resourcesWidth = Integer.parseInt(activity.getResources()
					.getString(R.string.morePhotoWidth));

			head.setMaxWidth(resourcesWidth);
			head.setMaxHeight(resourcesWidth);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0, 0);
			params.height = resourcesWidth;
			params.width = resourcesWidth;
			params.setMargins(c[0] - params.width / 2,
					c[1] - params.height / 2, 0, 0);
			head.setLayoutParams(params);
			head.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int resourceWidth = Integer.parseInt(activity
							.getResources().getString(
									R.string.morePhotoLinearWidth));

					LayoutParams params2 = new LayoutParams(
							(int) (resourceWidth * 2.5f), resourceWidth);
					params2.setMargins(d[0] - params2.width / 2, d[1]
							+ params2.height / 2, 0, 0);
					linear.setTag(mMoreFaceItem.getIndex());
					linear.setLayoutParams(params2);
					linear.setVisibility(View.VISIBLE);
					mMoreFaceItem.setHangest(true);
					selectMap(mBmps[mMoreFaceItem.getIndex() * 2]);
				}
			});
			mMoreFaceItem.setmButton(head);
			view.addView(head);
		}
	}

	public Bitmap getCurrentPic() {
		Bitmap resultBitmap = Bitmap.createBitmap(sceneBitmap.getWidth(),
				sceneBitmap.getHeight(), ConstValue.MY_CONFIG_8888);
		Canvas saveToPhoneCanvas = new Canvas(resultBitmap);
		saveToPhoneCanvas.drawBitmap(sceneBitmap, 0, 0, null);
		for (Bmp mBmp : mBmps) {
			if (mBmp != null) {
				mBmp.cancelHighLight();
				saveToPhoneCanvas.drawBitmap(mBmp.getPic(), mBmp.matrix, mBmp.paint);
				
			}
		}
		return resultBitmap;
	}

	public void setPaint(Paint paint) {
		if(mBmps[0]!=null){
			mBmps[0].setPaint(paint);
		}
		if(mBmps[2]!=null){
			mBmps[2].setPaint(paint);
		}
		if(mBmps[4]!=null){
			mBmps[4].setPaint(paint);
		}
		
	}
}
