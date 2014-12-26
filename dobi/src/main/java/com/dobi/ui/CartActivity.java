package com.dobi.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.common.NetUtils;
import com.dobi.item.CartItem;
import com.dobi.item.GoodsInfo;
import com.dobi.item.PostInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CartActivity extends BaseActivity implements OnScrollListener {
	private SharedPreferences sp;
	private Editor edit;
	private ListView mListView;
	private LayoutInflater mInflater;
	private TextView totlePrice;
	private List<CartItem> cartItems = new ArrayList<CartItem>();
	private Map<String, Boolean> selectMap = new HashMap<String, Boolean>();
	private GoodsInfo infos = new GoodsInfo();
	private ArrayList<PostInfo> postInfos = new ArrayList<PostInfo>();
	private ArrayList<CartItem> commitItems = new ArrayList<CartItem>();
	private ArrayList<String> images = new ArrayList<String>();

	/**
	 * Image 下载器
	 */
	private ImageLoader mImageDownLoader;
	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 */
	private boolean isFirstEnter = true;
	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;
	/**
	 * 一屏中所有item的个数
	 */
	private int mVisibleItemCount;
	private Dialog dialog;
	private ImageView commit;
	private float totle = 0.0f;
	private CheckBox selectAll;
	private boolean isAddEmpty=true;
	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		mListView = (ListView) findViewById(R.id.list_cart);
		totlePrice = (TextView) findViewById(R.id.totlePrice);
		commit = (ImageView) findViewById(R.id.order_commit);
		selectAll = (CheckBox) findViewById(R.id.selectAll);
		totlePrice.setText(totle + "");
		commit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelected()) {
					commitCart();
				} else {
					Toast.makeText(getApplicationContext(), "请选择商品", 0).show();
				}
			}
		});

		selectAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int viewLength = mListView.getChildCount();
				int length = cartItems.size();
				totle = 0.0f;
				if (selectAll.isChecked()) {
					for (int i = 0; i < viewLength; i++) {
						((CheckBox) (mListView.getChildAt(i)
								.findViewById(R.id.check_goods)))
								.setChecked(true);
					}
					for (int i = 0; i < length; i++) {
						CartItem item = cartItems.get(i);
						totle = totle + Integer.parseInt(item.getPrice());
						selectMap.put(item.getPid(), true);
					}
				} else {
					for (int i = 0; i < viewLength; i++) {
						((CheckBox) (mListView.getChildAt(i)
								.findViewById(R.id.check_goods)))
								.setChecked(false);
					}
					for (int i = 0; i < length; i++) {
						CartItem item = cartItems.get(i);
						selectMap.put(item.getPid(), false);
					}
				}
				totlePrice.setText(totle + "");
			}
		});

		// selectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// int viewLength = mListView.getChildCount();
		// int length = cartItems.size();
		// totle = 0.0f;
		// if (isChecked) {
		// for (int i = 0; i < viewLength; i++) {
		// ((CheckBox) (mListView.getChildAt(i)
		// .findViewById(R.id.check_goods)))
		// .setChecked(true);
		// }
		// for (int i = 0; i < length; i++) {
		// CartItem item = cartItems.get(i);
		// totle = totle + Integer.parseInt(item.getPrice());
		// selectMap.put(item.getPid(), true);
		// }
		// } else {
		// for (int i = 0; i < viewLength; i++) {
		// ((CheckBox) (mListView.getChildAt(i)
		// .findViewById(R.id.check_goods)))
		// .setChecked(false);
		// }
		// for (int i = 0; i < length; i++) {
		// CartItem item = cartItems.get(i);
		// selectMap.put(item.getPid(), false);
		// }
		// }
		// totlePrice.setText(totle + "");
		// }
		// });
		mListView.setOnScrollListener(this);
		sp = CommonMethod.getPreferences(getApplicationContext());
		mInflater = LayoutInflater.from(CartActivity.this);
		mImageDownLoader = ImageLoader.initLoader(CartActivity.this);
		edit = sp.edit();
		dialog = CommonMethod.showMyDialog(CartActivity.this);
		loadCart();
	}

	protected boolean isSelected() {
		if (selectMap == null || selectMap.size() < 1) {
			return false;
		}
		int length = selectMap.size();
		for (int i = 0; i < length; i++) {
			CartItem item = cartItems.get(i);
			if (selectMap.get(item.getPid())) {
				return true;
			}
		}
		return false;

	}

	protected boolean isSelectedAll() {
		int length = selectMap.size();
		for (int i = 0; i < length; i++) {
			CartItem item = cartItems.get(i);
			if (!selectMap.get(item.getPid())) {
				return false;
			}
		}
		return true;
	}

	private void loadCart() {
		NetUtils.listCard(sp.getString("uid", null),
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						Log.i("jiang", new String(data));
						JSONArray array;
						try {
							JSONObject json = new JSONObject(new String(data));
							if (json.getInt("status") == 1) {
								array = json.getJSONArray("goodsList");
								if(array.length()>0){
									for (int i = 0; i < array.length(); i++) {
										JSONObject obj = (JSONObject) array.get(i);
										CartItem cartItem = new CartItem();
										obj.getString("price");
										obj.getString("num");
										obj.getString("size");
										cartItem.setBdId(obj.getString("hd_id"));
										cartItem.setHdId(obj.getString("bd_id"));
										cartItem.setNum(obj.getString("num"));
										cartItem.setPid(obj.getString("pid"));
										cartItem.setSizeType(obj.getString("size"));
										cartItem.setGoodsImage(obj
												.getString("goodsImage"));
										cartItem.setPrice(obj.getString("price"));
										selectMap.put(obj.getString("pid"), false);
										cartItems.add(cartItem);
									}
								}else{
									if (isAddEmpty) {
										isAddEmpty = false;
										View emptyView = mInflater.inflate(
												R.layout.empty_cart, null);
										emptyView.setVisibility(View.GONE);
										TextView goShop=(TextView) emptyView.findViewById(R.id.goShop);
										goShop.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												CartActivity.this.finish();
											}
										});
										((ViewGroup) mListView.getParent())
												.addView(emptyView);
										mListView.setEmptyView(emptyView);
									}
								}
								
								mListView.setAdapter(new CartAdapter());
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

	private class CartAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cartItems.size();
		}

		@Override
		public Object getItem(int position) {
			return cartItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder viewHolder;
			final CartItem cartItem = cartItems.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_cart, null);
				viewHolder.image = (ImageView) convertView
						.findViewById(R.id.goods_image);
				viewHolder.button = (Button) convertView
						.findViewById(R.id.edit_goods);
				viewHolder.mShow = (LinearLayout) convertView
						.findViewById(R.id.mShow);
				viewHolder.mDelete = (LinearLayout) convertView
						.findViewById(R.id.mDelete);
				viewHolder.delete = (ImageView) convertView
						.findViewById(R.id.goods_delete);
				viewHolder.checkBox = (CheckBox) convertView
						.findViewById(R.id.check_goods);
				viewHolder.goodsNum = (TextView) convertView
						.findViewById(R.id.goodsNum);
				viewHolder.goodsPrice = (TextView) convertView
						.findViewById(R.id.goodsPrice);
				viewHolder.editNum = (EditText) convertView
						.findViewById(R.id.editNum);
				viewHolder.goodsSize = (TextView) convertView
						.findViewById(R.id.goodsSize);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String path = NetUtils.IMAGE_PREFIX + cartItem.getGoodsImage();
			viewHolder.image.setTag(path + position);
			String sizeType = cartItem.getSizeType();
			if (sizeType.equals("0")) {
				viewHolder.goodsSize.setText("尺寸:8cm");
			} else if (sizeType.equals("1")) {
				viewHolder.goodsSize.setText("尺寸:12cm");
			} else if (sizeType.equals("2")) {
				viewHolder.goodsSize.setText("尺寸:15cm");
			}
			viewHolder.button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (viewHolder.isShowDelete
							&& !viewHolder.editNum.getText().toString()
									.equals("")) {
						hideInput(viewHolder.editNum);
						viewHolder.isShowDelete = false;
						viewHolder.button.setText("编辑");
						viewHolder.mShow.setVisibility(View.VISIBLE);
						viewHolder.mDelete.setVisibility(View.GONE);
						RequestParams params = new RequestParams();
						params.put("pid", cartItem.getPid());
						params.put("num", viewHolder.editNum.getText()
								.toString());
						NetUtils.changeGoodsNum(params,
								new AsyncHttpResponseHandler() {
									@Override
									public void onSuccess(int arg0,
											Header[] arg1, byte[] data) {
										JSONObject json;
										try {
											json = new JSONObject(new String(data));
											if(json.getInt("status")==1){
												CartItem item = cartItems.get(position);
												int itemPrice=Integer.parseInt(item.getPrice())/Integer.parseInt(item.getNum());
												if(selectMap.get(item.getPid())){
													totle=totle-Integer.parseInt(item.getPrice());
													totle=totle+itemPrice*Integer.parseInt(viewHolder.editNum.getText().toString());
													totlePrice.setText(totle+"");
												}
												viewHolder.goodsNum.setText("X"
														+ Integer.parseInt(viewHolder.editNum.getText()
																.toString().trim()));
												item.setNum(viewHolder.editNum.getText().toString());
												item.setPrice(itemPrice*Integer.parseInt(viewHolder.editNum.getText().toString())+"");
											}else{
												Toast.makeText(getApplicationContext(),
														json.getString("message"), 0).show();
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
										
									}

									@Override
									public void onFailure(int arg0,
											Header[] arg1, byte[] arg2,
											Throwable arg3) {

									}
								});
					} else {
						viewHolder.isShowDelete = true;
						viewHolder.button.setText("完成");
						viewHolder.mShow.setVisibility(View.GONE);
						viewHolder.mDelete.setVisibility(View.VISIBLE);
					}
				}
			});
			// 商品数量
			viewHolder.goodsNum.setText("X" + cartItem.getNum());
			viewHolder.editNum.setText(cartItem.getNum());
			// 商品价格
			viewHolder.goodsPrice.setText("￥"
					+ Integer.parseInt(cartItem.getPrice())
					/ Integer.parseInt(cartItem.getNum()));
			// 删除商品按钮
			viewHolder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.show();
					RequestParams params = new RequestParams();
					params.put("pid", cartItems.get(position).getPid());
					NetUtils.deleteCart(params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] data) {
							synchronized (CartActivity.this) {
								Log.e("jiang", new String(data));
								dialog.dismiss();
								if (viewHolder.checkBox.isChecked()) {
									CartItem item = cartItems.get(position);
									totle = totle
											- Integer.parseInt(item.getPrice());
									totlePrice.setText(totle + "");
								} else {
									if (isSelectedAll()) {
										selectAll.setChecked(true);
									}
								}
								selectMap.remove(cartItems.get(position)
										.getPid());
								cartItems.remove(position);
								if(cartItems.size()==0){
									View emptyView = mInflater.inflate(
											R.layout.empty_cart, null);
									emptyView.setVisibility(View.GONE);
									((ViewGroup) mListView.getParent())
											.addView(emptyView);
									TextView goShop=(TextView) emptyView.findViewById(R.id.goShop);
									goShop.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											CartActivity.this.finish();
										}
									});
									mListView.setEmptyView(emptyView);
								}
								mListView.setAdapter(new CartAdapter());
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] data, Throwable arg3) {
						}
					});
				}
			});
			// 选中checkbox按钮
			if (selectMap.get(cartItem.getPid())) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
			viewHolder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selectMap.get(cartItem.getPid())) {
						selectMap.put(cartItem.getPid(), false);
						CartItem item = cartItems.get(position);
						totle = totle - Integer.parseInt(item.getPrice());
						selectAll.setChecked(false);
					} else {
						selectMap.put(cartItem.getPid(), true);
						CartItem item = cartItems.get(position);
						totle = totle + Integer.parseInt(item.getPrice());
						if (isSelectedAll()) {
							selectAll.setChecked(true);
						}
					}
					totlePrice.setText(totle + "");

				}
			});
			Bitmap bitmap = mImageDownLoader.showCacheBitmap(
					path.replaceAll("[^\\w]", ""), new Point(80, 80), false);
			if (bitmap != null) {
				viewHolder.image.setImageBitmap(bitmap);
			} else {
				Bitmap bitmapL = BitmapFactory.decodeResource(
						CartActivity.this.getResources(),
						R.drawable.default_load);
				viewHolder.image.setImageBitmap(bitmapL);
			}
			return convertView;
		}
	}

	public static class ViewHolder {
		public ImageView image, delete;
		public Button button;
		public LinearLayout mShow, mDelete;
		boolean isShowDelete = false;
		public TextView goodsNum, goodsPrice, goodsSize;
		public CheckBox checkBox;
		public EditText editNum;
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTask() {
		mImageDownLoader.cancelTask();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelTask();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
	}

	private void showImage(int firstVisibleItem, int visibleItemCount) {
		if (visibleItemCount > cartItems.size()) {
			visibleItemCount = cartItems.size();
		}
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			CartItem item = cartItems.get(i);
			final String mImageUrl = NetUtils.IMAGE_PREFIX
					+ item.getGoodsImage();
			final ImageView mImageView = (ImageView) mListView
					.findViewWithTag(mImageUrl + i);
			if (mImageDownLoader != null) {
				mImageDownLoader.downloadImage(mImageUrl,
						new onImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (mImageView != null && bitmap != null) {
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									bitmap.compress(Bitmap.CompressFormat.PNG,
											100, baos);
									InputStream isBm = new ByteArrayInputStream(
											baos.toByteArray());
									Bitmap bitmapL = mImageDownLoader
											.decodeThumbBitmapForInputStream(
													isBm, 80, 80);
									bitmapL = bitmapL == null ? bitmap
											: bitmapL;
									mImageView.setImageBitmap(bitmapL);
								} else {
									if (mImageView != null) {
										mImageView.setOnClickListener(null);
									}
								}
							}

						}, new Point(80, 80));
			}
		}
	}

	// 隐藏软键盘
	public void hideInput(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void finishActivity(View view) {
		finish();
	}

	// 提交购物车

	private void commitCart() {
		RequestParams params = new RequestParams();
		params.put("uid", sp.getString("uid", null));
		// 拼接订单串
		StringBuffer buffer = new StringBuffer();
		int length = selectMap.size();
		for (int i = 0; i < length; i++) {
			CartItem item = cartItems.get(i);
			if (selectMap.get(item.getPid())) {
				buffer.append(item.getPid() + "-");
			}
		}
		buffer.delete(buffer.length() - 1, buffer.length());
		params.put("pid", buffer.toString());
		NetUtils.cartCommit(params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				try {
					JSONObject json = new JSONObject(new String(data));
					if (json.getInt("status") == 1) {
						// post信息Model
						JSONObject postObject = json
								.getJSONObject("postAddrList");
						postInfos.clear();
						if (JSONObject.NULL != postObject.get("postId")) {
							PostInfo info = new PostInfo();
							info.setPostId(postObject.getString("postId"));
							info.setPostName(postObject.getString("postName"));
							info.setPostAddr(postObject.getString("postAddr"));
							postInfos.add(info);
							infos.setPostAddrList(postInfos);
						} else {
							infos.setPostAddrList(null);
						}
						// goods信息Model
						JSONArray goodsArray = json.getJSONArray("goodsList");
						commitItems.clear();
						for (int i = 0; i < goodsArray.length(); i++) {
							JSONObject obj = (JSONObject) goodsArray.get(i);
							CartItem item = new CartItem();
							item.setPid(obj.getString("pid"));
							item.setNum(obj.getInt("num") + "");
							item.setSizeType(obj.getInt("size") + "");
							item.setPrice(obj.getString("price") + "");
							item.setGoodsImage(obj.getString("goodsImage") + "");
							commitItems.add(item);
						}
						infos.setGoodsList(commitItems);
						// Image信息Model
						JSONObject imageObject;
						JSONArray imageArray;
						try {
							imageObject = json.getJSONObject("userImage");
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
						Intent intent = new Intent();
						intent.setClass(CartActivity.this, UploadActivity.class);
						intent.putExtra("infos", infos);
						startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

			}
		});
	}

}
