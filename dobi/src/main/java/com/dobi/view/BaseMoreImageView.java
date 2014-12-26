package com.dobi.view;

import java.util.ArrayList;
import java.util.List;

import com.dobi.R;
import com.dobi.common.ConstValue;
import com.dobi.logic.ImageManager;
import com.dobi.ui.MainActivity;
import com.dobi.ui.MoreActivity;
import com.dobi.ui.BaseImageView.Bmp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

public class BaseMoreImageView extends ImageView {
	protected boolean Begin = true;
	/**
	 * 除道具以外的图片数量
	 */
	protected int baseImgCount = 6;
	protected final static int PROP_COUNT = 20;// 道具默认上限
	// 背景
	protected static Bitmap sceneBitmap;
	public static Bmp[] mBmps;
	protected Bmp tempBmp;
	//删除按钮
	protected Bmp deleteBmp;
	protected ImageManager mImageManager;
	protected int twoPoint = 0;
	protected float startX = 0, startY = 0;
	protected float centerX = 0, centerY = 0;
	protected int mode = 0;
	// 显示按钮
	protected Handler handler;
	/**
	 * 旋转前两指角度
	 */
	protected float preCos = 0f;
	/**
	 * 旋转后两指角度
	 */
	protected float cos = 0f;
	/**
	 * 每次执行放大，放大前的两指距离
	 */
	protected float preLength = 480.0f;
	/**
	 * 每次执行放大，放大后的两只距离
	 */
	protected float length = 480.0f;
	/**
	 * 是否显示删除
	 */
	protected boolean isDelete=true;
	protected Context context;
	protected boolean bool = true;

