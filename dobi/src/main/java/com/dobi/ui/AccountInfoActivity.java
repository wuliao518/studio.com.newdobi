package com.dobi.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dobi.R;
import com.dobi.common.CommonMethod;
import com.dobi.common.ImageLoader;
import com.dobi.common.ImageLoader.onImageLoaderListener;
import com.dobi.common.NetUtils;
import com.dobi.logic.ImageManager;
import com.dobi.view.CircleImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AccountInfoActivity extends Activity implements OnClickListener {

	private SharedPreferences sp;
	private Button logout;
	private ImageLoader mImageLoader;
	private CircleImageView mBtntx;
	private TextView mText_nc, mText_sjhm;
	private RelativeLayout linear_shdz, linear_xgmm, linear_tx;
	private Intent intent;
	private String uid;
	private ImageManager mImageManager;
	private File file;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_info);
		mImageManager=new ImageManager();
		String path=Environment.getExternalStorageDirectory()
				.toString() + "/dobi/avator.png";
				file=new File(path);
				if(file.exists()){
					file.delete();
				}
		// 初始化
		ini();
		if (uid != null) {
			loadUserInfo();
		}
	}

	private void loadUserInfo() {
		NetUtils.getUserInfo(uid, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] header, byte[] data) {
				try {
					JSONObject json = new JSONObject(new String(data));
					JSONObject userObject = json.getJSONObject("userInfo");
					setViewBitmap(mBtntx, userObject.getString("user_avatar"));
					mText_nc.setText(userObject.getString("user_login"));
					mText_sjhm.setText(userObject.getString("user_mobile"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] data,
					Throwable arg3) {
				
			}
		});

	}

	private void ini() {
		mBtntx = (CircleImageView) findViewById(R.id.mBtntx);
		mText_nc = (TextView) findViewById(R.id.mText_nc);
		mText_sjhm = (TextView) findViewById(R.id.mText_sjhm);
		linear_shdz = (RelativeLayout) findViewById(R.id.linear_shdz);
		linear_xgmm = (RelativeLayout) findViewById(R.id.linear_xgmm);
		linear_tx = (RelativeLayout) findViewById(R.id.linear_tx);
		logout = (Button) findViewById(R.id.logout);
		sp = CommonMethod.getPreferences(getApplicationContext());
		uid = sp.getString("uid", null);
		mImageLoader = ImageLoader.initLoader(AccountInfoActivity.this);
		logout.setOnClickListener(this);
		linear_shdz.setOnClickListener(this);
		linear_xgmm.setOnClickListener(this);
		linear_tx.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		intent = new Intent();
		switch (v.getId()) {
		case R.id.logout:

			Editor edit = sp.edit();
			edit.clear();
			edit.commit();
			intent.setClass(AccountInfoActivity.this, HomeActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.linear_shdz:
			intent.setClass(AccountInfoActivity.this, EditAddressActivity.class);
			startActivity(intent);
			break;
		case R.id.linear_xgmm:
			intent.setClass(AccountInfoActivity.this, UpdateActivity.class);
			startActivity(intent);
			break;
		case R.id.linear_tx:
			dialog = CommonMethod
					.showPhotoDialog(AccountInfoActivity.this);
			Button camera = (Button) dialog.findViewById(R.id.xiangji);
			Button photo = (Button) dialog.findViewById(R.id.xiangce);
			Button cancle = (Button) dialog.findViewById(R.id.selected_cancle);
			camera.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					Uri mOutPutFileUri;
					// 文件夹dobi
					String path = Environment.getExternalStorageDirectory()
							.toString() + "/dobi";
					File path1 = new File(path);
					if (!path1.exists()) {
						path1.mkdirs();
					}
					File file = new File(path1, "avator" + ".png");
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
			dialog.show();

			break;
		default:
			break;
		}

	}

	public void setViewBitmap(final ImageView view, String url) {
		Bitmap bitmap = mImageLoader.downloadImage(NetUtils.IMAGE_PREFIX + url,
				new onImageLoaderListener() {
					public void onImageLoader(Bitmap bitmap, String url) {
						if (bitmap != null) {
							view.setImageBitmap(bitmap);
						}
					}
				}, new Point(80, 80));
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_CANCELED) {
			Toast.makeText(this, "照片获取失败",0).show();
		} else {
			switch (requestCode) {
			case 0:// 相机
				if (file.exists()) {
					Uri uri = Uri.fromFile(file);
					startImageAction(uri, 200, 200, 2, true);
				}
				break;
			case 1:
				try {
					if (data == null) {
						return;
					}
					if (!Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						Toast.makeText(this, "SD不可用", 1).show();
						return;
					}
					Uri uri = null;
					uri = data.getData();
					startImageAction(uri, 200, 200, 2, true);

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 2:
				if (dialog != null&&dialog.isShowing()) {
					dialog.dismiss();
                }
                if (data == null) {
                    return;
                } else {
                    saveCropAvator(data);
                    RequestParams params=new RequestParams();
                    params.put("uid", uid);
                    try {
						params.put("avatar", file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
                    NetUtils.updateAvaor(params,new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] data) {
							
							Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
							mBtntx.setImageBitmap(bitmap);
						}
						@Override
						public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
							
						}
					});
                }
                break;
			}
		}

	}

	/**
	 * 将图片变为圆角
	 * 
	 * @param bitmap
	 *            原Bitmap图片
	 * @param pixels
	 *            图片圆角的弧度(单位:像素(px))
	 * @return 带有圆角的图片(Bitmap 类型)
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			if (bitmap != null) {
				bitmap = toRoundCorner(bitmap);// 调用圆角处理方法
				FileOutputStream out;
				try {
					out = mImageManager.creatFile("avator", ".png", "");
					bitmap.compress(CompressFormat.PNG, 100, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
	
	
	
	public void finishActivity(View view) {
		finish();
	}

}
