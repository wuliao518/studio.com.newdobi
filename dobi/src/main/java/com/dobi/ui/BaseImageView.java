package com.dobi.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.dobi.R;
import com.dobi.common.ConstValue;
import com.dobi.logic.ImageManager;
import com.dobi.logic.ImageManager.RectangleManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * @author wuliao
 * 
 */
public class BaseImageView extends ImageView {
	protected boolean Begin = true;
	// 存放场景Bitmap
	public Bmp[] prop = new Bmp[2];
	public Bitmap sceneBitmap;
	public Bmp[] persons = new Bmp[PERSON_COUNT];
	// 白色框
	public Bmp lightBmp;
	public Bmp[] propBmps;
	public static int ELEMENTS_COUNT = 6;
	public static int PERSON_COUNT = 2;
	public static int PROP_COUNT = 20;
	// 存放选中的Bmp和一个白色框和翻转、删除按钮
	public Bmp[] tempBmp = new Bmp[ELEMENTS_COUNT + 1];
	protected ImageManager mImageManager;
	protected int twoPoint = 0;
	protected float startX = 0, startY = 0;
	protected float centerX = 0, centerY = 0;
	protected int mode = 0;
	// 显示按钮
	protected static Handler handler;
	/**
	 * 旋转前两指角度
	 */
	protected float preCos = 0f;
	/**
	 * 旋转后两指角度
	 */
	public static boolean flagHandler = true;
	/**
	 * 每次执行放大，放大前的两指距离
	 */
	protected float preLength = 480.0f;
	/**
	 * 每次执行放大，放大后的两只距离
	 */
	protected float length = 480.0f;
	final static float FACE_EXP_SIZE = 0.21f;// 正比放大脸部，补充透明部分.
	protected final static float SCALE_PRE = 7f;
	/**
	 * 图像显示后的放缩倍数，初始化函数中控制比例
	 */
	protected float scale = 1;
	protected Activity activity;
	protected static int cj_width, cj_height;
	// 默认当前选中的人
	protected int currentPerson = 0;
	protected PersonItem[] items;
	protected Context context;
	// 0 表示人 1表示道具
	private int deleteType = -1;
	protected float cos = 0f;
	protected boolean bool = true;
	public static boolean isDelete = true;

	public BaseImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mImageManager = new ImageManager();
		propBmps = new Bmp[PROP_COUNT];
		items = new PersonItem[PERSON_COUNT];
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = 0;
			startX = event.getX();
			startY = event.getY();
			// 在道具 脸 眉毛 胡子情况可以自由操作
			order(event.getX(), event.getY());
			clearProp();
			Begin = true;
			break;
		case MotionEvent.ACTION_UP:
			switch (MainActivity.current) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				showDelete(tempBmp);
				break;
			default:
				break;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = -1;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = 1;
			preLength = spacing(event);
			preCos = rotation(event);
			setCenter(MainActivity.current);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == 0) {
				float x = event.getX();
				float y = event.getY();
				switch (MainActivity.current) {
				case 0:
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}
					break;
				case 1:
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}
					break;
				case 2:
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}
					break;
				case 3:
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}

					break;
				case 4:
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}
					break;
				case 5:
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}
					break;
				case 6:
//					if (tempBmp[0] != null && tempBmp[5] != null) {
//						translateBmp(x, y, tempBmp);
//					} else if (tempBmp[0] != null) {
//						translateBmp(x, y, tempBmp[0]);
//					}
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}
					break;
				case 7:
