package com.dobi.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.facebook.controller.UMFacebookHandler;
import com.umeng.socialize.facebook.controller.UMFacebookHandler.PostType;
import com.umeng.socialize.facebook.media.FaceBookShareContent;
import com.umeng.socialize.instagram.controller.UMInstagramHandler;
import com.umeng.socialize.instagram.media.InstagramShareContent;
import com.umeng.socialize.media.GooglePlusShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.TwitterShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author Administrator 共同方法
 */
public class CommonMethod {
	/**
	 * 屏幕密度
	 */
	private static float density = 0;

	/**
	 * 场景类型，0：单人扮演 ，1：多人扮演   2:3d
	 */
	private static int sceneType = 0;

	/**
	 * 获取屏幕宽度
	 * 
	 * @param activity
	 * @return
	 */
	// 进度加载提示Dialog
	public static JSONObject jsonObject;
	private static SharedPreferences mySharedPreferences;

	public static float GetDensity(Context context) {
		if (density == 0) {
			// 获取屏幕
			DisplayMetrics dm = new DisplayMetrics();
			((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
			density = dm.density;
		}
		return density;
	}
	
	public static int getHeight(Context context){
		return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight();
	}
	
	public static int getWidth(Context context){
		return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth();
	}
	/**
	 * 获取场景类型，
	 * 
	 * @return 0：单人扮演 ，1：多人扮演,2:初次进入多人扮演
	 */
	public static int GetSingleOrMore() {
		return sceneType;
	}

	/**
	 * 设置场景类型
	 * 
	 * @param i
	 *            0：单人扮演 ，1：多人扮演
	 */
	public static void SetSingleOrMore(int i) {
		sceneType = i;
	}

	/**
	 * 获取剪切前图片宽高比例
	 * 
	 * @return
	 */
	public static double GetFaceForClipScale() {
		return (double) 480 / (double) 601;
	}

	/**
	 * 获取Sharepreference的值
	 * 
	 * @param context
	 * @param key
	 * @return 如果未储存值返回-1
	 * 
	 */
	public static int GetSharepreferenceValue(Context context,
			ConstValue.SharepreferenceKey key) {
		if (mySharedPreferences == null) {
			mySharedPreferences = context.getSharedPreferences(ConstValue.DOBI,
					Context.MODE_PRIVATE); // 私有数据
		}

		int result = mySharedPreferences.getInt(key.toString(), -1);

		return result;

	}

	/**
	 * 设置Sharepreference的值
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void SetSharepreferenceValue(Context context,
			ConstValue.SharepreferenceKey key, int value) {
		if (mySharedPreferences == null) {
			mySharedPreferences = context.getSharedPreferences(ConstValue.DOBI,
					Context.MODE_PRIVATE); // 私有数据
		}
		Editor editor = mySharedPreferences.edit();// 获取编辑器
		editor.putInt(key.toString(), value);
		editor.commit();
	}
	
	/**
	 * 获取拍照对话框
	 * 
	 * @param context
	 */
	public static Dialog getPlayPhotoDialog(final Activity activity){
		final Dialog note=new Dialog(activity, R.style.Translucent_NoTitle);
		//渲染布局，获取相应控件
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.dialog_selected_photo, null);
		note.setContentView(view);
		note.setCanceledOnTouchOutside(true);
		Button one=(Button)view.findViewById(R.id.xiangji);
		Button two=(Button)view.findViewById(R.id.xiangce);
		Button cancle=(Button) view.findViewById(R.id.selected_cancle);
		Window dialogWindow = note.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		lp.x = 0;
		lp.y = 0;
		lp.width = CommonMethod.getWidth(activity); //宽度
        lp.height = lp.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		note.setContentView(view);
		one.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				note.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri mOutPutFileUri;
				//文件夹doubi
				String path = Environment.getExternalStorageDirectory().toString()+"/dobi/play";
				File path1 = new File(path);
				if(!path1.exists()){
					path1.mkdirs();
				}
				File file = new File(path1,"photo"+"jpg");
				mOutPutFileUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
				activity.startActivityForResult(intent, 0);
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
				activity.startActivityForResult(intent, 1);
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(note!=null&&note.isShowing()){
					note.dismiss();
				}
			}
		});
		return note;
	}
	/**
	 * 获取拍照对话框
	 * 
	 * @param context
	 */
	public static Dialog getFacePhotoDialog(final Activity activity){
		final Dialog note=new Dialog(activity, R.style.Translucent_NoTitle);
		//渲染布局，获取相应控件
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.dialog_selected_photo, null);
		note.setContentView(view);
		note.setCanceledOnTouchOutside(true);
		Button one=(Button)view.findViewById(R.id.xiangji);
		Button two=(Button)view.findViewById(R.id.xiangce);
		Button cancle=(Button) view.findViewById(R.id.selected_cancle);
		Window dialogWindow = note.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		lp.x = 0;
		lp.y = 0;
		lp.width = CommonMethod.getWidth(activity); //宽度
        lp.height = lp.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		note.setContentView(view);
		
		one.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				note.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri mOutPutFileUri;
				//文件夹doubi
				String path = Environment.getExternalStorageDirectory().toString()+"/dobi/face";
				File path1 = new File(path);
				if(!path1.exists()){
					path1.mkdirs();
				}
				File file = new File(path1,"photo"+"jpg");
				mOutPutFileUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
				activity.startActivityForResult(intent, 0);
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
				activity.startActivityForResult(intent, 1);
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(note!=null&&note.isShowing()){
					note.dismiss();
				}
			}
		});
		
		return note;
	}
	
	public static void showShare(Activity activity,String path){
		final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		//开启log
		//com.umeng.socialize.utils.Log.LOG = true;
		// 设置分享内容
		//mController.setShareContent("来自 dobi 的分享，http://www.do-bi.cn");
		// 设置分享图片, 参数2为图片的url地址
		//mController.setShareMedia(new UMImage(activity,path));
		// 设置分享图片，参数2为本地图片的资源引用
		//mController.setShareMedia(new UMImage(activity, path));
		// 设置分享图片，参数2为本地图片的路径(绝对路径)
		//mController.setShareMedia(new UMImage(activity, 
		//		                               BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));
		//Environment.getExternalStorageDirectory()
		//+ ConstValue.ROOT_PATH+ ConstValue.ImgName.resultImg.toString() + ".jpg"
		mController.setShareImage(new UMImage(activity, path));
		//添加微信和朋友圈的分享*********************************
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appID = "wx2b508e7f24e35497";
		String appSecret = "4b549eec2b5ffeb7aa51a5ed3cae8136";
		
//		String appID = "wx5a557607fa9bfb82";   dobi逗比
//		String appSecret = "bfd65477ed8c11b7ad3e89d3b5320d1f";
		
//		AppID：wx2b508e7f24e35497		特逗
//		AppSecret：4b549eec2b5ffeb7aa51a5ed3cae8136
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(activity,appID,appSecret);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(activity,appID,appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		//设置微信好友分享内容***
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		//设置分享文字
		//weixinContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，微信");
		//设置title
		//weixinContent.setTitle("特逗 的分享");
		//设置分享内容跳转URL
		weixinContent.setTargetUrl("Http://www.do-bi.cn");
		//设置分享图片
		weixinContent.setShareImage(new UMImage(activity, path));
		mController.setShareMedia(weixinContent);

		//设置微信朋友圈分享内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareImage(new UMImage(activity, path));
		circleMedia.setTargetUrl("http://www.do-bi.cn");
		mController.setShareMedia(circleMedia);
		
		//QQ分享****************************************
		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1103499761",
		                "BWQYLQ4C67ZShdxj");
		//特逗的appkey	1103499761       BWQYLQ4C67ZShdxj
		//dobi逗比          1103195405		23suWtOPCw8GbQrH
		qqSsoHandler.addToSocialSDK(); 
		//分享的内容设置
		QQShareContent qqShareContent = new QQShareContent();
		//设置分享文字
		//qqShareContent.setShareContent("来自  dobi 的分享");
		//设置分享title
		//qqShareContent.setTitle("hello, title");
		//设置分享图片
		qqShareContent.setShareImage(new UMImage(activity, path));
		//设置点击分享内容的跳转链接
		qqShareContent.setTargetUrl("http://www.do-bi.cn");
		mController.setShareMedia(qqShareContent);
		
		//QQ空间分享************
		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(activity, "1103499761",
		                "BWQYLQ4C67ZShdxj");
		qZoneSsoHandler.addToSocialSDK();
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareImage(new UMImage(activity, path));
		qzone.setShareContent("tedo特逗 ");
		//设置分享title
		qzone.setTitle("来自  tedo特逗  的分享");
		qzone.setTargetUrl("http://www.do-bi.cn");
		mController.setShareMedia(qzone);
		//设置新浪SSO handler****************
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		//设置分享的图片
		SinaShareContent sina = new SinaShareContent();
		sina.setShareImage(new UMImage(activity,path));
		sina.setShareContent("来自  tedo特逗   的分享");
		sina.setTargetUrl("Http://www.do-bi.cn");
		mController.setShareMedia(sina);
		//设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		TencentWbShareContent tencentwb = new TencentWbShareContent();
		//设置分享图片
		tencentwb.setShareImage(new UMImage(activity,path));
		tencentwb.setShareContent("来自  tedo特逗  的分享");
		tencentwb.setTargetUrl("Http://www.do-bi.cn");
		mController.setShareMedia(tencentwb);
				
				
		//添加人人网SSO授权功能
		//APPID:201874
		//API Key:28401c0964f04a72a14c812d6132fcef
		//Secret:3bf66e42db1e4fa9829b955cc300b737
		RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(activity,
		            "271793", "89467799953943859c5f91ec73463ce7",
		            "da9a33d176b34203bb45d8408f60fb8e");
		mController.getConfig().setSsoHandler(renrenSsoHandler);
		
		RenrenShareContent renren = new RenrenShareContent();
		renren.setTargetUrl("Http://www.do-bi.cn");
		//设置分享图片
		renren.setShareImage(new UMImage(activity,path));
		mController.setShareMedia(renren);
		//设置新浪SSO handler
		//mController.getConfig().setSsoHandler(new SinaSsoHandler());
		//设置website的方式如下：人人网
		//mController.setAppWebSite(SHARE_MEDIA.RENREN, "http://www.umeng.com/social");
		// 添加短信
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
		SmsShareContent smsshare = new SmsShareContent();
		//设置分享图片
		smsshare.setShareImage(new UMImage(activity,path));
		smsshare.setShareContent("来自  tedo特逗  的分享");
		mController.setShareMedia(smsshare);
		// 添加email
		EmailHandler emailHandler = new EmailHandler();
		//emailHandler.mShareMedia();
		emailHandler.addToSocialSDK();

		
		
		
		// 添加facebook支持, 参数1为当前activity
		// 设置facebook为大图分享,如果不设置则默认为图文分享
		UMFacebookHandler mFacebookHandler = new UMFacebookHandler(activity,
				"529811220455438",PostType.PHOTO);
		mFacebookHandler.addToSocialSDK();
		
		String fappid = "529811220455438";
		FaceBookShareContent facebook = new FaceBookShareContent(fappid);
		facebook.setShareImage(new UMImage(activity,path));
		facebook.setShareContent("来自  tedo 的分享");
		mController.setShareMedia(facebook);		
		// 添加Twitter分享
		mController.getConfig().supportAppPlatform(activity, SHARE_MEDIA.TWITTER,
				"com.umeng.share", true);
		TwitterShareContent twitter = new TwitterShareContent();
		twitter.setShareImage(new UMImage(activity,path));
		twitter.setShareContent("来自  tedo 的分享");
		mController.setShareMedia(twitter);
		
		
		// 构建Instagram的Handler
		UMInstagramHandler instagramHandler = new UMInstagramHandler(activity);
		// 将instagram添加到sdk中
		instagramHandler.addToSocialSDK();
		// 本地图片
		UMImage localImage = new UMImage(activity, path);
		// 设置分享到Instagram的内容， 注意由于instagram客户端的限制，目前该平台只支持纯图片分享，文字、音乐、url图片等都无法分享。
		InstagramShareContent instagramShareContent = new InstagramShareContent(localImage);
		// 设置Instagram的分享内容
		mController.setShareMedia(instagramShareContent);
		//添加G+分享
		mController.getConfig().supportAppPlatform(activity, SHARE_MEDIA.GOOGLEPLUS,
				"com.umeng.share", true) ; 
		GooglePlusShareContent goole = new GooglePlusShareContent();
		goole.setShareImage(new UMImage(activity,path));
		goole.setShareContent("来自  tedo 的分享");
		mController.setShareMedia(goole);
		// 是否只有已登录用户才能打开分享选择页
		mController.openShare(activity, false);
	}
	
	
	private static List<String> getJson(final String path){
		final ArrayList<String> mapList=new ArrayList<String>();
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient client = new DefaultHttpClient();
				// StringBuilder 字符串变量（非线程安全）
				StringBuilder builder = new StringBuilder();
				HttpGet get = new HttpGet(ConstValue.NET_URL);
				HttpResponse response;
				try {
					response = client.execute(get);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					for (String s = reader.readLine(); s != null; s = reader
							.readLine()) {
						builder.append(s);
					}
					jsonObject = null;
					jsonObject = new JSONObject(builder.toString());
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				int length;
				try {
					length = jsonObject.getInt(path);
					for(int i=0;i<length;i++){
						if(i>9){
							mapList.add("http://www.d-bi.cn/download"+path+"/0"+i+"png");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.execute();
		return null;

	}
	public static SharedPreferences getPreferences(Context context) {
		if (mySharedPreferences == null) {
			mySharedPreferences = context.getSharedPreferences(ConstValue.DOBI,
					Context.MODE_PRIVATE); // 私有数据
		}
		return mySharedPreferences;
		
	}
	public static String getStringFromShare(Context context,String name){
		if (mySharedPreferences == null) {
			mySharedPreferences = context.getSharedPreferences(ConstValue.DOBI,
					Context.MODE_PRIVATE); // 私有数据
		}
		return mySharedPreferences.getString(name, null);
	}	
	public static Dialog showMyDialog(Activity mActivity) {
		Dialog note;
		// 进度条底层图案
		ImageView progress;
		// 进行旋转的图案
		ImageView fresh;

		// 渲染布局，获取相应控件
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.dialog_prompt, null);
		progress = (ImageView) view.findViewById(R.id.progressBackground);
		fresh = (ImageView) view.findViewById(R.id.progressFresh);
		// 设置加载进度条动画
		Animation animation = AnimationUtils.loadAnimation(mActivity,
				R.anim.dialog_progress);
		fresh.startAnimation(animation);
		// 获取progress控件的宽高
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		progress.measure(w, h);
		int height = progress.getMeasuredHeight();
		int width = progress.getMeasuredWidth();
		// 新建Dialog
		note = new Dialog((Context)mActivity, R.style.Translucent_NoTitle);
		// note.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutParams params = new LayoutParams(width, height);
		// 设置对话框大小（不好用）
		WindowManager.LayoutParams params1 = note.getWindow().getAttributes();
		params1.width = width;
		params1.height = height;
		params1.x = 0;
		params1.y = 0;
		note.getWindow().setAttributes(params1);
		note.addContentView(view, params);
		note.setCancelable(false);
		return note;
	}
	public static Dialog showPhotoDialog(Activity mActivity) {
		final Dialog note = new Dialog((Context)mActivity, R.style.Translucent_NoTitle);;
		// 渲染布局，获取相应控件
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.dialog_selected_photo, null);
		Button cancle=(Button) view.findViewById(R.id.selected_cancle);
		note.setContentView(view);
		note.setCanceledOnTouchOutside(true);
		Window dialogWindow = note.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		lp.x = 0;
		lp.y = 0;
		lp.width = CommonMethod.getWidth(mActivity); //宽度
        lp.height = lp.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(note!=null&&note.isShowing()){
					note.dismiss();
				}
			}
		});
		return note;
	}


}
