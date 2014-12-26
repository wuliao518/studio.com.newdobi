package com.dobi.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.common.NetUtils;
import com.dobi.view.CircleImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @author xiaowang 功能描述：列表Fragment，用来显示滑动菜单打开后的内容
 */
@SuppressLint("NewApi")
public class SampleListFragment extends Fragment {
	private CircleImageView mBtnback_sy;
	private TextView username;
	private Intent intent;
	private Editor edit;
	private SharedPreferences sp;
	LinearLayout one, two, three, four, five, seven, eight, preEight;
	private int selectedColor = 0xd5d5d5;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list, null);
		intent = new Intent();
		sp = CommonMethod.getPreferences(getActivity());
		edit = sp.edit();
		// 设置左边图片的点击事件
		mBtnback_sy = (CircleImageView) view.findViewById(R.id.mBtnback_sy);
		one = (LinearLayout) view.findViewById(R.id.one);
		two = (LinearLayout) view.findViewById(R.id.two);
		three = (LinearLayout) view.findViewById(R.id.three);
		four = (LinearLayout) view.findViewById(R.id.four);
		five = (LinearLayout) view.findViewById(R.id.five);
		seven = (LinearLayout) view.findViewById(R.id.seven);
		eight = (LinearLayout) view.findViewById(R.id.eight);
		preEight = (LinearLayout) view.findViewById(R.id.preEight);
		username = (TextView) view.findViewById(R.id.username);
		// 返回
		mBtnback_sy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(getActivity(), HomeActivity.class);
				startActivity(intent);
			}
		});
		// 账户信息
		one.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sp.getString("uid", null)!=null){
					// 跳转到账户信息
					intent.setClass(getActivity(), AccountInfoActivity.class);
					startActivity(intent);
					setbackground();
					one.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
				}else{
					intent.setClass(getActivity(), LoginActivity.class);
					startActivity(intent);
					setbackground();
					one.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
				}
				
			}
		});
		// 订单信息
		two.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sp.getString("uid", null)!=null){
					// 跳转到账户信息
					intent.setClass(getActivity(), PayMomentActivity.class);
					startActivity(intent);
					setbackground();
					two.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
				}else{
					intent.setClass(getActivity(), LoginActivity.class);
					startActivity(intent);
					setbackground();
					one.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
				}
			}
		});
		// 相册
		three.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(getActivity(), AlbumActivity.class);
				startActivity(intent);
				setbackground();
				three.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
			}
		});

		four.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setbackground();
				four.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
				intent.setClass(getActivity(), NoticeInfoActivity.class);
				startActivity(intent);
			}
		});
		// 教程
		five.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(getActivity(), PromptActivity.class);
				intent.putExtra("url",
						"http://api.do-bi.cn/Api/Home/View/learning/learning.html");
				intent.putExtra("title","教程");
				startActivity(intent);
				setbackground();
				five.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
			}
		});
		// 团队介绍
		seven.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(getActivity(), OpinionActivity.class);
				startActivity(intent);
				setbackground();
				seven.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
			}
		});
		// 服务条款
		preEight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(getActivity(), ServerActivity.class);
				startActivity(intent);
				setbackground();
				preEight.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
			}
		});
		// 检测版本
		eight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "你已经是最新版本！", 0).show();
				setbackground();
				eight.setBackgroundColor(Color.rgb(0xd5, 0xd5, 0xd5));
			}
		});
		return view;
	}

	// 设置背景透明
	public void setbackground() {
		one.setBackgroundColor(color.transparent);
		two.setBackgroundColor(color.transparent);
		three.setBackgroundColor(color.transparent);
		four.setBackgroundColor(color.transparent);
		five.setBackgroundColor(color.transparent);
		seven.setBackgroundColor(color.transparent);
		preEight.setBackgroundColor(color.transparent);
		eight.setBackgroundColor(color.transparent);

	}

	@SuppressLint("NewApi")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		if (sp.getString("uid", null) != null) {
			Log.i("jiang", "我被执行了");
			NetUtils.getUserInfo(sp.getString("uid", null),
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] data) {
							try {
								JSONObject json = new JSONObject(new String(
										data));
								if (json.getInt("status") == 1) {
									
									JSONObject userObject = json.getJSONObject("userInfo");
									
									username.setText(userObject.getString("user_login"));
									Bitmap bitmap = ImageLoader.initLoader(
											getActivity()).downloadImage(
											NetUtils.IMAGE_PREFIX
													+ userObject.getString("user_avatar"),
											new onImageLoaderListener() {
												@Override
												public void onImageLoader(
														Bitmap bitmap,
														String url) {
													if (bitmap != null) {
														mBtnback_sy
																.setImageBitmap(bitmap);
													}
												}
											}, null);
									if (bitmap != null) {
										mBtnback_sy.setImageBitmap(bitmap);
									} else {
										mBtnback_sy
												.setImageResource(R.drawable.set_up_head_portrait);
									}
								} else {
									username.setText("");
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {

						}
					});

		}else{
			username.setText("");
		}
		super.onStart();
	}

}
