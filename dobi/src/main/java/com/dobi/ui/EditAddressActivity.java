package com.dobi.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.NetUtils;
import com.dobi.item.PostInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class EditAddressActivity extends BaseActivity {
	private ListView mListView;
	private SharedPreferences sp;
	private List<PostInfo> postInfos;
	private AddressAdapter adapter;
	private LayoutInflater mInflater;
	private int prePosition = 0;
	// 是否将第一个地址标记为默认
	private Boolean isDefault = false;
	private Intent intent;
	private TextView add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_address);
		intent = new Intent();
	}

	@Override
	protected void onStart() {
		super.onStart();
		prePosition = 0;
		initView();
	}
	private void initView() {
		sp = CommonMethod.getPreferences(getApplicationContext());
		mListView = (ListView) findViewById(R.id.list_address);
		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(EditAddressActivity.this, AddActivity.class);
				startActivity(intent);
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				PostInfo post = (PostInfo) adapter.getItem(position);
				RequestParams params = new RequestParams();
				params.put("postId", postInfos.get(position).getPostId());
				params.put("uid", sp.getString("uid", null));
				NetUtils.setDefaultAddress(params,
						new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								if (prePosition != position) {
									if (prePosition != -1) {
										View preView = (View) mListView
												.getChildAt(prePosition);
										TextView preTv = (TextView) preView
												.findViewById(R.id.item_postAddress);
										String preAddress = preTv.getText()
												.toString();
										if (preAddress.startsWith("[默认]")) {
											preAddress = preAddress.replace(
													"[默认]", "");
											preTv.setText(preAddress);
											preTv.setCompoundDrawables(null,
													null, null, null);
										}
									}
									prePosition = position;
									TextView tv = (TextView) view
											.findViewById(R.id.item_postAddress);
									String address = tv.getText().toString();
									tv.setText("[默认]" + address);
									Drawable drawable = getResources()
											.getDrawable(
													R.drawable.shipping_address_selected);
									drawable.setBounds(0, 0, tv.getHeight(),
											tv.getHeight());
									tv.setCompoundDrawables(null, null,
											drawable, null);
								}
							}

							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {

							}
						});
			}
		});
		loadAddress();
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final CharSequence[] items = { "删除", "修改", "取消" };
				AlertDialog.Builder builder = new AlertDialog.Builder(
						EditAddressActivity.this);
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							PostInfo info = postInfos.get(position);
							RequestParams params = new RequestParams();
							params.put("uid", sp.getString("uid", null));
							params.put("postId", info.getPostId());
							NetUtils.deleteAddress(params,
									new AsyncHttpResponseHandler() {
										@Override
										public void onSuccess(int arg0,
												Header[] arg1, byte[] arg2) {
											postInfos.remove(position);
											loadAddress();
										}

										@Override
										public void onFailure(int arg0,
												Header[] arg1, byte[] arg2,
												Throwable arg3) {

										}
									});
						} else if (item == 1) {
							NetUtils.getDescAddress(sp.getString("uid", null),
									postInfos.get(position).getPostId(),
									new AsyncHttpResponseHandler() {
										@Override
										public void onSuccess(int arg0,
												Header[] arg1, byte[] data) {
											Log.e("jiang", new String(data));
											PostInfo info = postInfos
													.get(position);
											try {
												JSONObject json = new JSONObject(
														new String(data));
												if (json.getInt("status") == 1) {
													JSONObject object = json
															.getJSONObject("postInfo");
													info.setProvince(object
															.getString("province"));
													info.setCity(object
															.getString("city"));
													info.setZone(object
															.getString("zone"));
													info.setAddr(object
															.getString("addr"));
													info.setPostNum(object
															.getString("postNum"));
													info.setpMobile(object
															.getString("pMobile"));
													info.setpTel(object
															.getString("pTel"));
													intent.setClass(
															EditAddressActivity.this,
															AddActivity.class);
													intent.putExtra(
															"info",
															postInfos
																	.get(position));
													EditAddressActivity.this
															.startActivity(intent);

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
							return;
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return false;
			}
		});

		mInflater = LayoutInflater.from(getApplicationContext());
	}

	private void loadAddress() {
		postInfos = new ArrayList<PostInfo>();
		NetUtils.listAddress(sp.getString("uid", null),
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						Log.i("jiang", new String(data));
						try {
							JSONObject json = new JSONObject(new String(data));
							JSONArray array = (JSONArray) json.get("postInfo");
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								if (convertStringToBoolean(object
										.getString("isDefault"))) {
									PostInfo info = new PostInfo();
									info.setPostId(object.getString("postId"));
									info.setPostName(object
											.getString("postName"));
									info.setPostAddr(object
											.getString("postAddr"));
									info.setIsDefault(convertStringToBoolean(object
											.getString("isDefault")));
									postInfos.add(info);
									isDefault = true;
								}
							}
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								if (!convertStringToBoolean(object
										.getString("isDefault"))) {
									PostInfo info = new PostInfo();
									info.setPostId(object.getString("postId"));
									info.setPostName(object
											.getString("postName"));
									info.setPostAddr(object
											.getString("postAddr"));
									info.setIsDefault(convertStringToBoolean(object
											.getString("isDefault")));
									postInfos.add(info);
								}
							}

							adapter = new AddressAdapter();
							mListView.setAdapter(adapter);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					private Boolean convertStringToBoolean(String str) {
						if (str.equals("1")) {
							return true;
						} else if (str.equals("0")) {
							return false;
						}
						return false;
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] data,
							Throwable arg3) {

					}
				});

	}

	private class AddressAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return postInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return postInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			PostInfo info = postInfos.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_address, null);
				viewHolder.postMan = (TextView) convertView
						.findViewById(R.id.item_postName);
				viewHolder.postAddress = (TextView) convertView
						.findViewById(R.id.item_postAddress);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (viewHolder != null) {

				viewHolder.postMan.setText(info.getPostName());
				if (isDefault && position == 0) {
					viewHolder.postAddress.setText("[默认]" + info.getPostAddr());
					Drawable drawable = getResources().getDrawable(
							R.drawable.shipping_address_selected);
					viewHolder.postAddress.measure(MeasureSpec.makeMeasureSpec(
							0, MeasureSpec.UNSPECIFIED), MeasureSpec
							.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
					drawable.setBounds(0, 0,
							viewHolder.postAddress.getMeasuredHeight(),
							viewHolder.postAddress.getMeasuredHeight());
					viewHolder.postAddress.setCompoundDrawables(null, null,
							drawable, null);
				} else {
					viewHolder.postAddress.setText(info.getPostAddr());
				}
			}
			return convertView;
		}

	}

	private static class ViewHolder {
		public TextView postMan, postAddress;
	}

	public void finishActivity(View view) {
		finish();
	}

}
