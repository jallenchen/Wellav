package com.wellav.dolphin.mmedia.utils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wellav.dolphin.imagelru.BaseImageCache;
import com.wellav.dolphin.imagelru.ImageCacheUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.util.LruCache;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * 本地图片加载器,采用的是异步解析本地图片，单例模式利用getInstance()获取NativeImageLoader实例
 * 调用loadNativeImage()方法加载本地图片，此类可作为一个加载本地图片的工具类
 * 
 * @blog 
 * 
 * @author 
 *
 */
public class NativeImageLoader extends BaseImageCache{
	private static final String TAG = NativeImageLoader.class.getSimpleName();
	private static NativeImageLoader mInstance = null;//单例模式
	private ExecutorService mImageThreadPool ;
	
	private NativeImageLoader(){
		super();
		mImageThreadPool = Executors.newFixedThreadPool(3);		
	}
	@Override
	public void release() {
		// TODO Auto-generated method stub
		super.release();
		mInstance=null;
	}
	/**
	 * 通过此方法来获取NativeImageLoader的实例
	 * @return
	 */
	public static NativeImageLoader getInstance(){
		if(mInstance==null)
			mInstance=new NativeImageLoader();
		return mInstance;
	}
	
	
	/**
	 * 加载本地图片，
	 * @param path
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path, final Point mPoint, final NativeImageCallBack mCallBack){
		//先获取内存中的Bitmap
				Bitmap bitmap = super.getCacheFromMemory(path);
				final Handler mHander = new Handler(){

					@Override
					public void handleMessage(Message msg) {
					
						if(mCallBack!=null)
							mCallBack.onImageLoader((Bitmap)msg.obj, path);
					}
					
				};
				
				//若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
				if(bitmap == null){
					
					mImageThreadPool.execute(new Runnable() {
						
						@Override
						public void run() {
							//先获取图片的缩略图
							Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint.x, mPoint.y);
							Message msg = mHander.obtainMessage();
							msg.obj = mBitmap;
							mHander.sendMessage(msg);					
							//将图片加入到内存缓存
							addBitmapToMemoryCache(path, mBitmap);
						}
					});
				}
				return bitmap;
	}
	
	/**
	 * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
	 * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack mCallBack)来加载
	 * @param path
	 * @param mPoint
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImageThumbnail(final GridView view,final String path, final Point mPoint, final NativeImageCallBack mCallBack){
		//先获取内存中的Bitmap
		Bitmap bitmap = super.getCacheFromMemory(path);
		
		final Handler mHander = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				final ImageView mImageView=(ImageView)view.findViewWithTag(path);
				if(mImageView!=null)
					mImageView.setImageBitmap((Bitmap)msg.obj);
				if(mCallBack!=null)
					mCallBack.onImageLoader((Bitmap)msg.obj, path);
			}
			
		};
		
		//若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
		if(bitmap == null){
			mImageThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					//先获取图片的缩略图
					Bitmap mBitmap = NativeThumbTool.getImageThumbnail(path, mPoint.x,mPoint.y);
					if(mBitmap == null){
						return;
					}
					Matrix matrix=new Matrix();
					matrix.postRotate(readPictureDegree(path));
					mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
					Message msg = mHander.obtainMessage();
					msg.obj = mBitmap;
					mHander.sendMessage(msg);					
					//将图片加入到内存缓存
					addBitmapToMemoryCache(path, mBitmap);
				}
			});
		}
		return bitmap;
		
	}

	
	public Bitmap loadNativeVideoThumbnail(final GridView view,final String path, final Point mPoint, final NativeImageCallBack mCallBack){
		//先获取内存中的Bitmap
		Bitmap bitmap = super.getCacheFromMemory(path);
		
		final Handler mHander = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				final ImageView mImageView=(ImageView)view.findViewWithTag(path);
				if(mImageView!=null)
					mImageView.setImageBitmap((Bitmap)msg.obj);
				if(mCallBack!=null)
					mCallBack.onImageLoader((Bitmap)msg.obj, path);
			}
			
		};
		
		//若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
		if(bitmap == null){
			mImageThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					//先获取图片的缩略图
					Bitmap mBitmap = NativeThumbTool.getVideoThumbnail(path,mPoint.x,mPoint.y, Thumbnails.MINI_KIND );
					Message msg = mHander.obtainMessage();
					msg.obj = mBitmap;
					mHander.sendMessage(msg);					
					//将图片加入到内存缓存
					addBitmapToMemoryCache(path, mBitmap);
				}
			});
		}
		return bitmap;
		
	}
	/**
	 * 往内存缓存中添加Bitmap
	 * 
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if(super.getMemoryLruCache() == null){
			return;
		}
		if (super.getCacheFromMemory(key)== null && bitmap != null) {
			mMemoryLruCache.put(key, bitmap);
		}
	}

	
	
	
	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public static Bitmap decodeThumbBitmapForFile (String path, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		//设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		//设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		
		
		
		Bitmap mBitmap=BitmapFactory.decodeFile(path, options);
		Matrix matrix=new Matrix();
		matrix.postRotate(readPictureDegree(path));
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
		return mBitmap;
	}
	
	
	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * @param options
	 * @param width
	 * @param height
	 */
	private static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		int inSampleSize = 1;
		if(viewWidth == 0 || viewWidth == 0){
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		
		//假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if(bitmapWidth > viewWidth || bitmapHeight > viewHeight){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewHeight);
			
			//为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}
	
	
	/**
	 * 加载本地图片的回调接口
	 * 
	 * @author 
	 *
	 */
	public interface NativeImageCallBack{
		/**
		 * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
		 * @param bitmap
		 * @param path
		 */
		public void onImageLoader(Bitmap bitmap, String path);
	}
	
	public static int readPictureDegree(String path) {
		int degree = 0;
		ExifInterface exifInterface;

		try {
			exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return degree;
	}
}
