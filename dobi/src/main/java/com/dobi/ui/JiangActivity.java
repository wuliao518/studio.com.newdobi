package com.dobi.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.adapter.ShopAdaper;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
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
import com.umeng.socialize.utils.Log;

import android.annotation.SuppressLint;
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
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class JiangActivity extends BaseActivity implements OnClickListener{
	private ListView leftList, rightList;
	private int currIndex = 0;
	private ArrayList<ShopItem> mList;
	private TextView addLine, goods_num_edit, price, totle, size,mTV1,mTV2,mTV3;
	private ImageView photo, cart, goods_num_plus, goods_num_subs,showGoods,iv_bottom_line,zoomLarge;
	private Button buyGoods;
	private WebView showDetail;
	private ImageManager mImageManager;
	private String url=null;
	// 人物造型
	private ShopView shopView;
	private boolean hasMeasured = false;// 确保只执行一次
	private Intent intent=new Intent();
	/**
	 * 商品展示尺寸
	 */
	private int stageWidth = 0;
	private int stageHeight = 0;
	private int width, itemWidth,item;
	private SharedPreferences sp;
	private Editor edit;
	private ImageLoader mImageLoader;
	public String bd_id="1",hd_id="1";
	private int localCartNum=1,cartNum = 1, sizeType = 1;
	private ScrollView scroll;
	// 是否刷新脸型
	boolean isFlash = false;
	//badgeview购物车
	private BadgeView badgeView;
	private GoodsInfo infos = new GoodsInfo();
	private ArrayList<PostInfo> postInfos = new ArrayList<PostInfo>();
	private ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
	private ArrayList<String> images = new ArrayList<String>();
	private LinearLayout showView;
	private int type=0;
	//网络加载类型   1是199  0是299系列
	private int netType=0;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gundong);
		sp = CommonMethod.getPreferences(getApplicationContext());
		mImageManager = new ImageManager();
		mImageLoader=ImageLoader.initLoader(getApplicationContext());
		type=getIntent().getExtras().getInt("type");
		initView();
		if(type==ConstValue.GOODS_199){
			netType=2;
			size.setText("8cm");
			price.setText("￥199");
			addLine.setText("市场价￥660");
			totle.setText(199+"");
			sizeType=0;
			bd_id="62";
			hd_id="62";
		}else if(type==ConstValue.GOODS_299){
			netType=0;
			size.setText("12cm");
			price.setText("￥299");
			addLine.setText("市场价￥999");
			totle.setText(299+"");
			sizeType=1;
			bd_id="1";
			hd_id="1";
		}else if(type==ConstValue.GOODS_399){
			netType=1;
			size.setText("15cm");
			price.setText("￥399");
			addLine.setText("市场价￥1330");
			totle.setText(399+"");
			sizeType=2;
			bd_id="31";
			hd_id="31";
		}
		loadData();
		initClick();
		setGoodsImage();
	}

	private void loadDefaultDress() {
		Bitmap hair=null;
		Bitmap body=null;
		if(type==ConstValue.GOODS_199){
			hair=BitmapFactory.decodeResource(getResources(), R.drawable.hair_199);
			body=BitmapFactory.decodeResource(getResources(), R.drawable.body_199);
			shopView.changeHair(hair,false);
			shopView.changeClothes(body,false);
		}else if(type==ConstValue.GOODS_299){
			hair=BitmapFactory.decodeResource(getResources(), R.drawable.hair_299);
			body=BitmapFactory.decodeResource(getResources(), R.drawable.body_299);
			shopView.changeHair(hair,true);
			shopView.changeClothes(body,true);
		}else if(type==ConstValue.GOODS_399){
			hair=BitmapFactory.decodeResource(getResources(), R.drawable.hair_399);
			body=BitmapFactory.decodeResource(getResources(), R.drawable.body_399);
			shopView.changeHair(hair,true);
			shopView.changeClothes(body,true);
		}
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
										.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
								badgeView.setText(cartNum+"");
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

	@SuppressLint({ "SetJavaScriptEnabled", "ClickableViewAccessibility" })
	private void initView() {
		leftList = (ListView) findViewById(R.id.threed_left);
		rightList = (ListView) findViewById(R.id.threed_right);
		shopView = (ShopView) findViewById(R.id.shopView);
		showView = (LinearLayout) findViewById(R.id.showView);
		addLine = (TextView) findViewById(R.id.addLine);
		goods_num_edit = (TextView) findViewById(R.id.goods_num_edit);
		cart = (ImageView) findViewById(R.id.cart);
		photo = (ImageView) findViewById(R.id.photo);
		showGoods = (ImageView) findViewById(R.id.showGoods);
		goods_num_plus = (ImageView) findViewById(R.id.goods_num_plus);
		goods_num_subs = (ImageView) findViewById(R.id.goods_num_subs);
		showDetail = (WebView) findViewById(R.id.showDetail);
		buyGoods = (Button) findViewById(R.id.buyGoods);
		size = (TextView) findViewById(R.id.size);
		price = (TextView) findViewById(R.id.price);
		totle = (TextView) findViewById(R.id.totle);
		scroll = (ScrollView) findViewById(R.id.scroll);
		zoomLarge = (ImageView) findViewById(R.id.zoomLarge);
		WindowManager window = getWindowManager();
		Display display = window.getDefaultDisplay();
		width = display.getWidth();
		itemWidth = width / 3;
		mTV1 = (TextView) findViewById(R.id.tv_tab_1);
		mTV2 = (TextView) findViewById(R.id.tv_tab_2);
		mTV3 = (TextView) findViewById(R.id.tv_tab_3);
		iv_bottom_line = (ImageView) findViewById(R.id.iv_bottom_line);
		LayoutParams param = (LayoutParams) iv_bottom_line.getLayoutParams();
		param.width = itemWidth;
		iv_bottom_line.setLayoutParams(param);
		showDetail.getSettings().setJavaScriptEnabled(true);
		showDetail.loadUrl("file:///android_asset/one.html");
		badgeView = new BadgeView(JiangActivity.this, cart);
		ViewTreeObserver vto = shopView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					// 化妆画面
					stageWidth = shopView.getMeasuredWidth();// 获取到宽度和高度后
					stageHeight = shopView.getMeasuredHeight();
					hasMeasured = true;
					shopView.initView(JiangActivity.this, stageWidth,
							stageHeight);
					loadDefaultDress();
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
		LayoutParams params3 = new LayoutParams(width/2, width);
		shopView.setLayoutParams(params3);
		shopView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				shopView.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		scroll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scroll.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	private void loadData() {
		// rightList load data
		NetUtils.mainFrame(netType,new AsyncHttpResponseHandler() {
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
					leftList.setAdapter(new ShopAdaper(mList,
							JiangActivity.this, leftList, shopView, 0));
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
		NetUtils.mainFrame(netType,new AsyncHttpResponseHandler() {
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
					rightList.setAdapter(new ShopAdaper(mList,
							JiangActivity.this, rightList, shopView, 1));
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
		showGoods.setOnClickListener(this);
		mTV1.setOnClickListener(this);
		mTV2.setOnClickListener(this);
		mTV3.setOnClickListener(this);
		zoomLarge.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 购物车
		case R.id.cart:
			if (sp.getString("uid", null) != null) {
				intent.setClass(JiangActivity.this, CartActivity.class);
				startActivity(intent);
			}else{
				intent.setClass(JiangActivity.this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		// photo
		case R.id.photo:
			Dialog photoDialog = CommonMethod
					.getPlayPhotoDialog(JiangActivity.this);
			photoDialog.show();
			break;
		/*case R.id.showGoods:
			
			
			break;*/
		case R.id.goods_num_plus:
			localCartNum = Integer.parseInt(goods_num_edit.getText().toString()
					.trim());
			localCartNum++;
			goods_num_edit.setText(localCartNum + "");
			if(sizeType==1){
				totle.setText(299 * localCartNum + "");
			}else if(sizeType==2){
				totle.setText(399 * localCartNum + "");
			}else if(sizeType==0){
				totle.setText(199 * localCartNum + "");
			}
			break;
		// 数量减
		case R.id.goods_num_subs:
			localCartNum = Integer.parseInt(goods_num_edit.getText().toString()
					.trim());
			if (localCartNum > 1) {
				localCartNum--;
				goods_num_edit.setText(localCartNum + "");
				if(sizeType==1){
					totle.setText(299 * localCartNum + "");
				}else if(sizeType==2){
					totle.setText(399 * localCartNum + "");
				}else if(sizeType==0){
					totle.setText(199 * localCartNum + "");
				}
			}
			break;
		case R.id.buyGoods:
			if (sp.getString("uid", null) != null) {
				File file=shopView.getSavePerson();
				RequestParams params = new RequestParams();
				params.put("uid", sp.getString("uid", null));
				params.put("hd_id", hd_id);
				params.put("bd_id", bd_id);
				params.put("num", localCartNum);
				params.put("size_type", sizeType);
				try {
					params.put("user_model", file);
				} catch (FileNotFoundException e1) {
				}
				NetUtils.commitGoods(params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						try {
							Log.e("jiang", new String(data));
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
								intent.setClass(JiangActivity.this,
										UploadActivity.class);
								intent.putExtra("infos", infos);
								startActivity(intent);
							}else{
								Toast.makeText(getApplicationContext(), json.getString("message"), 0).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] data,
							Throwable arg3) {
						Toast.makeText(getApplicationContext(), "网络错误请重试。", 0).show();
					}
				});
			} else {
				intent.setClass(JiangActivity.this, LoginActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.tv_tab_1:
			item = 0;
			showDetail.loadUrl("file:///android_asset/one.html");
			mTV1.setTextColor(getResources().getColor(R.color.pay_red));
			mTV1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mTV2.setTextColor(getResources().getColor(R.color.black));
			mTV3.setTextColor(getResources().getColor(R.color.black));
			break;
		case R.id.tv_tab_2:
			item = 1;
			showDetail.loadUrl("file:///android_asset/three.html");
			mTV2.setTextColor(getResources().getColor(R.color.pay_red));
			mTV2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mTV1.setTextColor(getResources().getColor(R.color.black));
			mTV3.setTextColor(getResources().getColor(R.color.black));
			break;
		case R.id.tv_tab_3:
			item = 2;
			showDetail.loadUrl("file:///android_asset/two.html");
			mTV3.setTextColor(getResources().getColor(R.color.pay_red));
			mTV3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mTV1.setTextColor(getResources().getColor(R.color.black));
			mTV2.setTextColor(getResources().getColor(R.color.black));
			break;
		case R.id.zoomLarge:
		case R.id.showGoods:
			Intent intent=new Intent();
			intent.setClass(JiangActivity.this, ZoomLargeActivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
			break;
		default:
			break;
		}
		
		Animation animation = new TranslateAnimation(currIndex * itemWidth, item
				* itemWidth, 0, 0);
		currIndex = item;
		animation.setFillAfter(true);
		animation.setDuration(300);
		iv_bottom_line.startAnimation(animation);
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
						ContentResolver cr = JiangActivity.this
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
							Intent intent = new Intent(JiangActivity.this,
									ShowPicActivity.class);
							intent.putExtra("type", "home");
							JiangActivity.this.startActivity(intent);
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
							url=NetUtils.IMAGE_PREFIX+ json.getString("goodsBigImage");
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
