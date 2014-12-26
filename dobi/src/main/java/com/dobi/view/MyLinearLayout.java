package com.dobi.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {
	private static int DIVIDE_WIDTH_COUNT=4;
	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count=getChildCount();
		for(int i=0;i<count;i++){
			View view=getChildAt(i);
			view.measure(MeasureSpec.makeMeasureSpec(getWindowWidth()/DIVIDE_WIDTH_COUNT, MeasureSpec. EXACTLY),
					MeasureSpec.makeMeasureSpec(dip2px(getContext(),45), MeasureSpec.EXACTLY));
		}
		if(count<4){
			MyImageButton image=new MyImageButton(getContext());
			image.setBackgroundColor(Color.parseColor("#99ece7e3"));
			LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
			addView(image,params);
			count=4;
		}
		setMeasuredDimension(MeasureSpec.makeMeasureSpec(getWindowWidth()/DIVIDE_WIDTH_COUNT*count,MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(dip2px(getContext(),45), MeasureSpec.EXACTLY));
		
	}
	private int getWindowWidth(){
		WindowManager window=(WindowManager) getContext().getSystemService("window");
		Display diaplay=window.getDefaultDisplay();
		return diaplay.getWidth();
	}
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

}
