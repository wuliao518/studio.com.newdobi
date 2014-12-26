package com.dobi.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dobi.R;
import com.dobi.adapter.MoreAdapter;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.db.DBManager;
import com.dobi.item.MapItem;
import com.dobi.logic.ImageManager;
import com.dobi.logic.LogicMore;
import com.dobi.view.MoreImageView;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MoreActivity extends BaseActivity implements OnClickListener,OnSeekBarChangeListener{
	public static int current = 0;
	public final static int PROP = 0;
	public final static int SCENE = 1;
	private boolean hasMeasured = false;
	private LinearLayout linear;// 照相机弹出框
	private ImageView btnCamera;// 弹出框里的相机按钮
	private ImageView btnPhoto;// 弹出框里的相册按钮
	private LogicMore mLogicMore;
	private ImageButton btnScene, btnProp;
	private ListView mListView;
	private LinearLayout mSediao;
	// 存放图片路径
	private List<String> mapList = new ArrayList<String>();
	private LinearLayout unitScene;// 化妆，装扮
	// 自定义气泡图形
	private String path;
	/**
	 * 场景按钮点击标志true：向上滚动
	 */
	private boolean flagScene = true;
	/**
	 * 是否刷新脸部
	 */
	boolean isFlash = false;
	/**
	 * 道具按钮点击标志true：向上滚动
	 */
	private boolean flagProp = true, flagPet = true, flagText = true;
	/**
	 * 装扮控件尺寸
	 */
	private int stageWidth = 0;
	private int stageHeight = 0;
	/**
	 * 标记第几个头像
	 */
	private static int index = 0;
	//
	private Dialog sceneDialog;
	/**
	 * 保存处于选择哪种道具状态，1：道具，2：宠物，3：气泡文字
	 */
	private int PropStage;
	private List<Bitmap> mData;
	private List<MapItem> sceneData;
	private ImageButton btnCZ, btnLB, btnMD, btnTX, btnZH, btnZZ;
	private int type;
	private Handler handler;
	/**
	 * 场景图片所在地址
	 */
	public String scenePath;
	private DBManager manager;
	// 存放网络上的图片路径
	Map<String, String> mapNetList;
	private Point mPoint = null;
	private FrameLayout mLFrameLayout;
	private MoreImageView image;
	private ImageManager mImageManager;
	private LinearLayout left;
	private SeekBar SaturationseekBar = null;
	private SeekBar BrightnessseekBar = null;
	private SeekBar ContrastseekBar = null;
	private ColorMatrix mBrightnessMatrix;
	private ColorMatrix mSaturationMatrix;
	private ColorMatrix mContrastMatrix;
	private ColorMatrix mAllMatrix;
	private ImageButton sediao;
	private MoreAdapter moreAdapter=null;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		mImageManager = new ImageManager();
		mLogicMore = new LogicMore();
		manager = new DBManager(this);
		mPoint = new Point();
		mapNetList = manager.getInfo();
		initView();
		sp=CommonMethod.getPreferences(getApplicationContext());
		if(sp.getBoolean("isCenter",true)){
			final Dialog dialog=new Dialog(this, R.style.Translucent_NoTitle);
			//提示对话款
			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
			View view = inflater.inflate(R.layout.moredialog, null);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        dialog.setContentView(view);
	        dialog.getWindow().setLayout(CommonMethod.getWidth(getApplicationContext()),CommonMethod.getHeight(getApplicationContext()) );
		    RelativeLayout ivi = (RelativeLayout)view.findViewById(R.id.dialog_more);  
		    ivi.setOnClickListener(new OnClickListener() {
			    @Override  
			    public void onClick(View v) {
			        dialog.dismiss();
			    }
		    });
			
			 dialog.setCanceledOnTouchOutside(true);
			 dialog.show();
			 Editor edit=sp.edit();
			 edit.putBoolean("isCenter", false);
			 edit.commit();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

	private void loadData() {
		if (mapList != null) {
			mapList.clear();
		}
		mPoint.x = 100;
		mPoint.y = 100;
		switch (current) {
		case SCENE:
			switch (type) {
			case 0:
				path = "/moreScene/cloud";
				break;
			case 1:
				path = "/moreScene/friend";
				break;
			case 2:
				path = "/moreScene/happy";
				break;
			case 3:
				path = "/moreScene/opera";
				break;
			case 4:
				path = "/moreScene/three";
				break;
			case 5:
				path = "/moreScene/world";
				break;
			default:
				break;
			}
			setSceneMapList(path);
			if(moreAdapter==null){
				moreAdapter=new MoreAdapter(MoreActivity.this, mapList,
						mPoint, mListView, image, type, path,handler);
				mListView.setAdapter(moreAdapter);
			}else{
				moreAdapter.cancelTask();
				moreAdapter.notifyDataSetChanged();
			}
			
			break;
		case PROP:
			mPoint.x = 120;
			mPoint.y = 120;
			path = "/prop/bubble";
			setPropMapList(path, ".png");
			if(moreAdapter!=null){
				moreAdapter.cancelTask();
				moreAdapter=null;
			}
			moreAdapter=new MoreAdapter(MoreActivity.this, mapList,
					mPoint, mListView, image, type, path,handler);
			mListView.setAdapter(moreAdapter);
//			if(moreAdapter==null){
//				moreAdapter=new MoreAdapter(MoreActivity.this, mapList,
//						mPoint, mListView, image, type, path,handler);
//				mListView.setAdapter(moreAdapter);
//			}else{
//				moreAdapter.notifyDataSetChanged();
//			}
			break;
		default:
			break;
		}
	}

	private void setPropMapList(String path, String postfix) {
		if (mapNetList.get(path) != null) {
			int length = Integer.parseInt(mapNetList.get(path));
			for (int i = 0; i < length; i++) {
				mapList.add(path + "/" + (i + 1) + postfix);
			}
			Collections.reverse(mapList);
		}
	}

	private void setSceneMapList(String path) {
		if (mapNetList.get(path) != null) {
			int length = Integer.parseInt(mapNetList.get(path));
			for (int i = 0; i < length; i++) {
				mapList.add(path + "/" + (i + 1) + "/0.jpg");
			}
			Collections.reverse(mapList);
		}
	}

	private void initView() {
		// 色调和滑动块
		mSediao = (LinearLayout) findViewById(R.id.ll_sediao);
		sediao = (ImageButton) findViewById(R.id.sediao);
		SaturationseekBar = (SeekBar) findViewById(R.id.Saturationseekbar);
		BrightnessseekBar = (SeekBar) findViewById(R.id.Brightnessseekbar);
		ContrastseekBar = (SeekBar) findViewById(R.id.Contrastseekbar);
		SaturationseekBar.setOnSeekBarChangeListener(this);
		BrightnessseekBar.setOnSeekBarChangeListener(this);
		ContrastseekBar.setOnSeekBarChangeListener(this);

		// 场景init
		btnCZ = (ImageButton) findViewById(R.id.btnCZ);
		btnLB = (ImageButton) findViewById(R.id.btnLB);
		btnMD = (ImageButton) findViewById(R.id.btnMD);
		btnTX = (ImageButton) findViewById(R.id.btnTX);
		btnZH = (ImageButton) findViewById(R.id.btnZH);
		btnZZ = (ImageButton) findViewById(R.id.btnZZ);
		btnCZ.setOnClickListener(this);
		btnLB.setOnClickListener(this);
		btnMD.setOnClickListener(this);
		btnTX.setOnClickListener(this);
		btnZH.setOnClickListener(this);
		btnZZ.setOnClickListener(this);
		btnProp = (ImageButton) this.findViewById(R.id.btnPropMore);
		btnScene = (ImageButton) this.findViewById(R.id.btnScene);
		btnProp.setOnClickListener(this);
		btnScene.setOnClickListener(this);
		left = (LinearLayout) findViewById(R.id.left);
		// 对listview进行监听处理
		mListView = (ListView) findViewById(R.id.propListView);
		// mSceneListView=(ListView) findViewById(R.id.sceneListView);
		unitScene = (LinearLayout) this.findViewById(R.id.moreScene);
		// unitProp = (LinearLayout) this.findViewById(R.id.prop);
		// 相机弹出框
		linear = (LinearLayout) this.findViewById(R.id.cameraWidget);
		btnCamera = (ImageView) linear.findViewById(R.id.cameraButton);
		btnPhoto = (ImageView) linear.findViewById(R.id.photoButton);
		btnCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				linear.setVisibility(View.GONE);
				index = (Integer) linear.getTag();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri mOutPutFileUri;
				// 文件夹doubi/moerClipFace
				String path = Environment.getExternalStorageDirectory()
						.toString() + "/dobi/face";
				File path1 = new File(path);
				if (!path1.exists()) {
					path1.mkdirs();
				}
				File file = new File(path1, "photo" + "jpg");
				mOutPutFileUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
				startActivityForResult(intent, 0);
			}
		});
		btnPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				linear.setVisibility(View.GONE);
				index = (Integer) linear.getTag();
				Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(intent, 1);
			}
		});

		// 主要画面
		mLFrameLayout = (FrameLayout) findViewById(R.id.drawViewFrameLayout);
		image = new MoreImageView(MoreActivity.this, null);
		ViewTreeObserver vto = mLFrameLayout.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					// 启动化妆画面
					image.initView(MoreActivity.this);
					mLFrameLayout.addView(image);
					hasMeasured = true;
					mLogicMore.creatBtnToFace(image, MoreActivity.this, linear);
				}
				return true;
			}
		});
		image.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					linear.setVisibility(View.GONE);
					mSediao.setVisibility(View.GONE);
					break;
				}
				return false;
			}
		});
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					sceneDialog=CommonMethod.showMyDialog(MoreActivity.this);
					sceneDialog.show();
					break;
				case 1:
					if(sceneDialog!=null&&sceneDialog.isShowing()){
						sceneDialog.dismiss();
					}
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnScene:
			current = SCENE;
			type = 0;
			// 设置场景点击后的图片转换
			ImgBtncolor();
			setImageResource();
			btnScene.setImageResource(R.drawable.changjing_bk);
			btnProp.setImageResource(R.drawable.qipao);
			if (flagScene) {
				MoreImageView.CurrentStage = ConstValue.Stage.Scene;
				btnCZ.setBackgroundColor(Color.WHITE);
				btnCZ.setImageResource(R.drawable.chizha_bk);
				unitScene.setVisibility(View.VISIBLE);
				left.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				sediao.setVisibility(View.VISIBLE);
				flagScene = false;
				flagProp = true;
				loadData();
				showView(left);
			} else {
				flagScene = true;
				flagProp = true;
				left.setVisibility(View.GONE);
				hideView(left);
			}
			break;
		case R.id.btnPropMore:
			current = PROP;
			type = 0;
			// 设置场景点击后的图片转换
			btnProp.setImageResource(R.drawable.qipao_bk);
			btnScene.setImageResource(R.drawable.changjing);
			if (flagProp) {
				MoreImageView.CurrentStage = ConstValue.Stage.Prop;
				PropStage = 1;
				left.setVisibility(View.VISIBLE);
				sediao.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				unitScene.setVisibility(View.GONE);
				flagProp = false;
				flagScene = true;
				loadData();
				showView(left);
			} else {
				flagProp = true;
				flagScene = true;
				left.setVisibility(View.GONE);
				hideView(left);
			}

			break;
		case R.id.btnCZ:
			type = 0;
			// 设置图片的点击背景
			ImgBtncolor();
			btnCZ.setBackgroundColor(Color.WHITE);
			// 设置图片的颜色
			setImageResource();
			btnCZ.setImageResource(R.drawable.chizha_bk);
			current = SCENE;
			loadData();
			break;
		case R.id.btnLB:
			type = 1;
			// 设置图片的点击背景
			ImgBtncolor();
			btnLB.setBackgroundColor(Color.WHITE);
			// 设置图片的颜色
			setImageResource();
			btnLB.setImageResource(R.drawable.lebu_bk);
			current = SCENE;
			loadData();
			break;
		case R.id.btnMD:
			type = 2;
			// 设置图片的点击背景
			ImgBtncolor();
			btnMD.setBackgroundColor(Color.WHITE);
			// 设置图片的颜色
			setImageResource();
			btnMD.setImageResource(R.drawable.modeng_bk);
			current = SCENE;
			loadData();
			break;
		case R.id.btnTX:
			type = 3;
			// 设置图片的点击背景
			ImgBtncolor();
			btnTX.setBackgroundColor(Color.WHITE);
			// 设置图片的颜色
			setImageResource();
			btnTX.setImageResource(R.drawable.tianxia_bk);
			current = SCENE;
			loadData();
			break;
		case R.id.btnZH:
			type = 4;
			// 设置图片的点击背景
			ImgBtncolor();
			btnZH.setBackgroundColor(Color.WHITE);
			// 设置图片的颜色
			setImageResource();
			btnZH.setImageResource(R.drawable.zongheng_bk);
			current = SCENE;
			loadData();
			break;
		case R.id.btnZZ:
			type = 5;
			// 设置图片的点击背景
			ImgBtncolor();
			btnZZ.setBackgroundColor(Color.WHITE);
			// 设置图片的颜色
			setImageResource();
			btnZZ.setImageResource(R.drawable.zuizhong_bk);
			current = SCENE;
			loadData();
			break;
		default:
			break;
		}

	}

	/**
	 * 设置动画效果向下
	 * 
	 * @param view
	 */
	private void showView(View view) {
		Animation animate = AnimationUtils.loadAnimation(this,
				R.anim.translate_left);
		view.startAnimation(animate);
	}

	/**
	 * 设置动画效果向上
	 * 
	 * @param view
	 */
	private void hideView(View view) {
		Animation animate = AnimationUtils.loadAnimation(this,
				R.anim.translate_right);
		sediao.setVisibility(View.GONE);
		animate.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				unitScene.setVisibility(View.GONE);
				mListView.setVisibility(View.GONE);
			}
		});
		view.setAnimation(animate);
	}

	// 设置点击按钮的初始颜色
	public void ImgBtncolor() {
		btnCZ.setBackgroundColor(Color
				.parseColor(getString(R.color.button_sixty)));
		btnLB.setBackgroundColor(Color
				.parseColor(getString(R.color.button_sixty)));
		btnMD.setBackgroundColor(Color
				.parseColor(getString(R.color.button_sixty)));
		btnTX.setBackgroundColor(Color
				.parseColor(getString(R.color.button_sixty)));
		btnZH.setBackgroundColor(Color
				.parseColor(getString(R.color.button_sixty)));
		btnZZ.setBackgroundColor(Color
				.parseColor(getString(R.color.button_sixty)));
	}

	// 设置图片的原来颜色
	public void setImageResource() {
		btnCZ.setImageResource(R.drawable.chizha);
		btnLB.setImageResource(R.drawable.lebu);
		btnMD.setImageResource(R.drawable.modeng);
		btnTX.setImageResource(R.drawable.tianxia);
		btnZH.setImageResource(R.drawable.zongheng);
		btnZZ.setImageResource(R.drawable.zuizhong);

	}

	@Override
	protected void onRestart() {
		if (isFlash) {
			image.flashFace(this, index);
		}
		super.onRestart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {

		} else {
			isFlash = true;
			switch (requestCode) {
			case 0:
				Intent intent1 = new Intent(MoreActivity.this, ShowMoreActivity.class);
				intent1.putExtra("type", "more");
				intent1.putExtra("index", index);
				MoreActivity.this.startActivity(intent1);
				break;
			case 1:
				new Thread() {
					@Override
					public void run() {
						Uri uri = data.getData();
						ContentResolver cr = MoreActivity.this
								.getContentResolver();
						try {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inJustDecodeBounds = true;
							Bitmap bitmap = BitmapFactory.decodeStream(
									cr.openInputStream(uri), null, options);
							int scale = 1;
							float bitWidth = options.outWidth;
							float bitHeight = options.outHeight;
							WindowManager wm = (WindowManager) getSystemService("window");
							Display display = wm.getDefaultDisplay();
							float width = display.getWidth() * 1f;
							float height = display.getHeight() * 1f;
							float scaleX = (float) bitWidth / width;
							float scaleY = (float) bitHeight / height;
							scale = (int) Math.max(scaleX, scaleY);
							if (scale > 1) {
								options.inJustDecodeBounds = false;
								options.inSampleSize = scale;
								bitmap = BitmapFactory.decodeStream(
										cr.openInputStream(uri), null, options);
							} else {
								options.inJustDecodeBounds = false;
								options.inSampleSize = 1;
								bitmap = BitmapFactory.decodeStream(
										cr.openInputStream(uri), null, options);
							}

							ImageManager mImageManager = new ImageManager();
							try {
								FileOutputStream out = mImageManager.creatFile(
										"photo", "jpg", "face");
								bitmap.compress(CompressFormat.JPEG, 100, out);
								out.close();
							} catch (IOException e) {
								bitmap.recycle();
								e.printStackTrace();
							}

							Intent intent = new Intent(MoreActivity.this,
									ShowMoreActivity.class);
							intent.putExtra("type", "more");
							intent.putExtra("index", index);
							MoreActivity.this.startActivity(intent);
						} catch (FileNotFoundException e) {

						}

					}
				}.start();
				break;
			default:
				isFlash = false;
				/** 使用SSO授权必须添加如下代码 */
				UMSocialService mController = UMServiceFactory
						.getUMSocialService("com.umeng.share");
				UMSsoHandler ssoHandler1 = mController.getConfig()
						.getSsoHandler(requestCode);
				if (ssoHandler1 != null) {
					ssoHandler1
							.authorizeCallBack(requestCode, resultCode, data);
				}
				break;
			}

		}
	}

	/**
	 * 分享
	 * 
	 * @param view
	 * @throws IOException
	 */
	public void btnShareOnclick(View view) throws IOException {
		Bitmap tempBitmap = image.getCurrentPic();
		FileOutputStream out = mImageManager.creatFile("moreShare", "jpg",
				"face");
		tempBitmap.compress(CompressFormat.JPEG, 100, out);
		out.close();
		tempBitmap.recycle();
		System.gc();
		String strsImage = Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH + ConstValue.FACE_PATH + "moreShare"
				+ "jpg";
//		Bitmap mark = BitmapFactory.decodeResource(this.getResources(),
//				R.drawable.logo11);
//		Bitmap photo = BitmapFactory.decodeFile(strsImage);
//		// 生成新的Bitmap对象
//		Bitmap photoMark = Bitmap.createBitmap(photo.getWidth(),
//				photo.getHeight(), Config.ARGB_8888);
//		Canvas canvas = new Canvas(photoMark);
//		canvas.drawBitmap(photo, 0, 0, null);
//		canvas.drawBitmap(mark, photo.getWidth() - mark.getWidth() - 25,
//				photo.getHeight() - mark.getHeight(), null);
//		canvas.save(Canvas.ALL_SAVE_FLAG);
//		canvas.restore();
//		mImageManager.saveToSDCard(photoMark, ConstValue.ImgName.resultImg);
//		FileOutputStream out1 = mImageManager.creatFile("moreShare", "jpg",
//				"face");
//		photoMark.compress(CompressFormat.JPEG, 100, out1);
//		out1.close();
//		photo.recycle();
//		System.gc();
		CommonMethod.showShare(this, strsImage);
	}

	/**
	 * 保存
	 * 
	 * @param view
	 */
	public void btnSaveOnclick(View view) {
		String msg = MoreActivity.this.getResources().getString(
				R.string.save_album);
		try {
			Bitmap bitmap = image.getCurrentPic();
			mImageManager.saveToAlbum(MoreActivity.this, bitmap);
			if (bitmap != null) {
				bitmap.recycle();
			}
			Toast.makeText(getApplicationContext(), msg, 0).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "请检测SD卡！", 0).show();
		}
	}

	/**
	 * 拍照
	 * 
	 * @param view
	 */
	public void btnPhotoOnclick(View view) {
		isFlash = true;
		index = 0;
		Dialog dialog = CommonMethod.getFacePhotoDialog(this);
		dialog.show();
	}

	/**
	 * 返回主页
	 * 
	 * @param view
	 */
	public void btnMainOnclick(View view) {
		Intent intent = new Intent(MoreActivity.this, HomeActivity.class);
		startActivity(intent);
		image.clearBuffer();
		finish();
	}

	/**
	 * 色调
	 */
	// 色调
	public void btnSediao(View v) {
		if (mSediao.getVisibility() != View.VISIBLE) {
			mSediao.setVisibility(View.VISIBLE);
		} else {
			mSediao.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// // 创建一个相同尺寸的可变的位图区,用于绘制调色后的图片
		Canvas canvas = new Canvas(); // 得到画笔对象
		Paint paint = new Paint(); // 新建paint
		paint.setAntiAlias(true); // 设置抗锯齿,也即是边缘做平滑处理
		if (null == mAllMatrix) {
			mAllMatrix = new ColorMatrix();
		}

		if (null == mBrightnessMatrix) {
			mBrightnessMatrix = new ColorMatrix(); // 用于颜色变换的矩阵，android位图颜色变化处理主要是靠该对象完成
		}
		if (null == mSaturationMatrix) {
			mSaturationMatrix = new ColorMatrix();
		}

		if (null == mContrastMatrix) {
			mContrastMatrix = new ColorMatrix();
		}

		switch (seekBar.getId()) {
		case R.id.Saturationseekbar: // 需要改变饱和度
			
			// saturation 饱和度值，最小可设为0，此时对应的是灰度图(也就是俗话的“黑白图”)，
						// 为1表示饱和度不变，设置大于1，就显示过饱和
						// mBrightnessMatrix.reset();
			int brightness = progress - 127;
			mBrightnessMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,
					0, 0, brightness,// 改变亮度
					0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
			break;
		case R.id.Brightnessseekbar: // 需要改变亮度
			// hueColor就是色轮旋转的角度,正值表示顺时针旋转，负值表示逆时针旋转
			// mContrastMatrix.reset(); // 设为默认值
			float contrast = (float) ((progress + 64) / 128.0);
			mBrightnessMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0,
					contrast, 0, 0, 0,// 改变对比度
					0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });
			break;
		case R.id.Contrastseekbar: // 对比度
			// 设置饱和度
			mSaturationMatrix.setSaturation((float) (progress / 100.0));
			break;
		}
		mAllMatrix.reset();
		mAllMatrix.postConcat(mContrastMatrix);
		mAllMatrix.postConcat(mSaturationMatrix); // 效果叠加
		mAllMatrix.postConcat(mBrightnessMatrix); // 效果叠加
		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));// 设置颜色变换效果
		image.setPaint(paint);
		image.invalidate();
	}

	@Override
	protected void onDestroy() {
		image.recycleBitmap();
		super.onDestroy();
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}

}
