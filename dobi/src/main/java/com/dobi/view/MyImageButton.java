package com.dobi.view;


import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MyImageButton extends ImageButton{
	
	public MyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyImageButton(Context context) {
		super(context);
	}

	public void defaultBK() {
		ViewGroup group=(ViewGroup) getParent();
		int count=group.getChildCount();
		for(int i=0;i<count;i++){
			ImageButton button=(ImageButton) group.getChildAt(i);
			((MyImageButton) button).setNormal();
		}
	}

	public void setSelected() {
		Log.i("jiang", "选中");
	}
	public void setNormal() {
		Log.i("jiang", "默认");
	}
	
	
}
