package com.dobi.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

/**
 * 本地图片加载器,采用的是异步解析本地图片，单例模式利用getInstance()获取NativeImageLoader实例
 * 调用loadNativeImage()方法加载本地图片，此类可作为一个加载本地图片的工具类
 * 
 * @blog http://blog.csdn.net/xiaanming
 * 
 * @author xiaanming
 * 
 */
public class ImageLoader {
	/**
	 * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * 操作文件相关类对象的引用
	 */
	private FileUtils fileUtils;
	/**
	 * 下载Image的线程池
	 */
	private ExecutorService mImageThreadPool = null;

	public static final int StreamFlushBufferSzie = 100;// buffer size= 1K
	public static ImageLoader mImageLoader;
	private ImageLoader(Context context) {
		// 获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 8;
		// 给LruCache分配1/8 4M
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
			// 必须重写此方法，来测量Bitmap的大小
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
		fileUtils = new FileUtils(context);
	}
	public static ImageLoader initLoader(Context context){
		if (mImageLoader == null) {
			synchronized (ImageLoader.class) {
				if (mImageLoader == null) {
					// 为了下载图片更加的流畅，我们用了2个线程来下载图片
					mImageLoader = new ImageLoader(context);
				}
			}
		}
		return mImageLoader;
	}
	
	
	
	
	
	
	/**
	 * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
	 * 
	 * @return
	 */
	public ExecutorService getThreadPool() {
		if (mImageThreadPool == null) {
			synchronized (ExecutorService.class) {
				if (mImageThreadPool == null) {
					// 为了下载图片更加的流畅，我们用了2个线程来下载图片
					mImageThreadPool = Executors.newFixedThreadPool(1);
				}
			}
		}

		return mImageThreadPool;

	}

