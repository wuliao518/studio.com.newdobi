package com.tedo.myapplication;

/**
 * Created by wuliao on 2014/12/26.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PetImageView extends ImageView {
    private float[] bottom=new float[]{30,125};
    public PetImageView(Context context) {
        super(context);

    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Path path=new Path();
        //Paint paint =new Paint();
       // Paint circlePaint=new Paint();
        //paint.setAntiAlias(true);
        //circlePaint.setAntiAlias(true);
        //paint.setStyle(Style.STROKE);
       // path.moveTo(75,25);
       // circlePaint.setColor(Color.BLUE);
        //起始蓝点坐标
        //canvas.drawCircle(75, 25, 2, circlePaint);
       // path.quadTo(25,25,25,62f);
        //canvas.drawCircle(25, 62, 2, circlePaint);
       // path.quadTo(25,100,50,100);
        //canvas.drawCircle(50, 100, 2, circlePaint);
       // path.quadTo(50,120,bottom[0],bottom[1]);
       // path.quadTo(60,120,65,100);
       // path.quadTo(125,100,125,62f);
      //  path.quadTo(125,25,75,25);
       // canvas.drawPath(path, paint);
        Paint ovalPaint=new Paint();
        ovalPaint.setColor(Color.CYAN);
        //中心是（400,400）   假设两个点是（300,400）（400，300）
        Path ovalPath=new Path();
        ovalPath.moveTo(bottom[0],bottom[1]);
        //canvas.drawCircle(bottom[0], bottom[1], 5, circlePaint);
        //ovalPath.quadTo(300,bottom[1],300,400);
        ovalPath.lineTo(300,400);
        //canvas.drawCircle(300, 400, 5, circlePaint);
        ovalPath.moveTo(bottom[0],bottom[1]);
        ovalPath.lineTo(400,300);
        ovalPath.close();
        //canvas.drawLine();
        canvas.drawPath(ovalPath, ovalPaint);
        RectF oval= new RectF(100,200,700,630);

        canvas.drawOval(oval,ovalPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                bottom[0]=event.getX();
                bottom[1]=event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("jiang","move move");
                bottom[0]=event.getX();
                bottom[1]=event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        return true;
    }
}