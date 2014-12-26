package com.dobi.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class NetImageInfo {
	private static JSONObject jsonObject = null;
	private static final int REQUEST_TIMEOUT = 5*1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 5*1000;  //设置等待数据超时时间10秒钟
	private static Map<String,String> mapList=new HashMap<String, String>();
	 /** 
     * 异步下载图片的回调接口 
     * @author len 
     * 
     */  
    public interface NetListener{  
        void onNetListener(Map<String,String> mapList);
    }
	public static void setJson(final NetListener mCallBack){
		
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				BasicHttpParams httpParams=new BasicHttpParams();
				HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpClient client = new DefaultHttpClient(httpParams);
				// StringBuilder 字符串变量（非线程安全）
				StringBuilder builder = new StringBuilder();
				HttpGet get = new HttpGet(ConstValue.JSONURL);
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
				if(jsonObject!=null){
					 Iterator it = jsonObject.keys();  
			            while (it.hasNext()) {  
			                String key = (String) it.next();
			                String value = null;
							try {
								value = jsonObject.getString(key);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			                mapList.put(key, value);
			            }
				}else{
					mapList=null;
				}
	            mCallBack.onNetListener(mapList);
	            super.onPostExecute(result);
			}
		}.execute();

	}
}