	/**
	 * 添加Bitmap到内存缓存
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public void removeBitmapToMemoryCache(String key) {
		if (key != null && getBitmapFromMemCache(key) != null) {
			mMemoryCache.remove(key);
		}
	}

	/**
	 * 从内存缓存中获取一个Bitmap
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 先从内存缓存中获取Bitmap,如果没有就从SD卡或者手机缓存中获取，SD卡或者手机缓存 没有就去下载
	 * 
	 * @param url
	 * @param listener
	 * @return
	 */
	public Bitmap downloadImage(final String url,
			final onImageLoaderListener listener, final Point mPoint) {
		// 替换Url中非字母和非数字的字符，这里比较重要，因为我们用Url作为文件名，比如我们的Url
		// 是Http://xiaanming/abc.jpg;用这个作为图片名称，系统会认为xiaanming为一个目录，
		// 我们没有创建此目录保存文件就会报错
		final String subUrl = url.replaceAll("[^\\w]", "");
		Bitmap bitmap = showCacheBitmap(subUrl, mPoint, false);
		if (bitmap != null) {
			return bitmap;
		} else {
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if(msg.obj!=null&&listener!=null){
						listener.onImageLoader((Bitmap) msg.obj, url);
					}
					super.handleMessage(msg);
				}
			};
			getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					Bitmap sampleBitmap = getBitmapFormUrl(url);
					if(sampleBitmap!=null){
						Bitmap bitmap = decode(sampleBitmap, mPoint == null ? 0: mPoint.x, mPoint == null ? 0 : mPoint.y);
						Message msg = handler.obtainMessage();
						msg.obj = bitmap;
						handler.sendMessage(msg);
						try {
							// 保存在SD卡或者手机目录
							fileUtils.savaBitmap(subUrl, sampleBitmap);
							// sampleBitmap.recycle();
						} catch (IOException e) {
							e.printStackTrace();
						}

						// 将Bitmap 加入内存缓存
						addBitmapToMemoryCache(subUrl, bitmap);
					}else{
						Message msg = handler.obtainMessage();
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			});
		}

		return null;
	}
	
	
	
	/**
	 * 先从内存缓存中获取Bitmap,如果没有就从SD卡或者手机缓存中获取，SD卡或者手机缓存 没有就去下载
	 * 
	 * @param url
	 * @param listener
	 * @return
	 */
	public Bitmap downMoreloadImage(final String url,
			final onImageLoaderListener listener, final Point mPoint,final int count) {
		// 替换Url中非字母和非数字的字符，这里比较重要，因为我们用Url作为文件名，比如我们的Url
		// 是Http://xiaanming/abc.jpg;用这个作为图片名称，系统会认为xiaanming为一个目录，
		// 我们没有创建此目录保存文件就会报错
		final String prefix=(ConstValue.urlPrefix+url).substring(0, (ConstValue.urlPrefix+url).lastIndexOf("/"));
		final String subUrl = (ConstValue.urlPrefix+url).replaceAll("[^\\w]", "");
		Bitmap bitmap = showCacheBitmap(subUrl, mPoint, false);
		if (bitmap != null) {
			return bitmap;
		} else {
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					listener.onImageLoader((Bitmap) msg.obj, url);
					super.handleMessage(msg);
				}
			};
			getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					
					Bitmap sampleBitmap = getBitmapFormUrl((ConstValue.urlPrefix+url));
					for(int i=0;i<count;i++){
						try {
							String path=prefix+"/"+(i+1)+".png";
							Bitmap bitmap=getBitmapFormUrl(path);
							fileUtils.savaBitmap(path.replaceAll("[^\\w]", ""), bitmap);
							bitmap.recycle();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					if(sampleBitmap!=null){
						Message msg = handler.obtainMessage();
						msg.obj = sampleBitmap;
						handler.sendMessage(msg);
						try {
							// 保存在SD卡或者手机目录
							fileUtils.savaBitmap(subUrl, sampleBitmap);
							// sampleBitmap.recycle();
						} catch (IOException e) {
							e.printStackTrace();
						}

						// 将Bitmap 加入内存缓存
						addBitmapToMemoryCache(subUrl, sampleBitmap);
					}else{
						Message msg = handler.obtainMessage();
						msg.obj = null;
						handler.sendMessage(msg);
					}
				}
			});
		}

		return null;
	}
	
	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * 
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public Bitmap decodeThumbBitmapForInputStream(InputStream in,
			int viewWidth, int viewHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		// 设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);

		// 设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;  
		options.inInputShareable = true;
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		return BitmapFactory.decodeStream(in, null, options);
	}

	public Bitmap decode(Bitmap bitmap, int viewWidth, int viewHeight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int scale = 1;
		if (viewWidth == 0 || viewWidth == 0) {
			scale = 1;
		} else {
			if (width > viewWidth || height > viewWidth) {
				int widthScale = Math.round((float) width / (float) viewWidth);
				int heightScale = Math
						.round((float) height / (float) viewWidth);

				// 为了保证图片不缩放变形，我们取宽高比例最小的那个
				scale = widthScale < heightScale ? widthScale : heightScale;
			}
		}
		scale = scale == 0 ? 1 : scale;
		return Bitmap.createScaledBitmap(bitmap, width / scale, height / scale,
				false);
	}

	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * 
	 * @param options
	 * @param width
	 * @param height
	 */
	private int computeScale(BitmapFactory.Options options, int viewWidth,
			int viewHeight) {
		int inSampleSize = 1;
		if (viewWidth == 0 || viewWidth == 0) {
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;

		// 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
			int widthScale = Math
					.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight
					/ (float) viewWidth);

			// 为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

	// /**
	// * 先从内存缓存中获取Bitmap,如果没有就从SD卡或者手机缓存中获取，SD卡或者手机缓存
	// * 没有就去下载
	// * @param url
	// * @param listener
	// * @return
	// */
	// public Bitmap downloadImage(final String url, final onImageLoaderListener
	// listener,String postfix){
	//
	// final String subUrl = url.replaceAll("[^\\w]", "");
	// Bitmap bitmap = showCacheBitmap(subUrl);
	// if(bitmap != null){
	// return bitmap;
	// }else{
	// final Handler handler = new Handler(){
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// listener.onSVGLoader((InputStream) msg.obj,url);
	// }
	// };
	// getThreadPool().execute(new Runnable() {
	// @Override
	// public void run() {
	// InputStream in=getInputStreamFormUrl(url);
	// SVG s=SVGParser.getSVGFromInputStream(in);
	// Path facePath = s.getPath();
	// ImageManager mImageManager=new ImageManager();
	// Bitmap bitmap = mImageManager.getBitmapFromPath(facePath,
	// s.getPicture().getWidth(),
	// s.getPicture().getHeight());
	// Message msg = handler.obtainMessage();
	// msg.obj = in;
	// handler.sendMessage(msg);
	// try {
	// //保存在SD卡或者手机目录
	// fileUtils.savaSVG(subUrl, in);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// addBitmapToMemoryCache(subUrl, bitmap);
	// }
	// });
	//
	// }
	//
	//
	//
	//
	// return null;
	// }

	/**
	 * 获取Bitmap, 内存中没有就去手机或者sd卡中获取，这一步在getView中会调用，比较关键的一步
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap showCacheBitmap(String url) {
		if (getBitmapFromMemCache(url) != null) {
			return getBitmapFromMemCache(url);
		} else if (fileUtils.isFileExists(url)
				&& fileUtils.getFileSize(url) != 0) {
			// 从SD卡获取手机里面获取Bitmap
			Bitmap bitmap = fileUtils.getBitmap(url);

			// 将Bitmap 加入内存缓存
			addBitmapToMemoryCache(url, bitmap);
			return bitmap;
		}

		return null;
	}

	public Bitmap showCacheBitmap(String url, Point mPoint, Boolean isSample) {
		if (getBitmapFromMemCache(url) != null) {
			return getBitmapFromMemCache(url);
		} else if (fileUtils.isFileExists(url)
				&& fileUtils.getFileSize(url) != 0) {

			// 从SD卡获取手机里面获取Bitmap
			Bitmap bitmap = null;
			if (isSample) {
				bitmap = fileUtils.getBitmap(url);
				addBitmapToMemoryCache(url, fileUtils.getBitmap(url, mPoint));
			} else {
				bitmap = fileUtils.getBitmap(url, mPoint);
				// 将Bitmap 加入内存缓存
				addBitmapToMemoryCache(url, bitmap);
			}

			return bitmap;
		}

		return null;
	}

	/**
	 * 从Url中获取Bitmap
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFormUrl(String url) {
		
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL mImageUrl = new URL(url);
			con = (HttpURLConnection) mImageUrl.openConnection();
			con.setConnectTimeout(6 * 1000);
			con.setReadTimeout(6 * 1000);
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			options.inJustDecodeBounds=true;
			options.inSampleSize = computeScale(options,600,800);
			options.inJustDecodeBounds=false;
			options.inPurgeable = true;  
		    options.inInputShareable = true;
			bitmap = BitmapFactory.decodeStream(con.getInputStream(),null,options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return bitmap;
	}

	/**
	 * 从Url中获取Bitmap
	 * 
	 * @param url
	 * @return
	 */
	private InputStream getInputStreamFormUrl(String url) {
		InputStream in = null;
		HttpURLConnection con = null;
		try {
			URL mImageUrl = new URL(url);
			con = (HttpURLConnection) mImageUrl.openConnection();
			con.setConnectTimeout(10 * 1000);
			con.setReadTimeout(10 * 1000);
			con.setDoInput(true);
			con.setDoOutput(true);
			in = con.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return in;
	}

	/**
	 * 取消正在下载的任务
	 */
	public synchronized void cancelTask() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}
	public Bitmap getFromFile(String path){
		return fileUtils.getBitmap(path);
	}

	/**
	 * 异步下载图片的回调接口
	 * 
	 * @author len
	 * 
	 */
	public interface onImageLoaderListener {
		void onImageLoader(Bitmap bitmap, String url);
		// void onSVGLoader(InputStream in, String url);
	}


}
