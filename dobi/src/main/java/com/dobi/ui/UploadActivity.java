package com.dobi.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.FileUtils;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.common.NetUtils;
import com.dobi.item.CartItem;
import com.dobi.item.GoodsInfo;
import com.dobi.item.OrderInfo;
import com.dobi.item.PostInfo;
import com.dobi.logic.ImageManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class UploadActivity extends BaseActivity implements OnClickListener {
	private ImageView image1, image2, image3;
	private TextView arrowRight;
	private SharedPreferences sp;
	private String uid;
	private GoodsInfo infos;
	private String postId, pid;
	private OrderInfo order = new OrderInfo();
	//底部对话框
	private Dialog dialog;
	//旋转对话框
	private Dialog note;
	private List<String> images;
	//判断3张图片
	private Boolean[] isFill;
	// 代表三张图片
	private int selectedType = 1;
	private ImageLoader mImageLoader;
	private ImageView image = null;
	public static  UploadActivity instance =null;
	//对话框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		instance=this;
		mImageLoader = ImageLoader.initLoader(getApplicationContext());
		dialog = CommonMethod.showPhotoDialog(UploadActivity.this);
		note = CommonMethod.showMyDialog(UploadActivity.this);
		sp = CommonMethod.getPreferences(getApplicationContext());
		infos = (GoodsInfo) getIntent().getExtras().get("infos");
		isFill=new Boolean[3];
		initBoolean();
		uid = sp.getString("uid", null);
		image1 = (ImageView) findViewById(R.id.upload1);
		image2 = (ImageView) findViewById(R.id.upload2);
		image3 = (ImageView) findViewById(R.id.upload3);
		arrowRight = (TextView) findViewById(R.id.arrow_right);
		image1.setOnClickListener(this);
		image2.setOnClickListener(this);
		image3.setOnClickListener(this);
		arrowRight.setOnClickListener(this);
		List<CartItem> cartItems = infos.getGoodsList();
		pid = cartItems.get(0).getPid();
		images = infos.getUserImage();
		List<PostInfo> postInfos = infos.getPostAddrList();
		if (postInfos != null && postInfos.size() > 0) {
			postId = postInfos.get(0).getPostId();
		}
		if (images != null) {
			for (int i = 0; i < images.size(); i++) {
				if (images.get(i) != null) {
					Bitmap bitmap = mImageLoader.downloadImage(
							NetUtils.IMAGE_PREFIX + images.get(i),
							new onImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null) {
										if ((NetUtils.IMAGE_PREFIX + images
												.get(0)).equals(url)) {
											image1.setImageBitmap(bitmap);
											isFill[0]=true;
										}else if ((NetUtils.IMAGE_PREFIX + images
												.get(1)).equals(url)) {
											image2.setImageBitmap(bitmap);
											isFill[1]=true;
										}else if ((NetUtils.IMAGE_PREFIX + images
												.get(2)).equals(url)) {
											image3.setImageBitmap(bitmap);
											isFill[2]=true;
										}
									}
								}
							}, new Point(160, 160));
					if (bitmap != null) {
						if (i == 0) {
							image1.setImageBitmap(bitmap);
							isFill[0]=true;
						} else if (i == 1) {
							image2.setImageBitmap(bitmap);
							isFill[1]=true;
						} else if (i == 2) {
							image3.setImageBitmap(bitmap);
							isFill[2]=true;
						}
					}
				}
			}
		}

	}
	//初始化填充器
	private void initBoolean() {
		for(int i=0;i<3;i++){
			isFill[i]=false;
		}
	}

	@Override
	public void onClick(View v) {
		System.gc();
		final Intent intent = new Intent();
		Button camera = (Button) dialog.findViewById(R.id.xiangji);
		Button photo = (Button) dialog.findViewById(R.id.xiangce);
		Button cancle = (Button) dialog.findViewById(R.id.selected_cancle);
		camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri mOutPutFileUri;
				// 文件夹doubi
				String path = Environment.getExternalStorageDirectory()
						.toString() + "/dobi";
				File path1 = new File(path);
				if (!path1.exists()) {
					path1.mkdirs();
				}
				File file = new File(path1, "three"+selectedType+".jpg");
				mOutPutFileUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
				startActivityForResult(intent, 0);
			}
		});
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(intent, 1);

			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		switch (v.getId()) {
		case R.id.upload1:
			try {
				selectedType = 0;
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.upload2:
			try {
				selectedType = 1;
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.upload3:
			try {
				selectedType = 2;
				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.arrow_right:
			if(isFilled()){
				intent.setClass(UploadActivity.this, OrderListActivity.class);
				intent.putExtra("infos", infos);
				startActivity(intent);
			}else{
				Toast.makeText(getApplicationContext(), "请在图片显示和确认后操作！", 0).show();
			}
			break;
		default:
			break;
		}
	}

	private boolean isFilled() {
		for(int i=0;i<3;i++){
			if(!isFill[i]){
				return false;
			}
		}
		return true;
	}
	public String convert(long mill) {
		Date date = new Date(mill * 1000);
		String strs = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			strs = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {

		} else {
			note.show();
			final String path = Environment.getExternalStorageDirectory()
					.toString() + "/dobi/three"+selectedType+".jpg";
			File file = new File(path);
			switch (requestCode) {
			case 0:// 相机
				if (file.exists()) {
					try {
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
						Bitmap bitmap = BitmapFactory.decodeFile(path,options);
						int scale = 1;
						float bitWidth = options.outWidth;
						float bitHeight = options.outHeight;
						WindowManager wm = (WindowManager) getSystemService("window");
						Display display = wm.getDefaultDisplay();
						float width = display.getWidth() * 1.0f;
						float height = display.getHeight() * 1.0f;
						float scaleX = (float) bitWidth / width;
						float scaleY = (float) bitHeight / height;
						scale = (int) Math.ceil(Math.max(scaleX, scaleY));
						if (scale > 1) {
							options.inJustDecodeBounds = false;
							options.inSampleSize = scale;
							bitmap = BitmapFactory.decodeFile(path,options);
						} else {
							options.inJustDecodeBounds = false;
							options.inSampleSize = 1;
							bitmap = BitmapFactory.decodeFile(path,options);
						}
						
						switch (selectedType) {
						case 0:
							upLoadFile(path, file,1);
							break;
						case 1:
							upLoadFile(path, file,2);
							break;
						case 2:
							upLoadFile(path, file,3);
							break;
						default:
							break;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				try {
					Uri uri = data.getData();
					ContentResolver cr = this.getContentResolver();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					Bitmap bitmap = BitmapFactory.decodeStream(
							cr.openInputStream(uri), null, options);
					int scale = 1;
					float bitWidth = options.outWidth;
					float bitHeight = options.outHeight;
					WindowManager wm = (WindowManager) getSystemService("window");
					Display display = wm.getDefaultDisplay();
					float width = display.getWidth() * 0.8f;
					float height = display.getHeight() * 0.8f;
					float scaleX = (float) bitWidth / width;
					float scaleY = (float) bitHeight / height;
					scale = (int) Math.ceil(Math.max(scaleX, scaleY));
					if (scale > 1) {
						options.inJustDecodeBounds = false;
						options.inSampleSize = scale;
						bitmap = BitmapFactory.decodeStream(
								cr.openInputStream(uri), null, options);
					} else {
						options.inJustDecodeBounds = false;
						options.inSampleSize = 1;
						bitmap = BitmapFactory.decodeStream(
								cr.openInputStream(uri), null, options);
					}
					ImageManager mImageManager = new ImageManager();
					FileOutputStream out = mImageManager.creatFile("three"+selectedType,
							".jpg", "");
					bitmap.compress(CompressFormat.JPEG, 100, out);
					out.close();
					if (file.exists()) {
							switch (selectedType) {
							case 0:
								upLoadFile(path, file,1);
								break;
							case 1:
								upLoadFile(path, file,2);
								break;
							case 2:
								upLoadFile(path, file,3);
								break;
							default:
								break;
							}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
		}
	}

	private void upLoadFile(final String path, File file,final int type) throws Exception {
		NetUtils.doUpload(uid, type, "file", file,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0,
							Header[] arg1, byte[] data) {
						if(note!=null&&note.isShowing()){
							note.dismiss();
						}
						setImageBitmap(type,path);
						Toast.makeText(
								getApplicationContext(),
								"上传成功！", 0).show();
						if(images!=null&&images.size()>=type){
							FileUtils
							.deleteFromName((NetUtils.IMAGE_PREFIX + images
									.get(type-1))
									.replaceAll(
											"[^\\w]",
											""));
						}
					}

					@Override
					public void onFailure(int arg0,
							Header[] arg1, byte[] arg2,
							Throwable arg3) {
						if(dialog!=null&&dialog.isShowing()){
							dialog.dismiss();
						}
						Toast.makeText(
								getApplicationContext(),
								"上传失败请重试！", 0).show();
					}
				});
	}
	
	private void setImageBitmap(int type,String path) {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize=3;
		switch (type) {
		case 1:
			isFill[0]=true;
			image1.setImageBitmap(BitmapFactory
					.decodeFile(path,options));
			break;
		case 2:
			isFill[1]=true;
			image2.setImageBitmap(BitmapFactory
					.decodeFile(path,options));
			break;
		case 3:
			isFill[2]=true;
			image3.setImageBitmap(BitmapFactory
					.decodeFile(path,options));
			break;

		default:
			break;
		}
	}
	
	
	public void finishActivity(View view){
		finish();
	}
	
	
	
}