//					if (tempBmp[0] != null && tempBmp[5] != null) {
//						translateBmp(x, y, tempBmp);
//					} else if (tempBmp[0] != null) {
//						translateBmp(x, y, tempBmp[0]);
//					}
					if (tempBmp[0] != null) {
						translateBmp(x, y, tempBmp);
					}
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
				switch (MainActivity.current) {
				case 0:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				case 1:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				case 2:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				case 3:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				case 4:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				case 5:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				case 6:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				case 7:
					if (tempBmp[0] != null) {
						rotateBmp(tempBmp);
						zoomBmp(tempBmp);
					}
					break;
				default:
					break;
				}
				setCenter(MainActivity.current);
				preCos = cos;
				preLength = length;
				invalidate();
			}
		}
		return true;
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

	private void translateBmp(float x, float y, Bmp... bmps) {
		if (Math.abs(x - startX) > 2 || Math.abs(y - startY) > 2) {
			for (Bmp bmp : bmps) {
				if (bmp != null && bmp.getPic() != null
						&& !isChange(bmp, x - startX, y - startY)) {
					return;
				}
			}
			for (Bmp bmp : bmps) {
				if (bmp != null && bmp.getPic() != null
						&& isChange(bmp, x - startX, y - startY)) {
					bmp.matrix.postTranslate((float) ((x - startX) / 1.5),
							(float) ((y - startY) / 1.5));
				}
			}
		}
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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	/** ============内部算法，不需调整===========start======= */

	public float[] rotalPoint(float[] p, float X, float Y, float width,
			float height, Matrix matrix) {
		float re[] = new float[2];
		float matrixArray[] = new float[9];
		matrix.getValues(matrixArray);
		float a = p[0] - X;
		float b = p[1] - Y;
		re[0] = a * matrixArray[0] - b * matrixArray[1] + X;
		re[1] = -a * matrixArray[3] + b * matrixArray[4] + Y;
		return re;
	}

	public void scale(float preX, float preY, float x, float y, Matrix matrix) {
		float[] matrixArray = new float[9];
		matrix.getValues(matrixArray);
		float a = x - preX;
		float b = y - preY;
		matrixArray[2] = a;
		matrixArray[5] = b;
		matrix.setValues(matrixArray);
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

	private void setCenter(int current) {
		float[] center = new float[2];
		if (tempBmp[0] != null) {
			center = getCenter(tempBmp);
			centerX = center[0];
			centerY = center[1];
		}

	}

	// 取消所有光圈
	public void cancleAll() {
		// 取消道具
		for (int i = 0; i < PROP_COUNT; i++) {
			if (propBmps[i] != null) {
				propBmps[i].cancelHighLight();
			}
		}
		// 取消大线圈
		if (lightBmp != null) {
			lightBmp.recycleMap();
			lightBmp = null;
		}
		// 取消人物线圈
		for (int i = PERSON_COUNT - 1; i >= 0; i--) {
			if (items[i] != null) {
				for (int j = ELEMENTS_COUNT - 1; j >= 0; j--) {
					if (items[i].person[j] != null) {
						items[i].person[j].cancelHighLight();
					}
				}
			}
		}
		this.invalidate();
	}

	public void clearCick() {
		for (int i = 0; i < ELEMENTS_COUNT + 1; i++) {
			if (tempBmp[i] != null) {
				tempBmp[i] = null;
			}
		}
	}

	public void setPropBack() {
		for(int i=0;i<PROP_COUNT;i++){
			if(propBmps[i]!=null){
				propBmps[i].isBeforePerson=false;
			}
		}
	}

	/** ===============内部算法，不需调整 ===========end====================* */

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
		public Matrix matrix;
		// paint处理颜色
		public Paint paint;
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
		public boolean isBeforePerson = true;
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
		 * 表示人在集合中的id
		 */
		private int personId = -1;
		/**
		 * 是否容易选中（判断选中状态是否支持触碰透明部分）
		 */
		private boolean isEasySelect;

		/**
		 * 图片已旋转的角度
		 */
		private float rotateSize;

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

		protected void setPaint(Paint paint) {
			this.paint = paint;
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

		public int getPersonId() {
			return this.personId;
		}

		public void setPersonId(int id) {
			personId = id;
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
		public int getImgId() {
			return imgId;
		}

		/**
		 * 设置唯一标示
		 * 
		 * @param imgId
		 */
		public void setImgId(int imgId) {
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
		public void addHighLight() {

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
					(int) pic.getHeight(), Config.ARGB_8888);
			Canvas tempcanvas = new Canvas(bitmap);
			RectF rec = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());// 画边框

			Paint paint = new Paint();
			paint.setAntiAlias(true);
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
			if (this.pic == this.basePic) {
				return;
			} else {
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

		// /**
		// * 初次加载的图片初始化位置，保证以中心为轴旋转
		// */
		// protected void intBitmap() {
		// this.matrix.preTranslate(this.preX - this.width / 2, this.preY
		// - this.height / 2);
		// }

		/**
		 * 是否只能平移
		 * 
		 * @return
		 */
		protected boolean isOnlyCanTranslation() {
			return onlyCanTranslation;
		}

		/**
		 * 获取已经旋转的角度
		 * 
		 * @return
		 */
		protected float getRotateSize() {
			return rotateSize;
		}

		/**
		 * 设置已经旋转的角度
		 * 
		 * @param rotateSize
		 */
		protected void setRotateSize(float rotateSize) {
			this.rotateSize = rotateSize;
		}

		/**
		 * 获取对应的矩形类
		 * 
		 * @return
		 */
		protected RectangleManager getRectangle() {
			RectangleManager result = mImageManager.new RectangleManager(
					this.preX, this.preY, this.pic.getWidth(),
					this.pic.getHeight(), this.rotateSize);
			return result;
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
	 */
	protected void order(float x, float y) {

		for (int j = 0; j < 2; j++) {
			if (prop[j] != null) {
				if (isPoint(prop[j], x, y)) {
					if (j == 0) {
						reverseProp();
					}
					if (j == 1) {
						delProp();
						flagHandler = false;
					}
					return;
				}
			}
		}
		cancleAll();
		switch (MainActivity.current) {
		case 0:
			break;
		case 1:
		case 2:
		case 3:
			isDelete = false;
			flagHandler = false;
			for (int i = PERSON_COUNT - 1; i >= 0; i--) {
				if (items[i] != null) {
					for (int j = ELEMENTS_COUNT - 1; j >= 0; j--) {
						if (items[i].person[j] != null) {
							if (isPoint(items[i].person[j], x, y)) {
								currentPerson = i;
								tempBmp[0] = items[i].person[j];
								tempBmp[1] = null;
								tempBmp[2] = null;
								tempBmp[3] = null;
								tempBmp[4] = null;
								tempBmp[5] = null;
								tempBmp[6] = null;
								tempBmp[0].addHighLight();
								if (j == 0 || j == 1 || j == 5) {
									flagHandler = true;
								}
								return;
							}
						}
					}
				}
			}
			flagHandler = false;
			break;
		case 4:
			isDelete = false;
			for (int i = PERSON_COUNT - 1; i >= 0; i--) {
				if (items[i] != null) {
					for (int j = ELEMENTS_COUNT - 1; j >= 0; j--) {
						if (items[i].person[j] != null) {
							if (isPoint(items[i].person[j], x, y)) {
								currentPerson = i;
								if (j == 0) {// 选中头
									tempBmp[0] = items[i].person[j];
									tempBmp[1] = null;
									tempBmp[2] = null;
									tempBmp[3] = null;
									tempBmp[4] = null;
									tempBmp[5] = null;
									tempBmp[6] = null;
									tempBmp[0].addHighLight();
								} else if (j == 5) {// 选中
									tempBmp[0] = items[i].person[j];
									tempBmp[1] = null;
									tempBmp[2] = null;
									tempBmp[3] = null;
									tempBmp[4] = null;
									tempBmp[5] = null;
									tempBmp[6] = null;
									tempBmp[0].addHighLight();
								} else {
									tempBmp[0] = items[i].person[1];
									tempBmp[1] = items[i].person[2];
									tempBmp[2] = items[i].person[3];
									tempBmp[3] = items[i].person[4];
									tempBmp[4] = null;
									tempBmp[5] = null;
									tempBmp[6] = null;
									Bitmap rect = getRect(tempBmp);
									lightBmp = new Bmp(rect, -1,
											getCenter(tempBmp)[0],
											getCenter(tempBmp)[1], true, false,
											false);
									tempBmp[6] = lightBmp;
								}
								flagHandler = true;
								return;
							}
						}
					}
				}
			}
			flagHandler = false;
			break;
		case 5:
			isDelete = false;
			for (int i = PERSON_COUNT - 1; i >= 0; i--) {
				if (items[i] != null) {
					for (int j = ELEMENTS_COUNT - 1; j >= 0; j--) {
						if (items[i].person[j] != null) {
							if (isPoint(items[i].person[j], x, y)) {
								currentPerson = i;
								if (j == 0) {
									tempBmp[0] = items[i].person[0];
									tempBmp[1] = null;
									tempBmp[2] = null;
									tempBmp[3] = null;
									tempBmp[4] = null;
									tempBmp[5] = null;
									tempBmp[6] = null;
									tempBmp[0].addHighLight();
								} else {
									tempBmp[0] = items[i].person[1];
									tempBmp[1] = items[i].person[2];
									tempBmp[2] = items[i].person[3];
									tempBmp[3] = items[i].person[4];
									tempBmp[4] = items[i].person[5];
									tempBmp[5] = null;
									tempBmp[6] = null;
									Bitmap rect = getRect(tempBmp);
									lightBmp = new Bmp(rect, -1,
											getCenter(tempBmp)[0],
											getCenter(tempBmp)[1], true, false,
											false);
									tempBmp[6] = lightBmp;
								}
								flagHandler = true;
								return;
							}
						}
					}
				}
			}
			flagHandler = false;
			break;
		case 6:
		case 7:
			isDelete = true;
			// 前选择道具
			for (int g = PROP_COUNT - 1; g >= 0; g--) {
				if (propBmps[g] != null && propBmps[g].isBeforePerson) {
					if (isPoint(propBmps[g], x, y)) {
						tempBmp[0] = null;
						tempBmp[1] = null;
						tempBmp[2] = null;
						tempBmp[3] = null;
						tempBmp[4] = null;
						tempBmp[5] = null;
						tempBmp[6] = null;
						if (g == PROP_COUNT - 1) {
							tempBmp[0] = propBmps[g];
						} else {
							if (propBmps[PROP_COUNT - 1] == null) {
								propBmps[PROP_COUNT - 1] = propBmps[g];
								propBmps[g] = null;
								tempBmp[0] = propBmps[PROP_COUNT - 1];
							} else {
								Bmp temp = propBmps[g];
								propBmps[g] = propBmps[PROP_COUNT - 1];
								propBmps[PROP_COUNT - 1] = temp;
								tempBmp[0] = propBmps[PROP_COUNT - 1];
							}
						}
						tempBmp[0].addHighLight();
						flagHandler = true;
						deleteType = 1;
						return;
					}
				}
			}
			// 选择人物
			for (int k = PROP_COUNT - 1; k >= 0; k--)
				for (int i = PERSON_COUNT - 1; i >= 0; i--) {
					if (items[i] != null) {
						for (int j = ELEMENTS_COUNT - 1; j >= 0; j--) {
							if (items[i].person[j] != null) {
								if (isPoint(items[i].person[j], x, y)) {
									setPropBack();
									if (i == 0) {
										if (items[1] != null) {
											currentPerson = 1;
											PersonItem person;
											person = items[0];
											items[0] = items[1];
											items[0].personId = 0;
											items[1] = person;
											items[1].personId = 1;
											tempBmp[0] = items[1].person[0];
											tempBmp[1] = items[1].person[1];
											tempBmp[2] = items[1].person[2];
											tempBmp[3] = items[1].person[3];
											tempBmp[4] = items[1].person[4];
											tempBmp[5] = items[1].person[5];
											tempBmp[6] = null;
											Bitmap rect = getRect(tempBmp);
											lightBmp = new Bmp(rect, -1,
													getCenter(tempBmp)[0],
													getCenter(tempBmp)[1],
													true, false, false);
											tempBmp[6] = lightBmp;
											deleteType = 0;
											flagHandler = true;
											return;
										}
										currentPerson = i;
										tempBmp[0] = items[i].person[0];
										tempBmp[1] = items[i].person[1];
										tempBmp[2] = items[i].person[2];
										tempBmp[3] = items[i].person[3];
										tempBmp[4] = items[i].person[4];
										tempBmp[5] = items[i].person[5];
										tempBmp[6] = null;
										Bitmap rect = getRect(tempBmp);
										lightBmp = new Bmp(rect, -1,
												getCenter(tempBmp)[0],
												getCenter(tempBmp)[1], true,
												false, false);
										tempBmp[6] = lightBmp;
										deleteType = 0;
										flagHandler = true;
										return;
									} else if (i == 1) {
										currentPerson = i;
										tempBmp[0] = items[i].person[0];
										tempBmp[1] = items[i].person[1];
										tempBmp[2] = items[i].person[2];
										tempBmp[3] = items[i].person[3];
										tempBmp[4] = items[i].person[4];
										tempBmp[5] = items[i].person[5];
										tempBmp[6] = null;
										Bitmap rect = getRect(tempBmp);
										lightBmp = new Bmp(rect, -1,
												getCenter(tempBmp)[0],
												getCenter(tempBmp)[1], true,
												false, false);
										tempBmp[6] = lightBmp;
										deleteType = 0;
										flagHandler = true;
										return;

									}
								}
							}
						}
					}
				}
			// 后选择道具
			for (int g = PROP_COUNT - 1; g >= 0; g--) {
				if (propBmps[g] != null && !propBmps[g].isBeforePerson) {
					if (isPoint(propBmps[g], x, y)) {
						propBmps[g].isBeforePerson=true;
						tempBmp[0] = null;
						tempBmp[1] = null;
						tempBmp[2] = null;
						tempBmp[3] = null;
						tempBmp[4] = null;
						tempBmp[5] = null;
						tempBmp[6] = null;
						if (g == PROP_COUNT - 1) {
							tempBmp[0] = propBmps[g];
						} else {
							if (propBmps[PROP_COUNT - 1] == null) {
								propBmps[PROP_COUNT - 1] = propBmps[g];
								propBmps[g] = null;
								tempBmp[0] = propBmps[PROP_COUNT - 1];
							} else {
								Bmp temp = propBmps[g];
								propBmps[g] = propBmps[PROP_COUNT - 1];
								propBmps[PROP_COUNT - 1] = temp;
								tempBmp[0] = propBmps[PROP_COUNT - 1];
							}
						}
						tempBmp[0].addHighLight();
						flagHandler = true;
						deleteType = 1;
						return;
					}
				}
			}

			flagHandler = false;
			break;
		default:
			break;
		}
		cancleAll();
		this.invalidate();

	}

	private void clearTemp() {
		tempBmp[0] = null;
		tempBmp[1] = null;
		tempBmp[2] = null;
		tempBmp[3] = null;
		tempBmp[4] = null;
		tempBmp[5] = null;
		tempBmp[6] = null;
	}

	/**
	 * 根据显示级别获取图片
	 * 
	 * @param bmps
	 * @param priority
	 * @return
	 */
	protected Bmp findByPiority(Bmp[] bmps, int priority) {
		for (Bmp p : bmps) {
			if (p != null && p.priority == priority) {
				return p;
			}
		}
		return null;
	}

	/**
	 * 图片是否被点击到
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

	private float[] getLeftPoint(Bmp bmp) {
		float[] values = new float[] { 0, 0 };
		bmp.matrix.mapPoints(values);
		return values;
	}

	private float[] getRightPoint(Bmp bmp) {
		float[] values = new float[] { bmp.getPic().getWidth(), 0 };
		bmp.matrix.mapPoints(values);
		return values;
	}

	private float[] getLeftBottomPoint(Bmp bmp) {
		float[] values = new float[] { 0, bmp.getPic().getHeight() };
		bmp.matrix.mapPoints(values);
		return values;
	}

	private float[] getRightBottomPoint(Bmp bmp) {
		float[] values = new float[] { bmp.getPic().getWidth(),
				bmp.getPic().getHeight() };
		bmp.matrix.mapPoints(values);
		return values;
	}

	protected float getScale(Bmp bmp) {
		float[] values = new float[9];
		bmp.matrix.getValues(values);
		return (float) Math.sqrt(Math.pow(values[0], 2)
				+ Math.pow(values[3], 2));
	}

	protected float[] getCenter(Bmp bmp) {
		float[] values = new float[9];
		bmp.matrix.getValues(values);
		float[] point = new float[] { bmp.getPic().getWidth() / 2,
				bmp.getPic().getHeight() / 2 };
		bmp.matrix.mapPoints(point);
		return point;
	}

	protected float[] getCenter(Bmp... bmps) {
		List<float[]> apexs = new ArrayList<float[]>();

		for (Bmp bmp : bmps) {
			if (bmp != null && bmp.getPic() != null) {
				float[][] rect = getRect(bmp);
				// 将每个顶点放在集合中
				for (float[] f : rect) {
					apexs.add(f);
				}
			}

		}
		// 找出所有点中最外围的点
		float minX = apexs.get(0)[0], minY = apexs.get(0)[1], maxX = 0, maxY = 0;
		for (float[] apex : apexs) {
			minX = minX < apex[0] ? minX : apex[0];
			minY = minY < apex[1] ? minY : apex[1];
			maxX = maxX < apex[0] ? apex[0] : maxX;
			maxY = maxY < apex[1] ? apex[1] : maxY;
		}
		return new float[] { (maxX + minX) / 2, (maxY + minY) / 2 };
	}

	private float[][] getRect(Bmp bmp) {
		if (bmp != null && bmp.getPic() != null) {
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

	protected float[] getMin(Bmp... bmps) {
		List<float[]> apexs = new ArrayList<float[]>();
		for (Bmp bmp : bmps) {
			if (bmp != null && bmp.getPic() != null) {
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

	private boolean isChange(Bmp bmp, float scale) {
		// float[] center=getCenter(bmp);
		float zoom = scale * getScale(bmp);
		if (zoom > 4 || zoom < 0.4) {
			return false;
		}
		return true;
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

	protected void showDelete(Bmp[] bmp) {
		if (flagHandler) {
			if (bmp[6] != null && bmp[6].getPic() != null) {
				float[] left = getLeftPoint(bmp[6]);
				float[] right = getRightPoint(bmp[6]);
				float[] leftBottom = getLeftBottomPoint(bmp[6]);
				float[] rightBottom = getRightBottomPoint(bmp[6]);
				float[] min = left[0] < right[0] ? left : right;
				float[] max = leftBottom[0] >= rightBottom[0] ? leftBottom
						: rightBottom;
				Bitmap reverse = BitmapFactory.decodeResource(getResources(),
						R.drawable.reverse);
				Bitmap delete = BitmapFactory.decodeResource(getResources(),
						R.drawable.delete);
				if (isDelete) {
					prop[0] = new Bmp(reverse, -1, max[0], max[1], true, false,
							false);
					prop[1] = new Bmp(delete, -1, min[0], min[1], true, false,
							false);
				} else {
					prop[0] = new Bmp(reverse, -1, max[0], max[1], true, false,
							false);
					prop[1] = null;
				}
			} else {
				if (tempBmp[0] != null && tempBmp[0].getPic() != null) {
					float[] left = getLeftPoint(tempBmp[0]);
					float[] right = getRightPoint(tempBmp[0]);
					float[] leftBottom = getLeftBottomPoint(tempBmp[0]);
					float[] rightBottom = getRightBottomPoint(tempBmp[0]);
					float[] min = left[0] < right[0] ? left : right;
					float[] max = leftBottom[0] >= rightBottom[0] ? leftBottom
							: rightBottom;
					Bitmap reverse = BitmapFactory.decodeResource(
							getResources(), R.drawable.reverse);
					Bitmap delete = BitmapFactory.decodeResource(
							getResources(), R.drawable.delete);
					if (isDelete) {
						prop[0] = new Bmp(reverse, -1, max[0], max[1], true,
								false, false);
						prop[1] = new Bmp(delete, -1, min[0], min[1], true,
								false, false);
					} else {
						prop[0] = new Bmp(reverse, -1, max[0], max[1], true,
								false, false);
						prop[1] = null;
					}
				}
			}
		} else {
			clearProp();
		}
		invalidate();
	}

	public float getRotate(Bmp bmp) {
		float[] values = new float[9];
		bmp.matrix.getValues(values);
		return (float) (Math.asin(values[3]) / Math.PI * 180);
	}

	public Bitmap getRect(Bmp[] bmps) {
		bmps[6] = null;
		float[] min = getMin(bmps);
		float[] max = getMax(bmps);
		Bitmap rectBitmap = Bitmap.createBitmap((int) (max[0] - min[0]),
				(int) (max[1] - min[1]), ConstValue.MY_CONFIG_8888);
		Canvas canvas = new Canvas(rectBitmap);
		RectF rec = new RectF(0, 0, rectBitmap.getWidth(),
				rectBitmap.getHeight());// 画边框
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);// 设置边框颜色
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5f);// 设置边框宽度
		canvas.drawRoundRect(rec, 10f, 10f, paint);
		return rectBitmap;
	}

	public void clearProp() {
		for (int i = 0; i < 2; i++) {
			if (prop[i] != null) {
				prop[i].getPic().recycle();
				prop[i] = null;
			}
		}
		this.invalidate();
	}

	// 删除道具
	public void delProp() {

		if (deleteType == 0) {// 表示人
			deletePerson();
		} else if (deleteType == 1) {// 表示物体
			if (propBmps[PROP_COUNT - 1] != null) {
				propBmps[PROP_COUNT - 1].getPic().recycle();
				propBmps[PROP_COUNT - 1] = null;
			}
		}
	}

	/**
	 * 删除人
	 */
	private void deletePerson() {
		if (null != items[currentPerson]) {
			for (int i = 0; i < ELEMENTS_COUNT; i++) {
				if (items[currentPerson].person[i] != null) {
					items[currentPerson].person[i].getPic().recycle();
					items[currentPerson].person[i] = null;
				}
			}
			items[currentPerson] = null;
			if(currentPerson==0){
				currentPerson=1;
			}else if(currentPerson==1){
				currentPerson=0;
			}
			cancleAll();
		}
	}

	public void reverseProp() {
		float[] center = getCenter(tempBmp);
		for (int i = 0; i < ELEMENTS_COUNT + 1; i++) {
			if (tempBmp[i] != null) {
				tempBmp[i].matrix.postScale(-1, 1, center[0], center[1]);
			}
		}
		this.invalidate();
	}

	// person基础类
	public class PersonItem {
		public Bmp[] person = new Bmp[ELEMENTS_COUNT];
		public int personId;

		public PersonItem(int personId) {
			this.personId = personId;
		}

		public void initPerson() {
			String path = Environment.getExternalStorageDirectory()
					+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
					+ ConstValue.ImgName.playPhotoClip.toString() + personId
					+ "png";
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			if (!new File(path).exists()) {
				path = Environment.getExternalStorageDirectory()
						+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
						+ ConstValue.ImgName.playPhotoClip.toString() + 0
						+ "png";
			}
			Bitmap lianBitmap = BitmapFactory.decodeFile(path, options);
			Bitmap bodyBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.clothes, options);

			Bitmap hairBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.hair, options);
			float expHeadTop = 3f;
			scale = (float) ((float) BaseImageView.cj_height / (float) 2)
					/ (float) bodyBitmap.getHeight();
			bodyBitmap = Bitmap.createScaledBitmap(bodyBitmap,
					(int) (bodyBitmap.getWidth() * scale),
					(int) (bodyBitmap.getHeight() * scale), false);
			float lianScale = (float) ((float) bodyBitmap.getWidth() * 0.6 / (float) (lianBitmap
					.getWidth()));
			// lianBitmap = Bitmap.createScaledBitmap(lianBitmap,
			// (int) (lianBitmap.getWidth() * lianScale),
			// (int) (lianBitmap.getHeight() * lianScale), true);

			lianBitmap = Bitmap
					.createScaledBitmap(
							lianBitmap,
							(int) (lianBitmap.getWidth() * BaseImageView.FACE_EXP_SIZE),
							(int) (lianBitmap.getHeight() * BaseImageView.FACE_EXP_SIZE),
							true);

			hairBitmap = Bitmap.createScaledBitmap(hairBitmap,
					(int) (hairBitmap.getWidth() * scale),
					(int) (hairBitmap.getHeight() * scale), false);
			int random = new Random().nextInt(300);
			// 确定坐标
			float body_x = cj_width * 71 / 100 - random;
			float body_y = cj_height * 59 / 100;

			float hair_x = body_x - 30;// cj_width / 2 + hairBitmap.getWidth() *
										// 78 / 100;
			float hair_y = (float) (body_y - bodyBitmap.getHeight() * 45 / 100 - hairBitmap
					.getHeight() / expHeadTop) + 30;
			float face_x = hair_x;// hair_x - lianBitmap.getWidth() * 2 / 100;
			float face_y = hair_y + 40;
			// 0代表身体 1代表脸 2眉毛 3腮红 4胡子 5代表头发
			person[0] = new Bmp(bodyBitmap, 0, body_x, body_y, true, false,
					false);
			person[1] = new Bmp(lianBitmap, 1, face_x, face_y, true, false,
					false);
			person[5] = new Bmp(hairBitmap, 5, hair_x, hair_y, true, false,
					false);
		}

		public void changeHair(Bitmap bitmap) {
			if (bitmap != null) {
				if(person[5]!=null){
					person[5].recycleMap();
					person[5].SetBmpInfo(bitmap);
				}else{
					float[] center = getCenter(person[1]);
					float x = center[0];
					float scale = 7f;// 正比例影响相对脸部位置
					float y = center[1] - person[1].getPic().getHeight()
							* getScale(person[1]) / scale;
					person[5] = new Bmp(bitmap, 2, x, y, true, false, false);
				}
			}else{
				person[5].getPic().recycle();
				person[5]=null;
			}
		}

		public void changeBody(Bitmap bitmap) {
			if (bitmap != null) {
				person[0].getPic().recycle();
				person[0].SetBmpInfo(bitmap);
			}
		}

		/**
		 * 设置脸型
		 * 
		 */
		public void setFace() {
			String path = Environment.getExternalStorageDirectory()
					+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
					+ ConstValue.ImgName.playPhotoClip.toString() + personId
					+ "png";
			if (!new File(path).exists()) {
				path = Environment.getExternalStorageDirectory()
						+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
						+ ConstValue.ImgName.playPhotoClip.toString() + 0
						+ "png";
			}
			Bitmap faceBitmap = BitmapFactory.decodeFile(path);
			faceBitmap = Bitmap
					.createScaledBitmap(
							faceBitmap,
							(int) (faceBitmap.getWidth() * BaseImageView.FACE_EXP_SIZE),
							(int) (faceBitmap.getHeight() * BaseImageView.FACE_EXP_SIZE),
							true);
			person[1].setPic(faceBitmap);
		}

		/**
		 * 增加胡子
		 * 
		 */
		public void setBeard(Bitmap mBitmap) {
			Bitmap beardBitmap = mBitmap;
			if (beardBitmap != null) {
				if (null == person[4]) {
					// 放大
					beardBitmap = Bitmap.createScaledBitmap(beardBitmap,
							(int) (beardBitmap.getWidth() * scale),
							(int) (beardBitmap.getHeight() * scale), false);

					float[] center = getCenter(person[1]);
					float x = center[0];
					float scale = 8.5f;// 反比例影响相对脸部位置
					float y = center[1] + person[1].getPic().getHeight()
							* getScale(person[1]) / scale;
					person[4] = new Bmp(mBitmap, 4, x, y, true, false, false);
				} else {
					if (person[4].getPic() != null) {
						person[4].recycleMap();
					}
					person[4].setPic(beardBitmap);
				}

			} else {
				person[4] = null;
			}
		}

		/**
		 * 加载眉毛
		 * 
		 */
		public void setEyebrows(Bitmap mBitmap) {
			Bitmap eyebrowsBitmap = mBitmap;
			if (eyebrowsBitmap != null) {
				if (null == person[2]) {
					// 放大
					eyebrowsBitmap = Bitmap.createScaledBitmap(eyebrowsBitmap,
							(int) (eyebrowsBitmap.getWidth() * scale),
							(int) (eyebrowsBitmap.getHeight() * scale), false);
					float[] center = getCenter(person[1]);
					float x = center[0];
					float scale = 7f;// 正比例影响相对脸部位置
					float y = center[1] - person[1].getPic().getHeight()
							* getScale(person[1]) / scale;
					person[2] = new Bmp(mBitmap, 2, x, y, true, false, false);
				} else {
					if (person[2].getPic() != null) {
						person[2].recycleMap();
					}
					person[2].setPic(eyebrowsBitmap);
				}

			} else {
				person[2] = null;
			}
		}

		/**
		 * 加载腮红
		 * 
		 */
		public void setBlusher(Bitmap mBitmap) {
			Bitmap blusherBitmap = mBitmap;
			if (blusherBitmap != null) {
				if (null == person[3]) {
					// 放大
					blusherBitmap = Bitmap.createScaledBitmap(blusherBitmap,
							(int) (blusherBitmap.getWidth() * scale),
							(int) (blusherBitmap.getHeight() * scale), false);
					float[] center = getCenter(person[1]);
					float x = center[0];
					float scale = 80f;// 反比例影响相对脸部位置
					float y = center[1] + person[1].getPic().getHeight()
							* getScale(person[1]) / scale;
					person[3] = new Bmp(mBitmap, 3, x, y, true, false, false);
				} else {
					if (person[3].getPic() != null) {
						person[3].recycleMap();
					}
					person[3].setPic(blusherBitmap);
				}
			} else {
				person[3] = null;
			}
		}

		public void setPaint(Paint paint) {
			if (paint != null) {
				person[1].setPaint(paint);
			}
		}

	}

}
