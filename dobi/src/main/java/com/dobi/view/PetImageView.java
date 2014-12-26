package com.dobi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.widget.ImageView;

public class PetImageView extends ImageView {
	public PetImageView(Context context) {
		super(context);
		
	}
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Path path=new Path();
		Paint paint =new Paint();
		Paint circlePaint=new Paint();
		paint.setAntiAlias(true);
		circlePaint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		path.moveTo(75,25);
	    circlePaint.setColor(Color.BLUE);
		canvas.drawCircle(75, 25, 2, circlePaint);
		path.quadTo(25,25,25,62f);
		canvas.drawCircle(25, 62, 2, circlePaint);
		path.quadTo(25,100,50,100);
		canvas.drawCircle(50, 100, 2, circlePaint);
		path.quadTo(50,120,30,125);
		path.quadTo(60,120,65,100);
		path.quadTo(125,100,125,62f);
		path.quadTo(125,25,75,25);
		path.moveTo(75,25);
		path.quadTo(25,25,25,62f);
		canvas.drawPath(path, paint);
	}
	


}
