package com.dobi.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ImageLoader;
import com.dobi.common.NetUtils;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.db.MyDialog;
import com.dobi.exception.ExitAppUtils;
import com.dobi.logic.ImageManager;
import com.dobi.view.CircleImageView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class HomeActivity extends FragmentActivity {
	private ImageButton threed;
	private ImageManager mImageManager;
	private ImageLoader mImageLoader;
	private Button login, setting;
	private SlidingMenu menu;
	private SharedPreferences shared;
	private Editor edit;
	private SharedPreferences sp;
	private CircleImageView avator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitAppUtils.getInstance().addActivity(this);
		setContentView(R.layout.activity_home);
		mImageManager = new ImageManager();
		mImageLoader = ImageLoader.initLoader(getApplicationContext());
		shared = CommonMethod.getPreferences(getApplicationContext());
		threed = (ImageButton) findViewById(R.id.threed);
		setting = (Button) findViewById(R.id.setting);
		login = (Button) findViewById(R.id.login);
		avator = (CircleImageView) findViewById(R.id.avator);
		edit = shared.edit();
		initSlidingMenu();
		sp = CommonMethod.getPreferences(getApplicationContext());
		Boolean isShow = sp.getBoolean("isShow", false);
		if (isShow) {
			// 初始化一个自定义的Dialog
			Dialog dialog = new MyDialog(HomeActivity.this, R.style.MyDialog);
			dialog.show();
			sp.edit().putBoolean("isShow", false).commit();
		}
		View.OnClickListener vivo = new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				// 我的扮演和多人扮演按钮
				switch (v.getId()) {
				case R.id.threed:
					CommonMethod.SetSingleOrMore(2);
					// 如果没有头像可使用，需要拍头像
					intent.setClass(HomeActivity.this, ThreeHomeActivity.class);
					startActivity(intent);
					break;
				case R.id.setting:
					menu.toggle();
					break;
				case R.id.login:
					intent.setClass(HomeActivity.this, LoginActivity.class);
					startActivity(intent);
					break;
				case R.id.avator:
					intent.setClass(HomeActivity.this,
							AccountInfoActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		};
		setting.setOnClickListener(vivo);
		login.setOnClickListener(vivo);
		threed.setOnClickListener(vivo);
		avator.setOnClickListener(vivo);
	}

	@Override
	protected void onStart() {
		if (shared.getString("uid", null) != null) {
			NetUtils.getUserInfo(shared.getString("uid", null),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] header,
								byte[] data) {
							try {
								login.setVisibility(View.INVISIBLE);
								avator.setVisibility(View.VISIBLE);
								JSONObject json = new JSONObject(new String(
										data));
								JSONObject userObject;
								userObject = json.getJSONObject("userInfo");
								setViewBitmap(avator,
										userObject.getString("user_avatar"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] data, Throwable arg3) {

						}
					});
			login.setText("");
		} else {
			login.setVisibility(View.VISIBLE);
			avator.setVisibility(View.INVISIBLE);
			login.setBackgroundColor(Color.TRANSPARENT);
			login.setText("登录");
		}
		super.onStart();
	}

	@Override
	protected void onRestart() {
		if (shared.getString("uid", null) != null) {
			NetUtils.getUserInfo(shared.getString("uid", null),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] header,
								byte[] data) {
							try {
								login.setVisibility(View.INVISIBLE);
								avator.setVisibility(View.VISIBLE);
								JSONObject json = new JSONObject(new String(
										data));
								JSONObject userObject;
								userObject = json.getJSONObject("userInfo");
								setViewBitmap(avator,
										userObject.getString("user_avatar"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] data, Throwable arg3) {
						}
					});

			login.setText("");
			login.setEnabled(true);
		} else {
			login.setVisibility(View.VISIBLE);
			avator.setVisibility(View.INVISIBLE);
			login.setBackgroundColor(Color.TRANSPARENT);
			login.setText("登陆");
			login.setEnabled(true);
		}
		super.onRestart();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 友盟统计
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 友盟统计
		MobclickAgent.onPause(this);
	}

	/**
	 * 单人扮演
	 * 
	 * @param v
	 */
	public void ImgBtnSingleOnclick(View v) {
		CommonMethod.SetSingleOrMore(0);
		// 如果没有头像可使用，需要拍头像
		if (!mImageManager.loadImg()) {
			final Dialog note;
			RelativeLayout relativeLayout;
			// 渲染布局，获取相应控件
			LayoutInflater inflater = LayoutInflater
					.from(getApplicationContext());
			View view = inflater.inflate(R.layout.window_pop, null);
			ImageButton one = (ImageButton) view.findViewById(R.id.xiangji);
			ImageButton two = (ImageButton) view.findViewById(R.id.xiangce);
			relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_layout);
			// 获取progress控件的宽高
			int height = CommonMethod.getHeight(this);// (int)
														// (CommonMethod.GetDensity(HomeActivity.this)
														// * 180 + 0.5);
			int width = CommonMethod.getWidth(this);// (int)
													// (CommonMethod.GetDensity(HomeActivity.this)
													// * 200 + 0.5);
			// 新建Dialog
			note = new Dialog(this, R.style.Translucent_NoTitle);
			// note.requestWindowFeature(Window.FEATURE_NO_TITLE);
			LayoutParams params = new LayoutParams(width, height);
			// 设置对话框大小（不好用）
			WindowManager.LayoutParams params1 = note.getWindow()
					.getAttributes();
			params1.width = width;
			params1.height = height;
			params1.x = 0;
			params1.y = 0;
			note.getWindow().setAttributes(params1);
			note.addContentView(view, params);
			note.show();
			one.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					note.dismiss();
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri mOutPutFileUri;
					// 文件夹doubi
					String path = Environment.getExternalStorageDirectory()
							.toString() + "/dobi/play";
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
			two.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					note.dismiss();
					Intent intent = new Intent();
					/* 开启Pictures画面Type设定为image */
					intent.setType("image/*");
					/* 使用Intent.ACTION_GET_CONTENT这个Action */
					intent.setAction(Intent.ACTION_GET_CONTENT);
					/* 取得相片后返回本画面 */
					startActivityForResult(intent, 1);
				}
			});
			relativeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					note.dismiss();
				}
			});

		} else {
			Intent intent = new Intent(this, MainActivity.class);
			this.startActivity(intent);
		}

	}

	/**
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SampleListFragment()).commit();
		// 设置滑动菜单的属性值
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		int slidingWidth = CommonMethod.getWidth(getApplicationContext()) * 8 / 10;
		menu.setBehindWidth(slidingWidth);
		// menu.setBehindOffsetRes(slidingWidth);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// 设置滑动菜单的视图界面
		menu.setMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SampleListFragment()).commit();
	}

	/**
	 * 换脸
	 * 
	 * @param v
	 */
	public void ImgBtnMoreOnclick(View v) {
		// 弹出提示
		// final ImageView img = (ImageView)
		// this.findViewById(R.id.imgvWaiting);
		// img.setVisibility(View.VISIBLE);
		// Handler mHandler = new Handler();
		// mHandler.postDelayed(new Runnable() {
		// public void run() {
		// img.setVisibility(View.INVISIBLE);
		// }
		// }, 2000);
		// 进入换脸
		CommonMethod.SetSingleOrMore(1);
		// // 如果没有头像可使用，需要拍头像
		if (!(new ImageManager()).loadImgForMore()) {
			final Dialog note;
			RelativeLayout relativeLayout;
			// 渲染布局，获取相应控件
			LayoutInflater inflater = LayoutInflater
					.from(getApplicationContext());
			View view = inflater.inflate(R.layout.window_pop, null);
			ImageButton one = (ImageButton) view.findViewById(R.id.xiangji);
			ImageButton two = (ImageButton) view.findViewById(R.id.xiangce);
			relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_layout);
			// 获取progress控件的宽高
			int height = CommonMethod.getHeight(this);// (int)
														// (CommonMethod.GetDensity(HomeActivity.this)
														// * 180 + 0.5);
			int width = CommonMethod.getWidth(this);// (int)
													// (CommonMethod.GetDensity(HomeActivity.this)
													// * 200 + 0.5);
			// 新建Dialog
			note = new Dialog(this, R.style.Translucent_NoTitle);
			LayoutParams params = new LayoutParams(width, height);
			// 设置对话框大小（不好用）
			WindowManager.LayoutParams params1 = note.getWindow()
					.getAttributes();
			params1.width = width;
			params1.height = height;
			params1.x = 0;
			params1.y = 0;
			note.getWindow().setAttributes(params1);
			note.addContentView(view, params);
			note.show();
			one.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					note.dismiss();
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri mOutPutFileUri;
					// 文件夹dubi
					String path = Environment.getExternalStorageDirectory()
							.toString() + "/dobi/face/";
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
			two.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					note.dismiss();
					Intent intent = new Intent();
					/* 开启Pictures画面Type设定为image */
					intent.setType("image/*");
					/* 使用Intent.ACTION_GET_CONTENT这个Action */
					intent.setAction(Intent.ACTION_GET_CONTENT);
					/* 取得相片后返回本画面 */
					startActivityForResult(intent, 1);
				}
			});
			relativeLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					note.dismiss();
				}
			});

		} else {
			Intent intent = new Intent(HomeActivity.this, MoreActivity.class);
			startActivity(intent);
		}

	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, this.getString(R.string.drop), 0).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 3000); // 如果3秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
			System.exit(0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {

		} else {
			switch (requestCode) {
			case 0:
				Intent intent1 = null;
				if (CommonMethod.GetSingleOrMore() == 0) {
					intent1 = new Intent(this, ShowPicActivity.class);
				} else if (CommonMethod.GetSingleOrMore() == 1) {
					intent1 = new Intent(this, ShowMoreActivity.class);
				} else if (CommonMethod.GetSingleOrMore() == 2) {
					intent1 = new Intent(this, ShowPicActivity.class);
				}
				intent1.putExtra("type", "index");
				this.startActivity(intent1);
				break;
			case 1:

				Uri uri = data.getData();
				ContentResolver cr = this.getContentResolver();
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
					float width = display.getWidth() * 1.0f;
					float height = display.getHeight() * 1.0f;
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
					Intent intent = null;
					try {
						if (CommonMethod.GetSingleOrMore() == 0
								|| CommonMethod.GetSingleOrMore() == 2) {
							FileOutputStream out = mImageManager.creatFile(
									"photo", "jpg", "play");
							bitmap.compress(CompressFormat.JPEG, 100, out);
							out.close();
							intent = new Intent(this, ShowPicActivity.class);
							intent.putExtra("type", "index");
						} else {

							FileOutputStream out = mImageManager.creatFile(
									"photo", "jpg", "face");
							bitmap.compress(CompressFormat.JPEG, 100, out);
							out.close();
							intent = new Intent(this, ShowMoreActivity.class);
							intent.putExtra("type", "index");

						}
					} catch (IOException e) {
						bitmap.recycle();
						System.gc();
					}
					this.startActivity(intent);
					bitmap.recycle();
					System.gc();
				} catch (FileNotFoundException e) {

				}
				break;
			}
		}
	}

	public void setViewBitmap(final CircleImageView view, String url) {
		Bitmap bitmap = mImageLoader.downloadImage(NetUtils.IMAGE_PREFIX + url,
				new onImageLoaderListener() {
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							view.setImageBitmap(bitmap);
						}
					}
				}, null);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
		}
	}

}
