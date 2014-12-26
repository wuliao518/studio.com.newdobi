package com.dobi.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ImageLoader;
import com.dobi.common.NetUtils;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.item.CartItem;
import com.dobi.item.OrderInfo;
import com.dobi.item.PayModel;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class PayMomentActivity extends BaseActivity implements OnClickListener {
	private ImageView mImage;
	private int currIndex = 0;
	private int width, itemWidth;
	private SharedPreferences sp;
	private TextView mTV1, mTV2, showText;
	private ListView mListView;
	private final static int PAGER_NUMBER = 5;
	private LayoutInflater mInflater;
	private boolean isAddEmpty = true;
	private ArrayList<PayModel> payList;
	private MyAdapterOne adapter;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.haha_main_activity);
		mInflater = LayoutInflater.from(getApplicationContext());
		sp = CommonMethod.getPreferences(getApplicationContext());
		initView();
		if (sp.getString("uid", null) != null) {
			loadData(0);
		}
	}

	private void loadData(final int type) {
		dialog.show();
		payList = new ArrayList<PayModel>();
		RequestParams params = new RequestParams();
		params.put("start", 0);
		params.put("num", PAGER_NUMBER);
		params.put("uid", sp.getString("uid", null));
		params.put("type", type);
		NetUtils.getOrderList(params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				try {
					JSONObject json = new JSONObject(new String(data));
					if (json.getInt("status") == 1) {
						JSONArray orderArray = json.getJSONArray("orderList");
						if (orderArray.length() > 0) {
							for (int i = 0; i < orderArray.length(); i++) {
								JSONObject object = orderArray.getJSONObject(i);
								PayModel model = new PayModel();
								model.setOrderId(object.getString("orderId"));
								model.setOrderNum(object.getString("order_num"));
								model.setGoodsNum(object.getString("goods_num"));
								model.setPid(object.getString("pid"));
								model.setTotle(object.getString("total"));
								model.setcTime(object.getString("cTime"));
								model.setPostAddr(object.getString("postAddr"));
								model.setStatus(object.getString("status"));
								ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
								JSONArray array = object
										.getJSONArray("goodsList");
								int length = array.length();
								for (int j = 0; j < length; j++) {
									JSONObject cartObj = array.getJSONObject(j);
									CartItem cartItem = new CartItem();
									cartItem.setBdId(cartObj.getString("hd_id"));
									cartItem.setHdId(cartObj.getString("bd_id"));
									cartItem.setNum(cartObj.getString("num"));
									cartItem.setPid(cartObj.getString("pid"));
									cartItem.setSizeType(cartObj
											.getString("size"));
									cartItem.setGoodsImage(cartObj
											.getString("goodsImage"));
									cartItem.setPrice(cartObj
											.getString("price"));
									cartItems.add(cartItem);
								}
								model.setCartItems(cartItems);
								payList.add(model);
							}
						} else {
							if (isAddEmpty) {
								isAddEmpty = false;
								View emptyView = mInflater.inflate(
										R.layout.empty_pay, null);
								emptyView.setVisibility(View.GONE);
								((ViewGroup) mListView.getParent())
										.addView(emptyView);
								mListView.setEmptyView(emptyView);
							}

						}
						adapter = new MyAdapterOne(type);
						mListView.setAdapter(adapter);
						dialog.dismiss();
					}

				} catch (JSONException e) {
					dialog.dismiss();
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				dialog.dismiss();
			}
		});

	}

	private void initView() {
		WindowManager window = getWindowManager();
		Display display = window.getDefaultDisplay();
		width = display.getWidth();
		itemWidth = width / 2;
		dialog = CommonMethod.showMyDialog(PayMomentActivity.this);
		mTV1 = (TextView) findViewById(R.id.tv_tab_1);
		mTV2 = (TextView) findViewById(R.id.tv_tab_2);
		showText = (TextView) findViewById(R.id.showText);
		mListView = (ListView) findViewById(R.id.lv_one);
		mTV1.setOnClickListener(this);
		mTV2.setOnClickListener(this);
		mImage = (ImageView) findViewById(R.id.iv_bottom_line);
		LayoutParams params = (LayoutParams) mImage.getLayoutParams();
		params.width = itemWidth;
		mImage.setLayoutParams(params);

	}

	public void finishActivity(View v) {
		finish();
	}

	@Override
	public void onClick(View v) {
		Animation animation = null;
		int item = 0;
		switch (v.getId()) {
		case R.id.tv_tab_1:
			item = 0;
			mTV1.setTextColor(getResources().getColor(R.color.pay_red));
			mTV1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mTV2.setTextColor(getResources().getColor(R.color.black));
			showText.setText("未支付");
			if (sp.getString("uid", null) != null) {
				loadData(0);
			}
			break;
		case R.id.tv_tab_2:
			item = 1;
			mTV2.setTextColor(getResources().getColor(R.color.pay_red));
			mTV2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			mTV1.setTextColor(getResources().getColor(R.color.black));
			showText.setText("已支付");
			if (sp.getString("uid", null) != null) {
				loadData(1);
			}
			;
			break;
		default:
			break;
		}
		animation = new TranslateAnimation(currIndex * itemWidth, item
				* itemWidth, 0, 0);
		currIndex = item;
		animation.setFillAfter(true);
		animation.setDuration(300);
		mImage.startAnimation(animation);
	}

	private class MyAdapterOne extends BaseAdapter {
		private int type;

		public MyAdapterOne(int type) {
			this.type = type;
		}

		@Override
		public int getCount() {
			return payList.size();
		}

		@Override
		public Object getItem(int position) {
			return payList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final PayModel payModel = payList.get(position);
			LinearLayout linearLayout = new LinearLayout(
					getApplicationContext());
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT);
			linearLayout.setLayoutParams(params);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			if (type == 0) {
				View header = mInflater.inflate(R.layout.nopay_item_header,
						null);
				linearLayout.addView(header);

				TextView cancle = (TextView) header.findViewById(R.id.cancle);
				TextView orderNum = (TextView) header
						.findViewById(R.id.orderNum);
				TextView commit = (TextView) header.findViewById(R.id.commit);
				orderNum.setText(payModel.getOrderNum());
				cancle.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						RequestParams params = new RequestParams();
						params.put("uid", sp.getString("uid", null));
						params.put("orderId", payModel.getOrderId());
						NetUtils.deleteOrder(params,
								new AsyncHttpResponseHandler() {
									@Override
									public void onSuccess(int arg0,
											Header[] arg1, byte[] data) {
										payList.remove(position);
										adapter.notifyDataSetChanged();
									}

									@Override
									public void onFailure(int arg0,
											Header[] arg1, byte[] arg2,
											Throwable arg3) {
									}
								});
					}
				});
				commit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						OrderInfo info = new OrderInfo();
						info.setBid(payModel.getOrderNum());
						info.setcTime(payModel.getcTime());
						info.setGoodsNum(payModel.getGoodsNum());
						info.setOrderId(payModel.getOrderId());
						info.setPostAddr(payModel.getPostAddr());
						info.setTotal(payModel.getTotle());
						Intent intent = new Intent();
						intent.setClass(PayMomentActivity.this,
								OrderCommit.class);
						intent.putExtra("order", info);
						PayMomentActivity.this.startActivity(intent);
					}
				});
			} else if (type == 1) {
				View header = mInflater
						.inflate(R.layout.ipay_item_header, null);
				TextView showFlow = (TextView) header
						.findViewById(R.id.showFlow);
				TextView showStatus = (TextView) header
						.findViewById(R.id.showStatus);
				TextView cTime = (TextView) header.findViewById(R.id.cTime);
				TextView orderNum=(TextView) header.findViewById(R.id.orderNum);
				orderNum.setText(payModel.getOrderNum());
				linearLayout.addView(header);
				showStatus.setText(convertIntToStatus(Integer.parseInt(payModel
						.getStatus())));
				cTime.setText(convert(Long.parseLong(payModel.getcTime())));
				showFlow.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent=new Intent();
						intent.setClass(PayMomentActivity.this, FlowActivity.class);
						intent.putExtra("flowType", Integer.parseInt(payModel.getStatus()));
						PayMomentActivity.this.startActivity(intent);
					}
				});
			}

			for (int i = 0; i < payModel.getCartItems().size(); i++) {
				View view = mInflater.inflate(R.layout.item_nonpayment, null);
				linearLayout.addView(view);
				LinearLayout mLayout01 = (LinearLayout) view
						.findViewById(R.id.mLayout01);
				if (type == 0) {
					mLayout01.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							OrderInfo info = new OrderInfo();
							info.setBid(payModel.getOrderNum());
							info.setcTime(payModel.getcTime());
							info.setGoodsNum(payModel.getGoodsNum());
							info.setOrderId(payModel.getOrderId());
							info.setPostAddr(payModel.getPostAddr());
							info.setTotal(payModel.getTotle());
							Intent intent = new Intent();
							intent.setClass(PayMomentActivity.this,
									OrderCommit.class);
							intent.putExtra("order", info);
							PayMomentActivity.this.startActivity(intent);
						}
					});
				} else {
					mLayout01.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							
						}
					});
				}
				CartItem cartItem = payModel.getCartItems().get(i);
				ImageView goodsImage = (ImageView) view
						.findViewById(R.id.goodsImage);
				TextView goodsSize = (TextView) view
						.findViewById(R.id.goodsSize);
				TextView goodsPrice = (TextView) view
						.findViewById(R.id.goodsPrice);
				String path = NetUtils.IMAGE_PREFIX + cartItem.getGoodsImage();
				goodsImage.setTag(path);
				goodsSize.setText("3D玩偶("
						+ convertIntToize(Integer.parseInt(cartItem
								.getSizeType())) + ")");
				goodsPrice.setText(Integer.parseInt(cartItem.getPrice())
						/ Integer.parseInt(cartItem.getNum()) + "x"
						+ cartItem.getNum());
				Bitmap bitmap = ImageLoader.initLoader(PayMomentActivity.this)
						.downloadImage(path, new onImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								ImageView image = (ImageView) mListView
										.findViewWithTag(url);
								if (image != null && bitmap != null) {
									image.setImageBitmap(bitmap);
								}
							}
						}, new Point());
				if (bitmap != null) {
					goodsImage.setImageBitmap(bitmap);
				}
			}
			View footer = mInflater.inflate(R.layout.ipay_item_footer, null);
			linearLayout.addView(footer);
			TextView totlePrice = (TextView) footer
					.findViewById(R.id.totlePrice);
			totlePrice.setText(payModel.getTotle());
			return linearLayout;
		}

	}

	public String convertIntToize(int type) {
		switch (type) {
		case 0:
			return "8cm";
		case 1:
			return "12cm";
		case 2:
			return "15cm";

		default:
			return null;
		}
	}

	public String convertIntToStatus(int status) {
		switch (status) {
		case 0:
			return "订单确认";
		case 1:
			return "图片确认";
		case 2:
			return "建模中";
		case 3:
			return "打印中";
		case 4:
			return "发送";
		case 7:
			return "订单完成";
		case 8:
			return "图片驳回";

		default:
			return "未知状态";
		}
	}

	// 时间转换
	public String convert(long mill) {
		Date date = new Date(mill * 1000);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}
}
