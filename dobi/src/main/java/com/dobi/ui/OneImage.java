package com.dobi.ui;

import java.util.Random;
import com.dobi.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;

public class OneImage extends BaseImageView {

	public OneImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (sceneBitmap != null) {
			canvas.drawBitmap(sceneBitmap, new Matrix(), null);
		}
		// 画道具
		for (Bmp bmp : propBmps) {
			if (bmp != null&&bmp.getPic()!=null&&!bmp.isBeforePerson) {
				canvas.drawBitmap(bmp.getPic(), bmp.matrix, bmp.paint);
			}
		}
		// 画人物
		for (int i = 0; i < PERSON_COUNT; i++) {
			if (items[i] != null) {
				for (int j = 0; j < ELEMENTS_COUNT; j++) {
					if (items[i].person[j] != null) {
						canvas.drawBitmap(items[i].person[j].getPic(),
								items[i].person[j].matrix,
								items[i].person[j].paint);
					}
				}
			}
		}
		// 画道具
		for (Bmp bmp : propBmps) {
			if (bmp != null&&bmp.getPic()!=null&&bmp.isBeforePerson) {
				canvas.drawBitmap(bmp.getPic(), bmp.matrix, bmp.paint);
			}
		}
		// 画线
		if (lightBmp != null) {
			canvas.drawBitmap(lightBmp.getPic(), lightBmp.matrix, null);
		}
		// 画功能按钮
		for (int i = 0; i < 2; i++) {
			if (prop[i] != null) {
				canvas.drawBitmap(prop[i].getPic(), prop[i].matrix, null);
			}
		}
		super.onDraw(canvas);
	}

	public void initView(Activity activity, int cj_width, int cj_height) {
		this.activity = activity;
		if (cj_width != 0 && cj_height != 0) {
			OneImage.cj_width = cj_width;
			OneImage.cj_height = cj_height;
		}
		if (sceneBitmap == null) {
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inPreferredConfig=Config.RGB_565;
			options.inPurgeable = true;   
			options.inInputShareable = true;
			int[] drawables=new int[]{R.drawable.cj,R.drawable.cj01,R.drawable.cj02};
			int random=new Random().nextInt(drawables.length);
			Bitmap mBitmap = BitmapFactory.decodeResource(
					context.getResources(), drawables[random],options);
			sceneBitmap = getFitBitmap(mBitmap);
		}
		initPerson();
	}
	public void recycleBitmap(){
		if(sceneBitmap!=null&&!sceneBitmap.isRecycled()){
			sceneBitmap.recycle();
			sceneBitmap=null;
		}
		// 释放道具
		for (Bmp bmp : propBmps) {
			if (bmp != null&&bmp.getPic()!=null&&!bmp.getPic().isRecycled()) {
				bmp.recycleMap();
			}
		}
		// 释放背景线
		if (lightBmp != null&&lightBmp.getPic()!=null&&!lightBmp.getPic().isRecycled()) {
			lightBmp.recycleMap();
		}
		//释放人物
		for (int i = 0; i < PERSON_COUNT; i++) {
			if (items[i] != null) {
				for (int j = 0; j < ELEMENTS_COUNT; j++) {
					if (items[i].person[j] != null&&items[i].person[j].getPic()!=null&&!items[i].person[j].getPic().isRecycled()) {
						items[i].person[j].recycleMap();
					}
				}
			}
		}
		//释放道具
		for (int i = 0; i < 2; i++) {
			if (prop[i] != null&&prop[i].getPic()!=null&&!prop[i].getPic().isRecycled()) {
				prop[i].recycleMap();
			}
		}
		System.gc();
	}

	/**
	 * 初始化人物
	 */
	public void initPerson() {
		for (int i = 0; i < PERSON_COUNT; i++) {
			if (items[i] == null) {
				items[i] = new BaseImageView.PersonItem(i);
				items[i].initPerson();
				currentPerson = i;
				return;
			}
		}
	}

	/**
	 * 增加道具
	 * 
	 * @param mBitmap
	 */
	public void addPic(Bitmap mBitmap) {
		if (mBitmap != null) {
			for (int i = 0; i < PROP_COUNT; i++) {
				if (propBmps[i] == null) {
					int x = (new Random()).nextInt(cj_width * 2 / 4 - 1)
							+ cj_width / 4 + 1;
					int y = (new Random()).nextInt(cj_height * 2 / 4 - 1)+ cj_height / 4 + 1;
					propBmps[i] = new Bmp(mBitmap, i, x, y, true, false, false);
					break;
				}
			}
			this.invalidate();
		}
	}

	public void changeScence(Bitmap bitmap) {
		if (bitmap != null) {
			sceneBitmap.recycle();
			sceneBitmap = null;
			sceneBitmap = getFitBitmap(bitmap);
			this.invalidate();
		}
	}

	public void recycleBmp(Bmp bmp) {
		if (bmp != null) {
			bmp = null;
		}
	}

	/**
	 * 获取包含当前所有装扮内容的Bitmap，由子类调用
	 * 
	 * @return
	 */
	public Bitmap getCurrentPic() {
		Bitmap resultBitmap = Bitmap.createBitmap(sceneBitmap.getWidth(),
				sceneBitmap.getHeight(), Config.RGB_565);
		Canvas saveToPhoneCanvas = new Canvas(resultBitmap);
		saveToPhoneCanvas.drawBitmap(sceneBitmap, 0, 0, null);
		//取消白色线圈
		cancleAll();
		// 画人物
		for (int i = 0; i < PERSON_COUNT; i++) {
			if (items[i] != null) {
				for (int j = 0; j < ELEMENTS_COUNT; j++) {
					if (items[i].person[j] != null) {
						saveToPhoneCanvas.drawBitmap(
								items[i].person[j].getPic(),
								items[i].person[j].matrix,
								items[i].person[j].paint);
					}
				}
			}
		}
		// 获取道具
		for (Bmp mBmp : propBmps) {
			if (mBmp != null) {
				mBmp.cancelHighLight();
				saveToPhoneCanvas.drawBitmap(mBmp.getPic(), mBmp.matrix,
						mBmp.paint);
			}
		}
		return resultBitmap;
	}

	private Bitmap getFitBitmap(Bitmap bitmap) {
		Bitmap bitmapL = null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width <= cj_width && height <= cj_height) {
			float scaleX = (float) cj_width / (float) width;
			float scaleY = (float) cj_height / (float) height;
			float max = scaleX > scaleY ? scaleX : scaleY;
			if(max==scaleX){
				bitmapL = Bitmap.createScaledBitmap(bitmap, (int)Math.ceil(width * max),
						(int) (height * max), false);
				int marginY=(bitmapL.getHeight()-cj_height)/2;
				bitmapL=bitmapL.createBitmap(bitmapL, 0, marginY,
						cj_width>=bitmapL.getWidth()?cj_width:bitmapL.getWidth(),cj_height);
			}else{
				bitmapL = Bitmap.createScaledBitmap(bitmap, (int) (width * max),
						(int)Math.ceil(height * max), false);
				int marginX=(bitmapL.getWidth()-cj_width)/2;
				bitmapL=bitmapL.createBitmap(bitmapL, marginX, 0, cj_width, 
						cj_height>=bitmapL.getHeight()?cj_height:bitmapL.getHeight());
			}
			return bitmapL;

		} else if (width > cj_width && height > cj_height) {
			float scaleX = (float) width / (float)cj_width ;
			float scaleY = (float) height / (float) cj_height;
			float min = scaleX < scaleY ? scaleX : scaleY;
			if(min==scaleX){
				bitmapL = Bitmap.createScaledBitmap(bitmap, (int) (width / min),
						(int) (height / min), false);
				int marginY=(bitmapL.getHeight()-cj_height)/2;
				bitmapL=bitmapL.createBitmap(bitmapL, 0, marginY, cj_width, cj_height);
			}else{
				bitmapL = Bitmap.createScaledBitmap(bitmap, (int) (width / min),
						(int) (height / min), false);
				int marginX=(bitmapL.getHeight()-cj_height)/2;
				bitmapL=bitmapL.createBitmap(bitmapL, marginX,0, cj_width, cj_height);
			}

			return bitmapL;
		} else {
			bitmapL = Bitmap.createScaledBitmap(bitmap, cj_width, cj_height,
					false);
			return bitmapL;
		}
	}

	public void changeHair(Bitmap bitmap) {
		if (null != items[currentPerson]) {
			items[currentPerson].changeHair(bitmap);
			this.invalidate();
		}
	}

	public void changeBody(Bitmap bitmap) {
		if (null != items[currentPerson]) {
			items[currentPerson].changeBody(bitmap);
			this.invalidate();
		}
	}

	public void setBlusher(Bitmap bitmap) {
		if (null != items[currentPerson]) {
			items[currentPerson].setBlusher(bitmap);
			this.invalidate();
		}
	}

	public void setEyebrows(Bitmap bitmap) {
		if (null != items[currentPerson]) {
			items[currentPerson].setEyebrows(bitmap);
			this.invalidate();
		}
	}

	public void setBeard(Bitmap bitmap) {
		if (null != items[currentPerson]) {
			items[currentPerson].setBeard(bitmap);
			this.invalidate();
		}
	}

	public void setFace() {
		if (null != items[currentPerson]) {
			items[currentPerson].setFace();
			this.invalidate();
		}
		
	}

	public void setPaint(Paint paint) {
		if (null != items[currentPerson]) {
			items[currentPerson].setPaint(paint);
			this.invalidate();
		}
		
	}
	

}
