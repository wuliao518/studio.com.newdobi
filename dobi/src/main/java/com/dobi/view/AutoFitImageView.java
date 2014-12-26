package com.dobi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.dobi.common.CommonMethod;

@SuppressLint("NewApi")
public class AutoFitImageView extends ImageView {
	// 控件默认长、宽  
    private int defaultWidth = 0;  
    private int defaultHeight = 0;  
    // 比例  
    private float scale = 0;
	public AutoFitImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/*@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int imageHeight=getMeasuredHeight();
		int imageWidth=getMeasuredWidth();
		float width=CommonMethod.getWidth(getContext());
		float height=CommonMethod.getHeight(getContext());
		//720   720
		Log.i("jiang", width+"宽度....."+imageWidth);
		//1280  920
		Log.i("jiang", height+"高度....."+imageHeight);
		float scale=(float)imageWidth/(float)width;
		height=(float)imageHeight/(float)scale;
		
		setMeasuredDimension((int) width, (int) height);
		
	}*/
	
	@Override  
    protected void onDraw(Canvas canvas) {  
        Drawable drawable = getDrawable();  
        if (drawable == null) {  
            return;  
        }  
        if (getWidth() == 0 || getHeight() == 0) {  
            return;  
        }  
        this.measure(0, 0);  
        if (drawable.getClass() == NinePatchDrawable.class)  
            return;  
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();  
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);  
        if (bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {  
            return;  
        }  
        if (defaultWidth == 0) {  
            defaultWidth = getWidth();  
        }  
        if (defaultHeight == 0) {  
            defaultHeight = getHeight();  
        }  
  
        scale = (float) defaultWidth / (float) bitmap.getWidth();  
        defaultHeight = Math.round(bitmap.getHeight() * scale);  
        LayoutParams params = this.getLayoutParams();  
        params.width = defaultWidth;  
        params.height = defaultHeight;  
        this.setLayoutParams(params);  
        super.onDraw(canvas);  
    }
	

}
