package com.dobi.ui;

import java.io.File;

import com.dobi.R;
import com.dobi.common.ConstValue;
import com.dobi.logic.ImageManager;
import com.dobi.view.MyMoreImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class ShowMoreActivity extends BaseActivity {
	private Bitmap cameraBitmap = null;// 渲染前
	private MyMoreImageView myImageView;
	private ImageManager mImageManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showmore);
		myImageView = (MyMoreImageView) findViewById(R.id.clipImage);
		mImageManager = new ImageManager();
//		setImageBitmap();
//		if (cameraBitmap != null) {
//			myImageView.Inteligense(ShowMoreActivity.this, cameraBitmap);
//		}
		String path = Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH + ConstValue.FACE_PATH
				+ ConstValue.ImgName.photo.toString() + "jpg";
		if(new File(path).exists()){
			myImageView.initView(ShowMoreActivity.this, path);
		}else{
			finish();
		}
	}

	public void setImageBitmap() {
		String path = Environment.getExternalStorageDirectory()
				+ ConstValue.ROOT_PATH + ConstValue.FACE_PATH
				+ ConstValue.ImgName.photo.toString() + "jpg";
		File mFile = new File(path);
		// 若该文件存在
		if (mFile.exists()) {
			cameraBitmap = mImageManager.getBitmapFromFile(mFile, 1000);
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
		if (type.equals("index")) {
			myImageView.saveBitmap(index);
			Intent intent = new Intent(ShowMoreActivity.this,
					MoreActivity.class);
			startActivity(intent);
		} else {
			myImageView.saveBitmap(index);
		}
		ShowMoreActivity.this.finish();
	}
	/**
	 * 取消截图
	 * 
	 * @param v
	 */
	public void btnCancelOnclick(View v) {
		ShowMoreActivity.this.finish();
	}
}
