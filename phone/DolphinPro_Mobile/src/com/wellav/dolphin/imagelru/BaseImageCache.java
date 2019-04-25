package com.wellav.dolphin.imagelru;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年3月10日
 * @author Cheng
 */
public abstract class BaseImageCache {
	/**
	 * 内存缓存队列
	 */
	protected static LruCache<String, Bitmap> mMemoryLruCache;

	public void release() {
		if (mMemoryLruCache != null)
			mMemoryLruCache.evictAll();
		mMemoryLruCache = null;
	}

	protected BaseImageCache() {
		if (mMemoryLruCache == null) {
			int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
			mMemoryLruCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getRowBytes() * bitmap.getHeight();
				}
			};
		}
	}

	public LruCache<String, Bitmap> getMemoryLruCache() {
		return mMemoryLruCache;
	}

	public Bitmap getCacheFromMemory(String key) {
		return mMemoryLruCache.get(key);
	}

	public Bitmap removeCacheFromMemory(String key) {
		return mMemoryLruCache.remove(key);
	}
}
