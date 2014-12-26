package com.dobi.ui;



import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.dobi.R;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;

public class ZoomLargeActivity extends Activity {
	/** Called when the activity is first created. */
	Bitmap bp = null;
	ImageView imageview;
	private String url = null;
	int h;
	boolean num = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom_large);
		imageview = (ImageView) findViewById(R.id.zoom);
		url = getIntent().getExtras().getString("url");
		bp = ImageLoader.initLoader(getApplicationContext()).downloadImage(url,
				new onImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							imageview.setImageBitmap(bitmap);
						}
					}
				}, null);
		if (bp != null) {
			imageview.setImageBitmap(bp);
		}else{
			imageview.setImageResource(R.drawable.default_load);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			finish();
			break;
		}

		return super.onTouchEvent(event);
	}

}
