package com.wellav.dolphin.imagelru;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView.ScaleType;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.imagelru.DiskLruCache.Snapshot;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月11日
 * @author Cheng
 */
public class ImageCacheUtil extends BaseImageCache implements ImageCache {
	
	private static ImageCacheUtil mInstance;
	/**
	 * 硬盘缓存队列
	 */
	private static DiskLruCache mDiskLruCache;
	/**
	 * 硬盘缓存容量
	 */
	private static final int DISK_MAXSIZE = 10 * 1024 * 1024;
	
	private ImageCacheUtil() {
		
		super();

		try {
			mDiskLruCache = DiskLruCache
					.open(getDiskCacheDir(HBaseApp.getInstance(), "Rabbit"),
							getAppVersion(HBaseApp.getInstance()), 1,
							DISK_MAXSIZE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static ImageCacheUtil getInstance()
	{
		if(mInstance==null)
			mInstance=new ImageCacheUtil();
		return mInstance;
	}
	@Override
	public Bitmap getBitmap(String url) {
		if(url == null || mInstance == null){
			return null;
		}
		
		if (mMemoryLruCache.get(url) != null)
			return mMemoryLruCache.get(url);
		else {
			String key = MD5Utils.md5(url);
			try {
				if (mDiskLruCache.get(key) != null) {
					Snapshot snapshot = mDiskLruCache.get(key);
					Bitmap bitmap = null;
					if (snapshot != null) {
						bitmap = BitmapFactory.decodeStream(snapshot
								.getInputStream(0));
						mMemoryLruCache.put(url, bitmap);
					}
					return bitmap;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		if(url == null || mInstance == null){
			return;
		}
			mMemoryLruCache.put(url, bitmap);
		// TODO Auto-generated method stub
		// ����LruCache����
		
		// �ж��Ƿ����DiskLruCache���棬��û�д���
		String key = MD5Utils.md5(url);
		try {
			if (mDiskLruCache.get(key) == null) {
				DiskLruCache.Editor editor = mDiskLruCache.edit(key);
				if (editor != null) {
					OutputStream outputStream = editor.newOutputStream(0);
					if (bitmap.compress(CompressFormat.JPEG, 100, outputStream)) {
						editor.commit();
					} else {
						editor.abort();
					}
				}
				mDiskLruCache.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
	public static String getCacheKey(String url, int maxWidth, int maxHeight,
			ScaleType scaleType) {
		return new StringBuilder(url.length() + 12).append("#W")
				.append(maxWidth).append("#H").append(maxHeight).append("#S")
				.append(scaleType.ordinal()).append(url).toString();
	}
	
	
	@Override
	public void release() {
		// TODO Auto-generated method stub
		super.release();
		try {
			mDiskLruCache.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mDiskLruCache = null;
		mInstance=null;
	}
}
