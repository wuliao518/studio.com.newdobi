package com.dobi.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.dobi.common.NetUtils;
import com.dobi.item.PlayItem;
import com.dobi.item.ShowItem;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class CeshiActivity extends Activity {
	private List<PlayItem> playItems=new ArrayList<PlayItem>();
	private List<ShowItem> showItems=new ArrayList<ShowItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NetUtils.getAllInfo(new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				try {
					JSONObject json=new JSONObject(new String(data));
					int status=(Integer) json.get("status");
					if(status==1){
						Log.i("jiang", "hehehehda");
						JSONObject object=json.getJSONObject("disguise");
						PlayItem play=new PlayItem();
						for(int i=0;i<object.length();i++){
							if(i==0){
								play.setName("one");
								JSONObject one=object.getJSONObject("one");
								Log.i("jiang", one.length()+"....");
								//注意为什么是one.length()-1，json有一个"id"=0，所以要减去1。
								for(int j=0;j<one.length()-1;j++){
									ShowItem item=new ShowItem();
									i++;
									JSONObject obj=one.getJSONObject(i+"");
									item.setTypeName(obj.getString("type_name"));
									item.setNormalBack(obj.getBoolean("background")+"...");
									item.setSelectedBack(obj.getBoolean("background")+"...");
									JSONArray array=obj.getJSONArray("imageList");
									List<String> path=new ArrayList<String>();
									for(int k=0;k<array.length();k++){
										path.add(array.getJSONObject(k).getString("url"));
									}
									item.setPathList(path);
								}
								play.setItems(showItems);
								Log.e("jiang", play.toString());
								
							}else if(i==1){
								play.setName("two");
							}else if(i==2){
								play.setName("three");
							}else if(i==3){
								play.setName("four");
							}
						}
						
						JSONObject haha=(JSONObject) object.getJSONObject("one").getJSONObject("1").getJSONArray("imageList").get(0);
						Log.e("jiang", haha.getString("url"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
