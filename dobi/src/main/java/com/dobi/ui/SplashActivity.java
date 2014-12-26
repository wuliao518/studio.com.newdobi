package com.dobi.ui;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.FileUtils;
import com.dobi.common.NetImageInfo;
import com.dobi.common.NetManager;
import com.dobi.common.SharedConfig;
import com.dobi.common.NetImageInfo.NetListener;
import com.dobi.db.DBManager;
import com.dobi.db.MyPushIntentService;
import com.dobi.exception.ExitAppUtils;
import com.dobi.logic.Intelegence;
import com.umeng.message.PushAgent;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class SplashActivity extends BaseActivity {
	private boolean first; // 判断是否第一次打开软件
	private View view;
	private Context context;
	private Animation animation;
	private NetManager netManager;
	private SharedPreferences shared;
	// 存放图片路径
	private Map<String, String> mapNetList;
	private DBManager manager;
	private boolean isEmpty;
	private Dialog dialog;
	private String imageVersion=null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				
				if(imageVersion!=null&&Integer.parseInt(imageVersion)!=manager.getVersion("ImageVersion")){
					new Thread(){
						public void run(){
							new FileUtils(getApplicationContext()).deleteFile();
						};
					}.start();
				}
				Iterator it = mapNetList.entrySet().iterator();
				manager.clear();
				while (it.hasNext()) {
					Entry entry = (Entry) it.next();
					manager.add((String) entry.getKey(),
							(String) entry.getValue());
				}
				animateStart();

				break;
			case 1:
				animateStart();
				if(dialog!=null&&dialog.isShowing()){
					dialog.dismiss();
				}
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 友盟推送
		PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.enable();
		mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(Environment.getExternalStorageDirectory().exists()&&Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			// 检查并创建素材目录
			(new Intelegence()).CheckAndCreatRoot();
			view = View.inflate(this, R.layout.activity_splash, null);
			setContentView(view);
			context = this; // 得到上下文
			shared = new SharedConfig(context).GetConfig(); // 得到配置文件
			netManager = new NetManager(context); // 得到网络管理器
			manager = new DBManager(getApplicationContext());
			isEmpty = manager.isEmpty();
		}else{
			Toast.makeText(getApplicationContext(), "请检测SD卡！", 0).show();
			ExitAppUtils.getInstance().exit();
		}
	}

	@Override
	protected void onStart() {
		into();
		super.onStart();
	}

	// 进入主程序的方法
	public void into() {
		if (netManager.isOpenNetwork() || netManager.isOpenWifi()) {
			// 获取网络上的图片路径
			NetImageInfo.setJson(new NetListener() {
				@Override
				public void onNetListener(Map<String, String> mapList) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					mapNetList = mapList;
					if (mapNetList == null && isEmpty) {
						Toast.makeText(getApplicationContext(), R.string.network_error, 0)
								.show();
						SplashActivity.this.finish();
					} else if (mapNetList != null) {
						imageVersion=mapNetList.get("ImageVersion");
						handler.sendEmptyMessage(0);
					} else if (mapNetList == null && !isEmpty) {
						handler.sendEmptyMessage(1);
					}
				}
			});
		} else {
			if (isEmpty) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				handler.sendEmptyMessage(1);
			}
//				final Dialog note;
//				RelativeLayout relativeLayout;
//				// 渲染布局，获取相应控件
//				LayoutInflater inflater = LayoutInflater
//						.from(getApplicationContext());
//				View view = inflater.inflate(R.layout.net_dialog, null);
//				Button one = (Button) view.findViewById(R.id.button1);
//				Button two = (Button) view.findViewById(R.id.button2);
//				relativeLayout = (RelativeLayout) view
//						.findViewById(R.id.rl_layout);
//				// 获取progress控件的宽高
//				int height = (int) (CommonMethod
//						.GetDensity(SplashActivity.this) * 160 + 0.5);
//				int width = (int) (CommonMethod.GetDensity(SplashActivity.this) * 240 + 0.5);
//				// 新建Dialog
//				note = new Dialog(this, R.style.Translucent_NoTitle);
//				// note.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				LayoutParams params = new LayoutParams(width, height);
//				// 设置对话框大小（不好用）
//				WindowManager.LayoutParams params1 = note.getWindow()
//						.getAttributes();
//				params1.width = width;
//				params1.height = height;
//				params1.x = 0;
//				params1.y = 0;
//				note.getWindow().setAttributes(params1);
//				note.addContentView(view, params);
//				note.setCancelable(false);
//				//note.show();
//				one.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						note.dismiss();
//						Intent intent = null;
//						try {
//							String sdkVersion = android.os.Build.VERSION.SDK;
//							if (Integer.valueOf(sdkVersion) > 10) {
//								intent = new Intent(
//										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
//							} else {
//								intent = new Intent();
//								ComponentName comp = new ComponentName(
//										"com.android.settings",
//										"com.android.settings.WirelessSettings");
//								intent.setComponent(comp);
//								intent.setAction("android.intent.action.VIEW");
//							}
//							SplashActivity.this.startActivity(intent);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				});
//				two.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						note.dismiss();
//						SplashActivity.this.finish();
//					}
//				});
//			} else {
			
		}

	}

	private void animateStart() {
		// 如果网络可用则判断是否第一次进入，如果是第一次则进入欢迎界面
		first = shared.getBoolean("First", true);
		// 设置动画效果是alpha，在anim目录下的alpha.xml文件中定义动画效果
		animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		// 给view设置动画效果
		view.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						Intent intent;
						// 如果第一次，则进入引导页WelcomeActivity
						if (first) {
							intent = new Intent(SplashActivity.this,
									WelcomeActivity.class);
						} else {
							intent = new Intent(SplashActivity.this,
									HomeActivity.class);
						}
						startActivity(intent);
						// 设置Activity的切换效果
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						SplashActivity.this.finish();
					}
				});
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}
