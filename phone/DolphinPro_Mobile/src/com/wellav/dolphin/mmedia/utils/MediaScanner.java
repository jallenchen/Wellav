package com.wellav.dolphin.mmedia.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

public class MediaScanner {
	private Context mContext;

	/**
	 * 要查询的uri
	 */
	private Uri mUri;
	
	public MediaScanner(Context context,Uri uri){
		this.mContext = context;
		mUri=uri;
	}
	
	/**
	 * 利用ContentProvider扫描手机中的图片或视频，此方法在运行在子线程中
	 */
	public void scanMedia(final ScanCompleteCallBack callback) {
		final Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				callback.scanComplete((Cursor)msg.obj);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				ContentResolver mContentResolver = mContext.getContentResolver();


				Cursor mCursor = mContentResolver.query(mUri, null, null,null, null);
				
				//利用Handler通知调用线程
				Message msg = mHandler.obtainMessage();
				msg.obj = mCursor;
				mHandler.sendMessage(msg);
			}
		}).start();

	}
	
	
	public static interface ScanCompleteCallBack{
		public void scanComplete(Cursor cursor);
	}


}

