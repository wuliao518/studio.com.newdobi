package com.dobi.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.NativeImageLoader;
import com.dobi.common.NativeImageLoader.NativeImageCallBack;
import com.dobi.view.GridImageView;
import com.dobi.view.GridImageView.OnMeasureListener;

public class AlbumActivity extends Activity{
	private GridView gridView;
	private ArrayList<String> imageList;
	private Point mPoint;
    /** 
     * 是否已经扫描 
     */  
    public static boolean isScaned;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		gridView=(GridView) findViewById(R.id.gridView);
		imageList=new ArrayList<String>();
		mPoint=new Point();
		loadData();
		gridView.setAdapter(new GroupAdapter());
	}
	private void loadData() {
		String path=Environment.getExternalStorageDirectory().toString()+"/dobi/";
		File file=new File(path);
		String[] list=file.list();
		for(int i=0;i<list.length;i++){
			if(list[i].endsWith(".png")){
				imageList.add(path+list[i]);
			}else if(list[i].endsWith(".jpg")){
				imageList.add(path+list[i]);
			}
		}
	}
	
	private class GroupAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public Object getItem(int position) {
			return imageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final GridImageView view;  
	        String path = imageList.get(position);
	        if(convertView==null){
	        	view=new GridImageView(getApplicationContext(),null);
	        	view.setScaleType(ScaleType.CENTER_CROP);
	        	convertView=view;
	        }else{
	        	view=(GridImageView) convertView;
	        }
	        
	        final LayoutParams params=new LayoutParams((int) ((float)CommonMethod.getWidth(getApplicationContext())/(float)2.5),
	        		(int) ((float)CommonMethod.getWidth(getApplicationContext())/(float)3));
			mPoint.set(CommonMethod.getWidth(getApplicationContext())/3, CommonMethod.getWidth(getApplicationContext())/3);  
             
	        view.setTag(path); 
	        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, new Point(200,200), new NativeImageCallBack() {  
	            @Override  
	            public void onImageLoader(Bitmap bitmap, String path) {  
	                ImageView mImageView = (ImageView) gridView.findViewWithTag(path);  
	                if(bitmap != null && mImageView != null){
	                	mImageView.setLayoutParams(params);
	                    mImageView.setImageBitmap(bitmap);  
	                }  
	            }  
	        });  
	        if(bitmap != null){  
	        	view.setLayoutParams(params);
	            view.setImageBitmap(bitmap);  
	        }
			return convertView;
		}
		
	}
	public static class ViewHolder{  
        public ImageView mImageView;   
    }
	
	public void finishActivity(View view){
		finish();
	}
	
}
