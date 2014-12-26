package com.dobi.ui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dobi.R;
import com.dobi.adapter.ShopAdaper;
import com.dobi.common.CommonMethod;
import com.dobi.common.ImageLoader;
import com.dobi.common.NetUtils;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.item.CartItem;
import com.dobi.item.GoodsInfo;
import com.dobi.item.PostInfo;
import com.dobi.item.ShopItem;
import com.dobi.logic.ImageManager;
import com.dobi.view.ShopView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.readystatesoftware.viewbadger.BadgeView;

public class TestActivity extends BaseActivity implements OnClickListener {
	private ListView leftList, rightList;
	private ArrayList<ShopItem> mList;
	private TextView addLine, goods_num_edit, price, totle;
	private ImageView photo, cart, goods_num_plus, goods_num_subs,showGoods,detail;
	private Button buyGoods, size_12, size_15;
	private ImageManager mImageManager;
	// 人物造型
	private ShopView shopView;
	private boolean hasMeasured = false;// 确保只执行一次
	private Intent intent=new Intent();
	/**
	 * 商品展示尺寸
	 */
	private int stageWidth = 0;
	private int stageHeight = 0;
	private SharedPreferences sp;
	private Editor edit;
	private ImageLoader mImageLoader;
	public String bd_id="1",hd_id="1";
	private int cartNum = 1, sizeType = 1;
	// 是否刷新脸型
	boolean isFlash = false;
	// badgeview购物车
	private BadgeView badgeView;
	private GoodsInfo infos = new GoodsInfo();
	private ArrayList<PostInfo> postInfos = new ArrayList<PostInfo>();
	private ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
	private ArrayList<String> images = new ArrayList<String>();
	private LinearLayout showView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hahaha);
		sp = CommonMethod.getPreferences(getApplicationContext());
		mImageManager = new ImageManager();
		mImageLoader=ImageLoader.initLoader(getApplicationContext());
		initView();
		initClick();
		setGoodsImage();
	}

	@Override
	protected void onStart() {
		super.onStart();
		showCardNum();
	}
	@Override
	protected void onRestart() {
		if (isFlash) {
			shopView.setFace();
			shopView.invalidate();
		}
		super.onRestart();
	}

	private void showCardNum() {
		if (sp.getString("uid",null) != null) {
			NetUtils.listCard(sp.getString("uid",null), new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					JSONArray array;
					try {
						JSONObject json = new JSONObject(new String(data));
						if (json.getInt("status") == 1) {
							array = json.getJSONArray("goodsList");
							if (array.length() >= 1) {
								cartNum = array.length();
								badgeView
										.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
								badgeView.setText("+" + cartNum);
								badgeView.show();
							} else {
								cartNum = 0;
								badgeView.hide();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] data,
						Throwable arg3) {

				}
			});
		}

	}

	private void initView() {
		leftList = (ListView) findViewById(R.id.threed_left);
		rightList = (ListView) findViewById(R.id.threed_right);
		shopView = (ShopView) findViewById(R.id.shopView);
		showView = (LinearLayout) findViewById(R.id.showView);
		addLine = (TextView) findViewById(R.id.addLine);
		goods_num_edit = (TextView) findViewById(R.id.goods_num_edit);
		cart = (ImageView) findViewById(R.id.cart);
		photo = (ImageView) findViewById(R.id.photo);
		detail = (ImageView) findViewById(R.id.detail);
		showGoods = (ImageView) findViewById(R.id.showGoods);
		goods_num_plus = (ImageView) findViewById(R.id.goods_num_plus);
		goods_num_subs = (ImageView) findViewById(R.id.goods_num_subs);
		buyGoods = (Button) findViewById(R.id.buyGoods);
		size_12 = (Button) findViewById(R.id.size_12);
		size_15 = (Button) findViewById(R.id.size_15);
		price = (TextView) findViewById(R.id.price);
		totle = (TextView) findViewById(R.id.totle);
		
		badgeView = new BadgeView(TestActivity.this, cart);
		ViewTreeObserver vto = shopView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					// 化妆画面
					stageWidth = shopView.getMeasuredWidth();// 获取到宽度和高度后
					stageHeight = shopView.getMeasuredHeight();
					hasMeasured = true;
					shopView.initView(TestActivity.this, stageWidth,
							stageHeight);
				}
				return true;
			}
		});

		addLine.getPaint().setFlags(
				Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		int width = CommonMethod.getWidth(getApplicationContext());
		LayoutParams params = new LayoutParams(width /4, width);
		leftList.setLayoutParams(params);
		rightList.setLayoutParams(params);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(width, width);
		params2.addRule(RelativeLayout.BELOW,R.id.title);
		showView.setLayoutParams(params2);
		LayoutParams params3 = new LayoutParams(width /2, width);
		shopView.setLayoutParams(params3);
		// rightList load data
		NetUtils.mainFrame(0,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				try {
					mList = new ArrayList<ShopItem>();
					String result = new String(data);
					JSONObject json = new JSONObject(result);
					JSONArray array = (JSONArray) json.get("hdList");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						ShopItem item = new ShopItem();
						item.setUrl(object.getString("url"));
						item.setcTime(object.getString("cTime"));
						item.setId(object.getInt("hd_id"));
						mList.add(item);
					}
//					leftList.setAdapter(new ShopAdaper(mList,
//							TestActivity.this, leftList, shopView, 0));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] data,
					Throwable arg3) {

			}
		});
		// rightList load data
		NetUtils.mainFrame(0,new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				try {
					mList = new ArrayList<ShopItem>();
					String result = new String(data);
					JSONObject json = new JSONObject(result);
					JSONArray array = (JSONArray) json.get("bdList");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						ShopItem item = new ShopItem();
						item.setUrl(object.getString("url"));
						item.setcTime(object.getString("cTime"));
						item.setId(object.getInt("bd_id"));
						mList.add(item);
					}
//					rightList.setAdapter(new ShopAdaper(mList,
//							TestActivity.this, rightList, shopView, 1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] data,
					Throwable arg3) {

			}
		});
	}

	private void initClick() {
		cart.setOnClickListener(this);
		photo.setOnClickListener(this);
		goods_num_plus.setOnClickListener(this);
		goods_num_subs.setOnClickListener(this);
		buyGoods.setOnClickListener(this);
		size_12.setOnClickListener(this);
		size_15.setOnClickListener(this);
		detail.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 购物车
		case R.id.cart:
			if (sp.getString("uid", null) != null) {
				intent.setClass(TestActivity.this, CartActivity.class);
				startActivity(intent);
			}else{
				intent.setClass(TestActivity.this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		// photo
		case R.id.photo:
			Dialog photoDialog = CommonMethod
					.getPlayPhotoDialog(TestActivity.this);
			photoDialog.show();
			break;
		// photo
		case R.id.detail:
			intent.setClass(TestActivity.this, PromptActivity.class);
			intent.putExtra("url", "http://api.do-bi.cn/Api/Home/View/Info/index.html");
			startActivity(intent);
			break;
		case R.id.goods_num_plus:
			cartNum = Integer.parseInt(goods_num_edit.getText().toString()
					.trim());
			cartNum++;
			goods_num_edit.setText(cartNum + "");
			if(sizeType==1){
				totle.setText(299 * cartNum + "");
			}else{
				totle.setText(399 * cartNum + "");
			}
			break;
		// 数量减
		case R.id.goods_num_subs:
			cartNum = Integer.parseInt(goods_num_edit.getText().toString()
					.trim());
			if (cartNum > 1) {
				cartNum--;
				goods_num_edit.setText(cartNum + "");
				if(sizeType==1){
					totle.setText(299 * cartNum + "");
				}else{
					totle.setText(399 * cartNum + "");
				}
			}
			break;
		case R.id.size_12:
			sizeType = 1;
			price.setText("￥299");
			totle.setText(299 * cartNum + "");
			size_12.setTextColor(Color.rgb(0xff, 0, 0));
			size_15.setTextColor(Color.rgb(0, 0, 0));
			break;
		case R.id.size_15:
			sizeType = 2;
			price.setText("￥399");
			totle.setText(399 * cartNum + "");
			size_12.setTextColor(Color.rgb(0, 0, 0));
			size_15.setTextColor(Color.rgb(0xff, 0, 0));
			break;
		case R.id.buyGoods:
			if (sp.getString("uid", null) != null) {
				RequestParams params = new RequestParams();
				params.put("uid", sp.getString("uid", null));
				params.put("hd_id", hd_id);
				params.put("bd_id", bd_id);
				params.put("num", cartNum);
				params.put("size_type", sizeType);
				NetUtils.commitGoods(params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						Log.i("jiang", new String(data));
						try {
							JSONObject json = new JSONObject(new String(data));
							if (json.getInt("status") == 1) {
								// post信息Model
								JSONObject postObject = json
										.getJSONObject("postAddrList");
								postInfos.clear();
								if(postObject.get("postId")!=JSONObject.NULL){
									PostInfo info = new PostInfo();
									info.setPostId(postObject.getString("postId"));
									info.setPostName(postObject
											.getString("postName"));
									info.setPostAddr(postObject
											.getString("postAddr"));
									postInfos.add(info);

									infos.setPostAddrList(postInfos);
								}
								
								
							/*	PostInfo info = new PostInfo();
								info.setPostId(postObject.getString("postId"));
								info.setPostName(postObject
										.getString("postName"));
								info.setPostAddr(postObject
										.getString("postAddr"));
								postInfos.add(info);

								infos.setPostAddrList(postInfos);*/
								// goods信息Model
								JSONArray goodsArray = json
										.getJSONArray("goodsList");
								cartItems.clear();
								for (int i = 0; i < goodsArray.length(); i++) {
									JSONObject obj = (JSONObject) goodsArray
											.get(i);
									CartItem item = new CartItem();
									item.setPid(obj.getString("pid"));
									item.setNum(obj.getInt("num") + "");
									item.setSizeType(obj.getInt("size") + "");
									item.setPrice(obj.getInt("price") + "");
									item.setGoodsImage(obj
											.getString("goodsImage"));
									cartItems.add(item);
								}
								infos.setGoodsList(cartItems);
								// Image信息Model
								JSONObject imageObject;
								JSONArray imageArray;
								try {
									imageObject = json
											.getJSONObject("userImage");
									images.clear();
									for (int i = 0; i < imageObject.length(); i++) {
										String str = new String();
										str = imageObject.getString(i + 1 + "");
										images.add(str);
									}
									infos.setUserImage(images);
								} catch (JSONException e) {
									imageArray = json.getJSONArray("userImage");
									infos.setUserImage(null);
								}
								intent.setClass(TestActivity.this,
										UploadActivity.class);
								intent.putExtra("infos", infos);
								startActivity(intent);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] data,
							Throwable arg3) {
					}
				});
			} else {
				intent.setClass(TestActivity.this, LoginActivity.class);
				startActivity(intent);
			}

			break;
		default:
			break;
		}

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
						ContentResolver cr = TestActivity.this
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
							Intent intent = new Intent(TestActivity.this,
									ShowPicActivity.class);
							intent.putExtra("type", "home");
							TestActivity.this.startActivity(intent);
							bitmap.recycle();
						} catch (FileNotFoundException e) {

						}
					}
				}.run();
				isFlash = true;
				break;
			default:

				break;
			}
		}

	}
	
	public void finishActivity(View view){
		finish();
	}
	public void setGoodsImage(){
		NetUtils.getImageUrl(null,
				hd_id, bd_id,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] data) {
						JSONObject json;
						try {
							json = new JSONObject(new String(data));
							Bitmap bitmap = mImageLoader.downloadImage(
									NetUtils.IMAGE_PREFIX
											+ json.getString("goodsImage"),
									new onImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap,
												String url) {
											if (bitmap != null) {
												showGoods.setImageBitmap(bitmap);
											}
										}
									}, new Point(0, 0));
							if (bitmap != null) {
								showGoods.setImageBitmap(bitmap);
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
	}
	

}
