package com.dobi.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

public class FileUtils {  
    /** 
     * sd卡的根目录 
     */  
    private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();  
    /** 
     * 手机的缓存根目录 
     */  
    private static String mDataRootPath = null;  
    /** 
     * 保存Image的目录名 
     */  
    private final static String FOLDER_NAME = "/dobi/cache";  
      
      
    public FileUtils(Context context){  
        mDataRootPath = context.getCacheDir().getPath();  
    }  
      
  
    /** 
     * 获取储存Image的目录 
     * @return 
     */  
    private static String getStorageDirectory(){  
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?  
                mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;  
    }  
      
    /** 
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录 
     * @param fileName  
     * @param bitmap    
     * @throws IOException 
     */  
    public void savaBitmap(String fileName, Bitmap bitmap) throws IOException{  
        if(bitmap == null){  
            return;  
        }  
        String path = getStorageDirectory();  
        File folderFile = new File(path);  
        if(!folderFile.exists()){  
            folderFile.mkdir();  
        }  
        File file = new File(path + File.separator + fileName);  
        file.createNewFile();  
        FileOutputStream fos = new FileOutputStream(file);  
        bitmap.compress(CompressFormat.PNG, 100, fos);  
        fos.flush();  
        fos.close();  
    }  
    /** 
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录 
     * @param fileName  
     * @param bitmap    
     * @throws IOException 
     */  
    public void savaSVG(String fileName, InputStream in) throws IOException{  
        if(in == null){  
            return;  
        }  
        String path = getStorageDirectory();  
        File folderFile = new File(path);  
        if(!folderFile.exists()){  
            folderFile.mkdir();  
        }  
        File file = new File(path + File.separator + fileName);  
        file.createNewFile();  
        FileOutputStream fos = new FileOutputStream(file);
        int len=0;
        byte[] buffer=new byte[1024];
        while (in.read(buffer)!=-1) {
			fos.write(buffer, 0, len);
		}
        fos.flush();  
        fos.close();  
    }  
      
    /** 
     * 从手机或者sd卡获取Bitmap 
     * @param fileName 
     * @return 
     */  
    public Bitmap getBitmap(String fileName){  
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);  
    }
    /** 
     * 从手机或者sd卡获取Bitmap 
     * @param fileName 
     * @return 
     */  
    public Bitmap getBitmap(String fileName,Point mPoint){
    	return decodeThumbBitmapForFile(getStorageDirectory() + File.separator + fileName,
    			mPoint == null ? 0: mPoint.x, mPoint == null ? 0: mPoint.y);
    }
    /** 
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图 
     * @param path 
     * @param viewWidth 
     * @param viewHeight 
     * @return 
     */  
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        //设置为true,表示解析Bitmap对象，该对象不占内存  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeFile(path, options);  
        //设置缩放比例  
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);  
          
        //设置为false,解析Bitmap对象加入到内存中  
        options.inJustDecodeBounds = false;  
          
        return BitmapFactory.decodeFile(path, options);  
    }      
    /** 
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放 
     * @param options 
     * @param width 
     * @param height 
     */  
    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){  
        int inSampleSize = 1;  
        if(viewWidth == 0 || viewWidth == 0){  
            return inSampleSize;  
        }  
        int bitmapWidth = options.outWidth;  
        int bitmapHeight = options.outHeight;  
          
        //假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例  
        if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){  
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);  
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);  
              
            //为了保证图片不缩放变形，我们取宽高比例最小的那个  
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;  
        }  
        return inSampleSize;  
    }  
      
      
    /** 
     * 判断文件是否存在 
     * @param fileName 
     * @return 
     */  
    public boolean isFileExists(String fileName){  
        return new File(getStorageDirectory() + File.separator + fileName).exists();  
    }  
      
    /** 
     * 获取文件的大小 
     * @param fileName 
     * @return 
     */  
    public long getFileSize(String fileName) {  
        return new File(getStorageDirectory() + File.separator + fileName).length();  
    }  
      
    public static void deleteFromName(String name){
    	File file=new File(getStorageDirectory() + File.separator + name);
    	Log.i("jiang", file.getAbsolutePath());
    	if(file.exists()){
    		file.delete();
    	}
    }
    /** 
     * 删除SD卡或者手机的缓存图片和目录 
     */  
    public void deleteFile() {  
        File dirFile = new File(getStorageDirectory());  
        if(! dirFile.exists()){  
            return;  
        }  
        if (dirFile.isDirectory()) {  
            String[] children = dirFile.list();  
            for (int i = 0; i < children.length; i++) {  
                new File(dirFile, children[i]).delete();  
            }  
        }  
          
        dirFile.delete();  
    }  
}