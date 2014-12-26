package com.dobi.ui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.adapter.SingleAdapter;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.db.DBManager;
import com.dobi.logic.ImageManager;
import com.liu.hz.view.HorizontalListView;
import com.tencent.weibo.sdk.android.api.util.SharePersistent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

public class MainActivity extends BaseActivity implements OnClickListener,
		OnSeekBarChangeListener {
	// 0代表脸 1代表眉毛 2代表胡子 3代表腮红
	// 4代表头饰 5代表身体 6代表全部 7代表道具
	public static int current = 1;
	// 是否刷新脸型
	boolean isFlash = false;
	private HorizontalListView mListView;
	private int currentHZ = 3;
	private int currentZB = 1;
	private int currentCJ = 1;
	private int currentDJ = 1;
	private List<String> mapList = new ArrayList<String>();
	private Point mPoint = null;
	private SingleAdapter adapter;
	private LinearLayout mLinearLayout;
	private ImageButton btnHZ;
	private ImageButton btnZB;
	private ImageButton btnScene;
	private ImageButton btnProp;
	private ImageManager mImageManager;
	// 显示标记
	private boolean hzFlag = true;
	private boolean zbFlag = true;
	private boolean cjFlag = true;
	private boolean djFlag = true;
	private ColorMatrix mBrightnessMatrix;
	private ColorMatrix mSaturationMatrix;
	private ColorMatrix mContrastMatrix;
	private ColorMatrix mAllMatrix;

	private SeekBar SaturationseekBar = null;
	private SeekBar BrightnessseekBar = null;
	private SeekBar ContrastseekBar = null;

	// 定位 在该项中是第几个
	private int type = 0;
	// 化妆
	// private ImageButton btnFace,btnColor；
	private ImageButton btnBrow, btnMoustache, btnBlusher;
	// 装扮
	private ImageButton btnModernFemale, btnModernMale, btnRepublicFemale,
			btnRepublicMale, btnAncientFemale, btnAncientMale, btnRX, btnXR,
			btnRP;
	// 场景
	private ImageButton btnGM, btnYY, btnYD, btnPoker;
	// 道具
	private ImageButton btnQP, btnCW, btnDJ;
	// LinearLayout
	private LinearLayout mSediao;
	private HorizontalScrollView mLinearLayout1, mLinearLayout2,
			mLinearLayout3, mLinearLayout4;
	private OneImage image;
	private ImageButton sediao;
	private boolean hasMeasured = false;// 确保只执行一次
	private RelativeLayout mRelativeLayout;
	private Dialog dialog;
	/**
	 * 装扮控件尺寸
	 */
	private int stageWidth = 0;
	private int stageHeight = 0;
	private FrameLayout mLFrameLayout;
	private DBManager manager;
	// 存放网络上的图片路径
	Map<String, String> mapNetList;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		manager = new DBManager(getApplicationContext());
		mImageManager = new ImageManager();
		SingleDrawViewBase.CurrentStage = ConstValue.Stage.Face;
		mPoint = new Point();
		mapNetList = manager.getInfo();
		sp=CommonMethod.getPreferences(getApplicationContext());
		if (sp.getBoolean("isTip", true)) {
			getCenterTip().show();
			Editor edit = sp.edit();
			edit.putBoolean("isTip", false);
			edit.commit();
		}
		
		if (mapNetList != null) {
			type = 3;
			loadData();
			initView();
		}
	}

	private void loadData() {
		if (mapList != null) {
			mapList.clear();
		}
		// 设置显示图片的大小
		mPoint.x = 120;
		mPoint.y = 120;
		String path = "";
		if (SingleDrawViewBase.CurrentStage.equals(ConstValue.Stage.Face)) {
			switch (type) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				path = "/eyebrows";
				setMapList(path, ".png");
				break;
			case 4:
				path = "/beard";
				setMapList(path, ".png");
				break;
			case 5:
				path = "/blusher";
				setMapList(path, ".png");
				break;
			case 6:
				path = "/hair/modern_women";
				setMapList(path, ".png");
				break;
			case 7:
				path = "/hair/modern_men";
				setMapList(path, ".png");
				break;
			case 8:
				path = "/hair/republic_women";
				setMapList(path, ".png");
				break;
			case 9:
				path = "/hair/republic_men";
				setMapList(path, ".png");
				break;
			case 10:
				path = "/hair/ancient_women";
				setMapList(path, ".png");
				break;
			case 11:
				path = "/hair/ancient_men";
				setMapList(path, ".png");
				break;
			default:
				break;
			}

		} else if (SingleDrawViewBase.CurrentStage
				.equals(ConstValue.Stage.Hair)) {
			switch (type) {
			case 0:
				path = "/clothes/youngth";
				setMapList(path, ".png");
				break;
			case 1:
				path = "/clothes/youngth";
				setMapList(path, ".png");
				break;
			case 2:
				path = "/clothes/opera";
				setMapList(path, ".png");
				break;
			case 3:
				path = "/clothes/republic";
				setMapList(path, ".png");
				break;
			default:
				break;
			}
		} else if (SingleDrawViewBase.CurrentStage
				.equals(ConstValue.Stage.Scene)) {
			switch (type) {
			case 0:
				path = "/scene/poker";
				setMapList(path, ".jpg");
				break;
			case 1:
				path = "/scene/poker";
				setMapList(path, ".jpg");
				break;
			case 2:
				path = "/scene/wooden";
				setMapList(path, ".jpg");
				break;
			case 3:
				path = "/scene/trave";
				setMapList(path, ".jpg");
				break;
			case 4:
				path = "/scene/move";
				setMapList(path, ".jpg");
				break;
			default:
				break;
			}

		} else if (SingleDrawViewBase.CurrentStage
				.equals(ConstValue.Stage.Prop)) {
			switch (type) {
			case 0:
				// 单独设置气泡文字的大小
				mPoint.x = 200;
				mPoint.y = 200;
				path = "/prop/bubble";
				setMapList(path, ".png");
				break;
			case 1:
				// 单独设置气泡文字的大小
				mPoint.x = 200;
				mPoint.y = 200;
				path = "/prop/bubble";
				setMapList(path, ".png");
				break;
			case 2:
				path = "/prop/pet";
				setMapList(path, ".png");
				break;
			case 3:
				path = "/prop/props";
				setMapList(path, ".png");
				break;
			default:
				break;
			}
		}

	}

	private void setMapList(String path, String postfix) {
		if (mapNetList.get(path) != null) {
			int length = Integer.parseInt(mapNetList.get(path));
			for (int i = 0; i < length; i++) {
				mapList.add(ConstValue.urlPrefix + path + "/" + (i + 1)
						+ postfix);
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
		mRelativeLayout = (RelativeLayout) findViewById(R.id.btnRelative);

		// FrameLayout赋值
		mLFrameLayout = (FrameLayout) findViewById(R.id.center);
		// LinearLayout赋值
		mLinearLayout = (LinearLayout) findViewById(R.id.bottom);
		mLinearLayout1 = (HorizontalScrollView) findViewById(R.id.btnRelative0);
		mLinearLayout1.setVisibility(View.VISIBLE);
		mLinearLayout2 = (HorizontalScrollView) findViewById(R.id.singleDressing);
		mLinearLayout3 = (HorizontalScrollView) findViewById(R.id.singleScene);
		mLinearLayout4 = (HorizontalScrollView) findViewById(R.id.prop);
		// 化妆、装扮、场景、道具初始化
		btnHZ = (ImageButton) findViewById(R.id.btnHZ);
		btnZB = (ImageButton) findViewById(R.id.btnPropMore);
		btnScene = (ImageButton) findViewById(R.id.btnScene);
		btnProp = (ImageButton) findViewById(R.id.btnProp);
		btnHZ.setOnClickListener(this);
		btnZB.setOnClickListener(this);
		btnScene.setOnClickListener(this);
		btnProp.setOnClickListener(this);
		btnBrow = (ImageButton) findViewById(R.id.btnBrow);
		btnMoustache = (ImageButton) findViewById(R.id.btnMoustache);
		btnBlusher = (ImageButton) findViewById(R.id.btnBlusher);
		// 化妆里按钮
		btnBrow.setOnClickListener(this);
		btnMoustache.setOnClickListener(this);
		btnBlusher.setOnClickListener(this);
		// 装扮里按钮
		btnModernFemale = (ImageButton) findViewById(R.id.btnModernFemale);
		btnModernMale = (ImageButton) findViewById(R.id.btnModernMale);
		btnRepublicFemale = (ImageButton) findViewById(R.id.btnRepublicFemale);
		btnRepublicMale = (ImageButton) findViewById(R.id.btnRepublicMale);
		btnAncientFemale = (ImageButton) findViewById(R.id.btnAncientFemale);
		btnAncientMale = (ImageButton) findViewById(R.id.btnAncientMale);
		btnRX = (ImageButton) findViewById(R.id.btnRX);
		btnXR = (ImageButton) findViewById(R.id.btnXR);
		btnRP = (ImageButton) findViewById(R.id.btnRP);
		btnModernFemale.setOnClickListener(this);
		btnModernMale.setOnClickListener(this);
		btnRepublicFemale.setOnClickListener(this);
		btnRepublicMale.setOnClickListener(this);
		btnAncientFemale.setOnClickListener(this);
		btnAncientMale.setOnClickListener(this);
		btnRX.setOnClickListener(this);
		btnXR.setOnClickListener(this);
		btnRP.setOnClickListener(this);
		// 场景里按钮
		btnGM = (ImageButton) findViewById(R.id.btnGM);
		btnYY = (ImageButton) findViewById(R.id.btnYY);
		btnYD = (ImageButton) findViewById(R.id.btnYD);
		btnPoker = (ImageButton) findViewById(R.id.btnPoker);
		btnGM.setOnClickListener(this);
		btnYY.setOnClickListener(this);
		btnYD.setOnClickListener(this);
		btnPoker.setOnClickListener(this);
		// 道具里按钮
		btnQP = (ImageButton) findViewById(R.id.btnQP);
		btnCW = (ImageButton) findViewById(R.id.btnCW);
		btnDJ = (ImageButton) findViewById(R.id.btnDJ);
		btnQP.setOnClickListener(this);
		btnCW.setOnClickListener(this);
		btnDJ.setOnClickListener(this);
		mListView = (HorizontalListView) findViewById(android.R.id.list);
		image = new OneImage(getApplicationContext(), null);
		// 初始化页面
		ViewTreeObserver vto = mLFrameLayout.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					// 化妆画面
					stageWidth = mLFrameLayout.getMeasuredWidth();// 获取到宽度和高度后
					stageHeight = mLFrameLayout.getMeasuredHeight();
					hasMeasured = true;
					image.initView(MainActivity.this, stageWidth, stageHeight);
					mLFrameLayout.addView(image);
				}
				return true;
			}
		});
		image.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mSediao.setVisibility(View.INVISIBLE);
					break;
				}
				return false;
			}
		});
		// 提示对话款
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_tip, null);
		dialog = new Dialog(this, R.style.Translucent_NoTitle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setLayout(
				CommonMethod.getWidth(getApplicationContext()),
				CommonMethod.getHeight(getApplicationContext()));
		RelativeLayout iv = (RelativeLayout) view.findViewById(R.id.dialog_tip);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

	@Override
	public void onClick(View v) {
		image.cancleAll();
		image.clearProp();
		OneImage.flagHandler = true;
		switch (v.getId()) {
		case R.id.btnHZ:
			SingleDrawViewBase.CurrentStage = ConstValue.Stage.Face;
			if (hzFlag) {
				image.clearCick();
				// 选中效果
				defaultBottom();
				defaultBk();
				defaultBkColor();
				type = currentHZ;
				btnHZ.setImageDrawable(getResources().getDrawable(
						R.drawable.huazhuang_bk));
				hzClick();
				loadData();
				// 隐藏所有LinearLayout
				defaultGone();
				mLinearLayout.setVisibility(View.VISIBLE);
				mLinearLayout1.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				sediao.setVisibility(View.VISIBLE);
				setTrue();
				hzFlag = false;
				// 添加动画效果
				hideView(mLinearLayout);
			} else {
				setTrue();
				// 添加动画效果
				showView(mLinearLayout);
			}
			break;
		case R.id.btnPropMore:
			SingleDrawViewBase.CurrentStage = ConstValue.Stage.Hair;
			current = 4;
			if (zbFlag) {
				// 选中效果
				image.clearCick();
				defaultBottom();
				defaultBk();
				defaultBkColor();
				type = currentZB;
				btnZB.setImageDrawable(getResources().getDrawable(
						R.drawable.zhuangban_bk));
				zbClick();
				loadData();
				// 选中效果

				// 隐藏所有LinearLayout
				defaultGone();
				mLinearLayout2.setVisibility(View.VISIBLE);
				mLinearLayout.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				sediao.setVisibility(View.VISIBLE);
				setTrue();
				zbFlag = false;
				// 添加动画效果
				hideView(mLinearLayout);
			} else {
				setTrue();
				// 添加动画效果
				showView(mLinearLayout);
			}
			break;
		case R.id.btnScene:
			SingleDrawViewBase.CurrentStage = ConstValue.Stage.Scene;
			current = 6;
			if (cjFlag) {
				image.clearCick();
				// 选中效果
				defaultBottom();
				defaultBk();
				defaultBkColor();
				type = currentCJ;
				btnScene.setImageDrawable(getResources().getDrawable(
						R.drawable.changjing_bk));
				cjClick();
				loadData();
				// 隐藏所有LinearLayout
				defaultGone();
				mLinearLayout3.setVisibility(View.VISIBLE);
				mLinearLayout.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				sediao.setVisibility(View.VISIBLE);
				setTrue();
				cjFlag = false;
				// 添加动画效果
				hideView(mLinearLayout);
			} else {
				setTrue();
				// 添加动画效果
				showView(mLinearLayout);
			}
			break;
		case R.id.btnProp:
			SingleDrawViewBase.CurrentStage = ConstValue.Stage.Prop;
			current = 7;
			if (djFlag) {
				image.clearCick();
				// 选中效果
				defaultBottom();
				defaultBk();
				defaultBkColor();
				type = currentDJ;
				btnProp.setImageDrawable(getResources().getDrawable(
						R.drawable.prop_bk));
				djClick();
				loadData();
				// 隐藏所有LinearLayout
				defaultGone();
				mLinearLayout.setVisibility(View.VISIBLE);
				mLinearLayout4.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				sediao.setVisibility(View.VISIBLE);
				setTrue();
				djFlag = false;
				// 添加动画效果
				hideView(mLinearLayout);
			} else {
				setTrue();
				// 添加动画效果
				showView(mLinearLayout);
			}
			break;
		// 装扮
		case R.id.btnBrow:
			clickR(2);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnBrow.setImageDrawable(getResources().getDrawable(
					R.drawable.meimao_bk));
			btnBrow.setBackgroundColor(Color.WHITE);
			current = 1;
			type = 3;
			currentHZ = 3;
			loadData();
			break;
		case R.id.btnMoustache:
			clickR(4);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnMoustache.setImageDrawable(getResources().getDrawable(
					R.drawable.huzi_bk));
			btnMoustache.setBackgroundColor(Color.WHITE);
			current = 2;
			type = 4;
			currentHZ = 4;
			loadData();
			break;
		case R.id.btnBlusher:
			clickR(3);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnBlusher.setImageDrawable(getResources().getDrawable(
					R.drawable.saihong_bk));
			btnBlusher.setBackgroundColor(Color.WHITE);
			current = 3;
			type = 5;
			currentHZ = 5;
			loadData();
			break;
		case R.id.btnModernFemale:
			clickR(5);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnModernFemale.setImageDrawable(getResources().getDrawable(
					R.drawable.modern_female_headdress_btn_selected));
			btnModernFemale.setBackgroundColor(Color.WHITE);
			current = 4;
			type = 6;
			currentHZ = type;
			loadData();
			break;
		case R.id.btnModernMale:
			clickR(5);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnModernMale.setImageDrawable(getResources().getDrawable(
					R.drawable.modern_male_headdress_btn_selected));
			btnModernMale.setBackgroundColor(Color.WHITE);
			current = 4;
			type = 7;
			currentHZ = type;
			loadData();
			break;
		case R.id.btnRepublicFemale:
			clickR(5);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnRepublicFemale
					.setImageDrawable(getResources()
							.getDrawable(
									R.drawable.republic_of_china_female_headdress_btn_selected));
			btnRepublicFemale.setBackgroundColor(Color.WHITE);
			current = 4;
			type = 8;
			currentHZ = type;
			loadData();
			break;
		case R.id.btnRepublicMale:
			clickR(5);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnRepublicMale.setImageDrawable(getResources().getDrawable(
					R.drawable.republic_of_china_male_headdress_btn_selected));
			btnRepublicMale.setBackgroundColor(Color.WHITE);
			current = 4;
			type = 9;
			currentHZ = type;
			loadData();
			break;
		case R.id.btnAncientFemale:
			clickR(5);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnAncientFemale.setImageDrawable(getResources().getDrawable(
					R.drawable.ancient_times_female_headdress_btn_selected));
			btnAncientFemale.setBackgroundColor(Color.WHITE);
			current = 4;
			type = 10;
			currentHZ = type;
			loadData();
			break;
		case R.id.btnAncientMale:
			clickR(5);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnAncientMale.setImageDrawable(getResources().getDrawable(
					R.drawable.ancient_times_male_headdress_btn_selected));
			btnAncientMale.setBackgroundColor(Color.WHITE);
			current = 4;
			type = 11;
			currentHZ = type;
			loadData();
			break;
		// 装扮

		case R.id.btnRX:
			clickR(0);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnRX.setImageDrawable(getResources().getDrawable(
					R.drawable.rexue_bk));
			btnRX.setBackgroundColor(Color.WHITE);
			current = 5;
			type = 1;
			currentZB = type;
			loadData();
			break;
		case R.id.btnXR:
			clickR(0);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnXR.setImageDrawable(getResources().getDrawable(
					R.drawable.rensheng_bk));
			btnXR.setBackgroundColor(Color.WHITE);
			current = 5;
			type = 2;
			currentZB = type;
			loadData();
			break;
		case R.id.btnRP:
			clickR(0);
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnRP.setImageDrawable(getResources().getDrawable(
					R.drawable.republican_clothes_btn_selected));
			btnRP.setBackgroundColor(Color.WHITE);
			current = 5;
			type = 3;
			currentZB = type;
			loadData();
			break;
		// 场景
		case R.id.btnPoker:
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnPoker.setImageDrawable(getResources().getDrawable(
					R.drawable.a54_plan_btn_selected));
			btnPoker.setBackgroundColor(Color.WHITE);
			current = 6;
			type = 1;
			currentCJ = type;
			loadData();
			break;
		case R.id.btnGM:
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnGM.setImageDrawable(getResources().getDrawable(
					R.drawable.gumu_bk));
			btnGM.setBackgroundColor(Color.WHITE);
			current = 6;
			type = 2;
			currentCJ = type;
			loadData();
			break;
		case R.id.btnYY:
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnYY.setImageDrawable(getResources().getDrawable(
					R.drawable.yunyou_bk));
			btnYY.setBackgroundColor(Color.WHITE);
			current = 6;
			type = 3;
			currentCJ = type;
			loadData();
			break;
		case R.id.btnYD:
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnYD.setImageDrawable(getResources().getDrawable(
					R.drawable.yidong_bk));
			btnYD.setBackgroundColor(Color.WHITE);
			current = 6;
			type = 4;
			currentCJ = type;
			loadData();
			break;
		// 道具
		case R.id.btnQP:
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnQP.setImageDrawable(getResources().getDrawable(
					R.drawable.qipao_bk));
			btnQP.setBackgroundColor(Color.WHITE);
			current = 7;
			type = 1;
			currentDJ = type;
			loadData();
			break;
		case R.id.btnCW:
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnCW.setImageDrawable(getResources()
					.getDrawable(R.drawable.pet_bk));
			btnCW.setBackgroundColor(Color.WHITE);
			current = 7;
			type = 2;
			currentDJ = type;
			loadData();
			break;
		case R.id.btnDJ:
			// 选中效果
			defaultBk();
			defaultBkColor();
			btnDJ.setImageDrawable(getResources().getDrawable(
					R.drawable.daoju_bk));
			btnDJ.setBackgroundColor(Color.WHITE);
			current = 7;
			type = 3;
			currentDJ = type;
			loadData();
			break;
		default:
			break;
		}
		if (adapter != null) {
			adapter.cancelTask();
			adapter.notifyDataSetChanged();
			adapter = null;
		}
		adapter = new SingleAdapter(this, mapList, mPoint, mListView, image,
				type);
		mListView.setAdapter(adapter);
	}

	private void hzClick() {
		switch (currentHZ) {
		case 3:
			current = 1;
			btnBrow.setImageDrawable(getResources().getDrawable(
					R.drawable.meimao_bk));
			btnBrow.setBackgroundColor(Color.WHITE);
			break;
		case 4:
			current = 2;
			btnMoustache.setImageDrawable(getResources().getDrawable(
					R.drawable.huzi_bk));
			btnMoustache.setBackgroundColor(Color.WHITE);
			break;
		case 5:
			current = 3;
			btnBlusher.setImageDrawable(getResources().getDrawable(
					R.drawable.saihong_bk));
			btnBlusher.setBackgroundColor(Color.WHITE);
			break;
		case 6:
			current = 4;
			btnModernFemale.setImageDrawable(getResources().getDrawable(
					R.drawable.modern_female_headdress_btn_selected));
			btnModernFemale.setBackgroundColor(Color.WHITE);
			break;
		case 7:
			current = 4;
			btnModernMale.setImageDrawable(getResources().getDrawable(
					R.drawable.modern_male_headdress_btn_selected));
			btnModernMale.setBackgroundColor(Color.WHITE);
			break;
		case 8:
			current = 4;
			btnRepublicFemale
					.setImageDrawable(getResources()
							.getDrawable(
									R.drawable.republic_of_china_female_headdress_btn_selected));
			btnRepublicFemale.setBackgroundColor(Color.WHITE);
			break;
		case 9:
			current = 4;
			btnRepublicMale.setImageDrawable(getResources().getDrawable(
					R.drawable.republic_of_china_male_headdress_btn_selected));
			btnRepublicMale.setBackgroundColor(Color.WHITE);
			break;
		case 10:
			current = 4;
			btnAncientFemale.setImageDrawable(getResources().getDrawable(
					R.drawable.ancient_times_female_headdress_btn_selected));
			btnAncientFemale.setBackgroundColor(Color.WHITE);
			break;
		case 11:
			current = 4;
			btnAncientMale.setImageDrawable(getResources().getDrawable(
					R.drawable.ancient_times_male_headdress_btn_selected));
			btnAncientMale.setBackgroundColor(Color.WHITE);
			break;
		default:
			break;
		}
	}

	private void zbClick() {
		switch (currentZB) {
		case 1:
			current = 5;
			btnRX.setImageDrawable(getResources().getDrawable(
					R.drawable.rexue_bk));
			btnRX.setBackgroundColor(Color.WHITE);
			break;
		case 2:
			current = 5;
			btnXR.setImageDrawable(getResources().getDrawable(
					R.drawable.rensheng_bk));
			btnXR.setBackgroundColor(Color.WHITE);
			break;
		case 3:
			current = 5;
			btnRP.setImageDrawable(getResources().getDrawable(
					R.drawable.republican_clothes_btn_selected));
			btnRP.setBackgroundColor(Color.WHITE);
			break;
		default:
			break;
		}
	}

	private void cjClick() {
		switch (currentCJ) {
		case 1:
			current = 6;
			btnPoker.setImageDrawable(getResources().getDrawable(
					R.drawable.a54_plan_btn_selected));
			btnPoker.setBackgroundColor(Color.WHITE);
			break;
		case 2:
			current = 6;
			btnGM.setImageDrawable(getResources().getDrawable(
					R.drawable.gumu_bk));
			btnGM.setBackgroundColor(Color.WHITE);
			break;
		case 3:
			current = 6;
			btnYY.setImageDrawable(getResources().getDrawable(
					R.drawable.yunyou_bk));
			btnYY.setBackgroundColor(Color.WHITE);
			break;
		case 4:
			current = 6;
			btnYD.setImageDrawable(getResources().getDrawable(
					R.drawable.yidong_bk));
			btnYD.setBackgroundColor(Color.WHITE);
			break;

		default:
			break;
		}
	}

	private void djClick() {
		switch (currentDJ) {
		case 1:
			current = 7;
			btnQP.setImageDrawable(getResources().getDrawable(
					R.drawable.qipao_bk));
			btnQP.setBackgroundColor(Color.WHITE);
			break;
		case 2:
			current = 7;
			btnCW.setImageDrawable(getResources()
					.getDrawable(R.drawable.pet_bk));
			btnCW.setBackgroundColor(Color.WHITE);
			break;
		case 3:
			current = 7;
			btnDJ.setImageDrawable(getResources().getDrawable(
					R.drawable.daoju_bk));
			btnDJ.setBackgroundColor(Color.WHITE);
			break;
		default:
			break;
		}
	}

	private void clickR(int i) {

	}

	@Override
	protected void onRestart() {
		if (isFlash) {
			image.setFace();
			image.invalidate();
		}
		super.onRestart();
	}

	/**
	 * 返回主页
	 * 
	 * @param v
	 */
	public void btnMainOnclick(View v) {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 重新拍照
	 * 
	 * @param v
	 */
	public void btnPhotoOnclick(View v) {
		Dialog dialog = CommonMethod.getPlayPhotoDialog(this);
		dialog.show();
	}

	/**
	 * 保存按钮
	 * 
	 * @param v
	 * 
	 * @throws IOException
	 */
	public void btnSaveOnclick(View v) {

		String msg = MainActivity.this.getResources().getString(
				R.string.save_album);
		try {
			Bitmap bitmap = image.getCurrentPic();
			mImageManager.saveToAlbum(MainActivity.this, bitmap);
			if (bitmap != null) {
				bitmap.recycle();
				System.gc();
			}
			Toast.makeText(getApplicationContext(), msg, 0).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "请检测SD卡！", 0).show();
		}
	}

	/**
	 * 多人
	 */
	public void btnMoreOnclick(View v) {
		image.initView(MainActivity.this, 0, 0);
		image.invalidate();
	}

	/**
	 * 点击分享按钮
	 * 
	 * @throws IOException
	 */
	public void btnShareOnclick(View v) throws IOException {
		Bitmap tempBitmap = image.getCurrentPic();
		mImageManager.saveToSDCard(tempBitmap, ConstValue.ImgName.resultImg);
		tempBitmap.recycle();
		String strsImage = Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH
				+ ConstValue.ImgName.resultImg.toString() + "jpg";
		// Bitmap mark = BitmapFactory.decodeResource(this.getResources(),
		// R.drawable.logo11);
		// Bitmap photo = BitmapFactory.decodeFile(strsImage);
		// // 生成新的Bitmap对象
		// Bitmap photoMark = Bitmap.createBitmap(photo.getWidth(),
		// photo.getHeight(), Config.ARGB_8888);
		// Canvas canvas = new Canvas(photoMark);
		// canvas.drawBitmap(photo, 0, 0, null);
		// canvas.drawBitmap(mark, photo.getWidth() - mark.getWidth() - 25,
		// photo.getHeight() - mark.getHeight(), null);
		// canvas.save(Canvas.ALL_SAVE_FLAG);
		// canvas.restore();
		// mImageManager.saveToSDCard(photoMark, ConstValue.ImgName.resultImg);
		// photo.recycle();
		CommonMethod.showShare(this, strsImage);
		System.gc();
	}

	// 色调
	public void btnSediao(View v) {
		if (mSediao.getVisibility() != View.VISIBLE) {
			mSediao.setVisibility(View.VISIBLE);
		} else {
			mSediao.setVisibility(View.INVISIBLE);
		}
	}

	// 隐藏所有LinearLayout
	private void defaultGone() {
		mLinearLayout.setVisibility(View.GONE);
		mLinearLayout1.setVisibility(View.GONE);
		mLinearLayout2.setVisibility(View.GONE);
		mLinearLayout3.setVisibility(View.GONE);
		mLinearLayout4.setVisibility(View.GONE);
	}

	/**
	 * 设置动画效果向下
	 * 
	 * @param view
	 */
	private void showView(View view) {
		Animation animate = AnimationUtils.loadAnimation(this,
				R.anim.translate_down);
		animate.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				sediao.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// defaultGone();
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				defaultGone();
				mListView.setVisibility(View.GONE);
			}
		});
		view.startAnimation(animate);
	}

	/**
	 * 设置动画效果向上
	 * 
	 * @param view
	 */
	private void hideView(View view) {
		Animation animate = AnimationUtils.loadAnimation(this,
				R.anim.translate_up);
		view.setAnimation(animate);
	}

	// 设置标记状态true
	private void setTrue() {
		hzFlag = true;
		zbFlag = true;
		cjFlag = true;
		djFlag = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {

		} else {
			switch (requestCode) {
			case 0:
				Intent intent1 = new Intent(this, ShowPicActivity.class);
				intent1.putExtra("type", "home");
				intent1.putExtra("index", image.currentPerson);
				this.startActivity(intent1);
				isFlash = true;
				break;
			case 1:
				new Thread() {
					@Override
					public void run() {
						if (data == null) {
							return;
						}
						Uri uri = data.getData();
						ContentResolver cr = MainActivity.this
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
							try {
								FileOutputStream out = mImageManager.creatFile(
										"photo", "jpg", "play");
								bitmap.compress(CompressFormat.JPEG, 100, out);
							} catch (Exception e) {
								bitmap.recycle();
								e.printStackTrace();
							}
							Intent intent = new Intent(MainActivity.this,
									ShowPicActivity.class);
							intent.putExtra("type", "home");
							intent.putExtra("index", image.currentPerson);
							MainActivity.this.startActivity(intent);
							bitmap.recycle();
						} catch (FileNotFoundException e) {

						}

					}
				}.run();
				isFlash = true;
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// 创建一个相同尺寸的可变的位图区,用于绘制调色后的图片
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
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	private void defaultBk() {
		// 化妆
		btnBrow.setImageResource(R.drawable.meimao);
		btnMoustache.setImageResource(R.drawable.huzi);
		btnBlusher.setImageResource(R.drawable.saihong);
		// 装扮
		btnModernFemale
				.setImageResource(R.drawable.modern_female_headdress_btn_normal);
		btnModernMale
				.setImageResource(R.drawable.modern_male_headdress_btn_normal);
		btnRepublicFemale
				.setImageResource(R.drawable.republic_of_china_female_headdress_btn_normal);
		btnRepublicMale
				.setImageResource(R.drawable.republic_of_china_male_headdress_btn_normal);
		btnAncientFemale
				.setImageResource(R.drawable.ancient_times_female_headdress_btn_normal);
		btnAncientMale
				.setImageResource(R.drawable.ancient_times_male_headdress_btn_normal);

		btnRX.setImageResource(R.drawable.rexue);
		btnXR.setImageResource(R.drawable.rensheng);
		btnRP.setImageResource(R.drawable.republican_clothes_btn_normal);
		// 场景
		btnGM.setImageResource(R.drawable.gumu);
		btnYY.setImageResource(R.drawable.yunyou);
		btnYD.setImageResource(R.drawable.yidong);
		btnPoker.setImageResource(R.drawable.a54_plan_btn_normal);
		// 道具
		btnQP.setImageResource(R.drawable.qipao);
		btnCW.setImageResource(R.drawable.pet);
		btnDJ.setImageResource(R.drawable.daoju);
	}

	private void defaultBottom() {
		// 底部栏
		btnHZ.setImageResource(R.drawable.huazhuang);
		btnZB.setImageResource(R.drawable.zhuangban);
		btnScene.setImageResource(R.drawable.changjing);
		btnProp.setImageResource(R.drawable.prop);
	}

	private void defaultBkColor() {
		// 化妆
		btnBrow.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnMoustache.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnBlusher.setBackgroundColor(Color.parseColor("#99ece7e3"));
		// 装扮
		btnModernFemale.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnModernMale.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnRepublicFemale.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnRepublicMale.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnAncientFemale.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnAncientMale.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnRX.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnXR.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnRP.setBackgroundColor(Color.parseColor("#99ece7e3"));
		// 场景
		btnGM.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnYY.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnYD.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnPoker.setBackgroundColor(Color.parseColor("#99ece7e3"));
		// 道具
		btnQP.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnCW.setBackgroundColor(Color.parseColor("#99ece7e3"));
		btnDJ.setBackgroundColor(Color.parseColor("#99ece7e3"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		image.recycleBitmap();
	}

	public void showTip() {

	}

	// 中心提示框
	public Dialog getCenterTip() {
		final Dialog dialogCenter = new Dialog(this,
				R.style.Translucent_NoTitle);
		;
		// 提示对话框，中间的手势
		LayoutInflater inflatercenter = LayoutInflater
				.from(getApplicationContext());
		View viewcenter = inflatercenter.inflate(R.layout.dialog_center, null);// 提示左右滑动的手势
		dialogCenter.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogCenter.setContentView(viewcenter);
		dialogCenter.getWindow().setLayout(
				CommonMethod.getWidth(getApplicationContext()),
				CommonMethod.getHeight(getApplicationContext()));
		RelativeLayout ivcenter = (RelativeLayout) viewcenter
				.findViewById(R.id.dialog_center);
		ivcenter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogCenter.dismiss();
				// 选中效果
				defaultBottom();
				defaultBk();
				defaultBkColor();
				type = currentHZ;
				btnHZ.setImageDrawable(getResources().getDrawable(
						R.drawable.huazhuang_bk));
				hzClick();
				loadData();
				//隐藏所有LinearLayout
				defaultGone();
				mLinearLayout.setVisibility(View.VISIBLE);
				mLinearLayout1.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.VISIBLE);
				sediao.setVisibility(View.VISIBLE);
				setTrue();
				hzFlag = false;
				// 添加动画效果
				hideView(mLinearLayout);
				// 设置二级选中效果
				clickR(4);
				// 选中效果
				defaultBk();
				defaultBkColor();
				btnMoustache.setImageDrawable(getResources().getDrawable(
						R.drawable.huzi_bk));
				btnMoustache.setBackgroundColor(Color.WHITE);
				current = 2;
				type = 4;
				currentHZ = 4;
				loadData();
				
				image.cancleAll();// 取消所有的光圈
				image.clearProp();//
				
				getBottomTip().show();
			}
		});
		return dialogCenter;
	}

	private Dialog getBottomTip() {
		final Dialog dialogBottom = new Dialog(this,
				R.style.Translucent_NoTitle);
		// 提示对话框,最下面的手势
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.dialog_tip, null);// 提示左右滑动的手势
		dialogBottom.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogBottom.setContentView(view);
		dialogBottom.getWindow().setLayout(
				CommonMethod.getWidth(getApplicationContext()),
				CommonMethod.getHeight(getApplicationContext()));
		RelativeLayout iv = (RelativeLayout) view.findViewById(R.id.dialog_tip);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogBottom.dismiss();
				// 取消下面的弹出效果
				setTrue();
				// 添加动画效果
				showView(mLinearLayout);
				getTopTip().show();
			}
		});
		return dialogBottom;
	}
	//最上面的dialog
	private Dialog getTopTip(){
		final Dialog dialogTop=new Dialog(this, R.style.Translucent_NoTitle);
		LayoutInflater inflatertop = LayoutInflater
				.from(getApplicationContext());
		View viewtop = inflatertop.inflate(R.layout.dialog_top, null);// 提示左右滑动的手势
		dialogTop.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogTop.setContentView(viewtop);
		dialogTop.getWindow().setLayout(
				CommonMethod.getWidth(getApplicationContext()),
				CommonMethod.getHeight(getApplicationContext()));
		RelativeLayout ivtop = (RelativeLayout) viewtop
				.findViewById(R.id.dialog_top);
		ivtop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogTop.dismiss();
			}
		});
		return dialogTop;
	}
}
