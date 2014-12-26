package com.dobi.ui;

import java.io.File;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ConstValue;
import com.dobi.logic.ImageManager;
import com.dobi.view.MyImageView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

public class ShowPicActivity extends BaseActivity {
	private Bitmap cameraBitmap = null;// 渲染前
	private MyImageView myImageView;
	private ImageManager mImageManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpic);
		myImageView = (MyImageView) findViewById(R.id.clipImage);
		mImageManager = new ImageManager();
		
		String path = Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
				+ ConstValue.ImgName.photo.toString() + "jpg";
		if(new File(path).exists()){
			myImageView.initView(ShowPicActivity.this,path);
		}
	}

	public void setImageBitmap() {
		if (CommonMethod.GetSingleOrMore() == 0) {
			String path = Environment.getExternalStorageDirectory()
					+ ConstValue.ROOT_PATH + ConstValue.PLAY_PATH
					+ ConstValue.ImgName.photo.toString() + "jpg";
			File mFile = new File(path);
			// 若该文件存在
			if (mFile.exists()) {
				cameraBitmap = mImageManager.getBitmapFromFile(mFile, 1000);
			}
		} else {
			String path = Environment.getExternalStorageDirectory()
					+ ConstValue.ROOT_PATH + ConstValue.FACE_PATH
					+ ConstValue.ImgName.photo.toString() + "jpg";
			File mFile = new File(path);
			// 若该文件存在
			if (mFile.exists()) {
				cameraBitmap = mImageManager.getBitmapFromFile(mFile, 1000);
			}
		}

	}

	/**
	 * 確定
	 * 
	 * @param v
	 */
	public void btnSureOnclick(View v) {
		String type = getIntent().getExtras().getString("type");
		int index = getIntent().getExtras().getInt("index", 0);
		if(CommonMethod.GetSingleOrMore()==0){
			if (type.equals("index")) {
				myImageView.saveBitmap(index);
				Intent intent = new Intent(ShowPicActivity.this, MainActivity.class);
				startActivity(intent);
			} else {
				myImageView.saveBitmap(index);
			}
		}else if(CommonMethod.GetSingleOrMore()==2){
			myImageView.saveBitmap(index);
			//Intent intent = new Intent(ShowPicActivity.this, JiangActivity.class);
			//startActivity(intent);
		}
		
		finish();
	}

	/**
	 * 取消截图
	 * 
	 * @param v
	 */
	public void btnCancelOnclick(View v) {
		this.finish();
	}

}
