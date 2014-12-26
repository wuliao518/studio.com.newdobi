package com.dobi.db;


import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.dobi.common.CommonMethod;
import com.umeng.message.UTrack;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

public class MyPushIntentService extends UmengBaseIntentService {
	private static final String TAG = MyPushIntentService.class.getName();
	private SharedPreferences sp;

	@Override
	protected void onMessage(Context context, Intent intent) {
		super.onMessage(context, intent);
		try {
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
			UMessage msg = new UMessage(new JSONObject(message));
			UTrack.getInstance(context).trackMsgClick(msg);
			sp = CommonMethod.getPreferences(context);
			JSONObject jsonObject = msg.getRaw().getJSONObject("body");
			String custom = jsonObject.getString("custom");
			JSONObject jsonObjectextra = msg.getRaw().getJSONObject("extra");
			Boolean isShow = Boolean.parseBoolean(jsonObjectextra.getString("isShow"));
			String content = jsonObjectextra.getString("content");
			String url = jsonObjectextra.getString("url");
			Editor editors = sp.edit();
			editors.putBoolean("isShow", isShow);
			editors.putString("custom", custom);
			editors.putString("content", content);
			editors.putString("url", url);
			editors.commit();

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
