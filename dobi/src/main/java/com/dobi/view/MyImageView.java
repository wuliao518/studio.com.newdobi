package com.dobi.view;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.logic.ImageManager;
import com.dobi.view.MyMoreImageView.Bmp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MyImageView extends ImageView {
	private float width;
	private float height;
	private float itemWidth;
	private float itemHeight;

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		width = CommonMethod.getWidth(context);
		height = CommonMethod.getHeight(context);
		itemWidth = width / (float) 12;
		itemHeight = height / (float) 12;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.move);
		mBmps[0] = new Bmp(bitmap, 0, (int) (width / 2), (int) (itemHeight * 3));
		mBmps[1] = new Bmp(bitmap, 0, (int) (itemWidth * 10),
				(int) (itemHeight * 5));
		mBmps[2] = new Bmp(bitmap, 0, (int) (width / 2), (int) (itemHeight * 9));
		mBmps[3] = new Bmp(bitmap, 0, (int) (itemWidth * 2),
				(int) (itemHeight * 5));

		cornerBmps[0] = new Bmp(bitmap, 0, (int) (itemWidth * 10),
				(int) (itemHeight * 3));
		cornerBmps[1] = new Bmp(bitmap, 0, (int) (itemWidth * 10),
				(int) (itemHeight * 9));
		cornerBmps[2] = new Bmp(bitmap, 0, (int) (itemWidth * 2),
				(int) (itemHeight * 9));
		cornerBmps[3] = new Bmp(bitmap, 0, (int) (itemWidth * 2),
				(int) (itemHeight * 3));

	}

	/**
	 * 旋转前两指角度
	 */
	protected float preCos = 0f;
	/**
	 * 旋转后两指角度
	 */
	protected boolean flagHandler = true;
	/**
	 * 每次执行放大，放大前的两指距离
	 */
	protected float preLength = 480.0f;
	/**
	 * 每次执行放大，放大后的两只距离
	 */
	protected float length = 480.0f;

	protected Context context;
	protected float cos = 0f;
	protected boolean bool = true;
	// 点击模式
	protected int mode = 0;
	// 4个拖动按钮
	private Bmp[] mBmps = new Bmp[4];
	// 4个拐点
	private Bmp[] cornerBmps = new Bmp[4];
	// 背景Bmp
	private Bmp sceneBmp;
	private Bmp temp;
	private float startX, startY;
	private int widthScreen;
	private ImageManager mImageManager;
	private Bitmap mScene;
	protected float centerX = 0, centerY = 0;
	// 切脸bitmap
	Bitmap roundConcerImage = null;
	float movedX = 0, movedY = 0;
	Canvas mCanvas;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = 0;
			temp = null;
			startX = event.getX();
			startY = event.getY();
			order(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = -1;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = 1;
			preLength = spacing(event);
			preCos = rotation(event);
			float[] center = midPoint(event);
			centerX = center[0];
			centerY = center[1];
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == 0) {
				if (temp != null) {
					float x = event.getX();
					float y = event.getY();
					float moveX = x - startX;
					float moveY = y - startY;
					movedX += moveX;
					movedY += moveY;
					if (temp == sceneBmp) {
						temp.matrix.postTranslate(moveX, moveY);
					} else {
						temp.matrix.postTranslate(moveX, moveY);
					}
					startX = x;
					startY = y;
				}

			}
			if (mode == 1) {
				length = spacing(event);
				cos = rotation(event);
				temp = sceneBmp;
				rotateBmp(temp);
				zoomBmp(temp);
				preCos = cos;
				preLength = length;
				invalidate();
			}

		default:
			break;
		}
		invalidate();
		return true;
	}

	private void order(float x, float y) {
		for (int i = 0; i < 4; i++) {
			float[] cener = getCenter(mBmps[i]);
			if ((x > (cener[0] - 50) && x < (cener[0] + 50))
					&& (y > (cener[1] - 50) && y < (cener[1] + 50))) {
				temp = mBmps[i];
				return;
			}
		}
		for (int i = 0; i < 4; i++) {
			float[] cener = getCenter(cornerBmps[i]);
			if ((x > (cener[0] - 50) && x < (cener[0] + 50))
					&& (y > (cener[1] - 50) && y < (cener[1] + 50))) {
				temp = cornerBmps[i];
				return;
			}
		}
		temp = sceneBmp;

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paintScene = new Paint();
		paintScene.setStyle(Paint.Style.STROKE); // 空心
		paintScene.setAntiAlias(true);
		paintScene.setAlpha(75); // Bitmap透明度(0 ~ 100)
		Path path = new Path();
		float[] one = getCenter(mBmps[0]);
		float[] two = getCenter(mBmps[1]);
		float[] three = getCenter(mBmps[2]);
		float[] four = getCenter(mBmps[3]);

		float[] one2two = getCenter(cornerBmps[0]);
		float[] two2three = getCenter(cornerBmps[1]);
		float[] three2four = getCenter(cornerBmps[2]);
		float[] four2one = getCenter(cornerBmps[3]);

		// 0----1
		path.moveTo(one[0], one[1]);
		path.quadTo(one2two[0], one2two[1], two[0], two[1]);
		// 1----2
		// path.moveTo(two[0], two[1]);
		path.quadTo(two2three[0], two2three[1], three[0], three[1]);

		// 2----3
		// path.moveTo(three[0], three[1]);
		path.quadTo(three2four[0], three2four[1], four[0], four[1]);

		// 3----1
		// path.moveTo(four[0],four[1]);
		path.quadTo(four2one[0], four2one[1], one[0], one[1]);

		if (sceneBmp != null) {
			canvas.drawBitmap(sceneBmp.getPic(), sceneBmp.matrix, paintScene);
			// 创建一个和原始图片一样大小位图
			// float[] min=getMin(mBmps[0],mBmps[1],mBmps[2],mBmps[3]);
			// float[] max=getMax(mBmps[0],mBmps[1],mBmps[2],mBmps[3]);
			roundConcerImage = Bitmap.createBitmap((int) width, (int) height,
					Config.ARGB_8888);
			// roundConcerImage =
			// Bitmap.createBitmap((int)(max[0]-min[0]),(int)(max[1]-min[1]),
			// Config.ARGB_8888);
			// 创建带有位图roundConcerImage的画布
			mCanvas = new Canvas(roundConcerImage);
			// mCanvas.translate(-min[0], -min[1]);
			// canvas.drawBitmap(bitmap, new Matrix(), null);
			mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));// 消除锯齿
			mCanvas.clipPath(path, Region.Op.REPLACE);// 设置切割方式
			mCanvas.drawBitmap(mScene, sceneBmp.matrix, null);
			canvas.drawBitmap(roundConcerImage, new Matrix(), null);
		}
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		// canvas.drawBitmap(roundConcerImage, sceneBmp.matrix, paint);
		// 画路径
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		canvas.drawPath(path, paint);
		// 画四个点
		for (int i = 0; i < 4; i++) {
			canvas.drawBitmap(mBmps[i].getPic(), mBmps[i].matrix, new Paint());
		}
		// 画四个拐点
		for (int i = 0; i < 4; i++) {
			canvas.drawBitmap(cornerBmps[i].getPic(), cornerBmps[i].matrix,
					new Paint());
		}
	}

	protected float[] getCenter(Bmp bmp) {
		float[] values = new float[9];
		bmp.matrix.getValues(values);
		float[] point = new float[] { bmp.getPic().getWidth() / 2,
				bmp.getPic().getHeight() / 2 };
		bmp.matrix.mapPoints(point);
		return point;
	}

	// 取手势中心点
	private float[] midPoint(MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		return new float[] { x / 2, y / 2 };
	}

	/**
	 * 每个图片单体类
	 * 
	 * @author Administrator
	 * 
	 */
	public class Bmp {
		/**
		 * 负责移动、放缩、旋转bitmap的类
		 */
		protected Matrix matrix;

		// 放缩前尺寸
		protected float width = 0;
		protected float height = 0;
		/**
		 * 图片
		 */
		private Bitmap pic = null;

		/**
		 * 图片最终显示层级
		 */
		protected int priorityBase = 0;

		/**
		 * 图片中心在控件的X坐标
		 */
		private float preX = 0;
		/**
		 * 图片中心在控件的Y坐标
		 */
		private float preY = 0;

		/**
		 * 基础图片
		 */
		private Bitmap basePic;

		// 构造器
		private Bmp(Bitmap pic, int piority) {
			this.pic = pic;
			this.basePic = pic;
			this.priorityBase = piority;
		}

		/**
		 * @param pic
		 *            :the Bitmap to draw
		 * @param priority
		 *            : bitmap对应的index
		 * @param preX
		 *            坐标
		 * @param preY
		 *            坐标
		 */
		protected Bmp(Bitmap pic, int priority, float preX, float preY) {
			this(pic, priority);
			this.preX = preX;// + pic.getWidth() / 2 * 1.5f;
			this.preY = preY;// + pic.getHeight() / 2 * 1.5f;
			if (matrix == null) {
				this.matrix = new Matrix();
				if (preX == 0 && preY == 0) {

				} else {
					this.matrix.preTranslate(preX,preY);
				}
			}
		}

		/**
		 * 设置图片中心位置X坐标
		 * 
		 * @param x
		 */
		protected void setPreX(float x) {
			this.preX = x;
		}

		/**
		 * 设置图片中心位置Y坐标
		 * 
		 * @param x
		 */
		protected void setPreY(float y) {
			this.preY = y;
		}

		protected void setPic(Bitmap pic) {
			this.pic = pic;
		}

		/**
		 * getPicture
		 * 
		 * @return
		 */
		public Bitmap getPic() {
			return this.pic;
		}

		/**
		 * 释放图片内存
		 */
		protected void recycleMap() {
			if (this.basePic != null) {
				this.basePic.recycle();
				this.basePic = null;
			}
			if (this.pic != null) {
				this.pic.recycle();
				this.pic = null;
			}
		}

	}

	public void Inteligense(Activity activity, Bitmap mBitmap) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		widthScreen = dm.widthPixels;
		mImageManager = new ImageManager();
		mScene = mImageManager.getNewSizeMap(mBitmap, widthScreen);
		sceneBmp = new Bmp(mScene, 0, 0, 0);
		invalidate();
	}

	public void initView(Activity activity, String path) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		widthScreen = dm.widthPixels;
		mImageManager = new ImageManager();
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, option);
		option.inJustDecodeBounds = false;
		option.inPurgeable = true;
		option.inInputShareable = true;
		option.inDither = false;
		option.inPreferredConfig = Config.RGB_565;
		mScene = BitmapFactory.decodeFile(path, option);
		mScene=getFitBitmap(mScene);
		float marginX=0;
		if(mScene.getWidth()>this.width){
			marginX=(mScene.getWidth()-this.width)/2;
		}
		sceneBmp = new Bmp(mScene, 0, -marginX, 0);
		invalidate();
	}

	private Bitmap getFitBitmap(Bitmap bitmap) {
		Bitmap bitmapL = null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width > this.width && height > this.height) {
			float scaleX = (float) width / (float) this.width;
			float scaleY = (float) height / (float) this.height;
			float min = scaleX < scaleY ? scaleX : scaleY;
			bitmapL = Bitmap.createScaledBitmap(bitmap, (int) ((float)width / (float)min),
					(int) ((float)height / (float)min), false);
			return bitmapL;
			
		} else {
			float scaleX = (float) this.width / (float) width;
			float scaleY = (float) this.height / (float) height;
			float max = scaleX > scaleY ? scaleX : scaleY;
			bitmapL = Bitmap.createScaledBitmap(bitmap, (int) ((float)width * (float)max),
					(int) ((float)height * (float)max), false);
			return bitmapL;
		}

	}

	public void saveBitmap(int index) {
		try {
			FileOutputStream out = mImageManager.creatFile("playPhotoClip"
					+ index, "png", "play");
			roundConcerImage.compress(CompressFormat.PNG, 100, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected float[] getMin(Bmp... bmps) {
		List<float[]> apexs = new ArrayList<float[]>();
		for (Bmp bmp : bmps) {
			if (bmp != null) {
				float[][] rect = getRect(bmp);
				// 将每个顶点放在集合中
				for (float[] f : rect) {
					apexs.add(f);
				}
			}

		}
		// 找出所有点中最外围的点
		float minX = apexs.get(0)[0], minY = apexs.get(0)[1];
		for (float[] apex : apexs) {
			minX = minX < apex[0] ? minX : apex[0];
			minY = minY < apex[1] ? minY : apex[1];
		}
		return new float[] { minX, minY };
	}

	protected float[] getMax(Bmp... bmps) {
		List<float[]> apexs = new ArrayList<float[]>();
		for (Bmp bmp : bmps) {
			if (bmp != null) {
				float[][] rect = getRect(bmp);
				// 将每个顶点放在集合中
				for (float[] f : rect) {
					apexs.add(f);
				}
			}
		}
		// 找出所有点中最外围的点
		float maxX = 0, maxY = 0;
		for (float[] apex : apexs) {
			maxX = maxX < apex[0] ? apex[0] : maxX;
			maxY = maxY < apex[1] ? apex[1] : maxY;
		}
		return new float[] { maxX, maxY };
	}

	private float[][] getRect(Bmp bmp) {
		if (bmp != null) {
			float[] f = new float[9];
			bmp.matrix.getValues(f);
			float[][] rect = new float[4][2];
			rect[0][0] = f[0] * 0 + f[1] * 0 + f[2];
			rect[0][1] = f[3] * 0 + f[4] * 0 + f[5];
			rect[1][0] = f[0] * bmp.getPic().getWidth() + f[1] * 0 + f[2];
			rect[1][1] = f[3] * bmp.getPic().getWidth() + f[4] * 0 + f[5];
			rect[2][0] = f[0] * 0 + f[1] * bmp.getPic().getHeight() + f[2];
			rect[2][1] = f[3] * 0 + f[4] * bmp.getPic().getHeight() + f[5];
			rect[3][0] = f[0] * bmp.getPic().getWidth() + f[1]
					* bmp.getPic().getHeight() + f[2];
			rect[3][1] = f[3] * bmp.getPic().getWidth() + f[4]
					* bmp.getPic().getHeight() + f[5];
			return rect;
		}
		return null;
	}

	// 计算长度
	@SuppressLint("FloatMath")
	protected float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 取旋转角度
	protected float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	private void zoomBmp(Bmp... bmps) {
		for (Bmp bmp : bmps) {
			float scale = length / preLength;
			if (bmp != null) {
				if (!isChange(bmp, scale))
					return;
			}
		}

		for (Bmp bmp : bmps) {
			float scale = length / preLength;
			if (bmp != null) {
				if (scale > 1.5) {
					scale = (float) (scale * 0.9);
				}
				if (scale < 0.8) {
					scale = (float) (scale * 1.1);
				}
				bmp.matrix.postScale(scale, scale, centerX, centerY);
			}
		}
	}

	private void rotateBmp(Bmp... bmps) {
		for (Bmp bmp : bmps) {
			if (bmp != null) {
				bmp.matrix.postRotate(cos - preCos, centerX, centerY);
			}
		}
	}

	private boolean isChange(Bmp bmp, float scale) {
		// float[] center=getCenter(bmp);
		float zoom = scale * getScale(bmp);
		if (zoom > 4 || zoom < 0.3) {
			return false;
		}
		return true;
	}

	protected float getScale(Bmp bmp) {
		float[] values = new float[9];
		bmp.matrix.getValues(values);
		return (float) Math.sqrt(Math.pow(values[0], 2)
				+ Math.pow(values[3], 2));
	}

}
