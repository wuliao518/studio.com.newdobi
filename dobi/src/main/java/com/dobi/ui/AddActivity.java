package com.dobi.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.NetUtils;
import com.dobi.item.CityModel;
import com.dobi.item.CountyModel;
import com.dobi.item.PostInfo;
import com.dobi.item.ProvinceModel;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.weibo.sdk.android.api.util.SharePersistent;

import android.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 省市级联
 * @author XiaoWang
 */
public class AddActivity extends Activity implements OnClickListener {
	private String AddressXML; // xml格式的中国省市区信息
	private Button btn_province;
	private Button btn_city;
	private Button btn_county;
	private List<ProvinceModel> provinceList; // 地址列表
	private int pPosition;
	private int cPosition;
	private boolean isCity = true;
	private boolean isCounty = true;
	private ImageButton mImgBtn_true;
	private PostInfo postInfo;
	private SharedPreferences sp;
	private EditText consignee, mEditText_xxdz, mEditText_yb, mEditText_sjhm,
			mEditText_dhhm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		// 初始化
		initFindView();
		// 获取中国省市区信息
		initData();
		try {
			postInfo = (PostInfo) getIntent().getExtras().get("info");
			if (postInfo != null) {
				loadData();
			}
		} catch (Exception e) {
			postInfo=null;
		}
		

	}

	private void loadData() {
		// 初始化button数据
		consignee.setText(postInfo.getPostName());
		btn_province.setText(postInfo.getProvince());
		btn_city.setText(postInfo.getCity());
		btn_county.setText(postInfo.getZone());
		mEditText_xxdz.setText(postInfo.getAddr());
		mEditText_yb.setText(postInfo.getPostNum());
		mEditText_sjhm.setText(postInfo.getpMobile());
		mEditText_dhhm.setText(postInfo.getpTel());
	}

	public void initFindView() {
		btn_province = (Button) findViewById(R.id.btn_province);
		btn_city = (Button) findViewById(R.id.btn_city);
		btn_county = (Button) findViewById(R.id.btn_county);
		sp = CommonMethod.getPreferences(AddActivity.this);
		consignee = (EditText) findViewById(R.id.consignee);
		mEditText_xxdz = (EditText) findViewById(R.id.mEditText_xxdz);
		mEditText_yb = (EditText) findViewById(R.id.mEditText_yb);
		mEditText_sjhm = (EditText) findViewById(R.id.mEditText_sjhm);
		mEditText_dhhm = (EditText) findViewById(R.id.mEditText_dhhm);
		mImgBtn_true = (ImageButton) findViewById(R.id.mImgBtn_true);

		btn_province.setOnClickListener(this);
		btn_city.setOnClickListener(this);
		btn_county.setOnClickListener(this);

		// mbtnxiyibu.setOnClickListener(this);

		mImgBtn_true.setOnClickListener(this);
	}

	public void initData() {
		AddressXML = getRawAddress().toString();// 获取中国省市区信息
		try {
			analysisXML(AddressXML);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		// 初始化button数据
		btn_province.setText(provinceList.get(0).getProvince());
		btn_city.setText(provinceList.get(0).getCity_list().get(0).getCity());
		btn_county.setText(provinceList.get(0).getCity_list().get(0)
				.getCounty_list().get(0).getCounty());
		// 初始化列表下标
		pPosition = 0;
		cPosition = 0;
	}

	/**
	 * 获取地区raw里的地址xml内容
	 **/
	public StringBuffer getRawAddress() {
		InputStream in = getResources().openRawResource(R.raw.address);
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			br.close();
			isr.close();
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return sb;
	}

	/**
	 * 解析省市区xml， 采用的是pull解析， 为什么选择pull解析：因为pull解析简单浅显易懂！
	 * */
	public void analysisXML(String data) throws XmlPullParserException {
		try {
			ProvinceModel provinceModel = null;
			CityModel cityModel = null;
			CountyModel countyModel = null;
			List<CityModel> cityList = null;
			List<CountyModel> countyList = null;

			InputStream xmlData = new ByteArrayInputStream(
					data.getBytes("UTF-8"));
			XmlPullParserFactory factory = null;
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser;
			parser = factory.newPullParser();
			parser.setInput(xmlData, "utf-8");
			String currentTag = null;

			String province;
			String city;
			String county;

			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				String typeName = parser.getName();

				if (type == XmlPullParser.START_TAG) {
					if ("root".equals(typeName)) {
						provinceList = new ArrayList<ProvinceModel>();

					} else if ("province".equals(typeName)) {
						province = parser.getAttributeValue(0);// 获取标签里第一个属性,例如<city
																// name="北京市"
																// index="1">中的name属性
						provinceModel = new ProvinceModel();
						provinceModel.setProvince(province);
						cityList = new ArrayList<CityModel>();

					} else if ("city".equals(typeName)) {
						city = parser.getAttributeValue(0);
						cityModel = new CityModel();
						cityModel.setCity(city);
						countyList = new ArrayList<CountyModel>();

					} else if ("area".equals(typeName)) {
						county = parser.getAttributeValue(0);
						countyModel = new CountyModel();
						countyModel.setCounty(county);

					}

					currentTag = typeName;

				} else if (type == XmlPullParser.END_TAG) {
					if ("root".equals(typeName)) {

					} else if ("province".equals(typeName)) {
						provinceModel.setCity_list(cityList);
						provinceList.add(provinceModel);

					} else if ("city".equals(typeName)) {
						cityModel.setCounty_list(countyList);
						cityList.add(cityModel);

					} else if ("area".equals(typeName)) {
						countyList.add(countyModel);
					}

				} else if (type == XmlPullParser.TEXT) {

					currentTag = null;
				}

				type = parser.next();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_province:
			createDialog(1);
			btn_province.setBackgroundResource(color.transparent);
			break;

		case R.id.btn_city:
			if (isCity == true) {
				createDialog(2);
				btn_city.setBackgroundResource(color.transparent);
			}
			break;
		case R.id.btn_county:
			if (isCounty == true) {
				createDialog(3);
				btn_county.setBackgroundResource(color.transparent);
			}
			break;
		case R.id.mImgBtn_true:
			// mEditText.setText("地址：    " + btn_province.getText() + "、"
			// + btn_city.getText() + "、" +
			// btn_county.getText()+"、"+mEditTextxxdz.getText());
			RequestParams params = new RequestParams();
			params.add("uid", sp.getString("uid", null));
			if(postInfo!=null){
				params.add("postId",postInfo.getPostId());
			}
			params.add("pName", consignee.getText().toString().trim());
			params.add("province", btn_province.getText().toString().trim());
			params.add("city", btn_city.getText().toString().trim());
			params.add("zone", btn_county.getText().toString().trim());
			params.add("addr", mEditText_xxdz.getText().toString().trim());
			params.add("postNum", mEditText_yb.getText().toString().trim());
			params.add("pMobile", mEditText_sjhm.getText().toString().trim());
			params.add("pTel", mEditText_dhhm.getText().toString().trim());
			NetUtils.addressAdd(params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					Log.i("jiang", new String(data));
					try {
						JSONObject json = new JSONObject(new String(data));
						if (json.getInt("status") == 1) {
							PostInfo post = new PostInfo();
							post.setPostId(json.getInt("postId") + "");
							post.setPostName(consignee.getText().toString()
									.trim());
							post.setPostAddr(btn_province.getText().toString()
									.trim()
									+ btn_city.getText().toString().trim()
									+ btn_county.getText().toString().trim()
									+ mEditText_xxdz.getText().toString()
											.trim());
							if (getIntent() != null) {
								// 判断空，我就不判断了。。。。
								Intent intent = new Intent();
								intent.putExtra("address", post);
								// 请求代码可以自己设置，这里设置成20
								setResult(20, intent);
								// 关闭掉这个Activity
							}
							finish();
						}else{
							Toast.makeText(getApplicationContext(), json.getString("message"), 0).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {

				}
			});

			break;
		default:
			break;
		}
	}

	/**
	 * 根据调用类型显示相应的数据列表
	 * 
	 * @param type
	 *            显示类型 1.省；2.市；3.县、区
	 */
	public void createDialog(final int type) {
		ListView lv = new ListView(this);
		final Dialog dialog = new Dialog(this);
		dialog.setTitle("列表选择框");

		if (type == 1) {
			ProvinceAdapter pAdapter = new ProvinceAdapter(provinceList);
			lv.setAdapter(pAdapter);

		} else if (type == 2) {
			CityAdapter cAdapter = new CityAdapter(provinceList.get(pPosition)
					.getCity_list());
			lv.setAdapter(cAdapter);
		} else if (type == 3) {
			CountyAdapter coAdapter = new CountyAdapter(provinceList
					.get(pPosition).getCity_list().get(cPosition)
					.getCounty_list());
			lv.setAdapter(coAdapter);
		}

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (type == 1) {
					pPosition = position;
					btn_province.setText(provinceList.get(position)
							.getProvince());
					// 判断该省下是否有市级
					if (provinceList.get(position).getCity_list().size() < 1) {
						btn_city.setText("");
						btn_county.setText("");
						isCity = false;
						isCounty = false;
					} else {
						isCity = true;
						btn_city.setText(provinceList.get(position)
								.getCity_list().get(0).getCity());
						cPosition = 0;
						// 判断该市下是否有区级或县级
						if (provinceList.get(position).getCity_list().get(0)
								.getCounty_list().size() < 1) {
							btn_county.setText("");
							isCounty = false;

						} else {
							isCounty = true;
							btn_county.setText(provinceList.get(position)
									.getCity_list().get(0).getCounty_list()
									.get(0).getCounty());
						}

					}

				} else if (type == 2) {
					cPosition = position;
					btn_city.setText(provinceList.get(pPosition).getCity_list()
							.get(position).getCity());
					if (provinceList.get(pPosition).getCity_list()
							.get(position).getCounty_list().size() < 1) {
						btn_county.setText("");
						isCounty = false;
					} else {
						isCounty = true;
						btn_county.setText(provinceList.get(pPosition)
								.getCity_list().get(cPosition).getCounty_list()
								.get(0).getCounty());
					}

				} else if (type == 3) {
					btn_county.setText(provinceList.get(pPosition)
							.getCity_list().get(cPosition).getCounty_list()
							.get(position).getCounty());

				}

				dialog.dismiss();
			}
		});

		dialog.setContentView(lv);
		dialog.show();
	}

	class ProvinceAdapter extends BaseAdapter {
		public List<ProvinceModel> adapter_list;

		public ProvinceAdapter(List<ProvinceModel> list) {
			adapter_list = list;
		}

		@Override
		public int getCount() {
			return adapter_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			TextView tv = new TextView(AddActivity.this);
			tv.setPadding(20, 20, 20, 20);
			tv.setTextSize(18);
			tv.setText(adapter_list.get(position).getProvince());
			return tv;
		}

	}

	class CityAdapter extends BaseAdapter {
		public List<CityModel> adapter_list;

		public CityAdapter(List<CityModel> list) {
			adapter_list = list;
		}

		@Override
		public int getCount() {
			return adapter_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			TextView tv = new TextView(AddActivity.this);
			tv.setPadding(20, 20, 20, 20);
			tv.setTextSize(18);
			tv.setText(adapter_list.get(position).getCity());
			return tv;
		}

	}

	class CountyAdapter extends BaseAdapter {
		public List<CountyModel> adapter_list;

		public CountyAdapter(List<CountyModel> list) {
			adapter_list = list;
		}

		@Override
		public int getCount() {
			return adapter_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			TextView tv = new TextView(AddActivity.this);
			tv.setPadding(20, 20, 20, 20);
			tv.setTextSize(18);
			tv.setText(adapter_list.get(position).getCounty());
			return tv;
		}

	}

	public void finishActivity(View view) {
		finish();
	}

}