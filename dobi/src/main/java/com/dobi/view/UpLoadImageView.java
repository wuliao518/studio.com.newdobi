package com.dobi.view;

import java.io.File;

import org.apache.http.Header;

import com.dobi.common.NetUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class UpLoadImageView extends FrameLayout {
	public RoundProgressBar taskView;
	public ImageView image;
	public UpLoadImageView(Context context, AttributeSet attrs){
		super(context, attrs);
		taskView=new RoundProgressBar(context, attrs);
		image=new ImageView(context, attrs);
		LayoutParams paramsImage=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LayoutParams paramsTask=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(image, paramsImage);
		
		addView(taskView, paramsTask);
		taskView.setCricleColor(Color.parseColor("#D1D1D1"));
		taskView.setTextColor(Color.BLACK);
		taskView.setProgress(0);
		image.setScaleType(ScaleType.CENTER_INSIDE);
		//image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//View view 
		setMeasuredDimension(MeasureSpec.makeMeasureSpec(getWindowWidth()/3,MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(getWindowWidth()/3, MeasureSpec.EXACTLY));
		
	}
	public void startImage(){
		
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		View view=getChildAt(0);
		view.layout(0, 0, r, b);
		View task=getChildAt(1);
		int myWidth=getMeasuredWidth();
		int myHeight=getMeasuredHeight();
		int width=task.getMeasuredWidth();
		int height=task.getMeasuredHeight();
		task.layout((myWidth-width)/2, (myHeight-height)/2, (myWidth-width)/2+width, (myHeight-height)/2+height);
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
	
	
	
	public void uploadImage(String uId,int id,File file,int type,String name,final UploadCompleteListener listener) throws Exception{
		NetUtils.doUpload(uId, id, name, file, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] data) {
				taskView.setProgress(0);
				taskView.setVisibility(View.INVISIBLE);
				listener.complete();
				Toast.makeText(getContext(), "上传成功！", 0).show();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] data, Throwable arg3) {
				taskView.setProgress(0);
				Toast.makeText(getContext(), "上传失败，请重试！", 0).show();
			}
			@Override  
	        public void onProgress(int bytesWritten, int totalSize) {  
	            super.onProgress(bytesWritten, totalSize);  
	            int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
	            Log.i("jiang", count+"");
	            taskView.setVisibility(View.VISIBLE);
	            taskView.setProgress(count);
	        }  
		});
	}
	
	
	public interface UploadCompleteListener{
		void complete();
	}

	

}
