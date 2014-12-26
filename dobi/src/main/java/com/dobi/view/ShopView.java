package com.dobi.view;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.dobi.R;
import com.dobi.common.ConstValue;
import com.dobi.logic.ImageManager;
import com.dobi.logic.ImageManager.RectangleManager;
import com.dobi.ui.MainActivity;
import com.dobi.ui.BaseImageView.Bmp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

public class ShopView extends ImageView {
	private PersonItem goods;
	private static final int ELEMENTS_COUNT = 6;
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
	protected float cos = 0f;
	protected float length = 480.0f;
	final static float FACE_EXP_SIZE = 0.18f;// 正比放大脸部，补充透明部分.
	protected final static float SCALE_PRE = 7f;
	/**
	 * 图像显示后的放缩倍数，初始化函数中控制比例
	 */
	protected float scale = 1;
	protected Activity activity;
	protected static int cj_width, cj_height;
	// 默认当前选中的人
	protected Context context;
	private Bmp tempBmp;

	public ShopView(Context context, AttributeSet attrs) {
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
			// Begin = true;
			break;
		case MotionEvent.ACTION_UP:
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
			float x = event.getX();
			float y = event.getY();
			// 单指操作
			if (mode == 0) {
				if (tempBmp != null) {
					translateBmp(x, y, tempBmp);
				}
				startX = x;
				startY = y;
				invalidate();
			}
			// 两指操作
			if (mode == 1) {
				length = spacing(event);
				cos = rotation(event);
				if (tempBmp != null) {
					rotateBmp(tempBmp);
					zoomBmp(tempBmp);
				}
				preCos = cos;
				preLength = length;
				setCenter();
				invalidate();
			}
			break;
		}
		return true;
	}

	private void rotateBmp(Bmp... bmps) {
		for (Bmp bmp : bmps) {
			if (bmp != null) {
				bmp.matrix.postRotate(cos - preCos, centerX, centerY);
			}
		}
	}

	private void setCenter() {
		float[] center = new float[2];
		if (tempBmp != null) {
			center = getCenter(tempBmp);
			centerX = center[0];
			centerY = center[1];
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
				bmp.matrix.postScale(scale, scale, centerX, centerY);
			}
		}
	}

	private boolean isChange(Bmp bmp, float scale) {
		// float[] center=getCenter(bmp);
		float zoom = scale * getScale(bmp);
		if (zoom > 4 || zoom < 0.4) {
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

	private void order(float x, float y) {
		for (int i = ELEMENTS_COUNT - 1; i >= 0; i--) {
			if (goods.person[i] != null) {
				if (isPoint(goods.person[i], x, y)) {
					tempBmp = goods.person[i];
					return;
				}
			}
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

	/**
	 * 获取包含当前所有装扮内容的Bitmap，由子类调用
	 * 
	 * @return
	 */
	public Bitmap getCurrentPic() {
		savePerson();
		// Bitmap resultBitmap = Bitmap.createBitmap(cj_width, cj_height,
		// Config.ARGB_8888);
		// Canvas saveToPhoneCanvas = new Canvas(resultBitmap);
		// // 画人物
		// for (int j = 0; j < ELEMENTS_COUNT; j++) {
		// if (goods.person[j] != null) {
		// saveToPhoneCanvas.drawBitmap(goods.person[j].getPic(),
		// goods.person[j].matrix, goods.person[j].paint);
		// }
		// }
		Bitmap resultBitmap = BitmapFactory
				.decodeFile("/sdcard/dobi/shop/shoppng");
		return resultBitmap;
	}

	/**
	 * 仅仅保存到本地
	 * 
	 * @param index
	 */
	public void savePerson() {
		float[] min = getMin(goods.person);
		float[] max = getMax(goods.person);
		Bitmap person = Bitmap.createBitmap((int) (max[0] - min[0]),
				(int) (max[1] - min[1]), ConstValue.MY_CONFIG_8888);
		Canvas personCanvas = new Canvas(person);
		personCanvas.translate(-min[0], -min[1]);
		// 画人物
		for (int j = 0; j < ELEMENTS_COUNT; j++) {
			if (goods.person[j] != null) {
				personCanvas.drawBitmap(goods.person[j].getPic(),
						goods.person[j].matrix, goods.person[j].paint);
			}
		}
		FileOutputStream out;
		try {
			out = mImageManager.creatFile("shop", "png", "shop");
			person.compress(CompressFormat.PNG, 100, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	@Override
	protected void onDraw(Canvas canvas) {
		// 画人物
		try {
			if (goods != null) {
				for (int j = 0; j < ELEMENTS_COUNT; j++) {
					if (goods.person[j] != null) {
						canvas.drawBitmap(goods.person[j].getPic(),
								goods.person[j].matrix, goods.person[j].paint);
					}
				}
			}
		} catch (Exception e) {
			Log.i("jiang", "trying to use a recycled bitmap");
		}
	}

	public void initView(Activity activity, int cj_width, int cj_height) {
		if (cj_width != 0 && cj_height != 0) {
			ShopView.cj_width = cj_width;
			ShopView.cj_height = cj_height;
		}
		goods = new PersonItem(0);
		goods.initPerson();
	}

	// person基础类
	public class PersonItem {
		public Bmp[] person = new Bmp[ELEMENTS_COUNT];
		protected int personId;

		public PersonItem(int personId) {
			this.personId = personId;
		}

		// getPerson
		public void initPerson() {
			String path = Environment.getExternalStorageDirectory()
					+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
					+ ConstValue.ImgName.playPhotoClip.toString() + 0 + "png";
			Bitmap lianBitmap = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			if (!new File(path).exists()) {
				lianBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.default_avator, options);
				lianBitmap = Bitmap.createScaledBitmap(lianBitmap,
						(int) (lianBitmap.getWidth() * 0.25),
						(int) (lianBitmap.getHeight() * 0.25), true);
			} else {
				lianBitmap = BitmapFactory.decodeFile(path, options);
				lianBitmap = Bitmap
						.createScaledBitmap(
								lianBitmap,
								(int) (lianBitmap.getWidth() * ShopView.FACE_EXP_SIZE),
								(int) (lianBitmap.getHeight() * ShopView.FACE_EXP_SIZE),
								true);
			}
			Bitmap bodyBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.body_399, options);

			Bitmap hairBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.hair_399, options);
			bodyBitmap=getBitmapSize(bodyBitmap,cj_width*3/4);
			hairBitmap=getBitmapSize(hairBitmap,cj_width*2/5);
			// 确定坐标
			float body_x = cj_width /2;
			float body_y = cj_height * 3 / 5;
			float face_x = body_x;// hair_x - lianBitmap.getWidth() * 2 / 100;
			float face_y = body_y-(bodyBitmap.getHeight()/2)-60 + 40;
			float hair_x = body_x;// cj_width / 2 + hairBitmap.getWidth() *
			float hair_y = face_y-(lianBitmap.getHeight()/2)-60;
			// 0代表身体 1代表脸 2眉毛 3腮红 4胡子 5代表头发

			person[0] = new Bmp(bodyBitmap, 0, body_x, body_y, true, false,
					false);
			person[1] = new Bmp(lianBitmap, 1, face_x, face_y, true, false,
					false);
			person[5] = new Bmp(hairBitmap, 5, hair_x, hair_y, true, false,
					false);
		}

		public void changeHair(Bitmap bitmap, boolean isLarge) {
			if (bitmap != null) {
				bitmap=getBitmapSize(bitmap,cj_width*2/5);
				person[5].recycleMap();
				person[5].SetBmpInfo(bitmap);
				System.gc();
			}
		}

		private Bitmap getBitmapSize(Bitmap bitmap, int size) {
			int width=bitmap.getWidth();
			float scale=(float)width/(float)size;
			int height=bitmap.getHeight();
			bitmap=Bitmap.createScaledBitmap(bitmap, size, (int) (height/scale), false);
			return bitmap;
		}

		public void changeBody(Bitmap bitmap, boolean isLarge) {
			if (bitmap != null) {
				bitmap=getBitmapSize(bitmap,cj_width*3/4);
				person[0].recycleMap();
				person[0].SetBmpInfo(bitmap);
				System.gc();
			}
		}

		/**
		 * 设置脸型
		 * 
		 */
		public void setFace() {
			String path = Environment.getExternalStorageDirectory()
					+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
					+ ConstValue.ImgName.playPhotoClip.toString() + 0 + "png";
			if (new File(path).exists()) {
				Bitmap faceBitmap = BitmapFactory.decodeFile(path);
				faceBitmap = Bitmap
						.createScaledBitmap(
								faceBitmap,
								(int) (faceBitmap.getWidth() * ShopView.FACE_EXP_SIZE),
								(int) (faceBitmap.getHeight() * ShopView.FACE_EXP_SIZE),
								true);
				person[1].setPic(faceBitmap);
			}

		}

		public void setPaint(Paint paint) {
			if (paint != null) {
				person[1].setPaint(paint);
			}
		}

		/**
		 * 仅仅保存到本地
		 * 
		 * @param index
		 */
		public void savePerson() {
			float[] min = getMin(person[0], person[1], person[5]);
			float[] max = getMax(person[0], person[1], person[5]);
			Bitmap personOne = Bitmap.createBitmap((int) (max[0] - min[0]),
					(int) (max[1] - min[1]), ConstValue.MY_CONFIG_8888);
			Canvas personCanvas = new Canvas(personOne);
			personCanvas.translate(-min[0], -min[1]);
			personCanvas.drawBitmap(person[0].getPic(), person[0].matrix,
					person[0].paint);
			personCanvas.drawBitmap(person[1].getPic(), person[1].matrix,
					person[1].paint);
			personCanvas.drawBitmap(person[5].getPic(), person[5].matrix,
					person[5].paint);
			FileOutputStream out;
			try {
				out = mImageManager.creatFile("person", ".jpg", "play");
				personOne.compress(CompressFormat.JPEG, 100, out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

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

	public void changeHair(Bitmap bitmap, boolean isLarge) {
		if (goods != null) {
			goods.changeHair(bitmap, isLarge);
			invalidate();
		}
	}

	public void changeClothes(Bitmap bitmap, boolean isLarge) {
		if (goods != null) {
			goods.changeBody(bitmap, isLarge);
			invalidate();
		}
	}

	public void setFace() {
		if (goods != null) {
			goods.setFace();
			invalidate();
		}
	}

	public File getSavePerson() {
		goods.savePerson();
		return new File( Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH+"person.jpg");
	}

}
