package com.dobi.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.FileUtils;
import com.dobi.common.ImageLoader;
import com.dobi.common.NetUtils;
import com.dobi.exception.ExitAppUtils;
import com.dobi.item.CartItem;
import com.dobi.item.GoodsInfo;
import com.dobi.item.OrderInfo;
import com.dobi.item.PostInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderListActivity extends BaseActivity {
	private GoodsInfo infos;
	private ListView mListView;
	private List<CartItem> cartItems;
	private LayoutInflater mInflater;
	private ImageLoader mImageDownLoader;
	private TextView postMan, address,totlePrice;
	private SharedPreferences sp;
	private ImageView postOrder;
	private String postId, pid,uid;
	private float totle=0f; 
	private LinearLayout addressAdd,addressList;
	private OrderInfo order = new OrderInfo();
	Intent intent=new Intent();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_list);
		final Intent intent=new Intent();
		infos = (GoodsInfo) getIntent().getExtras().get("infos");
		cartItems=infos.getGoodsList();
		infos.getPostAddrList();
		mListView=(ListView) findViewById(R.id.order_list);
		address = (TextView) findViewById(R.id.address);
		postMan = (TextView) findViewById(R.id.postMan);
		postOrder = (ImageView) findViewById(R.id.postOrder);
		totlePrice = (TextView) findViewById(R.id.totlePrice);
		//listOrder=(ImageView) findViewById(R.id.list_order);
		addressAdd=(LinearLayout) findViewById(R.id.ll_address_add);
		addressList=(LinearLayout) findViewById(R.id.ll_address_show);
		getTotlePrice();
		final ArrayList<PostInfo> postInfos = infos.getPostAddrList();
//		listOrder.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				intent.putExtra("addresss",postInfos);
//				intent.setClass(OrderListActivity.this, AddressActivity.class);
//				OrderListActivity.this.startActivity(intent);
//			}
//		});
		sp=CommonMethod.getPreferences(getApplicationContext());
		uid = sp.getString("uid", null);
		StringBuffer buffer=new StringBuffer();
		int length = cartItems.size();
		for (int i = 0; i < length; i++) {
			CartItem item = cartItems.get(i);
			buffer.append(item.getPid()+"-");
		}
		buffer.delete(buffer.length() - 1, buffer.length());
		pid = buffer.toString();
		if (postInfos != null && postInfos.size()>0&&postInfos.get(0).getPostName()!=null&&postInfos.get(0).getPostAddr()!=null) {
			Log.i("jiang", postInfos.get(0).getPostName());
			postMan.setText("收货人" + postInfos.get(0).getPostName());
			address.setText(postInfos.get(0).getPostAddr());
			postId = postInfos.get(0).getPostId();
		}else{
			addressList.setVisibility(View.GONE);
			addressAdd.setVisibility(View.VISIBLE);
		}
		addressAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(OrderListActivity.this, AddActivity.class);
				OrderListActivity.this.startActivityForResult(intent, 0);
			}
		});
		addressList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//intent.putExtra("addresss",postInfos);
				intent.setClass(OrderListActivity.this, AddressActivity.class);
				OrderListActivity.this.startActivityForResult(intent, 1);
			}
		});
		
		postOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();
				params.put("uid", uid);
				params.put("postId", postId);
				params.put("pid", pid);
				params.put("total_price", totle+"");
				NetUtils.goodsOrder(params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] data) {
						Log.i("jiang", "fuckfuck");
						try {
							JSONObject json = new JSONObject(new String(data));
							if (json.getInt("status") == 1) {
								order.setBid(json.getString("bid"));
								order.setcTime(convert(json.getLong("cTime")));
								order.setGoodsNum(json.getInt("goodsNum")+"");
								order.setPostAddr(json.getString("postAddr"));
								order.setTotal(json.getString("total"));
								order.setOrderId(json.getString("orderId"));
								intent.setClass(OrderListActivity.this, OrderCommit.class);
								intent.putExtra("order", order);
								startActivity(intent);
								OrderListActivity.this.finish();
								UploadActivity.instance.finish();
							}
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
		});
		mInflater=LayoutInflater.from(getApplicationContext());
		mImageDownLoader=ImageLoader.initLoader(getApplicationContext());
		mListView.setAdapter(new OrderAdapter());
	}
	private void getTotlePrice() {
		for(CartItem item:cartItems){
			totle=totle+Integer.parseInt(item.getPrice());
		}
		totlePrice.setText(totle+"");
	}
	private class OrderAdapter extends BaseAdapter{

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
		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder viewHolder;
			final CartItem cartItem=cartItems.get(position);
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
				viewHolder.checkBox.setVisibility(View.GONE);
				viewHolder.button.setVisibility(View.GONE);
				viewHolder.goodsNum = (TextView) convertView
						.findViewById(R.id.goodsNum);
				viewHolder.goodsPrice= (TextView) convertView
						.findViewById(R.id.goodsPrice);
				viewHolder.editNum= (EditText) convertView
						.findViewById(R.id.editNum);
				viewHolder.goodsSize= (TextView) convertView
						.findViewById(R.id.goodsSize);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String path = NetUtils.IMAGE_PREFIX
					+ cartItem.getGoodsImage();
			viewHolder.image.setTag(path + position);
			//商品数量
			viewHolder.goodsNum.setText("X"+cartItem.getNum());
			viewHolder.editNum.setText(cartItem.getNum());
			//商品价格
			viewHolder.goodsPrice.setText("￥"+Integer.parseInt(cartItem.getPrice())
					/Integer.parseInt(cartItem.getNum()));
			
			String sizeType=cartItem.getSizeType();
			if(sizeType.equals("0")){
				viewHolder.goodsSize.setText("尺寸:8cm");
			}else if(sizeType.equals("1")){
				viewHolder.goodsSize.setText("尺寸:12cm");
			}else if(sizeType.equals("2")){
				viewHolder.goodsSize.setText("尺寸:15cm");
			}
			Bitmap bitmap = mImageDownLoader.showCacheBitmap(
					path.replaceAll("[^\\w]", ""), new Point(80, 80), false);
			if (bitmap != null) {
				viewHolder.image.setImageBitmap(bitmap);
			} else {
				Bitmap bitmapL = BitmapFactory.decodeResource(
						OrderListActivity.this.getResources(),
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
		public TextView goodsNum,goodsPrice,goodsSize;
		public CheckBox checkBox;
		public EditText editNum;
	}
	//时间转换
	public String convert(long mill) {
		Date date = new Date(mill*1000);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data==null){
			return;
		}
		PostInfo info=(PostInfo) data.getExtras().get("address");
		switch (requestCode) {
		case 0:
			addressList.setVisibility(View.VISIBLE);
			addressAdd.setVisibility(View.GONE);
			postMan.setText("收货人" + info.getPostName());
			address.setText(info.getPostAddr());
			postId = info.getPostId();
			break;
		case 1:
			postMan.setText("收货人" + info.getPostName());
			address.setText(info.getPostAddr());
			postId = info.getPostId();
			break;
		default:
			break;
		}
	}
	
	public void finishActivity(View view){
		finish();
	}
	
}