	public BaseMoreImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mImageManager = new ImageManager();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = 0;
			startX = event.getX();
			startY = event.getY();
			order(event.getX(), event.getY());
			Begin = true;
			break;
		case MotionEvent.ACTION_UP:
			if(tempBmp!=null){
				showDelete();
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = -1;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = 1;
			preLength = spacing(event);
			preCos = rotation(event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == 0) {
				float x = event.getX();
				float y = event.getY();
				switch (MoreActivity.current) {
				case 0:
				case 1:
					translateBmp(x, y, tempBmp);
					break;
				default:
					break;
				}
				setCenter(MainActivity.current);
				startX = x;
				startY = y;
				invalidate();
			}
			if (mode == 1) {
				length = spacing(event);
				cos = rotation(event);
				switch (MoreActivity.current) {
				case 0:
				case 1:
					zoomBmp(tempBmp);
					rotateBmp(tempBmp);
					break;
				default:
					break;
				}
				preCos = cos;
				preLength = length;
				invalidate();
			}
		}
		return true;
	}

	private void setCenter(int current) {
		float[] center = new float[2];
		switch (MoreActivity.current) {
		case 0:
			if (tempBmp != null) {
				center = getCenter(tempBmp);
				centerX = center[0];
				centerY = center[1];
			}
			break;
		case 1:
			if (tempBmp != null) {
				center = getCenter(tempBmp);
				centerX = center[0];
				centerY = center[1];
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 次序为i的图片是否被点击到
	 * 
	 * @param i
	 * @return
	 */
	private boolean isPoint(Bmp bmp, float X, float Y) {
		float[] min = getMin(bmp);
		float[] max = getMax(bmp);
		if (X > min[0] && X < max[0] && Y > min[1] && Y < max[1]) {
			return true;
		}
		return false;
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

	private float[] getMin(Bmp... bmps) {
		List<float[]> apexs = new ArrayList<float[]>();

		for (Bmp bmp : bmps) {
			float[][] rect = getRect(bmp);
			// 将每个顶点放在集合中
			for (float[] f : rect) {
				apexs.add(f);
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

	private float[] getMax(Bmp... bmps) {
		List<float[]> apexs = new ArrayList<float[]>();
		for (Bmp bmp : bmps) {
			float[][] rect = getRect(bmp);
			// 将每个顶点放在集合中
			for (float[] f : rect) {
				apexs.add(f);
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

	protected void order(float x, float y) {
		clearLine();
		if(deleteBmp!=null){
			if(isPoint(deleteBmp, x, y)){
				deleteProp();
				invalidate();
				isDelete=false;
				return;
			}
		}
		for (int i = baseImgCount + PROP_COUNT; i >= baseImgCount; i--) {
			if(mBmps[i]!=null){
				if (isPoint(mBmps[i], x, y)) {
					clearProp();
					int id = mBmps[i].getImgId();
					Bmp temp = mBmps[i];
					if (mBmps[baseImgCount + PROP_COUNT] != null) {
						mBmps[id] = mBmps[baseImgCount + PROP_COUNT];
						mBmps[id].setImgId(id);
						mBmps[baseImgCount + PROP_COUNT] = temp;
						mBmps[baseImgCount + PROP_COUNT].setImgId(baseImgCount + PROP_COUNT);

					} else {
						mBmps[baseImgCount + PROP_COUNT] = temp;
						mBmps[baseImgCount + PROP_COUNT].setImgId(baseImgCount + PROP_COUNT);
						mBmps[id] = null;
					}
					tempBmp = mBmps[baseImgCount + PROP_COUNT];
					tempBmp.addHighLight();
					isDelete=true;
					invalidate();
					return;
				}
			}
		}
		for (int i = 0; i < baseImgCount; i+=2) {
			if(mBmps[i]!=null){
				if (isPoint(mBmps[i], x, y)) {
					clearProp();
					tempBmp = mBmps[i];
					tempBmp.addHighLight();
					isDelete=false;
					invalidate();
					return;
				}
			}
		}
		clearProp();
		isDelete=false;
		invalidate();
	}

	private void deleteProp() {
		if(mBmps[baseImgCount + PROP_COUNT]!=null){
			mBmps[baseImgCount + PROP_COUNT].getPic().recycle();
			mBmps[baseImgCount + PROP_COUNT]=null;
			clearProp();
		}
		
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
				bmp.matrix.postScale(scale, scale, getCenter(bmp)[0], getCenter(bmp)[1]);
			}
		}
	}

	private boolean isChange(Bmp bmp, float scale) {
		float zoom = scale * getScale(bmp);
		if (zoom > 4 || zoom < 0.3) {
			return false;
		}
		return true;
	}
	public void clearLine(){
		for(int i=0;i<baseImgCount + PROP_COUNT+1;i++){
			if(mBmps[i]!=null){
				mBmps[i].cancelHighLight();
			}
		}
	}
	
	protected float getScale(Bmp bmp) {
		float[] values = new float[9];
		bmp.matrix.getValues(values);
		return (float) Math.sqrt(Math.pow(values[0], 2)
				+ Math.pow(values[3], 2));
	}

	private void rotateBmp(Bmp... bmps) {
		for (Bmp bmp : bmps) {
			if (bmp != null) {
				bmp.matrix.postRotate(cos - preCos, getCenter(bmp)[0], getCenter(bmp)[1]);
			}
		}
	}

	private void translateBmp(float x, float y, Bmp... bmps) {
		if (Math.abs(x - startX) > 2 || Math.abs(y - startY) > 2) {
			for (Bmp bmp : bmps) {
				if (bmp != null && !isChange(bmp, x - startX, y - startY)) {
					return;
				}
			}
			for (Bmp bmp : bmps) {
				if (bmp != null && isChange(bmp, x - startX, y - startY)) {
					bmp.matrix
							.postTranslate((x - startX) / 2, (y - startY) / 2);
				}
			}
		}
	}

	/**
	 * 获取Bmp中心
	 * 
	 * @param bmp
	 * @return
	 */
	protected float[] getCenter(Bmp bmp) {
		float[] values = new float[9];
		bmp.matrix.getValues(values);
		float[] point = new float[] { bmp.getPic().getWidth() / 2,
				bmp.getPic().getHeight() / 2 };
		bmp.matrix.mapPoints(point);
		return point;
	}

	private boolean isChange(Bmp bmp, float x, float y) {
		float[] center = getCenter(bmp);
		WindowManager manager = (WindowManager) context
				.getSystemService("window");
		Display display = manager.getDefaultDisplay();
		float width = display.getWidth();
		float height = display.getHeight();
		if (center[0] + x > 0 && center[1] + y > 0 && center[0] + x < width
				&& center[1] + y < height) {
			return true;
		}
		return false;
	}

	public float[] rotalPoint(float[] p, float X, float Y, Matrix matrix) {
		float re[] = new float[2];
		float matrixArray[] = new float[9];
		matrix.getValues(matrixArray);
		float a = p[0] - X;
		float b = p[1] - Y;
		re[0] = a * matrixArray[0] - b * matrixArray[1] + X;
		re[1] = -a * matrixArray[3] + b * matrixArray[4] + Y;
		return re;
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
		 * bitmap对应的当前显示层级别的index
		 */
		private int priority = 0;
		/**
		 * 图片标识
		 */
		private int imgId = 0;
		/**
		 * 是否可调整
		 */
		private boolean canChange;
		/**
		 * 是否只能平移
		 */
		private boolean onlyCanTranslation;

		/**
		 * 是否被选中
		 */
		private boolean isFocus;
		/**
		 * 基础图片
		 */
		private Bitmap basePic;

		/**
		 * 是否容易选中（判断选中状态是否支持触碰透明部分）
		 */
		private boolean isEasySelect;

		/**
		 * 图片已旋转的角度
		 */
		private float rotateSize;

		protected Paint paint;

		// 构造器
		private Bmp(Bitmap pic, int piority) {
			this.pic = pic;
			this.basePic = pic;
			this.priority = piority;
			imgId = piority;
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
		 * @param iscanChange
		 *            是否可被选中
		 * @param isEasySelect
		 *            是否可点中透明部分
		 * @param isOnlyCanTranslation
		 *            是否只能平移
		 */
		protected Bmp(Bitmap pic, int priority, float preX, float preY,
				boolean iscanChange, boolean isEasySelect,
				boolean isOnlyCanTranslation) {
			this(pic, priority);
			this.preX = preX;// + pic.getWidth() / 2 * 1.5f;
			this.preY = preY;// + pic.getHeight() / 2 * 1.5f;
			this.canChange = iscanChange;
			this.isEasySelect = isEasySelect;
			this.onlyCanTranslation = isOnlyCanTranslation;
			if (matrix == null) {
				this.matrix = new Matrix();
				this.matrix.preTranslate(this.preX - this.pic.getWidth() / 2,
						this.preY - this.pic.getHeight() / 2);
			}
		}

		/**
		 * 对初始化过的Bmp重新赋值，保证不影响原来旋转的角度
		 * 
		 * @param pic
		 * @param priority
		 *            bitmap对应的index
		 * @param preX
		 *            坐标
		 * @param preY
		 *            坐标
		 */
		protected void SetBmpInfo(Bitmap pic) {
			this.pic = pic;
			this.basePic = pic;
		}

		// set Priority
		protected void setPiority(int priority) {
			this.priority = priority;
		}

		// return Priority
		protected int getPriority() {
			return this.priority;
		}
		public void setPaint(Paint paint) {
			this.paint = paint;
		}
		/**
		 * 获取图片中心位置所在坐标
		 * 
		 * @param i
		 *            1X坐标，2Y坐标
		 * @return 图片中心位置所在坐标
		 */
		@SuppressWarnings("null")
		protected float getXY(int i) {
			if (i == 1) {
				return this.preX;
			} else if (i == 2) {
				return this.preY;
			}
			return (Float) null;
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
			this.basePic = pic;
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
		 * 获取放缩前宽度
		 * 
		 * @return
		 */
		protected float getWidth() {
			return width;
		}

		/**
		 * 获取放缩前高度
		 * 
		 * @return
		 */
		protected float getHeight() {
			return height;
		}

		/**
		 * 获取图片唯一标示，从0开始
		 * 
		 * @return
		 */
		protected int getImgId() {
			return imgId;
		}

		/**
		 * 设置唯一标示
		 * 
		 * @param imgId
		 */
		protected void setImgId(int imgId) {
			this.imgId = imgId;
			this.priority = imgId;
		}

		/**
		 * 获取是否可调整
		 * 
		 * @return
		 */
		protected boolean isCanChange() {
			return canChange;
		}

		/**
		 * 是否可调整
		 * 
		 * @return
		 */
		public void setCanChange(boolean canChange) {
			this.canChange = canChange;
		}

		protected void setBasePic(Bitmap basePic) {
			this.basePic = basePic;
		}

		/**
		 * 增加光圈
		 */
		protected void addHighLight() {

			/*
			 * 津贴图片边缘模式，不够清晰问题未解决 Paint p = new Paint();
			 * p.setColor(Color.BLUE);// 红色的光晕 p.setStyle(Paint.Style.STROKE);//
			 * 设置空心 p.setStrokeWidth(40f);
			 * 
			 * Bitmap b = this.pic; Bitmap bitmap =
			 * Bitmap.createBitmap(b.getWidth(), b.getHeight(),
			 * ConstValue.MY_CONFIG); Canvas canvas = new Canvas(bitmap);
			 * canvas.drawBitmap(b.extractAlpha(), 0, 0, p); StateListDrawable
			 * sld = new StateListDrawable(); sld.addState(new int[] {
			 * android.R.attr.state_pressed }, new BitmapDrawable(bitmap));
			 * DrawViewBase.this.setBackgroundDrawable(sld);
			 * 
			 * canvas.drawBitmap(b, 0, 0, null);
			 */

			Bitmap b = this.pic;
			Bitmap bitmap = Bitmap.createBitmap((int) pic.getWidth(),
					(int) pic.getHeight(), ConstValue.MY_CONFIG_8888);
			Canvas tempcanvas = new Canvas(bitmap);
			RectF rec = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());// 画边框

			Paint paint = new Paint();
			paint.setColor(Color.WHITE);// 设置边框颜色
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(5f);// 设置边框宽度

			tempcanvas.drawBitmap(b, 0, 0, null);
			tempcanvas.drawRoundRect(rec, 10f, 10f, paint);
			this.pic = bitmap;
		}

		/**
		 * 取消光圈
		 */
		protected void cancelHighLight() {
			if(this.pic==this.basePic){
				return;
			}else{
				this.pic.recycle();
				this.pic = null;
				this.pic = Bitmap.createScaledBitmap(this.basePic,
						(int) this.basePic.getWidth(),
						(int) this.basePic.getHeight(), false);
			}
			
		}

		/**
		 * 是否拥有焦点
		 * 
		 * @return
		 */
		protected boolean isFocus() {
			return isFocus;
		}

		protected void setFocus(boolean isFocus) {
			this.isFocus = isFocus;
		}
		/**
		 * 是否可点中透明部分
		 * 
		 * @return
		 */
		protected boolean isEasySelect() {
			return isEasySelect;
		}

		/**
		 * 是否只能平移
		 * 
		 * @return
		 */
		protected boolean isOnlyCanTranslation() {
			return onlyCanTranslation;
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

	/**
	 * 选中某图片，取消其他图片的选中状态
	 * 
	 * @param bm
	 */
	public void selectMap(Bmp bm) {
		if (bm == null) {
			return;
		}
		tempBmp = bm;
	}
	public void deleteItem(int id){
		if(mBmps[id]!=null){
			mBmps[id].getPic().recycle();
			mBmps[id]=null;
		}
	}
	/**
	 * 显示删除按钮
	 */
	public void showDelete(){
		if(isDelete){
			float[] left=getLeftPoint(tempBmp);
			Bitmap reverse=BitmapFactory.decodeResource(getResources(), R.drawable.delete);
			deleteBmp=new Bmp(reverse, -1,left[0],left[1],true,false,false);
			invalidate();
		}
		
	}
	/**
	 * 清楚删除按钮
	 */
	public void clearProp() {
		if(deleteBmp!=null){
			deleteBmp.getPic().recycle();
			deleteBmp=null;
		}
	}
	private float[] getLeftPoint(Bmp bmp) {
		float[] values = new float[] { 0, 0 };
		bmp.matrix.mapPoints(values);
		return values;
	}
	public void clearBuffer(){
		if (sceneBitmap != null) {
			sceneBitmap.recycle();
			sceneBitmap=null;
		}
		for (int i = 0; i < mBmps.length; i++) {
			if (mBmps[i] != null) {
				mBmps[i].getPic().recycle();
				mBmps[i]=null;
			}
		}
		if (deleteBmp != null) {
			deleteBmp.getPic().recycle();
			deleteBmp=null;
		}
	}


}
